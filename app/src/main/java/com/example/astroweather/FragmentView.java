package com.example.astroweather;

import android.annotation.SuppressLint;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.TimeZone;

public class FragmentView extends AppCompatActivity {

	private int day;
	private int month;
	private int year;
	private int hour;
	private int minute;
	private int second;
	private Double x;
	private Double y;
	private Thread update_time_thread;
	private Thread synchronize_data_thread;
	private int update_time;
	private SunFragment sun_fragment;
	private MoonFragment moon_fragment;
	private boolean update_text_views = true;


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("day", day);
		savedInstanceState.putInt("month", month);
		savedInstanceState.putInt("year", year);
		savedInstanceState.putInt("hour", hour);
		savedInstanceState.putInt("minute", minute);
		savedInstanceState.putInt("second", second);
	}


	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		day = savedInstanceState.getInt("day");
		month = savedInstanceState.getInt("month");
		year = savedInstanceState.getInt("year");
		hour = savedInstanceState.getInt("hour");
		minute = savedInstanceState.getInt("minute");
		second = savedInstanceState.getInt("second");
	}



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
		update_time = this_intent.getIntExtra("update_time", 15 * 60);

		TextView x_label = (TextView)findViewById(R.id.x_label);
		x_label.setText("x: " + Double.toString(x));
		TextView y_label = (TextView)findViewById(R.id.y_label);
		y_label.setText("y: " + Double.toString(y));

		final TextView current_time = (TextView)findViewById(R.id.current_time);
		updateDateTime();
		setTime(current_time);

		FloatingActionButton preferences_button = (FloatingActionButton)findViewById(R.id.preferences_button);
		if (preferences_button != null) {
			preferences_button.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(FragmentView.this, PreferencesActivity.class);
					Bundle b = new Bundle();
					b.putDouble("x", x);
					b.putDouble("y", y);
					b.putInt("update_time", update_time);
					intent.putExtras(b);
					startActivity(intent);
					finish();
				}
			});
		}

		// for big displays:
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		sun_fragment = (SunFragment) fragmentManager.findFragmentById(R.id.fragment_sun);
		if (sun_fragment != null) {
			sun_fragment.setX(x);
			sun_fragment.setY(y);
			sun_fragment.updateTextViews();
		}
		moon_fragment = (MoonFragment) fragmentManager.findFragmentById(R.id.fragment_moon);
		if (moon_fragment != null) {
			moon_fragment.setX(x);
			moon_fragment.setY(y);
			moon_fragment.updateTextViews();
		}

		// for smaller displays:
		ViewPager view_pager = findViewById(R.id.view_pager);
		if (view_pager != null) {
			ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
			view_pager.setAdapter(adapter);
			sun_fragment = (SunFragment)adapter.instantiateItem(view_pager, 0);
			if (sun_fragment != null) {
				sun_fragment.setX(x);
				sun_fragment.setY(y);
			}
			moon_fragment = (MoonFragment)adapter.instantiateItem(view_pager, 1);
			if (moon_fragment != null) {
				moon_fragment.setX(x);
				moon_fragment.setY(y);
			}
		}

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
								if (update_text_views) {
									try {
										if (moon_fragment != null)
											moon_fragment.updateTextViews();
										if (sun_fragment != null)
											sun_fragment.updateTextViews();
										update_text_views = false;
									} catch (Exception e) {}
								}
							}
						});
					}
				} catch (InterruptedException e) {}
			}
		};

		synchronize_data_thread = new Thread() {
			@Override
			public void run() {
				try {
					while (!synchronize_data_thread.isInterrupted()) {
						Thread.sleep(1000 * update_time);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								update_text_views = true;
							}
						});
					}
				} catch (InterruptedException e) {}
			}
		};

		update_time_thread.start();
		synchronize_data_thread.start();


	}


	//TODO: terminate or stop thread
	//TODO: add stable layouts
	//TODO: do not refresh data that often?
}
