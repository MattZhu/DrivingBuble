package com.dt.android.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.serverapi.message.School;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;

public class StaredSchoolActivity extends BackBaseActivity implements
		ListItemViewCreator<School>, OnItemClickListener, OnClickListener {
	private ListAdapter<School> adapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.stared_school);
		ListView list = (ListView) findViewById(R.id.schoolList);
		adapter = new ListAdapter<School>(this, this);
		this.findViewById(R.id.titleImg).setVisibility(View.GONE);
		TextView titletxt=(TextView)findViewById(R.id.titletext);
		titletxt.setText(R.string.school_sc);
		titletxt.setVisibility(View.VISIBLE);
		Button btn = (Button) findViewById(R.id.btn_clear);
		btn.setOnClickListener(this);
		btn.setText("清空收藏");
		// api = new SchoolApi();
		// api.getSchools();
		getData();
		
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
	}

	private void getData() {
		
		List<School> stared = dbadapter.getSchoolDAO().getStared();
	
		adapter.setData(stared.toArray(new School[0]));
		if(adapter.getCount()==0){
			this.findViewById(R.id.nostared).setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		getData();
	}

	public View createOrUpdateView(School item, View convertView, int position,
			ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.school_item, null);
		TextView schoolname = (TextView) result.findViewById(R.id.schoolname);
		schoolname.setText(item.getName());
//		TextView price = (TextView) result.findViewById(R.id.school_price);
//		price.setText("￥" + item.getPrice().toString());
		TextView phone = (TextView) result.findViewById(R.id.phone_num);
		phone.setText(item.getFphone()+" "+item.getMphone());
		TextView address = (TextView) result.findViewById(R.id.address);
		address.setText(item.getAddress());
		return result;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		Intent i = new Intent(this, SchoolDetailActivity.class);
		i.putExtra("school", (School) arg0.getItemAtPosition(arg2));
		startActivity(i);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_clear:
			this.showDialog(CLEAR_STARED);
			break;
		}

	}

	protected String [] getClearMessage(){
		return new String[]{"清空驾校收藏","确认要清空驾校收藏？"};
	};	
	
	protected void doClear() {
		dbadapter.getSchoolDAO().clearAll();
		this.getData();
	}


	

}
