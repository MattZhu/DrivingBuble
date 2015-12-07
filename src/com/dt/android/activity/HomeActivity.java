package com.dt.android.activity;

import java.util.Iterator;
import java.util.Map.Entry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.AppData;
import com.dt.android.domainobject.OpData;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;

public class HomeActivity extends BaseMainActivity implements
		ListItemViewCreator<OpData>, OnClickListener {

	private ImageView qtypeBtn;

	public void onCreate(Bundle savedInstanceState) {
		OpData[] data = new OpData[6];
		data[0] = new OpData(R.drawable.practice_seq, R.string.practice_seq,
				QuestionActivity.class);
		data[0].addExtra("title", R.string.practice_seq);
		data[1] = new OpData(R.drawable.practice_cha, R.string.practice_cha,
				ChapterListActivity.class);
		data[2] = new OpData(R.drawable.simulate_test, R.string.simulate_test,
				SimulateTestActivity.class);
		data[2].addExtra("title", R.string.simulate_test);
		data[3] = new OpData(R.drawable.school_query, R.string.school_query,
				SchoolQueryActivity.class);
		data[3].addExtra("queryschool", 1);
		data[4] = new OpData(R.drawable.regist, R.string.regist,
				SchoolQueryActivity.class);
		data[4].addExtra("queryschool", 0);
		data[5] = new OpData(R.drawable.consulting, R.string.consulting,
				ExamVedioActivity.class);

		OpData[] dataDown = new OpData[4];
		dataDown[0] = new OpData(R.drawable.drivertest_gl,
				R.string.drivertest_gl, SkillActivity.class);
		dataDown[1] = new OpData(R.drawable.error_q, R.string.error_q,
				ChapterListActivity.class);
		dataDown[1].addExtra("title", R.string.error_q);
		dataDown[1].addExtra("type", 2);
		dataDown[2] = new OpData(R.drawable.question_sc, R.string.question_sc,
				ChapterListActivity.class);
		dataDown[2].addExtra("title", R.string.question_sc);
		dataDown[2].addExtra("type", 1);
		dataDown[3] = new OpData(R.drawable.school_sc, R.string.school_sc,
				StaredSchoolActivity.class);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.practice);
//		findViewById(R.id.titleImg).setVisibility(View.GONE);
//		TextView titleTxt=(TextView)findViewById(R.id.titletext);
//		titleTxt.setVisibility(View.VISIBLE);
//		titleTxt.setText(R.string.app_name);
		qtypeBtn = (ImageView) this.findViewById(R.id.qtypeBtn);
		qtypeBtn.setOnClickListener(this);
		updateKemu();
		//
		GridView grid = (GridView) this.findViewById(R.id.op_grid);
		ListAdapter<OpData> adapter = new ListAdapter<OpData>(this, this);
		adapter.setData(data);
		grid.bringToFront();
		grid.setAdapter(adapter);
		grid.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				OpData opData = (OpData) arg0.getAdapter().getItem(arg2);
				if (opData.getClazz() != null) {
					Intent i = new Intent(HomeActivity.this, opData.getClazz());
					if (opData.getExtras() != null) {
						Iterator<Entry<String, Integer>> it = opData
								.getExtras().entrySet().iterator();
						Entry<String, Integer> entry;
						while (it.hasNext()) {
							entry = it.next();
							i.putExtra(entry.getKey(), entry.getValue());
						}
					}
					startActivity(i);
				}
			}
		});

		GridView grid_down = (GridView) findViewById(R.id.op_grid_down);
		ListAdapter<OpData> adapter_down = new ListAdapter<OpData>(this, this);
		adapter_down.setData(dataDown);
		grid_down.setAdapter(adapter_down);
		grid_down.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				OpData opData = (OpData) arg0.getAdapter().getItem(arg2);
				if (opData.getClazz() != null) {
					Intent i = new Intent(HomeActivity.this, opData.getClazz());
					if (opData.getExtras() != null) {
						Iterator<Entry<String, Integer>> it = opData
								.getExtras().entrySet().iterator();
						Entry<String, Integer> entry;
						while (it.hasNext()) {
							entry = it.next();
							i.putExtra(entry.getKey(), entry.getValue());
						}
					}
					startActivity(i);
				}
			}
		});

	}

	private void updateKemu() {
		if (AppData.getInstance().getqType() == 1) {
			qtypeBtn.setImageResource(R.drawable.kemu1);
		} else {
			qtypeBtn.setImageResource(R.drawable.kemu3);
		}
	}

	public View createOrUpdateView(OpData item, View convertView, int position,
			ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View result = null;
		switch (item.getImageRes()) {
		case R.drawable.practice_seq:
		case R.drawable.practice_cha:
		case R.drawable.simulate_test:
		case R.drawable.school_query:
		case R.drawable.regist:
		case R.drawable.consulting:
			result = inflater.inflate(R.layout.view_op, null);
			updateView(item, result);
			break;
		case R.drawable.drivertest_gl:
		case R.drawable.error_q:
		case R.drawable.school_sc:
		case R.drawable.question_sc:
			result = inflater.inflate(R.layout.view_h, null);
			updateView(item, result);
			break;
		}

		return result;
	}

	private void updateView(OpData item, View result) {
		ImageView img;
		TextView txt;
		img = (ImageView) result.findViewById(R.id.img);
		img.setImageResource(item.getImageRes());
		txt = (TextView) result.findViewById(R.id.txt);
		txt.setText(item.getStringId());
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.qtypeBtn:
			Intent i = new Intent(this, SelectQuesTypeActivity.class);

			startActivityForResult(i, 1);
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			if(resultCode==Activity.RESULT_OK){
				updateKemu();
			}
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	// @Override
	// protected Dialog onCreateDialog(int id) {
	// switch(id){
	// case 1:
	// return createQTypeDialog();
	// }
	// return super.onCreateDialog(id);
	// }
	//
	// private Dialog createQTypeDialog() {
	// AlertDialog.Builder builder = new AlertDialog.Builder(this);
	//
	// builder.setTitle(R.string.select_q_type);
	// int selected=0;
	// if(AppData.getInstance().getqType()==1){
	// selected=0;
	// }else{
	// selected=1;
	// }
	// builder.setSingleChoiceItems(R.array.q_type,selected, new
	// DialogInterface.OnClickListener() {
	//
	// public void onClick(DialogInterface dialog, int which) {
	// if(which==0){
	// AppData.getInstance().setqType(1);
	// }else{
	// AppData.getInstance().setqType(3);
	// }
	// dbadapter.getAppConfDAO().updateAppConf(
	// "question.type",AppData.getInstance().getqType()+"");
	//
	//
	// removeDialog(1);
	// }
	//
	// });
	//
	// return builder.create();
	// }

}
