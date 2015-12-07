package com.dt.android.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.VideoView;

import com.dt.android.R;
import com.dt.android.db.dao.QuestionDAO;
import com.dt.android.domainobject.AppData;
import com.dt.android.domainobject.Question;
import com.dt.android.serverapi.JsonApi;
import com.dt.android.serverapi.message.ExamResponse;
import com.dt.android.serverapi.message.RequestData;
import com.dt.android.utils.AsyncTaskEx;
import com.dt.android.utils.GallerySupport;
import com.dt.android.utils.ListAdapter;
import com.dt.android.utils.ViewPagerAdapter;
import com.dt.android.utils.ViewPagerSupport;
import com.dt.android.view.QuestionItemViewCreator;

public class SimulateTestActivity extends BackBaseActivity implements
		OnCheckedChangeListener, OnClickListener {
	private static final int TEST_MSG = 1;

	private static final int NOT_FINISHED = 2;

	private static final int EXAM_TIME = 2700;

	private QuestionDAO questionDAO;

	private ViewPager viewPager;

	private ViewPagerAdapter<Question> adapter;

	private ViewPagerSupport viewPagerSupport;

	private Timer timer = new Timer();
	private TextView timerView;
	private int timeLeft = EXAM_TIME;

	private QuestionItemViewCreator questionItemViewCreator;

	private Question[] data;

	private int errorCount = 0;
	private int finishedCount = 0;
	private int total = 0;

	private TextView title;

	private Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				updateTimer();
				return;
			}
			super.handleMessage(msg);
		}

	};
	TimerTask mtask = new TimerTask() {

		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
			timeLeft--;
		}

	};

	private boolean isQ1;

	private int allowedError;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simulate_test);
		questionDAO = dbadapter.getQuestionDAO();
		findViewById(R.id.titleImg).setVisibility(View.GONE);
		findViewById(R.id.rightbtn).setVisibility(View.GONE);
		Integer titleRes = getIntent().getIntExtra("title",
				R.string.simulate_test);
		title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText(titleRes);

		timerView = (TextView) findViewById(R.id.timerview);
		updateTimer();
		questionItemViewCreator = new QuestionItemViewCreator(this, this, this);
		questionItemViewCreator.setTesting(true);
		viewPager = (ViewPager) findViewById(R.id.pager);
		Button btn = (Button) findViewById(R.id.leftbtn);
		btn.setOnClickListener(this);
		View submit = findViewById(R.id.submit);
		submit.setOnClickListener(this);

		View showNotAnswered = findViewById(R.id.not_answered);
		showNotAnswered.setOnClickListener(this);
		this.rightBtnIsCarType = false;
		this.btnInited = true;
		isQ1 = AppData.getInstance().getqType() == 1;
		allowedError = isQ1 ? 10 : 5;
		this.showDialog(TEST_MSG);
	}

	private void startTest() {
		showDialog(PROGRESS_DIALOG);

		task = new AsyncTaskEx<Void, Integer, Question[]>() {

			@Override
			protected Question[] doInBackground(Void... params) {
				JsonApi<ExamResponse> api = new JsonApi<ExamResponse>(
						ExamResponse.class);
				RequestData datas = new RequestData();
				datas.set("api_name", "question_exam");
				datas.set("car", 3 - AppData.getInstance().getCarType());
				ExamResponse response = api.post(datas);
				if (response != null && response.getStatus_code() == 1) {
					saveExamQ(response);
					if (AppData.getInstance().getqType() == 1) {
						return questionDAO.getQuestions(response
								.getFirst_content()[0].getExam_set());
					} else {
						return questionDAO.getQuestions(response
								.getThird_content()[0].getExam_set());
					}
				} else {

					return generatTestQ();
				}
			}

			private Question[] generatTestQ() {
				String savedQuestions = dbadapter.getAppConfDAO().getAppConf("ExamQuestion." +  AppData.getInstance().getqType() + "."
								+ AppData.getInstance().getCarType());
				if (savedQuestions != null) {
					Log.d("SimultaeTestActivity",
							"use last time questions " + savedQuestions);
					return questionDAO.getQuestions(savedQuestions);
				} else {

					Question[] all = questionDAO.getQuestions(null, null);
					int t = isQ1 ? 100 : 50;
					Question[] result = new Question[t];
					int ind = 0;
					StringBuffer random = new StringBuffer();
					for (int i = 0; i < t; i++) {
						ind = (int) (Math.random() * all.length);
						result[i] = all[ind];
						random.append(",").append(ind);
					}
					Log.d("SimultaeTestActivity",
							"generate random:" + random.substring(1));
					return result;
				}
			}

			private void saveExamQ(ExamResponse response) {
				dbadapter.getAppConfDAO().updateAppConf(
						"ExamQuestion." + 1 + "."
								+ AppData.getInstance().getCarType(),
						response.getFirst_content()[0].getExam_set());
				dbadapter.getAppConfDAO().updateAppConf(
						"ExamQuestion." + 3 + "."
								+ AppData.getInstance().getCarType(),
						response.getThird_content()[0].getExam_set());
			}

			@Override
			protected void onPostExecute(Question[] result) {
				if (!this.isCancelled()) {
					dismissDialog(PROGRESS_DIALOG);
					if (result != null) {
						total = result.length;
						adapter = new ViewPagerAdapter<Question>();
						adapter.setData(result);
						data = result;
						adapter.setQuestionItemViewCreator(questionItemViewCreator);
						viewPager.setAdapter(adapter);
						
						viewPagerSupport = new ViewPagerSupport(SimulateTestActivity.this, viewPager);
						viewPagerSupport.init();
						timeLeft = isQ1 ? 2700 : 1800;
						timer.schedule(mtask, 0, 1000);
					} else {
						showToast("生成模拟试卷错误，无法开始考试");
					}
				}
			}

		}.execute();

	}

	@Override
	public void updateUIForSelectedItem() {
		Question question = (Question) adapter.getCurrentItemData(viewPager.getCurrentItem());
		if (isVedio(question.getImage())) {

			View selectedView = adapter.getCurrentView();
			View v = selectedView.findViewById(R.id.questionImage);
			if (v != null && v.getVisibility() == View.VISIBLE) {
				v.performClick();
			} else {
				VideoView v1 = (VideoView) selectedView
						.findViewById(R.id.questionVedio);
				if (v1 != null && !v1.isPlaying()) {
					v1.start();
				}
			}
		}
	}

	private boolean isVedio(String image) {
		return image.endsWith(".mp4");
	}

	private void preSubmit() {
		if (this.finishedCount < this.total) {
			removeDialog(NOT_FINISHED);
			showDialog(NOT_FINISHED);
		} else {
			submit();
		}

	}

	private void submit() {
		// save test data

		// show test summary
		Intent intent = new Intent(this, SimulateTestSummary.class);
		timer.cancel();
		this.finish();
		intent.putExtra("questions", (Serializable) Arrays.asList(data));
		intent.putExtra("time", EXAM_TIME - this.timeLeft);
		this.startActivity(intent);
	}

	private void updateTimer() {
		if (timeLeft > 0) {
			Log.d("Simulate Test", "update timer to " + formatTime(timeLeft));
			timerView.setText(formatTime(timeLeft));
		} else {
			timer.cancel();
			this.showToast("考试时间到，自动提交！");
			submit();
		}
	}

	private void updateTitle() {
		title.setText("已做" + finishedCount + "题,错" + errorCount + "题");
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Question question = adapter.getCurrentItemData(viewPager.getCurrentItem());
		question.setUserAnswer(new Integer[] { checkedId });
		finishedCount++;
		if (!question.isCorrected()) {
			errorCount++;
		}
		updateTitle();
		if (errorCount > 10) {
			submit();
		}
		if (finishedCount < total) {
			viewPagerSupport.next();
		} else {

		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.not_answered:
			Intent i = new Intent(this, SimulateTestGridActivity.class);
			i.putExtra("type", CHECK_NOT_ANSWERED);
			i.putExtra("questions", (Serializable) Arrays.asList(data));
			i.putExtra("title", R.string.check_not_answered);
			this.startActivityForResult(i, 1);
			break;
		case R.id.leftbtn:
		case R.id.submit:
			preSubmit();
			break;
		case R.id.answerBtn:
			v.setVisibility(View.INVISIBLE);
			submitAnswer();
			break;
		}

	}

	private void submitAnswer() {
		Question question = (Question) adapter.getCurrentItemData(viewPager.getCurrentItem());
		LinearLayout view = (LinearLayout) adapter.getCurrentView().findViewById(R.id.optionsM);
		int total = view.getChildCount();
		List<Integer> answer = new ArrayList<Integer>();
		for (int i = 0; i < total; i++) {
			CheckBox cb = (CheckBox) view.getChildAt(i);
			if (cb.isChecked()) {
				answer.add(i);
			}
		}
		question.setUserAnswer(answer.toArray(new Integer[0]));
		finishedCount++;
		if (!question.isCorrected()) {
			errorCount++;
		}

		updateTitle();
		if (errorCount > allowedError) {
			submit();
		}
		if (finishedCount < total) {
			viewPagerSupport.next();
		} else {

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onKeyUp(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			this.preSubmit();
			return true;
		} else {
			return super.onKeyUp(keyCode, event);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case RESULT_CANCELED:
			break;
		case RESULT_OK:
			int p = data.getIntExtra("question.id", -1);
			if (p != -1) {
				viewPager.setCurrentItem(p);
			}
			break;
		}
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TEST_MSG:
			int res = isQ1 ? R.string.simulatetestmsg
					: R.string.simulatetestmsgq3;
			return createDialog(R.string.infostr, res);
		case NOT_FINISHED:
			return createNotFinishedDialog();

		}
		return super.onCreateDialog(id);
	}

	private Dialog createNotFinishedDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.simulate_test)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage("还有" + (total - finishedCount) + "道题未做，是否交卷？")
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								submit();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
							}
						});
		return builder.create();
	}

	private Dialog createDialog(int titleId, int messageId) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(titleId)
				.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage(messageId)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								startTest();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								SimulateTestActivity.this.finish();
							}
						});
		return builder.create();
	}

}
