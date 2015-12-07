package com.dt.android.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.dt.android.db.Constants;
import com.dt.android.serverapi.message.School;

public class SchoolDAO extends BaseDAO implements Constants {

	public void saveStaredSchool(School school, boolean state) {
		if (state) {
			ContentValues values = new ContentValues();
			values.put("SCHOOL_ID", school.getSchool_id());
			values.put("NAME", school.getName());
			values.put("DISTANCE", school.getDistance());
			
			values.put("FPHONE", school.getFphone());
			values.put("MPHONE", school.getMphone());
			values.put("ADDRESS", school.getAddress());
			values.put("PRICE", school.getPrice());
			values.put("PICTURE", school.getPicture());
			values.put("LATITUDE", school.getLatitude());
			values.put("LONGTITUDE", school.getLongtitude());
			values.put("DESCRIPTION", school.getDescription());
			values.put("AREAID", school.getAreaid());
			db.insert(TABLE_STARED_SCHOOL, null, values);
		} else {
			db.delete(TABLE_STARED_SCHOOL, "SCHOOL_ID = " + school.getSchool_id(), null);
		}
	}

	public List<School>getStared(){
		Cursor c = db.query(TABLE_STARED_SCHOOL, new String[] { "SCHOOL_ID","NAME", "DISTANCE","FPHONE",
				"MPHONE","ADDRESS","PRICE","PICTURE","LATITUDE","LONGTITUDE","DESCRIPTION","AREAID"},
				null, null, null, null, null);
		List<School>result=new ArrayList<School>();
		try {
			while (c.moveToNext()) {
				School school=new School();
				school.setSchool_id(c.getInt(0));
				school.setName(c.getString(1));
				school.setDistance(c.getString(2));
				school.setFphone(c.getString(3));
				school.setMphone(c.getString(4));
				school.setAddress(c.getString(5));
				school.setPrice(c.getString(6));
				school.setPicture(c.getString(7));
				school.setLatitude(c.getFloat(8));
				school.setLongtitude(c.getFloat(9));
				school.setDescription(c.getString(10));
				school.setAreaid(c.getInt(11));
				result.add(school);
			}
			return result;
		} finally {
			c.close();
		}
	}
	
	public void clearAll(){
		db.delete(TABLE_STARED_SCHOOL, null, null);
	}
	
	public boolean isStared(Integer id) {
		Cursor c = db.query(TABLE_STARED_SCHOOL, new String[] { "SCHOOL_ID" },
				"SCHOOL_ID = " + id, null, null, null, null);
		try {
			if (c.moveToNext()) {
				return true;
			} else {
				return false;
			}
		} finally {
			c.close();
		}
	}
}
