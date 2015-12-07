package com.dt.android.activity;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.Question;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;

public class SimulateTestGridActivity extends BackBaseActivity implements
		ListItemViewCreator<Question>, OnItemClickListener {

	private TextView title;
	private int type;

	private List<Question> questionList;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_grid);

		findViewById(R.id.titleImg).setVisibility(View.GONE);
		findViewById(R.id.rightbtn).setVisibility(View.GONE);
		Integer titleRes = getIntent().getIntExtra("title",
				R.string.check_not_answered);
		title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText(titleRes);

		type = getIntent().getIntExtra("type", CHECK_NOT_ANSWERED);
		this.rightBtnIsCarType=false;
		questionList = (List<Question>) getIntent().getExtras()
				.getSerializable("questions");

		GridView grid = (GridView) this.findViewById(R.id.question_grid);

		ListAdapter<Question> adapter = new ListAdapter<Question>(this, this);
		adapter.setData(questionList.toArray(new Question[0]));
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(this);

	}

	public View createOrUpdateView(Question item, View convertView,
			int position, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView result = (TextView) inflater.inflate(R.layout.answer_item,
				parent, false);

		result.setText(String.valueOf(position + 1));
		if (this.type == 1) {
			if (item.isCorrected()) {
				result.setBackgroundColor(Color.GREEN);
			} else {
				if (item.isAnswered()) {
					result.setBackgroundColor(Color.RED);
				} else {
					result.setBackgroundColor(Color.GRAY);
				}
			}
		} else {
			if (item.isAnswered()) {
				result.setBackgroundColor(Color.GRAY);
			} else {
				result.setBackgroundColor(Color.WHITE);
			}
		}
		return result;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if(type==0){
			Intent data = new Intent();
			data.putExtra("question.id", arg2);
			this.setResult(RESULT_OK, data);
			this.finish();
		}else{
			Intent i= new Intent(this,QuestionActivity.class);
			i.putExtra("questions", (Serializable)questionList);
			i.putExtra("title", R.string.check_answered);
			i.putExtra("check", true);
			i.putExtra("question.id", arg2);
			startActivity(i);
		}
	}
}
