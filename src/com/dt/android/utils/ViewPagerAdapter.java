package com.dt.android.utils;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.dt.android.view.ListItemViewCreator;

public class ViewPagerAdapter<T> extends PagerAdapter {
	private T[] data;
	private int count;
	private View currentView;
	private ListItemViewCreator<T> questionItemViewCreator;
	public ViewPagerAdapter() {
	}

	
	@Override
	public int getCount() {
		return count;
	}	

	public void setData(T[] data) {
		if (data != null) {
			this.data = data;
			this.count = data.length;
		}
	}
	
	public T getCurrentItemData(int position)
	{
		return data[position];
	}

	public ListItemViewCreator<T> getQuestionItemViewCreator() {
		return questionItemViewCreator;
	}

	public void setQuestionItemViewCreator(
			ListItemViewCreator<T> questionItemViewCreator) {
		this.questionItemViewCreator = questionItemViewCreator;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {		
		return arg0.equals(arg1);
	}


	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}



	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		View v=questionItemViewCreator.createOrUpdateView(data[position],null,position,container);
		v.setTag(position);
		container.addView(v);
		return v;
	}


	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		super.setPrimaryItem(container, position, object);
		currentView=(View)object;
	}


	public View getCurrentView() {
		return currentView;
	}
	
}
