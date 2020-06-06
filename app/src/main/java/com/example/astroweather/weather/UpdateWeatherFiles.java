package com.example.astroweather.weather;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.astroweather.activities.FragmentView;
import com.example.astroweather.activities.PreferencesActivity;
import com.example.astroweather.fragments.WeatherFragment;

import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UpdateWeatherFiles extends Thread {

	FragmentView activity;
	Boolean isCelsius;


	public UpdateWeatherFiles(FragmentView activity, Boolean isCelsius) {
		this.activity = activity;
		this.isCelsius = isCelsius;
	}


	@Override
	public void run() {
		File f = new File(activity.getCacheDir().toString() + "/AstroWeather");
		String[] pathnames = f.list();
		int how_many_downloaded = 0;
		for (String pathname : pathnames) {
			String fullFilePath = null;
			try {
				fullFilePath = activity.getCacheDir().toString() + "/AstroWeather/" + pathname;
				System.out.println("File to update: " + fullFilePath);
				File fp = new File(fullFilePath);
				if (fp.exists()) {
					WeatherYahooCommunication yahooCommunication;
					if (!pathname.equals("default.json"))
						yahooCommunication = new WeatherYahooCommunication(pathname, activity, isCelsius);
					else {
						String content = new String(Files.readAllBytes(Paths.get(fullFilePath)));
						JSONObject jsonObject = new JSONObject(content);
						JSONObject locationObject = jsonObject.getJSONObject("location");
						yahooCommunication = new WeatherYahooCommunication(locationObject.get("city").toString(), activity, isCelsius);
					}
					yahooCommunication.execute();
					if (yahooCommunication.get() != null) {
						yahooCommunication.updateFile(pathname, yahooCommunication.get(), activity);
						++how_many_downloaded;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (how_many_downloaded > 0)
			activity.shouldRefreshFragments = true;
	}


}
