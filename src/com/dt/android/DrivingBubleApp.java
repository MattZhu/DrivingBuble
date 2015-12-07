package com.dt.android;

import android.app.Application;
import android.app.UiModeManager;
import android.content.Context;
import android.util.Log;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.dt.android.db.DBAdapter;
import com.dt.android.domainobject.AppData;

public class DrivingBubleApp extends Application {
	private DBAdapter dbAdapter;
	private BMapManager mBMapMan;

	public void onCreate() {
		super.onCreate();
		dbAdapter = new DBAdapter(this);
		dbAdapter.open();
		AppData.getInstance().setCurrentLocation(
				dbAdapter.getLocationsDAO().getCurrentLocation());
		AppData.getInstance().setNightMode(
				dbAdapter.getAppConfDAO().getNightMode());
		AppData.getInstance().setShowIntro(
				Boolean.valueOf(dbAdapter.getAppConfDAO().getAppConf("showIntro")));
		try {
			AppData.getInstance().setCarType(
					Integer.valueOf(dbAdapter.getAppConfDAO().getAppConf(
							"car.type")));
			AppData.getInstance().setqType(Integer.valueOf(dbAdapter.getAppConfDAO().getAppConf(
							"question.type")));
			
		} catch (NumberFormatException e) {
			AppData.getInstance().setCarType(0);
		}
		AppData.getInstance().setAppVersion(dbAdapter.getAppConfDAO().getAppConf("app.version"));
		AppData.getInstance().setDataVersion(dbAdapter.getAppConfDAO().getAppConf("data.version"));
		UiModeManager uiModeManager = (UiModeManager) this
				.getSystemService(Context.UI_MODE_SERVICE);

		if (AppData.getInstance().isNightMode()) {
			uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
		} else {
			uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
		}
		dbAdapter.close();

		mBMapMan = new BMapManager(this);
		mBMapMan.init("464F394E69E6E0CAA21D68B9C33A90714664120E",
				new MKGeneralListener() {

					public void onGetNetworkState(int arg0) {
						Log.d("SelectCityActivity", "Network State:" + arg0);
					}

					public void onGetPermissionState(int arg0) {
						Log.d("SelectCityActivity", "onGetPermission State:"
								+ arg0);
					}

				});
	}
	
	@Override
	public void onTerminate() {
		if (this.mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}
	
	public BMapManager getmBMapMan() {
		return mBMapMan;
	}



}
