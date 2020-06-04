package com.example.astroweather;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.astroweather.fragments.MoonFragment;
import com.example.astroweather.fragments.SunFragment;
import com.example.astroweather.fragments.WeatherFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

	private SunFragment sun_fragment = new SunFragment();
	private MoonFragment moon_fragment = new MoonFragment();
	private List<Fragment> fragmentList = new ArrayList<>();


	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
		fragmentList.add(sun_fragment);
		fragmentList.add(moon_fragment);
	}


	public void addNewWeatherFragment(WeatherFragment fragment) {
		fragmentList.add(fragment);
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