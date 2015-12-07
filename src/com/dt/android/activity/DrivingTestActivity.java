package com.dt.android.activity;

import java.util.concurrent.CountDownLatch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.cnzz.mobile.android.sdk.MobileProbe;
import com.dt.android.R;
import com.dt.android.db.DBAdapter;
import com.dt.android.domainobject.AppData;
import com.dt.android.receiver.DownloadCompleteReceiver;
import com.dt.android.serverapi.JsonApi;
import com.dt.android.serverapi.message.Chapter;
import com.dt.android.serverapi.message.ChapterResponse;
import com.dt.android.serverapi.message.Question;
import com.dt.android.serverapi.message.QuestionResponse;
import com.dt.android.serverapi.message.RequestData;
import com.dt.android.serverapi.message.VersionResponse;
import com.dt.android.utils.AsyncTaskEx;
import com.dt.android.utils.DownloadImageOrVedio;

public class DrivingTestActivity extends TabActivity implements
		OnClickListener, OnCancelListener {
	/** Called when the activity is first created. */
	View[] list = new View[5];
	int selected = -1;
	protected final static int PROGRESS_DIALOG = 1002;
	private TabHost tabHost;
	protected String TAG = "DrivingTestActivity";

	private VersionResponse version;
	protected DBAdapter dbadapter;
	private DownloadCompleteReceiver receiver;

	protected boolean dialogCanceled = false;
	protected ProgressDialog pd;

	AsyncTaskEx<Void, Integer, String> task;
	private ProgressBar pb;
	private DownloadImageOrVedio downloader;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		MobileProbe.startStatistic(this,"cnzz.a_2k8l1y5p11pem26e5s3x6sqt","cnzz");
		
		ImageView image = (ImageView) findViewById(R.id.intro);
		boolean showIntro = this.getIntent().getBooleanExtra("showIntro",
				AppData.getInstance().getShowIntro());
		Log.d(TAG, "showIntro:" + showIntro);
		if (showIntro) {
			image.setOnClickListener(this);
		} else {
			image.setVisibility(View.GONE);
		}
		dbadapter = new DBAdapter(this);
		dbadapter.open();

		int t = getIntent().getIntExtra("tab", 0);
		tabHost = getTabHost(); // The activity TabHost

		tabHost.getTabWidget().setBackgroundColor(Color.LTGRAY);
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View menuItem;
		TextView menuText;
		Intent intent; // Reusable Intent for each tab
		menuItem = inflater.inflate(R.layout.menuitem, null);
		menuText = (TextView) menuItem.findViewById(R.id.menuText);
		menuText.setText(R.string.home);
		intent = new Intent().setClass(this, HomeActivity.class);
		spec = tabHost.newTabSpec("home").setIndicator(menuItem)
				.setContent(intent);
		tabHost.addTab(spec);
		menuItem = inflater.inflate(R.layout.menuitem, null);
		menuText = (TextView) menuItem.findViewById(R.id.menuText);
		menuText.setText(R.string.biaozhi);
		intent = new Intent().setClass(this, SignActivity.class);
		spec = tabHost.newTabSpec("biaozhi").setIndicator(menuItem)
				.setContent(intent);
		tabHost.addTab(spec);
		menuItem = inflater.inflate(R.layout.menuitem, null);
		menuText = (TextView) menuItem.findViewById(R.id.menuText);
		menuText.setText(R.string.fagui);
		intent = new Intent().setClass(this, LawActivity.class);
		spec = tabHost.newTabSpec("fagui").setIndicator(menuItem)
		// .setIndicator("练习")
				.setContent(intent);
		tabHost.addTab(spec);
		menuItem = inflater.inflate(R.layout.menuitem, null);
		menuText = (TextView) menuItem.findViewById(R.id.menuText);
		menuText.setText(R.string.shangcu);
		intent = new Intent().setClass(this, PromotionActivity.class);
		spec = tabHost.newTabSpec("shangcu").setIndicator(menuItem)
				.setContent(intent);
		tabHost.addTab(spec);
		menuItem = inflater.inflate(R.layout.menuitem, null);
		menuText = (TextView) menuItem.findViewById(R.id.menuText);
		menuText.setText(R.string.more);
		intent = new Intent().setClass(this, MoreActivity.class);
		spec = tabHost.newTabSpec("more").setIndicator(menuItem)
				.setContent(intent);
		tabHost.addTab(spec);

		tabHost.setCurrentTab(t);
		

		receiver = new DownloadCompleteReceiver(this);
		new AsyncTaskEx<String, Void, VersionResponse>() {

			@Override
			protected VersionResponse doInBackground(String... params) {
				JsonApi<VersionResponse> api = new JsonApi<VersionResponse>(
						VersionResponse.class);
				RequestData datas = new RequestData();
				datas.set("api_name", "check_version");
				VersionResponse r = api.post(datas);
				return r;
			}

			@Override
			protected void onPostExecute(VersionResponse result) {
				version = result;
				if (result != null && result.getStatus_code() == 1) {
					Log.d(TAG, "AppVersion="
							+ AppData.getInstance().getAppVersion()
							+ ",DataVersion="
							+ AppData.getInstance().getDataVersion());
					String appVersion = "1.0";
					try {
						appVersion = getPackageManager().getPackageInfo(
								getPackageName(), 0).versionName;
						Log.d(TAG, "appVersion=" + appVersion);
					} catch (NameNotFoundException e) {
						Log.d(TAG, e.getMessage());
					}
					if (appVersion.compareTo(result.getApp_version()) < 0) {
						// showDialog(1);
					}
					if (AppData.getInstance().getDataVersion()
							.compareTo(result.getData_version()) < 0) {
						showDialog(2);

					}
				}
			}

		}.execute();
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			return alert("有新版本程序，是否更新？", id);
		case 2:
			return alert("有新版本题库，是否更新？", id);
		case PROGRESS_DIALOG:
			return createProgressDialog();
		}
		return super.onCreateDialog(id);
	}

	private AlertDialog alert(String msg, int op) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("检查更新").setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(msg).setCancelable(false);

		if (op == 1) {
			builder.setPositiveButton("更新",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							receiver.downloadApp();
						}
					});
		} else if (op == 2) {
			builder.setPositiveButton("更新",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							dialogCanceled = false;
							showDialog(PROGRESS_DIALOG);

							task = new AsyncTaskEx<Void, Integer, String>() {
								int total = version.getQuestion_num();

								@Override
								protected String doInBackground(Void... params) {
									JsonApi<QuestionResponse> api = new JsonApi<QuestionResponse>(
											QuestionResponse.class);

									RequestData datas = new RequestData();
									datas.set("api_name", "all_questions");
									datas.set("page_num", 0);
									datas.set("page_size", "100");
									QuestionResponse result;

									downloader= new DownloadImageOrVedio(DrivingTestActivity.this);
									dbadapter.getQuestionDAO().deleteAll();
									for (int i = 0; true; i++) {
										datas.set("page_num", i);
										result = api.post(datas);
										Log.d(TAG, "Result:" + result);
										if (result == null
												|| result.getStatus_code() == 0
												|| result.getContent().length == 0) {
											break;
										} else {

											publishProgress((int) (i * 100.0
													/ total * 95));

											download(result.getContent());
											dbadapter
													.getQuestionDAO()
													.insertQuestion(
															result.getContent());

											if (result.getContent().length < 100) {
												break;
											}
										}
									}
									while(downloader.getRunningTaskCount()>0){
										try {
											Log.d(TAG, "remain download files:" + downloader.getRunningTaskCount());
											Thread.sleep(100);
										} catch (InterruptedException e) {
										}
									}
									this.publishProgress(95);
									JsonApi<ChapterResponse> chApi = new JsonApi<ChapterResponse>(
											ChapterResponse.class);
									datas = new RequestData();
									datas.set("api_name", "question_chapter");
									ChapterResponse chapter = chApi.post(datas);
									Log.d(TAG, "ChapterResponse:" + chapter);
									if (chapter != null
											&& chapter.getStatus_code() == 1) {
										for (Chapter ch : chapter
												.getFirst_content()) {
											dbadapter.getChapterDAO()
													.insertChapter(ch, 1);
										}
										for (Chapter ch : chapter
												.getThird_content()) {
											dbadapter.getChapterDAO()
													.insertChapter(ch, 3);
										}
									}
									this.publishProgress(100);
									return null;
								}
								private void download(Question[] content) {
									for (Question q : content) {
										if (q.getPicture() != null
												&& q.getPicture().length() > 0) {
											
											if(q.getPicture().endsWith(".jpg") || q.getPicture().endsWith(".gif")){
												downloader.download(
														q.getPicture(),
														AppData.QUESTION_IMAGE_CACHE_FOLDER,
														q.getQuestion_id()
																+ ".png");
											}else if (q.getPicture().endsWith("mp4")) {
												downloader.download(
														q.getPicture(),
														AppData.QUESTION_VEDIO_CACHE_FOLDER,
														q.getQuestion_id()
																+ ".mp4");
											}
										}
									}									
								}


								@Override
								protected void onPostExecute(String result) {
									dismissDialog(PROGRESS_DIALOG);

									dbadapter.getAppConfDAO().updateAppConf(
											"data.version",
											version.getData_version());
									AppData.getInstance().setDataVersion(
											version.getData_version());

									Toast.makeText(DrivingTestActivity.this,
											"题库更新完成！", Toast.LENGTH_SHORT)
											.show();
								}

								@Override
								protected void onProgressUpdate(
										Integer... values) {
									if (pb != null){
										Log.d("","update progress to "+values[0]);
										pb.setProgress(values[0]);
									}
								}

							}.execute();

						}

					});
		}
		builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	protected void onDestroy() {
		dbadapter.close();
		receiver.onDestroy();
		super.onDestroy();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.intro:
			dbadapter.getAppConfDAO().updateAppConf("showIntro", "false");
			AppData.getInstance().setShowIntro(false);
			v.setVisibility(View.GONE);
			break;
		}

	}

	private Dialog createProgressDialog() {
		pd = ProgressDialog.show(this, "更新题库", "正在更新题库，请稍等……", false, true);
		LayoutInflater inflater = (LayoutInflater) this
		.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.alert_dialog_progress, null);
		pb=(ProgressBar)view.findViewById(R.id.progress);
		pd.setContentView(view);
		pd.setOnCancelListener(this);
		return pd;
	}

	public void onCancel(DialogInterface dialog) {
		dialogCanceled = true;
		if (task != null) {
			task.cancel(true);
		}
	}

}