package com.dt.android.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.AppData;

public class SelectQuesTypeActivity extends BackBaseActivity implements
		OnClickListener, OnCheckedChangeListener {
	private int qType;
	private RadioButton kemu1;
	private RadioButton kemu3;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_qtype);
		this.rightBtnIsCarType = false;
		Button btn = (Button) this.findViewById(R.id.rightbtn);
		this.findViewById(R.id.titleImg).setVisibility(View.GONE);
		TextView title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText("题库设置");
		btn.setVisibility(View.INVISIBLE);
//		btn.setBackgroundResource(R.drawable.check_btn);
//		btn.setOnClickListener(this);
		qType = AppData.getInstance().getqType();
		kemu1 = (RadioButton) this.findViewById(R.id.kemu1);
		kemu1.setText(kemu1.getText()+"("+dbadapter.getQuestionDAO().getCountByType(1)+"题)");
		kemu3 = (RadioButton) this.findViewById(R.id.kemu3);
		kemu3.setText(kemu3.getText()+"("+dbadapter.getQuestionDAO().getCountByType(3)+"题)");
		RadioGroup radioGroup = (RadioGroup) this
				.findViewById(R.id.radioGroup1);	
		if (qType == 1) {
			kemu1.setChecked(true);
			kemu3.setChecked(false);
		} else {
			kemu1.setChecked(false);
			kemu3.setChecked(true);
		}
		radioGroup.setOnCheckedChangeListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rightbtn:
			AppData.getInstance().setqType(qType);
			dbadapter.getAppConfDAO().updateAppConf("question.type",
					AppData.getInstance().getqType() + "");
			this.setResult(RESULT_OK);
			this.finish();
			break;
		}

	}

	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.kemu1:
			qType = 1;
			AppData.getInstance().setqType(qType);
			dbadapter.getAppConfDAO().updateAppConf("question.type",
					AppData.getInstance().getqType() + "");
			this.setResult(RESULT_OK);
			this.finish();
			break;
		case R.id.kemu3:
			qType = 3;
			AppData.getInstance().setqType(qType);
			dbadapter.getAppConfDAO().updateAppConf("question.type",
					AppData.getInstance().getqType() + "");
			this.setResult(RESULT_OK);
			this.finish();
			break;
		}
	}
}
