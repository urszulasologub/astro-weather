package com.example.astroweather.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.astroweather.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class WeatherFragment extends Fragment {

	String city_name = "City";
	String filepath;
	JSONObject json_object;
	String temperature_unit = " °C";
	String speed_unit = " km/h";
	String pressure_unit = " hPa";


	void setMetricUnits() {
		temperature_unit = " °C";
		speed_unit = " km/h";
		pressure_unit = " hPa";
	}

	void setImperialUnits() {
		temperature_unit = " °F";
		speed_unit = " mph";
		pressure_unit = " Hg";
	}


	public WeatherFragment(String filepath) throws Exception {
		this.filepath = filepath;
		String content = new String(Files.readAllBytes(Paths.get(this.filepath)));
		json_object = new JSONObject(content);
		if (json_object.get("unit").toString().equals("c"))
			setMetricUnits();
		else
			setImperialUnits();
	}


	public void update() throws Exception {
		String content = new String(Files.readAllBytes(Paths.get(this.filepath)));
		json_object = new JSONObject(content);
		if (json_object.get("unit").toString().equals("c"))
			setMetricUnits();
		else
			setImperialUnits();
	}


	void setImageDependingOnView(Integer code, ImageView imageView) {
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_weather, container, false);
	}


	public void updateTextViews() throws JSONException {
		JSONObject locationObject = json_object.getJSONObject("location");

		city_name = locationObject.get("city").toString();
		TextView city = (TextView)getView().findViewById(R.id.city);
		city.setText(city_name  + ", " + locationObject.get("country"));

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
		setImageDependingOnView(forecastArray.getJSONObject(0).getInt("code"), getView().findViewById(R.id.weather_icon_1));

		String clean_date2 = forecastArray.getJSONObject(1).get("day").toString();
		TextView date2 = (TextView)getView().findViewById(R.id.date2);
		date2.setText(clean_date2);
		setImageDependingOnView(forecastArray.getJSONObject(1).getInt("code"), getView().findViewById(R.id.weather_icon_2));

		String clean_date3 = forecastArray.getJSONObject(2).get("day").toString();
		TextView date3 = (TextView)getView().findViewById(R.id.date3);
		date3.setText(clean_date3);
		setImageDependingOnView(forecastArray.getJSONObject(2).getInt("code"), getView().findViewById(R.id.weather_icon_3));

		String clean_date4 = forecastArray.getJSONObject(3).get("day").toString();
		TextView date4 = (TextView)getView().findViewById(R.id.date4);
		date4.setText(clean_date4);
		setImageDependingOnView(forecastArray.getJSONObject(3).getInt("code"), getView().findViewById(R.id.weather_icon_4));

		String clean_date5 = forecastArray.getJSONObject(4).get("day").toString();
		TextView date5 = (TextView)getView().findViewById(R.id.date5);
		date5.setText(clean_date5);
		setImageDependingOnView(forecastArray.getJSONObject(4).getInt("code"), getView().findViewById(R.id.weather_icon_5));

		String clean_date1_temp = forecastArray.getJSONObject(0).get("low").toString() + " / " + forecastArray.getJSONObject(0).get("high").toString() + " " + temperature_unit;
		TextView date1_temperature = (TextView)getView().findViewById(R.id.date1_temperature);
		date1_temperature.setText(clean_date1_temp);

		String clean_date2_temp = forecastArray.getJSONObject(1).get("low").toString() + " / " + forecastArray.getJSONObject(1).get("high").toString() + " " + temperature_unit;
		TextView date2_temperature = (TextView)getView().findViewById(R.id.date2_temperature);
		date2_temperature.setText(clean_date2_temp);

		String clean_date3_temp = forecastArray.getJSONObject(2).get("low").toString() + " / " + forecastArray.getJSONObject(2).get("high").toString() + " " + temperature_unit;
		TextView date3_temperature = (TextView)getView().findViewById(R.id.date3_temperature);
		date3_temperature.setText(clean_date3_temp);

		String clean_date4_temp = forecastArray.getJSONObject(3).get("low").toString() + " / " + forecastArray.getJSONObject(3).get("high").toString() + " " + temperature_unit;
		TextView date4_temperature = (TextView)getView().findViewById(R.id.date4_temperature);
		date4_temperature.setText(clean_date4_temp);

		String clean_date5_temp = forecastArray.getJSONObject(4).get("low").toString() + " / " + forecastArray.getJSONObject(4).get("high").toString() + " " + temperature_unit;
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
