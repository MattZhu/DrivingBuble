package com.dt.android.utils;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class FileUtil {
	public static File createFileOnSDCard(Context context,String params) {
		File f;
		try {
			f = new File(context.getExternalCacheDir().getPath() + params);
		} catch (Throwable e) {
			Log.d("FileUtil", "error to create file " + e.getMessage());
			f = new File(Environment.getExternalStorageDirectory().getPath()
					+ "/Android/data/" + context.getPackageName() + "/cache"
					+ params);

		}
		if (f != null) {
			Log.d("FileUtil", "file path=" + f.getPath());
		}
		return f;
	}
}
