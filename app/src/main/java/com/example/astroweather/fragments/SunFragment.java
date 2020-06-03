package com.example.astroweather.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.example.astroweather.R;

import java.util.TimeZone;

import static com.example.astroweather.DateTimeUtils.formatTime;
import static com.example.astroweather.DateTimeUtils.getOffsetHours;


public class SunFragment extends Fragment {

	private Double x = 0.0;
	private Double y = 0.0;
	private String sunrise_time;
	private String sunrise_azimuth;
	private String sunset_time;
	private String sunset_azimuth;
	private String dusk;
	private String dawn;


	public void setX(double x) {
		this.x = x;
	}


	public void setY(double y) {
		this.y = y;
	}


	public void calculate(int day, int month, int year, int hour, int minute, int second) {
		AstroDateTime date_time = new AstroDateTime(year, month, day, hour, minute, second, getOffsetHours(TimeZone.getDefault()), true);
		AstroCalculator.Location location = new AstroCalculator.Location(x, y);
		AstroCalculator calculator = new AstroCalculator(date_time, location);
		AstroCalculator.SunInfo sun = calculator.getSunInfo();
		//sunrise_time = Integer.toString(minute) + ":" + Integer.toString(second);
		sunrise_time = formatTime(sun.getSunrise());
		sunrise_azimuth = Integer.toString((int)sun.getAzimuthRise());
		sunset_time = formatTime(sun.getSunset());
		sunset_azimuth = Integer.toString((int)sun.getAzimuthSet());
		dusk = formatTime(sun.getTwilightMorning());
		dawn = formatTime(sun.getTwilightEvening());
	}

	public SunFragment() {
		// Required empty public constructor
	}


	public void updateTextViews() {
		TextView sunrise_time_value = (TextView)getView().findViewById(R.id.sunrise_time_value);
		sunrise_time_value.setText(sunrise_time);
		TextView sunrise_azimuth_value = (TextView)getView().findViewById(R.id.sunrise_azimuth_value);
		sunrise_azimuth_value.setText(sunrise_azimuth);
		TextView sunset_time_value = (TextView)getView().findViewById(R.id.sunset_time_value);
		sunset_time_value.setText(sunset_time);
		TextView sunset_azimuth_value = (TextView)getView().findViewById(R.id.sunset_azimuth_value);
		sunset_azimuth_value.setText(sunset_azimuth);
		TextView dusk_time_value = (TextView)getView().findViewById(R.id.dusk_time_value);
		dusk_time_value.setText(dusk);
		TextView dawn_time_value = (TextView)getView().findViewById(R.id.dawn_time_value);
		dawn_time_value.setText(dawn);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_sun, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		updateTextViews();
	}

	public static SunFragment newInstance() {
		SunFragment fragment = new SunFragment();
		return fragment;
	}
}
