package com.dt.android.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.dt.android.serverapi.ServerApi;

/**
 * Load image from server.
 * 
 * First parameter is image path on server.
 * 
 * Second parameter is cached path. if this parameter is present, try to get
 * from local first. and also set cache image as true, and cache it when get
 * from server.
 * 
 * @author Matthew.Zhu
 * 
 */
public class ImageLoadTask extends AsyncTaskEx<String, Void, Bitmap> {
	private static final String TAG = "ImageLoader";
	private ImageView view;
	// if we load image failed, use the default resource if it is not -1.
	private int defaultResource = -1;

	private static List<String> executingTasks = Collections
			.synchronizedList(new ArrayList<String>());

	private boolean cacheImage;

	private LruCache<String, Bitmap> imageCache;

	private String url;
	private Context context;

	private Bitmap result;

	private boolean updateView = true;

	public ImageLoadTask(ImageView view, Context context) {
		this.view = view;
		cacheImage = false;
		this.context = context;
	}

	public ImageLoadTask(ImageView view, Context context, boolean updateView) {
		this.view = view;
		cacheImage = false;
		this.context = context;
		this.updateView = updateView;
	}

	public ImageLoadTask(ImageView view, int defaultResource, Context context) {
		this.view = view;
		cacheImage = false;
		this.defaultResource = defaultResource;
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// update the image to load image
		// this.view.setImageResource(R.drawable.load);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		try {
			
			url = params[0];

			cacheImage = params.length > 1;

			Bitmap result = null;
			result = getFromLruCache();
			if (result != null) {
				Log.d(TAG, "Loading image from LRU cache");
				return result;
			}
			if (cacheImage) {
				// get it from
				if (executingTasks.contains(url)) {
					Log.d(TAG, "Loading image " + url
							+ " already started. ignore this resquest.");
					while (executingTasks.contains(url)) {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {

						}
					}
					Log.d(TAG, "Loading image " + url
							+ " completed by another request.");
				} else {
					Log.d(TAG, "Loading image " + url + "started");
					executingTasks.add(url);
				}

				File f = createFileOnSDCard(params[1]);

				if (f != null) {
					if (!f.exists()) {
						Log.d(TAG, "create dirs for " + f.getPath());
						f.mkdirs();
					} else {
						f = new File(f, params[2]);
						if (f.exists()) {
							Log.d(TAG, "load " + params[0] + " from cache");
							result = BitmapFactory.decodeFile(f
									.getAbsolutePath());
							if (result != null) {
								putLruChache(result);
								return result;
							} else {
								Log.d(TAG, "can't load " + params[0]
										+ " form cache,try from server");
							}
						}
					}
				}
			}
			Log.d(TAG, "load" + url + " from server.");
			InputStream in = ServerApi.openInputStream(params[0]);
			if (in != null) {
				result = BitmapFactory.decodeStream(in);
				in.close();
			}
			if (result == null) {
				Log.e(TAG, "Couldn't log image for " + params[0]);
			} else {
				putLruChache(result);
			}
			// StringBuffer sb = new StringBuffer();
			// InputStreamReader isr = new InputStreamReader(in,
			// "UTF-8");
			// int bufferSize=1024;
			// char buff[] = new char[bufferSize];
			// int cnt;
			// while ((cnt = isr.read(buff, 0, bufferSize)) > 0) {
			// sb.append(buff, 0, cnt);
			// }
			//
			// Log.e("ImageLoader","response"+sb.toString());
			// isr.close();
			// in.close();
			// return null;
			// }
			if (cacheImage && result != null && externalStorageWriteable()) {
				try {
					File cacheFolder = createFileOnSDCard(params[1]);
					if (cacheFolder != null) {
						File f = new File(cacheFolder, params[2]);

						FileOutputStream os = new FileOutputStream(f);
						result.compress(Bitmap.CompressFormat.JPEG, 80, os);
						os.flush();
						os.close();
						Log.d(TAG, "save image to cache success.");
					} else {
						Log.d(TAG, "can't create cache folder");
					}
				} catch (Exception e) {
					Log.d(TAG, "" + e.getMessage());
				}

			}
			return result;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void putLruChache(Bitmap result2) {
		if (imageCache != null&&result2!=null) {
			Log.d(TAG, "put image to LRU cache");
			imageCache.put(url, result2);
		}

	}

	private Bitmap getFromLruCache() {
		if (imageCache != null) {
			return imageCache.get(url);
		} else {
			return null;
		}
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
		if (f != null) {
			Log.d(TAG, "file path=" + f.getPath());
		}
		return f;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		super.onPostExecute(result);
		this.result = result;
		executingTasks.remove(url);
		if (updateView) {
			if (result != null) {
				Log.i(TAG, "Post Execute,load " + url + " image success");
				view.setImageBitmap(result);
			} else {
				Log.e(TAG, "Post Execute,load  " + url + " image failed");
				if (this.defaultResource != -1) {
					view.setImageResource(this.defaultResource);
				}
			}
		}

	}

	public Bitmap getBitmap(long timeout) {
		long now = System.currentTimeMillis();
		while (result == null && (System.currentTimeMillis() - now) < timeout) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {

			}
		}
		return result;
	}

	private boolean externalStorageWriteable() {
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageWriteable = true;
		} else {
			// Something else is wrong. It may be one of many other states, but
			// all we need
			// to know is we can neither read nor write
			mExternalStorageWriteable = false;
		}
		return mExternalStorageWriteable;
	}

	/**
	 * @return the updateView
	 */
	public boolean isUpdateView() {
		return updateView;
	}

	/**
	 * @param updateView
	 *            the updateView to set
	 */
	public void setUpdateView(boolean updateView) {
		this.updateView = updateView;
	}

	public LruCache<String, Bitmap> getImageCache() {
		return imageCache;
	}

	public void setImageCache(LruCache<String, Bitmap> imageCache) {
		this.imageCache = imageCache;
	}

}
