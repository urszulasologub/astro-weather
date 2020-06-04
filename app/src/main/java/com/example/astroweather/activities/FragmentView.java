package com.example.astroweather.activities;

import android.annotation.SuppressLint;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.astroweather.R;
import com.example.astroweather.ViewPagerAdapter;
import com.example.astroweather.fragments.MoonFragment;
import com.example.astroweather.fragments.SunFragment;
import com.example.astroweather.fragments.WeatherFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class FragmentView extends AppCompatActivity {

	private int day;
	private int month;
	private int year = 0;
	private int hour;
	private int minute;
	private int second;
	private Double x;
	private Double y;
	TextView current_time;
	private Thread update_time_thread;
	private int update_time;
	private SunFragment sun_fragment;
	private MoonFragment moon_fragment;
	private int elapsed_seconds = 0;
	String new_city = null;
	private File astroDirectory = null;


	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("day", day);
		savedInstanceState.putInt("month", month);
		savedInstanceState.putInt("year", year);
		savedInstanceState.putInt("hour", hour);
		savedInstanceState.putInt("minute", minute);
		savedInstanceState.putInt("second", second);
		savedInstanceState.putInt("elapsed_seconds", elapsed_seconds);
	}



	private void updateDateTime() {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);
		second = calendar.get(Calendar.SECOND);
		day = calendar.get(Calendar.DATE);
		month = calendar.get(Calendar.MONTH) + 1;
		year = calendar.get(Calendar.YEAR);
	}


	private void setCurrentTime(TextView current_time) {
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		@SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d",
														calendar.get(Calendar.HOUR_OF_DAY),
														calendar.get(Calendar.MINUTE),
														calendar.get(Calendar.SECOND));
		current_time.setText(time);
	}


	@RequiresApi(api = Build.VERSION_CODES.O)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_view);
		if (savedInstanceState == null) {
			if (!isInternetAvailable()) {
				final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
				dialog.setTitle("Unable to connect servers");
				dialog.setMessage("Weather data may be outdated");
				dialog.show();
			}
			updateDateTime();
		} else {
			day = savedInstanceState.getInt("day");
			month = savedInstanceState.getInt("month");
			year = savedInstanceState.getInt("year");
			hour = savedInstanceState.getInt("hour");
			minute = savedInstanceState.getInt("minute");
			second = savedInstanceState.getInt("second");
			elapsed_seconds = savedInstanceState.getInt("elapsed_seconds");
		}
		Intent this_intent = getIntent();
		x = this_intent.getDoubleExtra("x", 0);
		y = this_intent.getDoubleExtra("y", 0);
		update_time = this_intent.getIntExtra("update_time", 15 * 60);
		new_city = this_intent.getStringExtra("location");

		TextView x_label = (TextView)findViewById(R.id.x_label);
		x_label.setText("x: " + Double.toString(x));
		TextView y_label = (TextView)findViewById(R.id.y_label);
		y_label.setText("y: " + Double.toString(y));

		current_time = (TextView)findViewById(R.id.current_time);
		setCurrentTime(current_time);

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
			sun_fragment.calculate(day, month, year, hour, minute, second);
			sun_fragment.updateTextViews();
		}
		moon_fragment = (MoonFragment) fragmentManager.findFragmentById(R.id.fragment_moon);
		if (moon_fragment != null) {
			moon_fragment.setX(x);
			moon_fragment.setY(y);
			moon_fragment.calculate(day, month, year, hour, minute, second);
			moon_fragment.updateTextViews();
		}

		// for smaller displays:
		ViewPager view_pager = findViewById(R.id.view_pager);
		ViewPagerAdapter adapter = null;
		if (view_pager != null) {
			adapter = new ViewPagerAdapter(getSupportFragmentManager());
			view_pager.setAdapter(adapter);
			sun_fragment = (SunFragment)adapter.instantiateItem(view_pager, 0);
			if (sun_fragment != null) {
				sun_fragment.setX(x);
				sun_fragment.setY(y);
				sun_fragment.calculate(day, month, year, hour, minute, second);
			}
			moon_fragment = (MoonFragment)adapter.instantiateItem(view_pager, 1);
			if (moon_fragment != null) {
				moon_fragment.setX(x);
				moon_fragment.setY(y);
				moon_fragment.calculate(day, month, year, hour, minute, second);
			}
			/*if (new_city != null) {
				WeatherFragment weather_fragment = new WeatherFragment(new_city);
				adapter.addNewWeatherFragment(weather_fragment);
				view_pager.setAdapter(adapter);
			}*/
		}

		astroDirectory = new File(getCacheDir(),"AstroWeather");
		if (!astroDirectory.exists())
			astroDirectory.mkdirs();
		File f = new File(getCacheDir().toString() + "/AstroWeather");
		String[] pathnames;
		pathnames = f.list();
		for (String pathname : pathnames) {
			String fullFilePath = null;
			try {
				fullFilePath = getCacheDir().toString() + "/AstroWeather/" + pathname;
				System.out.println(fullFilePath);
				String content = new String(Files.readAllBytes(Paths.get(fullFilePath)));
				System.out.println(content);
				JSONObject object = new JSONObject(content);
				JSONObject locationObject = object.getJSONObject("location");
				String location_name = locationObject.get("city").toString();
				WeatherFragment weather_fragment = new WeatherFragment(location_name);
				adapter.addNewWeatherFragment(weather_fragment);
				view_pager.setAdapter(adapter);
			} catch (Exception e) {
				if (fullFilePath != null) {
					File fileToDelete = new File(fullFilePath);
					fileToDelete.delete();
				}
				e.printStackTrace();
			}
		}


	}

	@Override
	protected void onStart() {
		super.onStart();

		update_time_thread = new Thread() {
			@Override
			public void run() {
				try {
					while (!update_time_thread.isInterrupted()) {
						Thread.sleep(1000);
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								elapsed_seconds++;
								setCurrentTime(current_time);
								if (elapsed_seconds >= update_time) {
									elapsed_seconds = 0;
									updateDateTime();
									try {
										if (moon_fragment != null) {
											moon_fragment.calculate(day, month, year, hour, minute, second);
											moon_fragment.updateTextViews();
										}
										if (sun_fragment != null) {
											sun_fragment.calculate(day, month, year, hour, minute, second);
											sun_fragment.updateTextViews();
										}
									} catch (Exception e) {
									}
								}
							}
						});
					}
				} catch (InterruptedException e) {}
			}
		};

		update_time_thread.start();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		update_time_thread.interrupt();
	}


	public boolean isInternetAvailable() {
		//TODO: change implementation
		return true;
	}


}
