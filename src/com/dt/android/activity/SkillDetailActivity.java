package com.dt.android.activity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.dt.android.R;

public class SkillDetailActivity extends BackBaseActivity {
	
	private WebView wv;
	private String fileName;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.skill_detail);
		this.rightBtnIsCarType=false;
		this.findViewById(R.id.rightbtn).setVisibility(View.GONE);
		ImageView img = (ImageView) findViewById(R.id.titleImg);
		img.setImageResource(R.drawable.skill);
		fileName=getIntent().getStringExtra("file_name");
		init();
		
	}
	
	public void init() {// 初始化
		wv = (WebView) findViewById(R.id.wv);
		wv.getSettings().setJavaScriptEnabled(true);// 可用JS
		wv.setScrollBarStyle(0);
		wv.loadUrl("file:///android_asset"+fileName);
	}

	
}
