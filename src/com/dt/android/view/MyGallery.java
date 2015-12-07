package com.dt.android.view;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Gallery;
import android.widget.ScrollView;
import android.widget.Toast;

public class MyGallery extends Gallery {

	private String noLeft = "已是第一题!";
	private String noRight = "已是最后一题!";

	public MyGallery(Context context) {
		super(context);
		float ppi = getContext().getResources().getDisplayMetrics().density * 160.0f;
		mDeceleration = SensorManager.GRAVITY_EARTH // g (m/s^2)
				* 39.37f // inch/meter
				* ppi // pixels per inch
				* ViewConfiguration.getScrollFriction();
	}

	public MyGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		float ppi = getContext().getResources().getDisplayMetrics().density * 160.0f;
		mDeceleration = SensorManager.GRAVITY_EARTH // g (m/s^2)
				* 39.37f // inch/meter
				* ppi // pixels per inch
				* ViewConfiguration.getScrollFriction();
	}

	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		float ppi = getContext().getResources().getDisplayMetrics().density * 160.0f;
		mDeceleration = SensorManager.GRAVITY_EARTH // g (m/s^2)
				* 39.37f // inch/meter
				* ppi // pixels per inch
				* ViewConfiguration.getScrollFriction();
	}

	private float mDeceleration;

	
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		View v=getSelectedView();
		
		if(isHorizontal(distanceX,distanceY)){
			return super.onScroll(e1, e2, distanceX, distanceY);
		}else{
			if (v != null && (v instanceof ScrollView)) {
				((ScrollView)v).scrollBy(0, (int)distanceY);
			}	
			return true;
		}
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (getSelectedView() != null) {
			View view=getSelectedView();			
			if(isHorizontal(velocityX,velocityY)){
				int width = this.getSelectedView().getWidth();
				float v = (float) Math.sqrt(width * 3.5 * mDeceleration);
				if (Math.abs(Math.abs(velocityX) - v) < 0.00001) {	
					return super.onFling(e1, e2, velocityX, velocityY);
//					int kEvent;
//					if (velocityX > 0) { // Check if scrolling left
//						kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
//					} else { // Otherwise scrolling right
//						kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
//					}
//					onKeyDown(kEvent, null);
//					return true;
				} else {
					int kEvent;
					if (velocityX > 0) { // Check if scrolling left
						kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
					} else { // Otherwise scrolling right
						kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
					}
					if (check(kEvent)) {
						onKeyDown(kEvent, null);
					}
					return true;
				}
			}else{
				if (view instanceof ScrollView) {
					((ScrollView)view).fling(-(int) velocityY);
				}
				return true;
			}
		} else {
			return true;
		}
	}

	private boolean isHorizontal(float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		return Math.abs(velocityX)>Math.abs(velocityY);
	}

	private boolean check(int kEvent) {
		int current = this.getSelectedItemPosition();
		if (kEvent == KeyEvent.KEYCODE_DPAD_LEFT && current == 0) {
			showToast(noLeft);
			return false;
		} else if (kEvent == KeyEvent.KEYCODE_DPAD_RIGHT
				&& current == this.getCount() - 1) {
			showToast(noRight);
			return false;
		}
		return true;
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

	private void showToast(String message) {
		Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}
	
	

}
