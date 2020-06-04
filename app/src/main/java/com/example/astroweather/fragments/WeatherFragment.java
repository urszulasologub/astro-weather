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
import org.w3c.dom.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WeatherFragment extends Fragment {

	String city_name = "City";
	String filepath;
	JSONObject json_object;

	/*public WeatherFragment(JSONObject json_object) {
		this.json_object = json_object;
	}*/

	public WeatherFragment(String filepath) throws Exception {
		this.filepath = filepath;
		String content = new String(Files.readAllBytes(Paths.get(this.filepath)));
		json_object = new JSONObject(content);
	}

	public void update() throws Exception {
		String content = new String(Files.readAllBytes(Paths.get(this.filepath)));
		json_object = new JSONObject(content);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_weather, container, false);
	}


	void updateTextViews() throws JSONException {
		JSONObject locationObject = json_object.getJSONObject("location");

		city_name = locationObject.get("city").toString() + ", " + locationObject.get("country");
		TextView city = (TextView)getView().findViewById(R.id.city);
		city.setText(city_name);

		String latitude = locationObject.get("lat").toString();
		TextView latitude_x = (TextView)getView().findViewById(R.id.latitude_x);
		latitude_x.setText(latitude);

		String longitude = locationObject.get("long").toString();
		TextView longitude_y = (TextView)getView().findViewById(R.id.longitude_y);
		longitude_y.setText(longitude);

		JSONArray forecastArray = json_object.getJSONArray("forecasts");

		String clean_date1 = forecastArray.getJSONObject(0).get("day").toString();
		TextView date1 = (TextView)getView().findViewById(R.id.date1);
		date1.setText(clean_date1);

		String clean_date2 = forecastArray.getJSONObject(1).get("day").toString();
		TextView date2 = (TextView)getView().findViewById(R.id.date2);
		date2.setText(clean_date2);

		String clean_date3 = forecastArray.getJSONObject(2).get("day").toString();
		TextView date3 = (TextView)getView().findViewById(R.id.date3);
		date3.setText(clean_date3);

		String clean_date4 = forecastArray.getJSONObject(3).get("day").toString();
		TextView date4 = (TextView)getView().findViewById(R.id.date4);
		date4.setText(clean_date4);

		String clean_date5 = forecastArray.getJSONObject(4).get("day").toString();
		TextView date5 = (TextView)getView().findViewById(R.id.date5);
		date5.setText(clean_date5);

		String clean_date1_temp = forecastArray.getJSONObject(0).get("low").toString() + "°/" + forecastArray.getJSONObject(0).get("high").toString() + "°";
		TextView date1_temperature = (TextView)getView().findViewById(R.id.date1_temperature);
		date1_temperature.setText(clean_date1_temp);

		String clean_date2_temp = forecastArray.getJSONObject(1).get("low").toString() + "°/" + forecastArray.getJSONObject(1).get("high").toString() + "°";
		TextView date2_temperature = (TextView)getView().findViewById(R.id.date2_temperature);
		date2_temperature.setText(clean_date2_temp);

		String clean_date3_temp = forecastArray.getJSONObject(2).get("low").toString() + "°/" + forecastArray.getJSONObject(2).get("high").toString() + "°";
		TextView date3_temperature = (TextView)getView().findViewById(R.id.date3_temperature);
		date3_temperature.setText(clean_date3_temp);

		String clean_date4_temp = forecastArray.getJSONObject(3).get("low").toString() + "°/" + forecastArray.getJSONObject(3).get("high").toString() + "°";
		TextView date4_temperature = (TextView)getView().findViewById(R.id.date4_temperature);
		date4_temperature.setText(clean_date4_temp);

		String clean_date5_temp = forecastArray.getJSONObject(4).get("low").toString() + "°/" + forecastArray.getJSONObject(4).get("high").toString() + "°";
		TextView date5_temperature = (TextView)getView().findViewById(R.id.date5_temperature);
		date5_temperature.setText(clean_date5_temp);
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
