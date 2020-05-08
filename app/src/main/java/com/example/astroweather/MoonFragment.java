package com.example.astroweather;

import android.annotation.SuppressLint;
//import android.app.Fragment;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static com.example.astroweather.DateTimeUtils.formatDate;
import static com.example.astroweather.DateTimeUtils.formatTime;
import static com.example.astroweather.DateTimeUtils.getOffsetHours;


public class MoonFragment extends Fragment {

	private int day;
	private int month;
	private int year;
	private int hour;
	private int minute;
	private int second;
	private Double x = 0.0;
	private Double y = 0.0;
	private String moon_rise;
	private String moon_set;
	private String new_moon;
	private String full_moon;
	private String phase;
	private String lunar_day;


	private void updateDateTime() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		day = calendar.get(Calendar.DATE);
		month = calendar.get(Calendar.MONTH) + 1;
		year = calendar.get(Calendar.YEAR);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
	}


	private void updateMoonInfo() {
		updateDateTime();
		AstroDateTime date_time = new AstroDateTime(year, month, day, hour, minute, second, getOffsetHours(TimeZone.getDefault()), true);
		AstroCalculator.Location location = new AstroCalculator.Location(x, y);
		AstroCalculator calculator = new AstroCalculator(date_time, location);
		AstroCalculator.MoonInfo moon = calculator.getMoonInfo();
		moon_rise = formatTime(moon.getMoonrise());
		moon_set = formatTime(moon.getMoonset());
		new_moon = formatDate(moon.getNextNewMoon());
		full_moon = formatDate(moon.getNextFullMoon());
		phase = Integer.toString((int)(moon.getIllumination() * 100)) + "%";
		lunar_day = Integer.toString((int)moon.getAge());
	}


	public void setX(double x) {
		this.x = x;
	}


	public void setY(double y) {
		this.y = y;
	}


	public MoonFragment() {
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_moon, container, false);
	}


	public void updateTextViews() {
		updateMoonInfo();
		TextView moonrise_time_value = (TextView)getView().findViewById(R.id.moonrise_time_value);
		moonrise_time_value.setText(moon_rise);
		TextView moonset_time_value = (TextView)getView().findViewById(R.id.moonset_time_value);
		moonset_time_value.setText(moon_set);
		TextView newmoon_time_value = (TextView)getView().findViewById(R.id.new_moon_time_value);
		newmoon_time_value.setText(new_moon);
		TextView fullmoon_time_value = (TextView)getView().findViewById(R.id.full_moon_time_value);
		fullmoon_time_value.setText(full_moon);
		TextView moonphase_value = (TextView)getView().findViewById(R.id.moon_phase_value);
		moonphase_value.setText(phase);
		TextView moonday_value = (TextView)getView().findViewById(R.id.moon_day_value);
		moonday_value.setText(lunar_day + "x: " + x.toString());
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		updateTextViews();
	}

	public static MoonFragment newInstance() {
		MoonFragment fragment = new MoonFragment();
		return fragment;
	}
}
