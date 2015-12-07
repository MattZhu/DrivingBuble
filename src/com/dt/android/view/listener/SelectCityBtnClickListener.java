package com.dt.android.view.listener;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;

import com.dt.android.activity.SelectCityActivity;

public class SelectCityBtnClickListener  implements OnClickListener {
	private Activity context;

	public SelectCityBtnClickListener(Activity context) {		
		this.context = context;
	}

	public void onClick(View v) {
		Intent intent = new Intent().setClass(context, SelectCityActivity.class);
		context.startActivity(intent);
	}
}
