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


	void setImageDependingOnView(Integer code) {
		ImageView imageView = getView().findViewById(R.id.imageView);
		switch (code) {
			default:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_48_2682803));
				break;
			case 0:
			case 1:
			case 2:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_41_2682810));
				break;
			case 3:
			case 4:
			case 37:
			case 38:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_11_2682840));
				break;
			case 5:
			case 6:
			case 7:
			case 18:
			case 35:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_35_2682816));
				break;
			case 8:
			case 9:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_32_2682819));
				break;
			case 10:
			case 11:
			case 12:
			case 39:
			case 40:
			case 45:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_12_2682839));
				break;
			case 13:
			case 14:
			case 15:
			case 16:
			case 41:
			case 46:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_28_2682823));
				break;
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_30_2682821));
				break;
			case 24:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_9_2682842));
				break;
			case 25:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_42_2682809));
				break;
			case 26:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_1_2682850));
				break;
			case 27:
			case 29:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_2_2682849));
				break;
			case 28:
			case 30:
			case 42:
			case 43:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_5_2682846));
				break;
			case 31:
			case 33:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_4_2682847));
				break;
			case 32:
			case 34:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_3_2682848));
				break;
			case 36:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_43_2682808));
				break;
			case 47:
				imageView.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.iconfinder_weather_23_2682828));
				break;
		}
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
		setImageDependingOnView(code);

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
