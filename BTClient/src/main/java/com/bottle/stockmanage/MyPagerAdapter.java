package com.bottle.stockmanage;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapter extends PagerAdapter {
	private final static String TAG = "MyPagerAdapter";
	private final static boolean DEBUG = true;

	private List<View> pageViews = null;

	public MyPagerAdapter(List<View> pageViews) {
		this.pageViews = pageViews;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pageViews.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		container.addView(pageViews.get(position));
		return pageViews.get(position);
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView(pageViews.get(position));
	}

}
