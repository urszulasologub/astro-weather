package com.example.astroweather.weather;

import android.app.Activity;
import android.os.AsyncTask;

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
		for (String pathname : pathnames) {
			String fullFilePath = null;
			try {
				fullFilePath = activity.getCacheDir().toString() + "/AstroWeather/" + pathname;
				System.out.println("File to update: " + fullFilePath);
				File fp = new File(fullFilePath);
				if (fp.exists()) {
					WeatherYahooCommunication yahooCommunication = new WeatherYahooCommunication(pathname, activity, isCelsius);
					yahooCommunication.execute();
					System.out.println("Execution");
					if (yahooCommunication.get() != null) {
						yahooCommunication.updateFile(yahooCommunication.get(), activity);
					}
					System.out.println("Executed");
				}
			} catch (Exception e) {
				System.out.println("Couldn't execute");
				e.printStackTrace();
			}
		}
		activity.updateDataFromAstroDirectory();
	}


}
