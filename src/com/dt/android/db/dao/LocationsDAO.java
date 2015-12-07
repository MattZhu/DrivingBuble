package com.dt.android.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.dt.android.db.Constants;
import com.dt.android.domainobject.CountyCode;

public class LocationsDAO extends AppConfDAO implements Constants {

	public void updateCurrentLocation(String location) {
		updateAppConf("current.location", location);
	}

	public CountyCode getCurrentLocation() {
		Cursor c = db.query(TABLE_APP_CONF, new String[] { "pvalue" },
				"pkey='current.location'", null, null, null, null);
		CountyCode result = null;
		if (c.moveToNext()) {
			String locationID = c.getString(0);
			result = this.getProvince(locationID);
		}
		return result;
	}

	public CountyCode getProvinceByName(String name) {
		Cursor c = db.query(TABLE_COUNTY_CODE, new String[] { "id", "name",
				"is_municipality" }, "name ='" + name + "'", null, null, null,
				"id");
		if (c.moveToNext()) {
			CountyCode p = new CountyCode();
			p.setId(c.getInt(0));
			p.setName(c.getString(1));
			p.setMunicipality(c.getInt(2) == 1);
			return p;
		}
		c.close();
		return null;
	}

	public CountyCode getProvince(String id) {
		Cursor c = db.query(TABLE_COUNTY_CODE, new String[] { "id",
				"name", "is_municipality","parentid" }, "id =" + id, null, null,
				null, null);
		int pid=0;
		if (c.moveToNext()) {
			CountyCode p = new CountyCode();
			p.setId(c.getInt(0));
			p.setName(c.getString(1));
			p.setMunicipality(c.getInt(2) == 1);
			pid=c.getInt(3);
			c.close();
			if(pid!=0){
				p.setParent(this.getProvince(pid+""));
			}
			
			return p;
		}else{
			c.close();
		}
		
		c.close();
		return null;
	}

	public List<CountyCode> getCountyCodeByParent(CountyCode parent) {
		List<CountyCode> result = new ArrayList<CountyCode>();
		Cursor c = db.query(TABLE_COUNTY_CODE, new String[] { "id",
				"name", "is_municipality" }, "parentid=" + (parent==null?0:parent.getId()), null,
				null, null, "id");
		while (c.moveToNext()) {
			CountyCode p = new CountyCode();
			p.setId(c.getInt(0));
			p.setName(c.getString(1));
			p.setMunicipality(c.getInt(2) == 1);
			p.setParent(parent);
			result.add(p);
		}
		c.close();
		return result;
	}

	public CountyCode getCity(String[] location) {

		String province = location[1];
		String city = location[2];
		CountyCode p = this.getProvinceByName(province);
		if (p.isMunicipality()) {

			return p;
		} else {
			CountyCode c= this.getProvinceByName(city);
			c.setParent(p);
			return c;

		}
	}

}
