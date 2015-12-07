package com.dt.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.SignCategory;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;

public class SignActivity extends BaseMainActivity implements
		ListItemViewCreator<SignCategory>, OnItemClickListener {
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.biaozhi);
		// findViewById(R.id.titleText).setVisibility(View.VISIBLE);
		ImageView img = (ImageView) findViewById(R.id.titleImg);
		img.setImageResource(R.drawable.bz);
		ListView v = (ListView) findViewById(R.id.biaozhilist);
		ListAdapter<SignCategory> adapter = new ListAdapter<SignCategory>(this,
				this);

		adapter.setData(dbadapter.getSignDAO().getSignCategorys(null)
				.toArray(new SignCategory[0]));
		v.setAdapter(adapter);

		v.setOnItemClickListener(this);
	}

	public View createOrUpdateView(SignCategory item, View convertView,
			int position, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.biaozhiitem, null);
		TextView catetoryTitle = (TextView) result
				.findViewById(R.id.category_title);
		catetoryTitle.setText(item.getName());

		catetoryTitle = (TextView) result.findViewById(R.id.sub_title);
		catetoryTitle.setText(item.getName());

		TextView count = (TextView) result.findViewById(R.id.sub_count);

		if (item.getCount() > 0) {
			count.setText("共" + item.getCount() + "张");
		} else {
			count.setVisibility(View.GONE);
		}

		ImageView img = (ImageView) result.findViewById(R.id.sub_image);
		if (item.getName().equals("交通标志图解")) {
			img.setImageResource(R.drawable.jtbz_sub_img);
		} else if (item.getName().equals("新版交警手势")) {
			img.setImageResource(R.drawable.jjss_sub_img);
		} else {
			img.setImageResource(R.drawable.sgdq_sub_img);
		}
		return result;
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		Intent intent = null;
		SignCategory item = (SignCategory) arg0.getAdapter().getItem(arg2);
		if (item.getDirectCount() == 0) {
			intent = new Intent().setClass(this, SignListActivity.class);
			intent.putExtra("categoryId", item.getId());
		} else {
			intent = new Intent().setClass(this, SignContentActivity.class);
			intent.putExtra("categoryId", item.getId());
		}
		this.startActivity(intent);
	}

}
