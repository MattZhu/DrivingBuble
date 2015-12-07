package com.dt.android.db;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class SqlFileLoader {
	public static String[] loadSqls(String sqlfile) {
		InputStream is = null;

		StringBuffer sb = new StringBuffer();
		char buffer[] = new char[512];
		int cnt = 0;
		try {
			is = SqlFileLoader.class.getResourceAsStream("/assets/" + sqlfile);
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			while ((cnt = isr.read(buffer)) > 0) {
				sb.append(buffer, 0, cnt);
				Log.d("ReaderSQL:", new String(buffer, 0, cnt));
			}
		} catch (IOException e) {
			Log.e("LoadSQLFile", "error read sql file", e);
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
				Log.e("LoadSQLFile", "error close file", e);
			}
		}
		return sb.toString().split(";");
	}

	public static void copyCacheFiles(final Context context) {
		new Thread() {
			public void run() {
				InputStream dataSource;
				try {
					dataSource = context.getAssets().open("caches.zip");
					ZipInputStream in = new ZipInputStream(dataSource);
					File outputDir = createFileOnSDCard(context);
					ZipEntry entry = in.getNextEntry();
					while (entry != null) {
						if (entry.isDirectory()) {
							File folder = new File(outputDir, entry.getName());
							folder.mkdirs();
						} else {
							File out = new File(outputDir, entry.getName());
							FileOutputStream fos = new FileOutputStream(out);

							byte b[] = new byte[1024];
							int c = 0;
							while ((c = in.read(b)) != -1) {
								fos.write(b, 0, c);
							}
							fos.close();
						}
						entry = in.getNextEntry();
					}
					in.close();
				} catch (IOException e) {
					Log.d("CopyCacheFile",
							"error to create file " + e.getMessage());
				}
			}
		}.start();
	}

	private static File createFileOnSDCard(Context context) {
		File f;
		try {
			f = new File(context.getExternalCacheDir().getPath());
		} catch (Throwable e) {
			Log.d("CopyCacheFile", "error to create file " + e.getMessage());
			f = new File(Environment.getExternalStorageDirectory().getPath()
					+ "/Android/data/" + context.getPackageName() + "/cache");

		}
		if (f != null) {
			Log.d("CopyCacheFile", "file path=" + f.getPath());
		}
		return f;
	}
}
