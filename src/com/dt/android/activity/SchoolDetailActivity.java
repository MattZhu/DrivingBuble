package com.dt.android.activity;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.dt.android.R;
import com.dt.android.db.DBAdapter;
import com.dt.android.domainobject.AppData;
import com.dt.android.serverapi.JsonApi;
import com.dt.android.serverapi.message.RequestData;
import com.dt.android.serverapi.message.School;
import com.dt.android.serverapi.message.SchoolDetailResponse;
import com.dt.android.utils.AsyncTaskEx;
import com.dt.android.utils.ImageLoadTask;
import com.dt.android.view.listener.BackButtonClickListener;

public class SchoolDetailActivity extends TabActivity implements
		OnTabChangeListener, OnClickListener, OnCancelListener {

	private TabHost tabHost;

	private School school;

	protected DBAdapter dbadapter;

	private boolean stared;

	private Button startBtn;

	protected AsyncTaskEx task;

	private final String encoding = "utf-8";

	protected boolean dialogCanceled = false;

	private final String TAG = "SchoolDetailActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schooldetail);
		findViewById(R.id.titleImg).setVisibility(View.GONE);

		TextView title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText("驾校信息");

		school = (School) this.getIntent().getSerializableExtra("school");
		dbadapter = new DBAdapter(this);
		dbadapter.open();
		stared = dbadapter.getSchoolDAO().isStared(school.getSchool_id());
		showDialog(1);

		task = (new AsyncTaskEx<String, Void, SchoolDetailResponse>() {

			@Override
			protected SchoolDetailResponse doInBackground(String... params) {
				JsonApi<SchoolDetailResponse> api = new JsonApi<SchoolDetailResponse>(
						SchoolDetailResponse.class);
				RequestData datas = new RequestData();
				datas.set("api_name", "get_school");
				datas.set("school_id", school.getSchool_id());
				SchoolDetailResponse resposne = api.post(datas);

				return resposne;
			}

			@Override
			protected void onPostExecute(SchoolDetailResponse result) {
				task = null;
				if (!dialogCanceled) {
					dismissDialog(1);
				}
				if (result != null) {
					school = result.getContent();
					updateUI();
				} else {
					Toast.makeText(SchoolDetailActivity.this, "加载驾校信息失败，请稍后重试",
							Toast.LENGTH_SHORT).show();
				}
			}

		}).execute();

		Button btn = (Button) findViewById(R.id.leftbtn);
		btn.setOnClickListener(new BackButtonClickListener(this));

		btn = (Button) findViewById(R.id.registe_btn);
		btn.setOnClickListener(this);

		startBtn = (Button) findViewById(R.id.rightbtn);
		startBtn.setOnClickListener(this);
		updateStarBtn();

		tabHost = getTabHost(); // The activity TabHost

		tabHost.getTabWidget().setBackgroundColor(Color.LTGRAY);
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		TextView menuText;

		View menuItem = inflater.inflate(R.layout.sc_menu, null);
		menuText = (TextView) menuItem.findViewById(R.id.menuText);
		menuText.setText("在线报名");
		spec = tabHost.newTabSpec("tab1").setIndicator(menuItem);
		spec.setContent(R.id.tab1);
		tabHost.addTab(spec);

		menuItem = inflater.inflate(R.layout.sc_menu, null);
		menuText = (TextView) menuItem.findViewById(R.id.menuText);
		menuText.setText("驾校简介");
		spec = tabHost.newTabSpec("tab2").setIndicator(menuItem);
		spec.setContent(R.id.tab2);
		tabHost.addTab(spec);

		menuItem = inflater.inflate(R.layout.sc_menu, null);
		menuText = (TextView) menuItem.findViewById(R.id.menuText);
		menuText.setText("班车路线");
		spec = tabHost.newTabSpec("tab3").setIndicator(menuItem);
		spec.setContent(R.id.tab3);
		tabHost.addTab(spec);

		tabHost.setOnTabChangedListener(this);

	}

	private void updateUI() {
		TextView name = (TextView) findViewById(R.id.schoolname);
		name.setText(school.getName());
		TextView price = (TextView) findViewById(R.id.school_price);
		if (school.getPrice() != null & school.getPrice().length() > 0) {
			price.setText("￥" + school.getPrice());
		} else {
			price.setText("");
		}
		
		View zizhiwarp= findViewById(R.id.zizhiwarp);
		TextView zizhiStatus=(TextView) findViewById(R.id.zizhi_status);
		ImageView zizhiIcon =(ImageView) findViewById(R.id.zizhi_icon);
		if(school.getZizhi_status()!=null&&school.getZizhi_status().trim().length()>0){
			zizhiStatus.setText(school.getZizhi_status());
		}else{
			zizhiwarp.setVisibility(View.GONE);
		}
		View changdiwrap= findViewById(R.id.changdiwarp);
		TextView changdiStatus=(TextView) findViewById(R.id.changdi_status);
		ImageView changdiIcon =(ImageView) findViewById(R.id.changdi_icon);
		if(school.getChangdi_status()!=null&&school.getChangdi_status().trim().length()>0){
			changdiStatus.setText(school.getChangdi_status());
		}else{
			changdiwrap.setVisibility(View.GONE);
		}
		TextView shoolAddress = (TextView) findViewById(R.id.address);
		shoolAddress.setText(school.getAddress());
		TextView shoolPhone = (TextView) findViewById(R.id.phone_num);
		shoolPhone.setText(school.getFphone() + " " + school.getMphone());

		WebView registeNotes = (WebView) findViewById(R.id.rigiste_notes);
		WebView content = (WebView) findViewById(R.id.short_desc);
		WebView bus_route = (WebView) findViewById(R.id.bus_route);

		registeNotes.loadDataWithBaseURL("fake://not/needed", school.getBmxz(),
				"text/html", encoding, null);
		content.loadDataWithBaseURL("fake://not/needed", school.getContent(),
				"text/html", encoding, null);
		bus_route.loadDataWithBaseURL("fake://not/needed", school.getBclx(),
				"text/html", encoding, null);

		if (school.getPicture() != null
				&& school.getPicture().trim().length() > 0) {
			ImageView schoolImage = (ImageView) this
					.findViewById(R.id.school_icon);
			ImageLoadTask task = new ImageLoadTask(schoolImage, this);
			task.execute(school.getPicture(),
					AppData.SCHOOL_IMAGE_CACHE_FOLDER, school.getSchool_id()
							+ ".png");
		}
	}

	private void updateStarBtn() {
		if (stared) {
			startBtn.setBackgroundResource(R.drawable.star);
		} else {
			startBtn.setBackgroundResource(R.drawable.un_star);
		}
	}

	public void onTabChanged(String tabId) {
		if (tabId.equals("tab1")) {

		}

	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 1:
			return createProgressDialog();
		}
		return super.onCreateDialog(id);
	}

	private Dialog createProgressDialog() {
		ProgressDialog d = ProgressDialog.show(this, "", "正在加载驾校信息，请稍等", true);
		d.setContentView(R.layout.progress_dialog);
		d.setCancelable(true);
		d.setOnCancelListener(this);
		return d;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.registe_btn:
			Intent i = new Intent(this, SignupActivity.class);
			i.putExtra("school", school);
			this.startActivity(i);
			break;
		case R.id.rightbtn:
			stared = !stared;

			dbadapter.getSchoolDAO().saveStaredSchool(school, stared);
			updateStarBtn();
			if (stared) {
				Toast.makeText(this, "驾校已收藏", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(this, "驾校收藏已取消", Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	protected void onDestroy() {
		dbadapter.close();
		super.onDestroy();
	}

	public void onCancel(DialogInterface dialog) {
		dialogCanceled = true;
		if (task != null) {
			task.cancel(true);
		}
	}
}
