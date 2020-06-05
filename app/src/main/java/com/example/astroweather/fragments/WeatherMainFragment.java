package com.example.astroweather.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.astroweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherMainFragment extends WeatherFragment {


	public WeatherMainFragment(String filepath) throws Exception {
		super(filepath);
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_weather, container, false);
	}


	void updateTextViews() throws JSONException {

	}



	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			updateTextViews();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
