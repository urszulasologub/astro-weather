package com.example.astroweather.weather;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

import com.example.astroweather.fragments.WeatherFragment;

import org.json.JSONObject;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UpdateWeatherFiles extends Thread {

	Activity activity;

	public UpdateWeatherFiles(Activity activity) {
		this.activity = activity;
	}

	@Override
	public void run() {
		File f = new File(activity.getCacheDir().toString() + "/AstroWeather");
		String[] pathnames = f.list();
		for (String pathname : pathnames) {
			String fullFilePath = null;
			try {
				WeatherYahooCommunication yahooCommunication = new WeatherYahooCommunication(pathname, activity);
				yahooCommunication.execute();
				if (yahooCommunication.get() != null) {
					yahooCommunication.createFile(yahooCommunication.get(), activity);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
