package com.example.astroweather;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

	private SunFragment sun_fragment;
	private MoonFragment moon_fragment;


	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		switch (position) {
			case 0:
				return new SunFragment();
			case 1:
				return new MoonFragment();
		}
		return null;
	}

	@Override
	public int getCount() {
		return 2;
	}

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
		}
		return createdFragment;
	}


	public void updateFragmentsXY(double x, double y) {
		if (sun_fragment != null) {
			sun_fragment.setX(x);
			sun_fragment.setY(y);
		}
		if (moon_fragment != null) {
			moon_fragment.setX(x);
			moon_fragment.setY(y);
		}
	}


	public void updateFragmentsTextViews() {
		if (sun_fragment != null) {
			sun_fragment.updateTextViews();
		}
		if (moon_fragment != null) {
			moon_fragment.updateTextViews();
		}
	}

}