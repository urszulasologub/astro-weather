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
		JSONObject locationObject = json_object.getJSONObject("location");

		city_name = locationObject.get("city").toString();
		TextView city = (TextView)getView().findViewById(R.id.city_name);
		city.setText(city_name);

		TextView city_location = (TextView) getView().findViewById(R.id.city_location);
		city_location.setText(locationObject.get("country").toString() + ", " + locationObject.get("region").toString());

		JSONObject observation = json_object.getJSONObject("current_observation");
		JSONObject condition = observation.getJSONObject("condition");

		TextView weather_condition_text = getView().findViewById(R.id.weather_condition_text);
		weather_condition_text.setText(condition.get("text").toString());

		TextView temp = getView().findViewById(R.id.temp);
		temp.setText(condition.get("temperature").toString() + "°");

		JSONArray forecastArray = json_object.getJSONArray("forecasts");

		TextView low_temp = getView().findViewById(R.id.low_temp);
		low_temp.setText(forecastArray.getJSONObject(0).get("low").toString() + "°");

		TextView high_temp = getView().findViewById(R.id.high_temp);
		high_temp.setText(forecastArray.getJSONObject(0).get("high").toString() + "°");

		JSONObject atmosphereObject = observation.getJSONObject("atmosphere");

		TextView pressure_val = getView().findViewById(R.id.pressure_val);
		pressure_val.setText(atmosphereObject.get("pressure").toString());

		JSONObject windObject = observation.getJSONObject("wind");

		TextView wind_val = getView().findViewById(R.id.wind_val);
		wind_val.setText(windObject.get("speed").toString());
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
