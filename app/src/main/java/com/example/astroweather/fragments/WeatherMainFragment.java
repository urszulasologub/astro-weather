package com.example.astroweather.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.astroweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

public class WeatherMainFragment extends WeatherFragment {


	public WeatherMainFragment(String filepath) throws Exception {
		super(filepath);
		this.filepath = filepath;
		String content = new String(Files.readAllBytes(Paths.get(this.filepath)));
		json_object = new JSONObject(content);
		if (json_object.get("unit").toString().equals("c"))
			setMetricUnits();
		else
			setImperialUnits();
		System.out.println(json_object.toString());
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_main_weather, container, false);
	}


	@Override
	public void updateTextViews() throws JSONException {
		JSONObject locationObject = json_object.getJSONObject("location");

		city_name = locationObject.get("city").toString();
		TextView city = (TextView)getView().findViewById(R.id.city_name);
		city.setText(city_name);

		TextView city_location = (TextView) getView().findViewById(R.id.city_location);
		city_location.setText(locationObject.get("country").toString() + ", " + locationObject.get("region").toString());

		JSONObject observation = json_object.getJSONObject("current_observation");
		JSONObject condition = observation.getJSONObject("condition");

		Integer code = condition.getInt("code");
		setImageDependingOnView(code, getView().findViewById(R.id.imageView));

		TextView weather_condition_text = getView().findViewById(R.id.weather_condition_text);
		weather_condition_text.setText(condition.get("text").toString());

		TextView temp = getView().findViewById(R.id.temp);
		temp.setText(condition.get("temperature").toString() + " " + temperature_unit);

		JSONArray forecastArray = json_object.getJSONArray("forecasts");

		TextView low_temp = getView().findViewById(R.id.low_temp);
		low_temp.setText(forecastArray.getJSONObject(0).get("low").toString() + " " + temperature_unit);

		TextView high_temp = getView().findViewById(R.id.high_temp);
		high_temp.setText(forecastArray.getJSONObject(0).get("high").toString() + " " + temperature_unit);

		JSONObject atmosphereObject = observation.getJSONObject("atmosphere");

		TextView pressure_val = getView().findViewById(R.id.pressure_val);
		pressure_val.setText(atmosphereObject.get("pressure").toString() + pressure_unit);

		JSONObject windObject = observation.getJSONObject("wind");

		TextView wind_val = getView().findViewById(R.id.wind_val);
		wind_val.setText(windObject.get("speed").toString() + speed_unit);
	}



	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		try {
			this.updateTextViews();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
