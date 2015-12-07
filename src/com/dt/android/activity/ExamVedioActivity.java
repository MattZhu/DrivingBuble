package com.dt.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.AppData;
import com.dt.android.serverapi.JsonApi;
import com.dt.android.serverapi.message.RequestData;
import com.dt.android.serverapi.message.Vedio;
import com.dt.android.serverapi.message.VedioResponse;
import com.dt.android.utils.AsyncTaskEx;
import com.dt.android.utils.ImageLoadTask;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;

@SuppressLint("NewApi")
public class ExamVedioActivity extends BackBaseActivity implements
		ListItemViewCreator<Vedio>, OnItemClickListener {

	private JsonApi<VedioResponse> api;
	private ListAdapter<Vedio> adapter;

	private List<String> downlist = new ArrayList<String>();
	private Map<String,String>uris=new HashMap<String,String>();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.list);
		ImageView title = (ImageView) this.findViewById(R.id.titleImg);
		title.setVisibility(View.GONE);

		TextView titleTxt = (TextView) this.findViewById(R.id.titletext);
		titleTxt.setText(R.string.consulting);
		titleTxt.setVisibility(View.VISIBLE);
		ListView l = (ListView) this.findViewById(R.id.lists);
		adapter = new ListAdapter<Vedio>(this, this);
		l.setAdapter(adapter);
		l.setOnItemClickListener(this);
		if (!isConnected()) {
			this.showDialog(NO_NETWORK);
			return;
		}
		showDialog(PROGRESS_DIALOG);
		api = new JsonApi<VedioResponse>(VedioResponse.class);

		task = new AsyncTaskEx<String, Void, VedioResponse>() {

			@Override
			protected VedioResponse doInBackground(String... params) {
				DownloadManager dm = (DownloadManager) ExamVedioActivity.this
						.getSystemService(Context.DOWNLOAD_SERVICE);
				Query query = new Query();
				query.setFilterByStatus(DownloadManager.STATUS_SUCCESSFUL);
				Cursor c = dm.query(query);
				while (c.moveToNext()) {
					
					String s = c.getString(c
							.getColumnIndex(DownloadManager.COLUMN_TITLE));
					downlist.add(s);
					uris.put(s, c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
				}
				c.close();

				RequestData datas = new RequestData();
				datas.set("api_name", "get_vo");
				return api.post(datas);
			}

			@Override
			protected void onPostExecute(VedioResponse result) {
				if (!this.isCancelled()) {
					dismissDialog(PROGRESS_DIALOG);
					if (result != null) {
						adapter.setData(result.getContent());
					} else {
						
					}
				}
				task = null;
			}

		}.execute();

	}

	public View createOrUpdateView(Vedio item, View convertView, int position,
			ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.vedio_item, parent, false);
		TextView t = (TextView) result.findViewById(R.id.titleTxt);
		t.setText(item.getTitle());
		t = (TextView) result.findViewById(R.id.descTxt);
		t.setText(item.getDesc());
		if (item.getThumb() != null && item.getThumb().length() > 0) {
			ImageView vedioImg = (ImageView) result.findViewById(R.id.vedioImg);
			ImageLoadTask task = new ImageLoadTask(vedioImg, this);
			task.execute(item.getThumb(), AppData.VEDIO_IMAGE_CACHE_FOLDER,
					item.getId() + ".png");
		}
		return result;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Vedio news = (Vedio) arg0.getItemAtPosition(arg2);
		
		Uri uri = Uri.parse(news.getVo_url());
		if(this.downlist.contains(news.getTitle()))
		{
			Log.d("Vedio", "Use cached video for file "+news.getTitle());
			uri=Uri.parse(uris.get(news.getTitle()));
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Log.v("URI:::::::::", uri.toString());
		intent.setDataAndType(uri, "video/mp4");
		startActivity(intent);
		// Intent i=new Intent(this,VedioWatchActivity.class);
		// i.putExtra("uri", news.getVo_url());
		// startActivity(i);

	}

}
