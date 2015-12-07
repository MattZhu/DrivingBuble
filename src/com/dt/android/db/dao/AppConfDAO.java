package com.dt.android.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.dt.android.db.Constants;

public class AppConfDAO  extends BaseDAO implements Constants{

	public void updateNightMode(boolean nightMode) {
		updateAppConf("nightmode", String.valueOf(nightMode));
	}

	public boolean getNightMode() {
		return Boolean.valueOf(getAppConf("nightmode"));
	}

	public String getAppConf(String key) {
		Cursor c = db.query(TABLE_APP_CONF, new String[] { "pvalue" },
				"pkey=?", new String[] { key }, null, null, null);
		String result = null;
		if (c.moveToNext()) {
			result = c.getString(0);
		}
		Log.d("AppConfDAO",key+"="+result);
		return result;
	}

	public void updateAppConf(String pkey, String pValue) {
		Log.d("AppConfDAO","update "+pkey+" value to "+pValue);
		ContentValues values = new ContentValues();
		values.put("pvalue", pValue);
		int result=db.update(TABLE_APP_CONF, values, "pkey='" + pkey + "'", null);
		if(result==0){
			values.put("pkey", pkey);
			db.insert(TABLE_APP_CONF, null, values);
		}
	}
	


	
}
