package com.example.astroweather.activities;

import android.annotation.SuppressLint;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.astroweather.DateTimeUtils;
import com.example.astroweather.MainActivity;
import com.example.astroweather.R;
import com.example.astroweather.ViewPagerAdapter;
import com.example.astroweather.fragments.MoonFragment;
import com.example.astroweather.fragments.SunFragment;
import com.example.astroweather.fragments.WeatherFragment;
import com.example.astroweather.weather.UpdateWeatherFiles;
import com.example.astroweather.weather.WeatherYahooCommunication;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
	ViewPager view_pager;
	ViewPagerAdapter adapter;
	private String default_location_name;
	private Boolean isCelsius = true;
	private UpdateWeatherFiles update;
	private String astroDirectory;
	Date update_date = new Date();

	public Boolean shouldUpdate = false;
	public Boolean shouldRefreshFragments = false;


	private boolean isAppOnForeground(Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
		if (appProcesses == null) {
			return false;
		}
		final String packageName = context.getPackageName();
		for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
				return true;
			}
		}
		return false;
	}


	public void createDataFromAstroDirectory() {
		File f = new File(astroDirectory);
		String[] pathnames = f.list();
		for (String pathname : pathnames) {
			String fullFilePath = null;
			if (!pathname.equals("default.json")) {
				try {
					fullFilePath = astroDirectory + "/" + pathname;
					WeatherFragment weather_fragment = new WeatherFragment(fullFilePath);
					adapter.addNewWeatherFragment(weather_fragment);
					view_pager.setAdapter(adapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


	public void updateDataFromAstroDirectory() {
		adapter.updateAllWeatherFragments();
	}


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
		astroDirectory = getCacheDir().toString() + "/AstroWeather";
		Intent this_intent = getIntent();
		x = this_intent.getDoubleExtra("x", 0);
		y = this_intent.getDoubleExtra("y", 0);
		shouldUpdate = this_intent.getBooleanExtra("should_update", false);
		default_location_name = this_intent.getStringExtra("location_name");
		update_time = this_intent.getIntExtra("update_time", 15 * 60);
		isCelsius = this_intent.getBooleanExtra("isCelsius", true);
		update_date = new Date(this_intent.getStringExtra("update_date"));

		TextView location_label = findViewById(R.id.location_name_label);
		location_label.setText(default_location_name);

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
					b.putString("location_name", default_location_name);
					b.putBoolean("isCelsius", isCelsius);
					b.putString("update_date", update_date.toString());
					intent.putExtras(b);
					startActivity(intent);
					finish();
				}
			});
		}

		// for smaller displays:
		view_pager = findViewById(R.id.view_pager);
		adapter = null;
		if (view_pager != null) {
			adapter = new ViewPagerAdapter(getSupportFragmentManager(), astroDirectory + "/default.json");
			view_pager.setAdapter(adapter);
			sun_fragment = (SunFragment)adapter.instantiateItem(view_pager, 0);
			sun_fragment.setX(x);
			sun_fragment.setY(y);
			sun_fragment.calculate(day, month, year, hour, minute, second);
			moon_fragment = (MoonFragment)adapter.instantiateItem(view_pager, 1);
			moon_fragment.setX(x);
			moon_fragment.setY(y);
			moon_fragment.calculate(day, month, year, hour, minute, second);
		}
		createDataFromAstroDirectory();
	}


	public void updateConfigFile(String filepath, int update_time, Activity activity) throws Exception {
		JSONObject object = new JSONObject();
		object.put("update_time", update_time);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, update_time);
		Date update_date = calendar.getTime();
		object.put("update_date", update_date);
		String config_json_path = filepath;
		File f = new File(config_json_path);
		if (f.exists()) {
			PrintWriter out = new PrintWriter(new FileWriter(config_json_path));
			out.write(object.toString());
			out.close();
		}
	}


	public void readConfigJson(String jsonContent) throws Exception {
		JSONObject jsonObject = new JSONObject(jsonContent);
		update_time = jsonObject.getInt("update_time");
		update_date = new Date(jsonObject.getString("update_date"));
		System.out.println("Readed update_time: " + update_time);
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
										e.printStackTrace();
									}
								}
								if (update_date.compareTo(new Date()) <= 0 || shouldUpdate) {
									try {
										updateConfigFile(astroDirectory + "/config.json", update_time, FragmentView.this);
										update = new UpdateWeatherFiles(FragmentView.this, isCelsius);
										update.start();
										shouldUpdate = false;
										readConfigJson(new String(Files.readAllBytes(Paths.get(astroDirectory + "/config.json"))));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								if (shouldRefreshFragments) {
									try {
										updateDataFromAstroDirectory();
										if (isAppOnForeground(FragmentView.this))
											Toast.makeText(FragmentView.this, "Data has been updated", Toast.LENGTH_LONG).show();
										shouldRefreshFragments = false;
									} catch (Exception e) {
										e.printStackTrace();
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


}
