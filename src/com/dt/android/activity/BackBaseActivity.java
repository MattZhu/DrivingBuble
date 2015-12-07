package com.dt.android.activity;

import android.os.Bundle;

public class BackBaseActivity extends BaseActivity {

	protected static final int CHECK_NOT_ANSWERED = 0;
	protected static final int VIEW_ANSWER = 1;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		leftBtnIsLocation = false;
	}
}
