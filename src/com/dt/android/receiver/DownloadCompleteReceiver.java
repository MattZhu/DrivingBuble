package com.dt.android.receiver;

import java.io.File;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;

@SuppressLint("NewApi")
public class DownloadCompleteReceiver extends BroadcastReceiver {

	private ContextWrapper contextWrapper;

	private boolean registed = false;

	public DownloadCompleteReceiver(ContextWrapper contextWrapper) {
		super();
		this.contextWrapper = contextWrapper;
	}

	@SuppressLint("NewApi")
	public void downloadApp() {
		String url = "http://www.kaozhao123.com/app_page/DrivingBuble.apk";
		if (Build.VERSION.SDK_INT > 8) {
			contextWrapper.registerReceiver(this, new IntentFilter(
					DownloadManager.ACTION_DOWNLOAD_COMPLETE));
			registed = true;
			DownloadManager dm = (DownloadManager) contextWrapper
					.getSystemService(Context.DOWNLOAD_SERVICE);
			Request request = new Request(Uri.parse(url));
			request.setDestinationInExternalFilesDir(contextWrapper, null,
					"dbdown.apk");

			dm.enqueue(request);
		} else {
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			contextWrapper.startActivity(i);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
			contextWrapper.unregisterReceiver(this);
			registed = false;
			install();
		}
	}

	public void onDestroy() {
		if (registed) {
			contextWrapper.unregisterReceiver(this);
		}
	}

	private void install() {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(contextWrapper
				.getExternalFilesDir(null), "dbdown.apk")),
				"application/vnd.android.package-archive");
		contextWrapper.startActivity(intent);
	}

}
