package com.dt.android.view.listener;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;

public class BackButtonClickListener implements OnClickListener {
	private Activity context;
	public BackButtonClickListener(Activity context){
		this.context=context;
	}
	
	public void onClick(View v) {
		context.finish();
	}

}