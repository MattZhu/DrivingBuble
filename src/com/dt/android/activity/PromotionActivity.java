package com.dt.android.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.dt.android.R;

public class PromotionActivity extends BaseMainActivity {

	WebView wv;
	ProgressDialog pd;
	Handler handler;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.promotion);
		ImageView img = (ImageView) findViewById(R.id.titleImg);
		img.setImageResource(R.drawable.promotion);
		if (!this.isConnected()) {
			this.showDialog(NO_NETWORK);
			return;
		}
		init();
		handler = new Handler() {
			public void handleMessage(Message msg) {// 定义一个Handler，用于处理下载线程与UI间通讯

				switch (msg.what) {
				case 0:
					PromotionActivity.this.showDialog(1);// 显示进度对话框
					break;
				case 1:
					if (pd != null) {
						pd.dismiss();
					}
					break;
				default:
					super.handleMessage(msg);
				}
			}
		};
		handler.sendEmptyMessage(0);
		wv.loadUrl("http://www.kaozhao123.com/m/business/index.html");
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {// 捕捉返回键
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv.canGoBack()) {
			wv.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dt.android.activity.BaseActivity#onCreateDialog(int)
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			pd = new ProgressDialog(PromotionActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage("数据载入中，请稍候！");
			return pd;
		}
		return super.onCreateDialog(id);
	}

	public void init() {// 初始化
		wv = (WebView) findViewById(R.id.wv);
		wv.getSettings().setJavaScriptEnabled(true);// 可用JS
		wv.setScrollBarStyle(0);// 滚动条风格，为0就是不给滚动条留空间，滚动条覆盖在网页上
		wv.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(final WebView view,
					final String url) {
				handler.sendEmptyMessage(0);
				loadurl(view, url);
				return true;
			}

		});
		wv.setWebChromeClient(new WebChromeClient() {

			@Override
			public void onProgressChanged(WebView view, int newProgress) {

				if (newProgress == 100) {
					handler.sendEmptyMessage(1);
				}
				super.onProgressChanged(view, newProgress);
			}

		});

	}

	public void loadurl(final WebView view, final String url) {

		handler.sendEmptyMessage(0);
		view.loadUrl(url);// 载入网页

	}
}
