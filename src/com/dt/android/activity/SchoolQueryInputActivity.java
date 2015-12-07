package com.dt.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.dt.android.R;

public class SchoolQueryInputActivity extends BackBaseActivity implements OnClickListener {
	
	private EditText keyword;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.school_query_input);
		findViewById(R.id.titleImg).setVisibility(View.GONE);

		TextView title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText(R.string.school_query);
		this.rightBtnIsCarType = false;
		keyword=(EditText)findViewById(R.id.queryKey);
		findViewById(R.id.rightbtn).setVisibility(View.GONE);
		
		findViewById(R.id.query_btn).setOnClickListener(this);
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.query_btn:
			Intent data = new Intent();
			data.putExtra("search.keyword",keyword.getText().toString());
			this.setResult(RESULT_OK, data);			
			this.finish();
			break;
		}
		
	}
	
	
}
