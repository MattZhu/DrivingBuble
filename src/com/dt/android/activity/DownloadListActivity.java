package com.dt.android.activity;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.Download;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;

@SuppressLint("NewApi")
public class DownloadListActivity extends BackBaseActivity implements
		ListItemViewCreator<Download>, OnClickListener, OnItemClickListener,
		OnItemLongClickListener {
	private boolean downloading = true;
	private ListAdapter<Download> adapter;
	private Button btnDownloading;
	private Button btnDownloaded;
	private Download deleteitem;
	private DownloadManager dm;
	private LruCache<String, Bitmap> imageCache = new LruCache<String, Bitmap>(
			4 * 1024 * 1024);
	private LayoutInflater inflater;
	private TextView nocontent;
	private NumberFormat nf=NumberFormat.getInstance();
	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				adapter.setData(getData());
				break;
			}
			super.handleMessage(msg);
		}

	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.downloadlist);

		dm = (DownloadManager) this.getSystemService(Context.DOWNLOAD_SERVICE);

		ImageView img = (ImageView) findViewById(R.id.titleImg);
		img.setVisibility(View.GONE);
		TextView titleText = (TextView) findViewById(R.id.titletext);
		titleText.setText("下载管理");
		titleText.setVisibility(View.VISIBLE);
		nocontent = (TextView) findViewById(R.id.nocontent);
		ListView l = (ListView) this.findViewById(R.id.downloadlist);
		adapter = new ListAdapter<Download>(this, this);
		l.setAdapter(adapter);
		adapter.setData(getData());
		btnDownloading = (Button) this.findViewById(R.id.downloading_btn);
		btnDownloading.setOnClickListener(this);
		btnDownloaded = (Button) findViewById(R.id.downloaded_btn);
		btnDownloaded.setOnClickListener(this);
		l.setOnItemClickListener(this);
		l.setOnItemLongClickListener(this);
		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		nf.setMaximumFractionDigits(2);
	}

	private Download[] getData() {

		Query query = new Query();
		if (this.downloading) {
			query.setFilterByStatus(DownloadManager.STATUS_PAUSED
					| DownloadManager.STATUS_PENDING
					| DownloadManager.STATUS_RUNNING);
		} else {
			query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);

		}
		Cursor c = dm.query(query);
		List<Download> downlist = new ArrayList<Download>();
		int titleInd = c.getColumnIndex(DownloadManager.COLUMN_TITLE);
		int descInd = c.getColumnIndex(DownloadManager.COLUMN_DESCRIPTION);
		int statusInd = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
		int totalSizeInd = c
				.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
		int downloadedSizeInd = c
				.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
		int uriInd = c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
		int idInd = c.getColumnIndex(DownloadManager.COLUMN_ID);
		while (c.moveToNext()) {

			Download d = new Download();
			d.setTitle(c.getString(titleInd));
			d.setDesc(c.getString(descInd));
			d.setStatus(c.getInt(statusInd));
			d.setSize(c.getInt(totalSizeInd));
			d.setDownloadSize(c.getInt(downloadedSizeInd));
			d.setUri(c.getString(uriInd));

			d.setId(c.getLong(idInd));
			downlist.add(d);
		}
		c.close();
		if (this.downloading) {
			if (downlist.size() > 0) {
				Message message = new Message();
				message.what = 1;
				handler.sendMessageDelayed(message, 1000);
				nocontent.setVisibility(View.GONE);
			} else {
				nocontent.setVisibility(View.VISIBLE);
				nocontent.setText("没有正在下载的视频");
			}
		}else{
			if (downlist.size() > 0) {
				nocontent.setVisibility(View.GONE);
			}else{
				nocontent.setVisibility(View.VISIBLE);
				nocontent.setText("没有已下载的视频");
			}
		}
		return downlist.toArray(new Download[0]);
	}

	public View createOrUpdateView(Download item, View convertView,
			int position, ViewGroup parent) {

		if (!downloading) {
			View result = inflater.inflate(R.layout.vedio_item, parent, false);
			TextView t = (TextView) result.findViewById(R.id.titleTxt);
			t.setText(item.getTitle());
			t = (TextView) result.findViewById(R.id.descTxt);
			t.setText(item.getDesc());
			ImageView vedioImg = (ImageView) result.findViewById(R.id.vedioImg);
			Bitmap thumnail = getFromLruCache(item.getUri());
			if (thumnail == null) {
				thumnail = ThumbnailUtils.createVideoThumbnail(item.getUri(),
						MediaStore.Video.Thumbnails.MINI_KIND);		
			}
			if(thumnail!=null){
				putLruChache(item.getUri(), thumnail);
				vedioImg.setImageBitmap(thumnail);
			}
			return result;
		} else {

			View result = inflater.inflate(R.layout.downloadingitem, parent,
					false);
			TextView title = (TextView) result.findViewById(R.id.dltitle);
			title.setText(item.getTitle());
			// title=(TextView)result.findViewById(R.id.desc);
			// title.setText(item.getDesc());
			TextView text=(TextView)result.findViewById(R.id.downsize);
			text.setText(getSizeStr(item));
			ProgressBar progress = (ProgressBar) result
					.findViewById(R.id.progress);
			// Log.d("Download",
			// item.getTitle()+" download size:"+item.getDownloadSize()+", total size:"+item.getSize());
			int per = (int) (item.getDownloadSize() / (float) item.getSize() * 100);
			progress.setProgress(per);
			return result;
		}
	}

	private CharSequence getSizeStr(Download item) {
		double d;
		double t;
		if(item.getSize()>1024*1024){
			 d=item.getDownloadSize()/(1024.0*1024);
			 t=item.getSize()/(1024.0*1024);
			 return nf.format(d)+"/"+nf.format(t)+"M";
		}else{
			 d=item.getDownloadSize()/(1024.0);
			 t=item.getSize()/(1024.0);
			 return nf.format(d)+"/"+nf.format(t)+"K";
		}
		
	}


	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Download item = (Download) arg0.getAdapter().getItem(arg2);
		if (!downloading) {

			Uri uri = Uri.parse(item.getUri());
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Log.v("URI:::::::::", uri.toString());
			intent.setDataAndType(uri, "video/mp4");
			startActivity(intent);
		}
	}

	private void putLruChache(String uri, Bitmap result2) {
		if (imageCache != null&&result2!=null) {
			Log.d("Download", "put image to LRU cache");
			imageCache.put(uri, result2);
		}

	}

	private Bitmap getFromLruCache(String uri) {
		if (imageCache != null) {
			return imageCache.get(uri);
		} else {
			return null;
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.downloaded_btn:
			this.downloading = false;
			adapter.setData(getData());
			btnDownloading.setBackgroundResource(R.drawable.btn_dl_n_l);
			btnDownloaded.setBackgroundResource(R.drawable.btn_dl_s_r);
			break;
		case R.id.downloading_btn:
			this.downloading = true;
			btnDownloading.setBackgroundResource(R.drawable.btn_dl_s_l);
			btnDownloaded.setBackgroundResource(R.drawable.btn_dl_n_r);
			adapter.setData(getData());
			break;
		}

	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (!downloading) {
			deleteitem = (Download) arg0.getAdapter().getItem(arg2);
			showDialog(1);
			// this.showDialog(id)
			return true;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
			return createDeleteConfirmDialog();
		}
		return super.onCreateDialog(id);
	}

	private Dialog createDeleteConfirmDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("删除下载内容").setIcon(android.R.drawable.ic_dialog_info)
				.setMessage("确认删除视频？").setCancelable(false)
				.setPositiveButton("删除", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dm.remove(deleteitem.getId());
						Log.d("DownalodList", "uri:" + deleteitem.getUri());
						File f = new File(deleteitem.getUri().substring(
								"file://".length()));
						if (f.exists())
							f.delete();
						adapter.setData(getData());
					}
				}).setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

}
