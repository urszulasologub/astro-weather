package com.example.astroweather;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.astrocalculator.AstroDateTime;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class FragmentView extends FragmentActivity {

	private int day;
	private int month;
	private int year;
	private int hour;
	private int minute;
	private int second;
	private Double x;
	private Double y;
	private Thread update_time_thread;


	private void updateDateTime() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		//Date date = calendar.getTime();
		day = calendar.get(Calendar.DATE);
		month = calendar.get(Calendar.MONTH) + 1;
		year = calendar.get(Calendar.YEAR);
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
	}

	private void setTime(TextView current_time) {
		@SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hour, minute, second);
		current_time.setText(time);
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_view);

		Intent this_intent = getIntent();
		x = this_intent.getDoubleExtra("x", 0);
		y = this_intent.getDoubleExtra("y", 0);

		TextView x_label = (TextView)findViewById(R.id.x_label);
		x_label.setText("x: " + Double.toString(x));
		TextView y_label = (TextView)findViewById(R.id.y_label);
		y_label.setText("y: " + Double.toString(y));

		final TextView current_time = (TextView)findViewById(R.id.current_time);
		updateDateTime();
		setTime(current_time);

		update_time_thread = new Thread() {
			@Override
			public void run() {
				try {
					while (!update_time_thread.isInterrupted()) {
						Thread.sleep(1000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								updateDateTime();
								setTime(current_time);
							}
						});
					}
				} catch (InterruptedException e) {}
			}
		};

		/*update_time_thread.start();
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		MoonFragment moon_fragment = (MoonFragment) fragmentManager.findFragmentById(R.id.fragment_moon);
		fragmentTransaction.replace(R.id.fragment_sun, MoonFragment.newInstance());
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();*/
	}

}
