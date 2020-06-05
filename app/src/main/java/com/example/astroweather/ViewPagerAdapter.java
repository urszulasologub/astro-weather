package com.example.astroweather;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.astroweather.fragments.MoonFragment;
import com.example.astroweather.fragments.SunFragment;
import com.example.astroweather.fragments.WeatherFragment;
import com.example.astroweather.fragments.WeatherMainFragment;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

	private SunFragment sun_fragment = new SunFragment();
	private MoonFragment moon_fragment = new MoonFragment();
	private WeatherMainFragment main_weather_fragment;
	private List<Fragment> fragmentList = new ArrayList<>();
	private List<WeatherFragment> weatherFragments = new ArrayList<>();


	public ViewPagerAdapter(FragmentManager fm, String default_location_path)  {
		super(fm);
		fragmentList.add(sun_fragment);
		fragmentList.add(moon_fragment);
		try {
			main_weather_fragment = new WeatherMainFragment(default_location_path);
			fragmentList.add(main_weather_fragment);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void addNewWeatherFragment(WeatherFragment fragment) {
		fragmentList.add(fragment);
		weatherFragments.add(fragment);
	}


	public void updateAllWeatherFragments() throws Exception {
		for (WeatherFragment fragment : weatherFragments) {
			fragment.update();
			fragment.updateTextViews();
		}
		main_weather_fragment.update();
		main_weather_fragment.updateTextViews();
	}


	@Override
	public Fragment getItem(int position) {
		return fragmentList.get(position);
	}


	@Override
	public int getCount() {
		return fragmentList.size();
	}


	@NonNull
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
		// save the appropriate reference depending on position
		switch (position) {
			case 0:
				sun_fragment = (SunFragment) createdFragment;
				break;
			case 1:
				moon_fragment = (MoonFragment) createdFragment;
				break;
			default:
				if (position > 1 && position < fragmentList.size())
					fragmentList.set(position, (WeatherFragment) createdFragment);
				break;
		}
		return createdFragment;
	}


}