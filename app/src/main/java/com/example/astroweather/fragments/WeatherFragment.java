package com.example.astroweather.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.astroweather.R;

import org.w3c.dom.Text;

public class WeatherFragment extends Fragment {

	String city_name = "City";

	public WeatherFragment(String city_name) {
		this.city_name = city_name;
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


	void updateTextViews() {
		TextView city = (TextView)getView().findViewById(R.id.city);
		city.setText(city_name);
		TextView latitude_x = (TextView)getView().findViewById(R.id.latitude_x);
		TextView longitude_y = (TextView)getView().findViewById(R.id.longitude_y);
		TextView date1_temperature = (TextView)getView().findViewById(R.id.date1_temperature);
		TextView date1 = (TextView)getView().findViewById(R.id.date1);
		TextView date2_temperature = (TextView)getView().findViewById(R.id.date2_temperature);
		TextView date2 = (TextView)getView().findViewById(R.id.date2);
		TextView date3_temperature = (TextView)getView().findViewById(R.id.date3_temperature);
		TextView date3 = (TextView)getView().findViewById(R.id.date3);
		TextView date4_temperature = (TextView)getView().findViewById(R.id.date4_temperature);
		TextView date4 = (TextView)getView().findViewById(R.id.date4);
		TextView date5_temperature = (TextView)getView().findViewById(R.id.date5_temperature);
		TextView date5 = (TextView)getView().findViewById(R.id.date5);
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		updateTextViews();
	}


}
