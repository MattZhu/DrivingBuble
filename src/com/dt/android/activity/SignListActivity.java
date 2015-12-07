package com.dt.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.SignCategory;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;
import com.dt.android.view.listener.BackButtonClickListener;

public class SignListActivity extends BackBaseActivity implements
		ListItemViewCreator<SignCategory>, OnItemClickListener {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		// findViewById(R.id.titleText).setVisibility(View.VISIBLE);
		ImageView img = (ImageView) findViewById(R.id.titleImg);
		img.setImageResource(R.drawable.bz);
		Button btn = (Button) findViewById(R.id.leftbtn);
		btn.setBackgroundResource(R.drawable.btn_back);
		btn.setText("");
		btn.setOnClickListener(new BackButtonClickListener(this));
		Integer categoryId=this.getIntent().getIntExtra("categoryId", -1);
		ListView v = (ListView) findViewById(R.id.lists);
		ListAdapter<SignCategory> adapter = new ListAdapter<SignCategory>(this, this);
		adapter.setData(dbadapter.getSignDAO().getSignCategorys(categoryId).toArray(new SignCategory[0]));
		v.setAdapter(adapter);
		v.setOnItemClickListener(this);

	}

	public View createOrUpdateView(SignCategory item, View convertView, int position,ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.listitem_textwithcount, null);
		TextView t=(TextView)result.findViewById(R.id.title);
		t.setText(item.getName());
		 t=(TextView)result.findViewById(R.id.count);
		 t.setText(String.valueOf(item.getCount()));
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
	 * .AdapterView, android.view.View, int, long)
	 */
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		SignCategory item = (SignCategory) arg0.getAdapter().getItem(arg2);
		Intent intent = new Intent().setClass(this, SignContentActivity.class);
		intent.putExtra("categoryId", item.getId());
		this.startActivity(intent);
	}

}
