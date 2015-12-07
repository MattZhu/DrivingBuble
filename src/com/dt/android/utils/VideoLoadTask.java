package com.dt.android.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

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
public class VideoLoadTask extends AsyncTaskEx<String, Void, String>   {
	private static final String TAG = "VideoLoadTask";
	private VideoView view;


	private static List<String> executingTasks = Collections
			.synchronizedList(new ArrayList<String>());

	private String url;
	private Context context;

	private Bitmap thumnail;
	
	private LruCache<String, Bitmap> imageCache;
	
	private ImageView preViewImage;
	
	private boolean updateView = true;
	
	private boolean play=false;

	public VideoLoadTask(VideoView view,ImageView iV, Context context) {
		this.view = view;
		this.context = context;
		this.preViewImage=iV;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		// update the image to load image
		// this.view.setImageResource(R.drawable.load);
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			// TODO save the download file in cache and first try to get from
			// cache.

			url = params[0];

			// get it from
			if (executingTasks.contains(url)) {
				Log.d(TAG, "Loading video " + url
						+ " already started. ignore this resquest.");
				while (executingTasks.contains(url)) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {

					}
				}
				Log.d(TAG, "Loading video " + url
						+ " completed by another request.");
			} else {
				Log.d(TAG, "Loading video " + url + "started");
				executingTasks.add(url);
			}

			File f = createFileOnSDCard(params[1]);

			if (f != null) {
				if (!f.exists()) {
					Log.d(TAG, "create dirs for " + f.getPath());
					f.mkdirs();
				} else {
					File videof = new File(f, params[2]);
					if (videof.exists()) {
						Log.d(TAG, "load " + params[0] + " from cache");
						thumnail=this.getFromLruCache();
						if(thumnail==null){
							thumnail =ThumbnailUtils.createVideoThumbnail(videof.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
							putLruChache(thumnail);
						}
						return videof.getPath();
					}
				}
			}
			Log.d(TAG, "load" + url + " from server.");
			InputStream in = ServerApi.openInputStream(params[0]);

			if (in != null) {
				try {
					File videof = new File(f, params[2]);
					FileOutputStream fos = new FileOutputStream(videof);
					byte[] buffer = new byte[1024];
					int len = 0;
					while ((len = in.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
					in.close();
					thumnail =ThumbnailUtils.createVideoThumbnail(videof.getPath(), MediaStore.Video.Thumbnails.MINI_KIND);
					putLruChache(thumnail);
					return videof.getPath();
				} catch (IOException e) {
					Log.d(TAG, "" + e.getMessage());

				}
			}

		}  catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;
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
	protected void onPostExecute(final String result) {
		super.onPostExecute(result);		
		executingTasks.remove(url);
		if (updateView) {
			if (result != null) {
				this.preViewImage.setImageBitmap(thumnail);
				view.setMediaController(new MediaController(context));
				view.setVideoPath(result);
				view.requestFocus();
				view.setOnCompletionListener(new OnCompletionListener(){
					public void onCompletion(MediaPlayer mp) {
						preViewImage.setVisibility(View.VISIBLE);
						view.setVisibility(View.GONE);								
					}
					
				});
				
				this.preViewImage.setOnClickListener(new OnClickListener(){
					public void onClick(View v) {
						preViewImage.setVisibility(View.GONE);						
						view.setVisibility(View.VISIBLE);						
						view.start();
					}
					
				});
				if(play){
					preViewImage.performClick();
				}
				Log.i(TAG, "Post Execute,load " + url + " video success");
			} else {
				Log.e(TAG, "Post Execute,load  " + url + " video failed");
			
			}
		}

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

	private void putLruChache(Bitmap result2) {
		if (imageCache != null && result2!=null) {
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


	public boolean isPlay() {
		return play;
	}


	public void setPlay(boolean play) {
		this.play = play;
	}
	

}
