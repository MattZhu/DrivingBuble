package com.dt.android.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.dt.android.R;
import com.dt.android.domainobject.AppData;
import com.dt.android.receiver.DownloadCompleteReceiver;
import com.dt.android.serverapi.JsonApi;
import com.dt.android.serverapi.message.Chapter;
import com.dt.android.serverapi.message.ChapterResponse;
import com.dt.android.serverapi.message.Question;
import com.dt.android.serverapi.message.QuestionResponse;
import com.dt.android.serverapi.message.RequestData;
import com.dt.android.serverapi.message.Vedio;
import com.dt.android.serverapi.message.VedioResponse;
import com.dt.android.serverapi.message.VersionResponse;
import com.dt.android.utils.AsyncTaskEx;
import com.dt.android.utils.DownloadImageOrVedio;

@SuppressLint("NewApi")
public class MoreActivity extends BaseMainActivity implements OnClickListener {

	public static final String TAG = "MoreActivity";
	private VersionResponse version;
	private DownloadCompleteReceiver receiver;

	private ProgressBar questionDownload;

	private View checkDataView;

	private DownloadImageOrVedio downloader;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.more);
		ImageView img = (ImageView) findViewById(R.id.titleImg);
		img.setImageResource(R.drawable.more);
		findViewById(R.id.newbieIntro).setOnClickListener(this);
		checkDataView = findViewById(R.id.data_version_check);
		checkDataView.setOnClickListener(this);
		questionDownload = (ProgressBar) findViewById(R.id.questionDownload);
		findViewById(R.id.app_version_check).setOnClickListener(this);
		findViewById(R.id.suggestion_view).setOnClickListener(this);
		findViewById(R.id.more_about).setOnClickListener(this);
		findViewById(R.id.downloadall).setOnClickListener(this);
		findViewById(R.id.downloadmanager).setOnClickListener(this);
		receiver = new DownloadCompleteReceiver(this);

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.newbieIntro:
			Intent i = new Intent(this, DrivingTestActivity.class);
			i.putExtra("showIntro", true);
			this.showConfirm = false;
			finish();
			this.startActivity(i);
			break;
		case R.id.data_version_check:
			getDataVersion(false);
			break;
		case R.id.app_version_check:
			getDataVersion(true);
			break;
		case R.id.suggestion_view:
			suggestion();
			break;
		case R.id.more_about:
			showDialog(4);
			break;
		case R.id.downloadall:
			downloadall();
			break;
		case R.id.downloadmanager:
			showdownloadManager();
			break;
		}

	}

	private void showdownloadManager() {
		Intent i=new Intent(this,DownloadListActivity.class);
		startActivity(i);

	}

	private void downloadall() {

		if (isConnected()) {
			showDialog(PROGRESS_DIALOG);
			
			new AsyncTaskEx<String, Void, Integer>() {

				@Override
				protected Integer doInBackground(String... params) {
					JsonApi<VedioResponse> api = new JsonApi<VedioResponse>(
							VedioResponse.class);
					RequestData datas = new RequestData();
					datas.set("api_name", "get_vo");
					VedioResponse r = api.post(datas);
					return download(r.getContent());
				}

				private Integer download(Vedio[] content) {
					DownloadManager dm = (DownloadManager) MoreActivity.this
							.getSystemService(Context.DOWNLOAD_SERVICE);
					Query query = new Query();
					query.setFilterByStatus(DownloadManager.STATUS_PAUSED|DownloadManager.STATUS_RUNNING|DownloadManager.STATUS_SUCCESSFUL|DownloadManager.STATUS_PENDING);
					Cursor c = dm.query(query);
					List<String> downlist = new ArrayList<String>();
					while (c.moveToNext()) {

						String s = c.getString(c
								.getColumnIndex(DownloadManager.COLUMN_TITLE));
						downlist.add(s);
					}
					c.close();
					Log.d(TAG, "downlist:"+downlist);
					int result = 0;
					for (Vedio v : content) {
						if (downlist.contains(v.getTitle())) {
							Log.d(TAG, "file " + v.getTitle()
									+ " already downloaded.");
						} else {
							result++;
							Log.d(TAG, "request download " + v.getTitle() + ".");
//							getExternalFilesDir("cache")
							Request request = new Request(Uri.parse(v
									.getVo_url()));
							request.setDestinationInExternalFilesDir(
									MoreActivity.this, Environment.DIRECTORY_MOVIES,
									v.getTitle());
							request.setDescription(v.getDesc());
							
							request.setVisibleInDownloadsUi(false);
							
							dm.enqueue(request);
						}
					}
					return result;
				}

				@Override
				protected void onPostExecute(Integer result) {
					dismissDialog(PROGRESS_DIALOG);
					if(result==0){
						showToast("没有新的视频需要下载");
					}else{
						showToast("添加"+result+"个视频到下载列表");
					}
				}

			}.execute();
		} else {
			showDialog(NO_NETWORK);
		}
		
	}

	private void suggestion() {
		String[] reciver = new String[] { "kaozhao123@kaozhao123.com" };
		String mySbuject = "给驾考宝典的建议";

		Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
		myIntent.setType("plain/text");
		myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
		myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
		startActivity(Intent.createChooser(myIntent, "Email"));

	}

	private void getDataVersion(final boolean isapp) {
		this.showDialog(PROGRESS_DIALOG);
		task = (new AsyncTaskEx<String, Void, VersionResponse>() {

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
				task = null;
				dismissDialog(PROGRESS_DIALOG);
				version = result;
				if (result != null && result.getStatus_code() == 1) {
					if (isapp) {
						Log.d("MoreActivity", "AppVersion="
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
							 showDialog(1);
//							showDialog(0);
						} else {
							showDialog(0);
						}
					} else {
						if (AppData.getInstance().getDataVersion()
								.compareTo(result.getData_version()) < 0) {
							showDialog(2);
						} else {
							showDialog(5);
						}
					}
				} else {
					showDialog(-1);
				}
			}

		}.execute());
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			return alert("有新版本程序，是否更新？", id);
		case 2:
			return alert("有新版本题库，是否更新？", id);
		case 0:
			return alert("你当前应用是最新版本。", 0);
		case 5:
			return alert("你当前题库是最新版本。", 0);
		case -1:
			return alert("检查版本出错，请检查你的网络是否连接。", -1);
		case 4:
			return createAbout();
		}
		return super.onCreateDialog(id);
	}

	private Dialog createAbout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.about)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok, null);
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.about_dialog, null);
		builder.setView(view);
		return builder.create();
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
							questionDownload.setVisibility(View.VISIBLE);
							checkDataView.setEnabled(false);

							new AsyncTaskEx<Void, Integer, String>() {

								@Override
								protected String doInBackground(Void... params) {
									JsonApi<QuestionResponse> api = new JsonApi<QuestionResponse>(
											QuestionResponse.class);

									RequestData datas = new RequestData();
									datas.set("api_name", "all_questions");
									datas.set("page_num", 0);
									datas.set("page_size", "100");
									QuestionResponse result;
									dbadapter.getQuestionDAO().deleteAll();
									downloader = new DownloadImageOrVedio(
											MoreActivity.this);
									for (int i = 0; true; i++) {
										datas.set("page_num", i);
										result = api.post(datas);
										Log.d(TAG, "Result:question size:"
												+ result.getContent().length);

										if (result == null
												|| result.getStatus_code() == 0
												|| result.getContent().length == 0) {
											break;
										} else {
											Long start = System
													.currentTimeMillis();
											download(result.getContent());
											dbadapter
													.getQuestionDAO()
													.insertQuestion(
															result.getContent());

											Log.d(TAG,
													"insert "
															+ result.getContent().length
															+ " questions take "
															+ (System
																	.currentTimeMillis() - start)
															+ "ms");
											if (i < 18) {
												publishProgress(i * 5);
											}
											if (result.getContent().length < 100) {
												break;
											}
										}
									}

									JsonApi<ChapterResponse> chApi = new JsonApi<ChapterResponse>(
											ChapterResponse.class);
									this.publishProgress(90);
									while(downloader.getRunningTaskCount()>0){
										try {
											Log.d(TAG, "remain download files:" + downloader.getRunningTaskCount());
											Thread.sleep(100);
										} catch (InterruptedException e) {
										}
									}
									this.publishProgress(95);
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
									if(content==null)
									{
										return;
									}
									
									for (Question q : content) {
//										System.out.println(toSql(q));
										
										if (q.getPicture() != null
												&& q.getPicture().length() > 0) {											
											if (q.getPicture().endsWith(".jpg")
													|| q.getPicture().endsWith(
															".gif")) {
												downloader.download(
														q.getPicture(),
														AppData.QUESTION_IMAGE_CACHE_FOLDER,
														q.getQuestion_id()
																+ ".png");
											} else if (q.getPicture().endsWith(
													"mp4")) {
												downloader.download(
														q.getPicture(),
														AppData.QUESTION_VEDIO_CACHE_FOLDER,
														q.getQuestion_id()
																+ ".mp4");
											}
										}
									}
									
								}

								
//								 private String toSql(Question q) {
//								
//								 return
//								 "insert into QUESTION (question_id,is_a boolean,	is_b boolean,is_c boolean,"
//								 +
//								 " q_type integer, description TEXT,	picture TEXT,answer TEXT,answerDetail TEXT,"
//								 +
//								 " options TEXT )values("+q.getQuestion_id()+","+q.isA()+","+q.isB()+","+q.isC()
//								 +","+q.getType()+",'"+q.getDesc()+"','"+q.getPicture()+"','"+covertString(q.getCorrect_answer())+"','"+q.getExplanation()+"','"+covertString(q.getAnswer())+"');";
//								 }
//								 private String covertString(String[]
//								 correct_answer) {
//								 String result = "";
//								 if (correct_answer == null ||
//								 correct_answer.length == 0) {
//								 return result;
//								 }
//								 for (String s : correct_answer) {
//								 result += "==" + s;
//								 }
//								 return result.substring("==".length());
//								 }
								@Override
								protected void onPostExecute(String result) {
									checkDataView.setEnabled(true);
									questionDownload.setVisibility(View.GONE);
									dbadapter.getAppConfDAO().updateAppConf(
											"data.version",
											version.getData_version());
									AppData.getInstance().setDataVersion(
											version.getData_version());
									showToast("题库更新完成！");
								}

								@Override
								protected void onProgressUpdate(
										Integer... values) {
									questionDownload.setProgress(values[0]);
								}

							}.execute();

						}
					});
		} else {
			builder.setPositiveButton(android.R.string.ok, null);
		}

		if (op > 0)
			builder.setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	@Override
	protected void onDestroy() {
		if (receiver != null)
			receiver.onDestroy();
		super.onDestroy();
	}

}
