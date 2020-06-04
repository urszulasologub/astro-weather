package com.example.astroweather.weather;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

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
		for (String pathname : pathnames) {
			String fullFilePath = null;
			try {
				fullFilePath = activity.getCacheDir().toString() + "/AstroWeather/" + pathname;
				WeatherYahooCommunication yahooCommunication = new WeatherYahooCommunication(pathname, activity, isCelsius);
				yahooCommunication.execute();
				if (yahooCommunication.get() != null) {
					yahooCommunication.createFile(yahooCommunication.get(), activity);
					activity.updateDataFromAstroDirectory();
				}
				System.out.println("Updated successfully");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
