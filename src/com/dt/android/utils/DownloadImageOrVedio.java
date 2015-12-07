package com.dt.android.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.dt.android.serverapi.ServerApi;

public class DownloadImageOrVedio {
	private static List<String> executingTasks = Collections
			.synchronizedList(new ArrayList<String>());
	private static final String TAG = "DownloadImageOrVedio";
	private String url;
	ExecutorService executor = Executors.newFixedThreadPool(30);
	private Context context;
	private int count=0;

	public DownloadImageOrVedio(Context context) {
		super();
		this.context = context;
		
	}

	public void download(final String... params) {
		count++;
		executor.execute(new Runnable(){
			public void run() {

				try {
					url = params[0];
					boolean usecache = params.length > 1;
					File f = createFileOnSDCard(params[1]);
					if (usecache) {
						// get it from
						if (executingTasks.contains(url)) {
							Log.d(TAG, "download for this link was started");
//							doneSignle.countDown();
							return;
						}
						if (f != null) {
							if (!f.exists()) {
//								Log.d(TAG, "create dirs for " + f.getPath());
								f.mkdirs();
								// } else {
								// f = new File(f, params[2]);
								// if (f.exists()) {
								// Log.d(TAG, "load " + params[0] +
								// " from cache");
								// return null;
								// }
							}
						}
					}
					InputStream in = ServerApi.openInputStream(params[0]);

					if (in != null) {
						try {
							File file = new File(f, params[2]);
							if (file.exists()) {
								file.delete();
							}
							FileOutputStream fos = new FileOutputStream(file);
							byte[] buffer = new byte[1024];
							int len = 0;
							while ((len = in.read(buffer)) > 0) {
								fos.write(buffer, 0, len);
							}
							fos.close();
							in.close();
//							doneSignle.countDown();
							return;
						} catch (IOException e) {
							Log.d(TAG, "" + e.getMessage());

						}
					}
				} catch (Exception e) {

				}finally{
					count--;
				}
//				doneSignle.countDown();
				return;
			}
		});
	}

	public File createFileOnSDCard(String params) {
		File f;
		try {
			f = new File(context.getExternalCacheDir().getPath() + params);
		} catch (Throwable e) {
			Log.d(TAG, "error to create file " + e.getMessage());
			f = new File(Environment.getExternalStorageDirectory().getPath()
					+ "/Android/data/" + context.getPackageName() + "/cache"
					+ params);

		}
		return f;
	}
	public int getRunningTaskCount(){
		return count;
	}
}
