package com.dt.android.activity;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.dt.android.DrivingBubleApp;
import com.dt.android.R;
import com.dt.android.serverapi.GeoCoderClient;
import com.dt.android.serverapi.JsonApi;
import com.dt.android.serverapi.MyMKSearchListener;
import com.dt.android.serverapi.message.RequestData;
import com.dt.android.serverapi.message.Response;
import com.dt.android.serverapi.message.School;
import com.dt.android.utils.AsyncTaskEx;

public class SignupActivity extends BackBaseActivity implements OnClickListener {
	private School school;
	private JsonApi<Response> api;

	private Object lock = new Object();
	private Location newLocation;
	private LocationManager locationManager;
	private boolean locatinListnerRigested;
	private LocationProvider provider;
	private String locationProvider;
	private BMapManager mBMapMan;
	private MKSearch mMKSearch;
	private MKAddrInfo mkAddrInfo;
	private Object lockBaiDu = new Object();
	private int isTg =0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		findViewById(R.id.titleImg).setVisibility(View.GONE);
		TextView title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText(R.string.regist);
		this.rightBtnIsCarType = false;
		Button rButton = (Button) findViewById(R.id.rightbtn);
		rButton.setVisibility(View.GONE);
		// rButton.setText("报名");
		// rButton.setOnClickListener(this);
		Button btn = (Button) this.findViewById(R.id.submit);
		btn.setOnClickListener(this);

		school = (School) this.getIntent().getSerializableExtra("school");
		isTg= getIntent().getIntExtra("tg", 0);
		if (school != null) {
			findViewById(R.id.school_area).setVisibility(View.VISIBLE);
			EditText schoolName = (EditText) findViewById(R.id.school_name);
			schoolName.setText(school.getName());
			schoolName.setEnabled(false);
		}

		mBMapMan = ((DrivingBubleApp) getApplication()).getmBMapMan();
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapMan, new MyMKSearchListener() {

			@Override
			public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
				Log.d("SelectCityActivity", "onGetAddrResult:" + arg0);
				if (arg1 == 0) {
					mkAddrInfo = arg0;
				}
				synchronized (lockBaiDu) {
					lockBaiDu.notify();
				}
			}

		});

		api = new JsonApi<Response>(Response.class);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.submit:
		case R.id.rightbtn:
			submit();
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.dt.android.activity.BaseActivity#getProgressMessage()
	 */
	@Override
	protected String getProgressMessage() {
		return "正在提交信息，请稍等...";
	}

	private void submit() {
		if(!isConnected()){
			this.showDialog(NO_NETWORK);
			return;
		}
		if (checkRequired()) {
			final RequestData data = getDatas();
			preGetAddress();
			task = new AsyncTaskEx<String, Void, Response>() {

				@Override
				protected Response doInBackground(String... params) {
					String address = getAddress();
					if (address != null) {
						data.set("get_address", address);
					}
					data.set("api_name", "sign_up");
					data.set("os", "2");
					data.set("is_tg", isTg);
					return api.post(data);
				}

				@Override
				protected void onCancelled() {
					showToast("取消了提交报名信息。");
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * com.dt.android.utils.AsyncTaskEx#onPostExecute(java.lang.
				 * Object)
				 */
				@Override
				protected void onPostExecute(Response result) {
					dismissDialog(PROGRESS_DIALOG);
					if (locatinListnerRigested) {
						locationManager.removeUpdates(locationListener);
					}

					if (result != null && result.getStatus_code() == 1) {
						showToast("您的报名已提交，感谢您对学车通的支持。");
						finish();
					} else {
						showToast("提交报名信息失败，请重试");
					}
				}

			}.execute();
		}
	}

	private boolean checkRequired() {
		boolean result=true;
		EditText text = (EditText) findViewById(R.id.name);
		if(text.getEditableText().toString().length()==0){
			text.setError("请输入您的名字");
			result=false;
		};


		text = (EditText) findViewById(R.id.phone_num);
		if(text.getEditableText().toString().length()==0){
			text.setError("请输入您的联系电话");
			result=false;
		};
		text = (EditText) findViewById(R.id.address);
		if(text.getEditableText().toString().length()==0){
			text.setError("请输入您的地址");
			result=false;
		};
		return result;
	}

	private void preGetAddress() {
		showDialog(PROGRESS_DIALOG);
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(false);
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		locationProvider = locationManager.getBestProvider(criteria, true);
		if (!locatinListnerRigested && locationProvider != null) {
			locationManager.requestLocationUpdates(locationProvider, 0, 0,
					locationListener);
			locatinListnerRigested = true;
		}
	}

	private String getAddress() {

		if (locationProvider == null) {
			return null;
		}
		provider = locationManager.getProvider(locationProvider);
		Location location = locationManager.getLastKnownLocation(provider
				.getName());

		if (location == null) {
			if (newLocation == null) {
				synchronized (lock) {
					try {
						lock.wait(120000);

					} catch (InterruptedException e) {
						Log.d("SignupActivity", e.getMessage());
					}
				}
				location = newLocation;
				if (location == null) {
					return null;
				}
			} else {
				location = newLocation;
			}
		}
		GeoPoint arg0 = new GeoPoint((int) (location.getLatitude() * 1000000),
				(int) (location.getLongitude() * 1E6));
		int r = mMKSearch.reverseGeocode(arg0);
		Log.d("SelectCityActivity", "Request " + arg0
				+ ",reverseGeocode response:" + r);
		if (r == 0) {
			synchronized (lockBaiDu) {
				try {
					Log.d("SelectCityActivity",
							"Waiting Bai reverse address...");
					lockBaiDu.wait(120000);
				} catch (InterruptedException e) {
					Log.d("", e.getMessage());
				}
			}
			if (mkAddrInfo != null) {
				return mkAddrInfo.strAddr;
			} else {
				return GeoCoderClient.fromLocationAddress(location);
			}
		} else {
			return GeoCoderClient.fromLocationAddress(location);
		}
	}

	@Override
	protected void onPause() {
		if (mBMapMan != null) {
			mBMapMan.stop();
		}
		super.onPause();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (mBMapMan != null) {
			mBMapMan.start();
		}
	}

	private LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the network location
			// provider.
			newLocation = location;
			synchronized (lock) {
				lock.notify();
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}
	};

	private RequestData getDatas() {
		RequestData data = new RequestData();
		if (school != null) {
			data.set("school_id", school.getSchool_id());
		}
		EditText text = (EditText) findViewById(R.id.name);
		data.set("name", text.getEditableText().toString());

		RadioGroup rg = (RadioGroup) findViewById(R.id.sex);
		int sex = getSex(rg);
		data.set("sex", sex);

		text = (EditText) findViewById(R.id.phone_num);
		data.set("phonenumber", text.getEditableText().toString());

		text = (EditText) findViewById(R.id.address);
		data.set("address", text.getEditableText().toString());

		text = (EditText) findViewById(R.id.notes);
		data.set("beizhu", text.getEditableText().toString());

		return data;
	}

	private int getSex(RadioGroup rg) {
		int sex = 1;
		switch (rg.getCheckedRadioButtonId()) {
		case R.id.man:
			sex = 1;
			break;
		case R.id.woman:
			sex = 2;
			break;
		}
		;
		return sex;
	}
}
