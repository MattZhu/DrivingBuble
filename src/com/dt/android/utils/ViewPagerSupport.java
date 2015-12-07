package com.dt.android.utils;

import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.activity.BaseActivity;

public class ViewPagerSupport implements OnClickListener, OnPageChangeListener {
	private static final String TAG = "GallerySupport";
	private ViewPager gallery;
	private BaseActivity activity;
	private TextView currentText;


	private String noLeft = "已是第一题!";
	private String noRight = "已是最后一题!";

	public ViewPagerSupport(BaseActivity activity, ViewPager gallery) {
		this.gallery = gallery;
		this.activity = activity;
	}

	public void init() {
		currentText = (TextView) activity.findViewById(R.id.current);
		TextView total = (TextView) activity.findViewById(R.id.total);
		if (total != null) {
			total.setText(String.valueOf(gallery.getAdapter().getCount()));
		}
		View pre = activity.findViewById(R.id.pre_view);
		View next = activity.findViewById(R.id.next_view);
		pre.setOnClickListener(this);
		next.setOnClickListener(this);
		gallery.setOnPageChangeListener(this);
//		gallery.setCallbackDuringFling(false);
//		gallery.setOnItemSelectedListener(this);
	}

	private void prev() {
		int i = gallery.getCurrentItem();
		if (i > 0) {
			gallery.setCurrentItem(i-1,true);
			// gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);

		} else {
			activity.showToast(noLeft);
		}
	}

	public void next() {
		int i = gallery.getCurrentItem();
		if (i < gallery.getAdapter().getCount()-1) {
			gallery.setCurrentItem(i+1,true);
			// gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, null);

		} else {
			activity.showToast(this.noRight);
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pre_view:
			prev();
			break;
		case R.id.next_view:
			next();
			break;
		}

	}



	/**
	 * @return the noLeft
	 */
	public String getNoLeft() {
		return noLeft;
	}

	/**
	 * @param noLeft
	 *            the noLeft to set
	 */
	public void setNoLeft(String noLeft) {
		this.noLeft = noLeft;
	}

	/**
	 * @return the noRight
	 */
	public String getNoRight() {
		return noRight;
	}

	/**
	 * @param noRight
	 *            the noRight to set
	 */
	public void setNoRight(String noRight) {
		this.noRight = noRight;
	}

	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		if(arg0==ViewPager.SCROLL_STATE_IDLE){
			activity.updateUIForSelectedItem();
		}
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		if (currentText != null) {
			currentText.setText(String.valueOf(arg0 + 1));
		}		
	}

	
}

