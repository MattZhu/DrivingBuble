package com.dt.android.utils;

import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Gallery;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.activity.BaseActivity;

public class GallerySupport implements OnClickListener, OnItemSelectedListener {
	private static final String TAG = "GallerySupport";
	private Gallery gallery;
	private BaseActivity activity;
	private TextView currentText;

	private float mDeceleration;

	private String noLeft = "已是第一题!";
	private String noRight = "已是最后一题!";

	public GallerySupport(BaseActivity activity, Gallery gallery) {
		this.gallery = gallery;
		this.activity = activity;

		float ppi = activity.getResources().getDisplayMetrics().density * 160.0f;
		mDeceleration = SensorManager.GRAVITY_EARTH // g (m/s^2)
				* 39.37f // inch/meter
				* ppi // pixels per inch
				* ViewConfiguration.getScrollFriction();
	}

	public void init() {
		currentText = (TextView) activity.findViewById(R.id.current);
		TextView total = (TextView) activity.findViewById(R.id.total);
		if (total != null) {
			total.setText(String.valueOf(gallery.getCount()));
		}
		View pre = activity.findViewById(R.id.pre_view);
		View next = activity.findViewById(R.id.next_view);
		pre.setOnClickListener(this);
		next.setOnClickListener(this);
		gallery.setCallbackDuringFling(false);
		gallery.setOnItemSelectedListener(this);
	}

	private void prev() {
		int i = gallery.getSelectedItemPosition();
		if (i > 0) {

			int width = gallery.getSelectedView().getWidth();
			float v = (float) Math.sqrt(width * 3.5 * mDeceleration);
			Log.d(TAG, "move prev v:" + v + " width:" + width);
			// gallery.scrollTo((i-1)*width, 0);
			gallery.onFling(null, null, v, 0);
			// gallery.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, null);

		} else {
			activity.showToast(noLeft);
		}
	}

	public void next() {
		int i = gallery.getSelectedItemPosition();
		if (i < gallery.getCount() - 1) {
			//
			int width = gallery.getSelectedView().getWidth();
			float v = (float) Math.sqrt(width * 3.5 * mDeceleration);

			Log.d(TAG, "move next v:" + v + " width:" + width);
			// gallery.scrollTo((i+1)*width, 0);
			gallery.onFling(null, null, -1 * v, 0);

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

	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		Log.d("GallerySupported", "Item " + (arg2 + 1) + " selected");
		if (currentText != null) {
			currentText.setText(String.valueOf(arg2 + 1));
		}
		
		activity.updateUIForSelectedItem();
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

}
