package com.dt.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

	public MarqueeTextView(Context context) {
		super(context);
	}
	

	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}


	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	public boolean isFocused() {
		return true;
	}
}
