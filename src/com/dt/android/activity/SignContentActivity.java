package com.dt.android.activity;

import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt.android.R;
import com.dt.android.domainobject.SignContent;
import com.dt.android.utils.ViewPagerAdapter;
import com.dt.android.utils.ViewPagerSupport;
import com.dt.android.view.ListItemViewCreator;

public class SignContentActivity extends BackBaseActivity implements
		ListItemViewCreator<SignContent> {

	private ViewPager viewPager;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		ImageView img = (ImageView) findViewById(R.id.titleImg);
		img.setImageResource(R.drawable.bz);
		viewPager = (ViewPager) findViewById(R.id.pager);

		Integer categoryId=this.getIntent().getIntExtra("categoryId", -1);
		ViewPagerAdapter<SignContent> adapter = new ViewPagerAdapter<SignContent>();
		adapter.setData(this.dbadapter.getSignDAO().getSignContent(categoryId).toArray(new SignContent[0]));
		viewPager.setAdapter(adapter);
		adapter.setQuestionItemViewCreator(this);
		ViewPagerSupport viewPagerSupport=new ViewPagerSupport(this,viewPager);
		viewPagerSupport.init();
//		viewPager.setNoLeft("已是第一张!");
//		viewPager.setNoRight("已是最后一张!");
		viewPagerSupport.setNoLeft("已是第一张!");
		viewPagerSupport.setNoRight("已是最后一张!");
	}

	public View createOrUpdateView(SignContent item, View convertView, int position,ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View result = inflater.inflate(R.layout.sign_content, null);
		TextView t = (TextView) result.findViewById(R.id.sign_content_desc);
		t.setText(item.getName());
		
		ImageView image=(ImageView)result.findViewById(R.id.sign_content_img);
		try {
			InputStream is=SignContentActivity.class.getResourceAsStream("/assets/signs/"+item.getCategoryId()+"/"+item.getPicture());
			Bitmap bm = BitmapFactory.decodeStream(is);
			image.setImageBitmap(bm);
		} catch (Exception e) {
			Log.e("SignContent","can't load image for "+item);
		}
		return result;
	}

}
