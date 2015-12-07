package com.dt.android.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
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
import com.dt.android.domainobject.Chapter;
import com.dt.android.domainobject.Question;
import com.dt.android.utils.ViewPagerAdapter;
import com.dt.android.utils.ViewPagerSupport;
import com.dt.android.view.QuestionItemViewCreator;

public class QuestionActivity extends BackBaseActivity implements
		OnCheckedChangeListener, OnClickListener {

	private ViewPager viewPager;

	private boolean checkAnswer = false;

	private boolean showStarBtn = true;

	private Button starBtn;

	private Chapter ch;
	private Integer type = -1;

	private QuestionDAO questionDAO;

	private ViewPagerAdapter<Question> adapter;

	private ViewPagerSupport viewPagerSupport;

	private QuestionItemViewCreator questionItemViewCreator;

	private boolean persistLQ = false;

	private Integer selected;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question);
		questionDAO = dbadapter.getQuestionDAO();
		findViewById(R.id.titleImg).setVisibility(View.GONE);
		Integer titleRes = getIntent().getIntExtra("title",
				R.string.practice_seq);
		TextView title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText(titleRes);

		checkAnswer = getIntent().getBooleanExtra("check", false);

		viewPager = (ViewPager) findViewById(R.id.pager);
		questionItemViewCreator = new QuestionItemViewCreator(this, this, this);
		questionItemViewCreator.setCheckAnswer(checkAnswer);
		adapter = new ViewPagerAdapter<Question>();
		selected = getIntent().getIntExtra("question.id", 0);
		List<Question> questions = (List<Question>) getIntent()
				.getSerializableExtra("questions");
		adapter.setQuestionItemViewCreator(questionItemViewCreator);
		viewPager.setAdapter(adapter);
		viewPagerSupport = new ViewPagerSupport(this, viewPager);
		if (questions != null) {
			adapter.setData(questions.toArray(new Question[0]));
			viewPager.setCurrentItem(selected);

		} else {
			ch = (Chapter) getIntent().getExtras().get("chapter");
			type = getIntent().getIntExtra("type", -1);
			persistLQ = true;
			Question[] result = questionDAO.getQuestions(ch, type);
			if (result.length == 0) {
				showToast("暂无题库");
				QuestionActivity.this.finish();
				return;
			} else {
				adapter.setData(result);
				selected = getLastStop(ch, type);
				if (selected != 0) {
					showDialog(1);
				} else {
					viewPager.setCurrentItem(selected);
				}
			}
			// showDialog(PROGRESS_DIALOG);
			//
			// new AsyncTaskEx<Void, Void, Question[]>() {
			//
			// @Override
			// protected Question[] doInBackground(Void... params) {
			// return questionDAO.getQuestions(ch, type);
			// }
			//
			// @Override
			// protected void onPostExecute(Question[] result) {
			// if (!dialogCanceled) {
			// dismissDialog(PROGRESS_DIALOG);
			// if(result.length==0){
			// showToast("暂无题库");
			// QuestionActivity.this.finish();
			// return;
			// }
			// adapter.setData(result);
			// gallerySupport.init();
			// }
			// }
			//
			// }.execute();

			findViewById(R.id.checkAnswer).setOnClickListener(this);
		}

		viewPagerSupport.init();
		// gallerySupport = new GallerySupport(this, gallery);
		// gallerySupport.init();
		this.rightBtnIsCarType = false;
		starBtn = (Button) findViewById(R.id.rightbtn);
		if (!showStarBtn) {
			starBtn.setVisibility(View.GONE);
		} else {
			starBtn.setOnClickListener(this);
		}
		updateNav();

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == 1) {
			return createDialog();
		}
		return super.onCreateDialog(id);
	}

	private Dialog createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info)
				.setMessage("您上次已经做到第" + (selected + 1) + "题,是否继续？")
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								viewPager.setCurrentItem(selected);
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								viewPager.setCurrentItem(0);
							}
						});
		return builder.create();
	}

	private int getLastStop(Chapter ch, Integer type) {
		if (ch == null) {
			String s = dbadapter.getAppConfDAO().getAppConf(
					"last.question.seq." + type + "."
					+ AppData.getInstance().getqType()+"."+ AppData.getInstance().getCarType());
			if (s == null) {
				return 0;
			} else {
				return Integer.valueOf(s);
			}
		} else if (type == 0) {
			String s = dbadapter.getAppConfDAO().getAppConf(
					"last.question.ch." + ch.getId() + "." + type + "."+ AppData.getInstance().getqType()+"."
							+ AppData.getInstance().getCarType());
			if (s == null) {
				return 0;
			} else {
				return Integer.valueOf(s);
			}
		}
		return 0;
	}

	@Override
	protected void onDestroy() {
		persist();
		super.onDestroy();
	}

	private void persist() {
		if (persistLQ) {
			if (ch == null) {
				dbadapter.getAppConfDAO().updateAppConf(
						"last.question.seq." + type + "."+ AppData.getInstance().getqType()+"."
								+ AppData.getInstance().getCarType(),
						this.viewPager.getCurrentItem() + "");
			} else {
				dbadapter.getAppConfDAO().updateAppConf(
						"last.question.ch." + ch.getId() + "." + type + "."+ AppData.getInstance().getqType()+"."
								+ AppData.getInstance().getCarType(),
						this.viewPager.getCurrentItem() + "");
			}
		}
	}

	private void updateNav() {
		TextView pre = (TextView) findViewById(R.id.txt_pre);
		pre.setText(R.string.pre_q);
		TextView next = (TextView) findViewById(R.id.txt_next);
		next.setText(R.string.next_q);
	}

	private void updateStarBtn() {
		if (showStarBtn) {
			Question question = adapter.getCurrentItemData(viewPager
					.getCurrentItem());

			if (question.isStared()) {
				starBtn.setBackgroundResource(R.drawable.star);
			} else {
				starBtn.setBackgroundResource(R.drawable.un_star);
			}
		}
	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		Question question = adapter.getCurrentItemData(viewPager
				.getCurrentItem());
		question.setUserAnswer(new Integer[] { checkedId });
		View parent = (View) group.getParent();
		questionItemViewCreator.showAnswer(parent, question, false);
		if (question.isCorrected()) {
			viewPagerSupport.next();
		} else {
			question.setErrored(true);
			questionDAO.updateQuestion(question);
		}

	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkAnswer:
			this.checkAnswer = !this.checkAnswer;
			Button b = (Button) findViewById(R.id.btn_checkAnswer);
			questionItemViewCreator.setCheckAnswer(checkAnswer);
			questionItemViewCreator.showAnswer(adapter.getCurrentView()
					.findViewById(R.id.questionDetailView), adapter
					.getCurrentItemData(viewPager.getCurrentItem()),
					checkAnswer);
			if (this.checkAnswer) {
				b.setBackgroundResource(R.drawable.check_answer_s);
			} else {
				b.setBackgroundResource(R.drawable.check_answer);
			}
			break;
		case R.id.rightbtn:
			Question question = adapter.getCurrentItemData(viewPager
					.getCurrentItem());
			question.setStared(!question.isStared());
			questionDAO.updateQuestion(question);
			if (question.isStared()) {
				this.showToast(R.string.stared);
			} else {
				this.showToast(R.string.unstared);
			}
			updateStarBtn();
			break;
		case R.id.answerBtn:
			v.setVisibility(View.INVISIBLE);
			submitAnswer();
			break;
		}

	}

	private void submitAnswer() {
		Question question = adapter.getCurrentItemData(viewPager
				.getCurrentItem());
		LinearLayout view = (LinearLayout) adapter.getCurrentView()
				.findViewById(R.id.optionsM);
		int total = view.getChildCount();
		List<Integer> answer = new ArrayList<Integer>();
		for (int i = 0; i < total; i++) {
			CheckBox cb = (CheckBox) view.getChildAt(i);
			if (cb.isChecked()) {
				answer.add(i);
			}
		}
		question.setUserAnswer(answer.toArray(new Integer[0]));
		questionItemViewCreator.showAnswer((View) view.getParent().getParent(),
				question, false);
		if (question.isCorrected()) {
			viewPagerSupport.next();
		} else {
			question.setErrored(true);
			questionDAO.updateQuestion(question);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dt.android.activity.BaseActivity#updateUIForSelectedItem()
	 */
	@Override
	public void updateUIForSelectedItem() {
		Question question = adapter.getCurrentItemData(viewPager
				.getCurrentItem());
		if (isVedio(question.getImage())) {

			View selectedView = viewPager.findViewWithTag(viewPager
					.getCurrentItem());// viewPager.getCurrentItem()
			if (selectedView != null) {
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
		updateStarBtn();
	}

	private boolean isVedio(String image) {
		return image.endsWith(".mp4");
	}
}
