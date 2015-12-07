package com.dt.android.activity;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.dt.android.DrivingBubleApp;
import com.dt.android.R;
import com.dt.android.db.dao.LocationsDAO;
import com.dt.android.domainobject.AppData;
import com.dt.android.domainobject.City;
import com.dt.android.domainobject.CountyCode;
import com.dt.android.serverapi.GeoCoderClient;
import com.dt.android.serverapi.MyMKSearchListener;
import com.dt.android.utils.AsyncTaskEx;
import com.dt.android.utils.ListAdapter;
import com.dt.android.view.ListItemViewCreator;
import com.dt.android.view.listener.BackButtonClickListener;

public class SelectCityActivity extends BackBaseActivity implements
		OnItemClickListener, OnClickListener {

	private static final int STATE_PROVINCE = 1;
	private static final int STATE_CITY = 2;

	public static final int DIALOG_SETTING_LM_ID = 1;

	private int state;
	private CountyCode countyCode;

	private LocationsDAO locationsDAO;

	private BackButtonClickListener backListener = new BackButtonClickListener(
			this);
	private LocationManager locationManager;

	private ProgressDialog dialog;

	private CountyCode location;

	private Object lock = new Object();

	private Object lockBaiDu = new Object();

	private Location newLocation;

	private Integer errorRes;
	private boolean canceled =false;

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
	private boolean locatinListnerRigested = false;

	private MKSearch mMKSearch;
	private MKAddrInfo mkAddrInfo;
	private BMapManager mBMapMan;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mBMapMan=((DrivingBubleApp)getApplication()).getmBMapMan();
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

		state = STATE_PROVINCE;
		locationsDAO = dbadapter.getLocationsDAO();
		this.setContentView(R.layout.select_city);
		Button btn = (Button) findViewById(R.id.rightbtn);
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		btn.setText(R.string.autolacate);
		btn.setOnClickListener(this);
		findViewById(R.id.titleImg).setVisibility(View.GONE);
		TextView title = (TextView) findViewById(R.id.titletext);
		title.setVisibility(View.VISIBLE);
		title.setText(R.string.selectcity);

		this.rightBtnIsCarType = false;
		updateCurrentLocationUI();

		updateUI();
	}

	private void updateCurrentLocationUI() {
		TextView currentLoc = (TextView) findViewById(R.id.currentLoc);

		currentLoc.setText(AppData.getInstance().getCurrentLocation()
				.getLocation());
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

	

	private void updateUI() {
		List<CountyCode> provinces = locationsDAO
				.getCountyCodeByParent(countyCode);
		ListView list = (ListView) findViewById(R.id.cityList);
		ListAdapter<CountyCode> adapter = new ListAdapter<CountyCode>(this,
				new ListItemViewCreator<CountyCode>() {

					public View createOrUpdateView(CountyCode item,
							View convertView, int position, ViewGroup parent) {
						LayoutInflater inflater = (LayoutInflater) SelectCityActivity.this
								.getSystemService(LAYOUT_INFLATER_SERVICE);
						View result = inflater.inflate(R.layout.citylistitem,
								null);
						// if(position%2==0){
						// result.setBackgroundColor(Color.rgb(232, 232,
						// 232));
						// }else{
						// result.setBackgroundColor(Color.WHITE);
						// }
						TextView cityTxt = (TextView) result
								.findViewById(R.id.cityTxt);
						cityTxt.setText(item.getName());
						if (item.isMunicipality()) {
							result.findViewById(R.id.cityNext).setVisibility(
									View.GONE);
						}
						return result;
					}
				});
		adapter.setData(provinces.toArray(new CountyCode[0]));
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		updateBackBtn();
	}

	private void updateBackBtn() {

		Button btn = (Button) findViewById(R.id.leftbtn);
		if (state == STATE_PROVINCE) {
			btn.setOnClickListener(backListener);
		} else {
			btn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					state = STATE_PROVINCE;
					countyCode = null;
					updateUI();
				}
			});
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if (state == STATE_PROVINCE) {
			CountyCode p = (CountyCode) arg0.getAdapter().getItem(arg2);
			if (p.isMunicipality()) {
				City c = new City();
				c.setId(p.getId());
				c.setName(p.getName());
				c.setProvice(null);
				AppData.getInstance().setCurrentLocation(p);
				locationsDAO.updateCurrentLocation(String.valueOf(p.getId()));
				finish();
			} else {
				state = STATE_CITY;
				countyCode = p;
				updateUI();
			}
		} else {
			CountyCode p = (CountyCode) arg0.getAdapter().getItem(arg2);
			AppData.getInstance().setCurrentLocation(p);
			locationsDAO.updateCurrentLocation(String.valueOf(p.getId()));
			finish();
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rightbtn:
			autolocate();
			break;
		}

	}

	private void autolocate() {
		if(!isConnected()){
			this.showDialog(NO_NETWORK);
			return;
		}
		newLocation = null;
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_COARSE);
		// criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setCostAllowed(false);
		final String locationProvider = locationManager.getBestProvider(
				criteria, true);
		if (locationProvider == null) {
			showDialog(DIALOG_SETTING_LM_ID);
		} else {
			if (!locatinListnerRigested) {
				locationManager.requestLocationUpdates(locationProvider, 0, 0,
						locationListener);
				locatinListnerRigested = true;
			}
			task=new AsyncTaskEx<Void, Void, CountyCode>() {

				

				@Override
				protected CountyCode doInBackground(Void... params) {
					try {
						errorRes = -1;
						LocationProvider provider = locationManager
								.getProvider(locationProvider);

						Location location = locationManager
								.getLastKnownLocation(provider.getName());

						if (location == null) {
							if (newLocation == null) {
								synchronized (lock) {
									try {
										lock.wait(120000);

									} catch (InterruptedException e) {
										Log.d("", e.getMessage());
									}
								}
								location = newLocation;
								if (location == null) {
									errorRes = R.string.locationfailed;
									return null;
								}
							} else {
								location = newLocation;
							}
						}
						GeoPoint arg0 = new GeoPoint(
								(int) (location.getLatitude() * 1000000),
								(int) (location.getLongitude() * 1E6));
						int r = mMKSearch.reverseGeocode(arg0);
						Log.d("SelectCityActivity", "Request "+arg0+ ",reverseGeocode response:" + r);
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
						}
						String[] address = null;
						if (mkAddrInfo != null) {
							address = new String[3];
							address[1] = mkAddrInfo.addressComponents.province;
							address[2] = mkAddrInfo.addressComponents.city;
						} else {
							address = GeoCoderClient.fromLocation(location);
						}

						if (address != null) {
							CountyCode city = locationsDAO.getCity(address);
							return city;
						} else {
							errorRes = R.string.cant_parse_location;
							return null;
						}
					} catch (Exception e) {
						return null;
					}
				}

				/*
				 * (non-Javadoc)
				 * 
				 * @see
				 * com.dt.android.utils.AsyncTaskEx#onPostExecute(java.lang.
				 * Object)
				 */
				@Override
				protected void onPostExecute(CountyCode result) {
					if(!dialogCanceled){
						dismissDialog();
						location = result;
						SelectCityActivity.this.removeDialog(2);
						if (errorRes == R.string.cant_parse_location) {
	
							SelectCityActivity.this.showDialog(3);
						} else {
	
							SelectCityActivity.this.showDialog(2);
						}
					}
				}

			}.execute();
			dialog = ProgressDialog.show(this, "",
					getResources().getString(R.string.locatingstr), true);
			dialog.setContentView(R.layout.progress_dialog);
			dialog.setCancelable(true);
			dialog.setOnCancelListener(this);
		}
	}

	private void enableLocationSettings() {
		Intent settingsIntent = new Intent(
				Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivityForResult(settingsIntent, 1);
		startActivity(settingsIntent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			autolocate();
			break;
		default:
			super.onActivityResult(requestCode, resultCode, data);

		}
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_SETTING_LM_ID:
			return createDialog(R.string.autolacate, R.string.settinglocation);
		case 2:
			if (location != null) {
				return createDialog(R.string.currentlocation,
						location.getLocation());
			} else {
				return createDialog2(R.string.autolacate, errorRes);
			}

		case 3:
			return createDialog3(R.string.autolacate, errorRes);
		}
		return super.onCreateDialog(id);
	}

	private Dialog createDialog3(int titleId, int message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(titleId).setIcon(android.R.drawable.stat_sys_warning)
				.setMessage(message).setCancelable(false)
				.setPositiveButton(android.R.string.ok, null);
		return builder.create();
	}

	private Dialog createDialog2(int titleId, int message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(titleId)
				.setIcon(android.R.drawable.stat_sys_warning)
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								autolocate();
							}
						})
				.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								unregisterLocationListner();
							}
						});
		return builder.create();
	}

	private Dialog createDialog(int titleId, String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(titleId)
				.setIcon(android.R.drawable.stat_sys_warning)
				.setMessage(message)
				.setCancelable(false)
				.setPositiveButton(R.string.uselocation,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								AppData.getInstance().setCurrentLocation(
										location);
								unregisterLocationListner();
								updateCurrentLocationUI();
								locationsDAO.updateCurrentLocation(location
										.getId().toString());
							}
						}).setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	private Dialog createDialog(int titleId, int messageId) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(titleId)
				.setIcon(android.R.drawable.stat_sys_warning)
				.setMessage(messageId)
				.setCancelable(false)
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								enableLocationSettings();
							}
						}).setNegativeButton(android.R.string.cancel, null);
		return builder.create();
	}

	protected void dismissDialog() {
		try {
			if (dialog != null && dialog.isShowing()) {

				dialog.dismiss();
				dialog = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unregisterLocationListner() {
		if (locatinListnerRigested) {
			locationManager.removeUpdates(locationListener);
		}
	}
}
