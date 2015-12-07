package com.dt.android.activity;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.Question;

public class SimulateTestSummary extends BackBaseActivity implements
		OnClickListener {
	private List<Question> questions;
	private TextView title;
	
	private boolean pass;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simuate_test_summary);

		questions = (List<Question>) this.getIntent().getExtras()
				.getSerializable("questions");
		findViewById(R.id.titleImg).setVisibility(View.GONE);
		findViewById(R.id.rightbtn).setVisibility(View.GONE);
		title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText(R.string.test_summray);

		TextView score = (TextView) findViewById(R.id.score);
		score.setText(getScore());
		if(pass){
			TextView detail = (TextView) findViewById(R.id.summary_Detail);
			detail.setText(R.string.exam_pass);
			TextView titletx = (TextView) findViewById(R.id.summary_title);
			titletx.setText(R.string.exam_pass_title);
			titletx.setTextColor(Color.BLUE);
		}
		TextView time = (TextView) findViewById(R.id.time);
		time.setText(formatTime(getIntent().getIntExtra("time", -1)));
		this.rightBtnIsCarType=false;
		View checkAnser = findViewById(R.id.checkAnswer);
		checkAnser.setOnClickListener(this);
	}

	private CharSequence getScore() {

		int score = 0;
		for (Question q : questions) {
			if (q.isCorrected()) {
				score++;
			}
		}
		pass=(score>=90);
		return score + "/" + questions.size();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.checkAnswer:
			
			Intent i = new Intent(this, SimulateTestGridActivity.class);
			i.putExtra("type", VIEW_ANSWER);
			i.putExtra("questions", (Serializable) questions);
			i.putExtra("title", R.string.check_answered);
			this.startActivityForResult(i, 1);
			break;
		}

	}

}
