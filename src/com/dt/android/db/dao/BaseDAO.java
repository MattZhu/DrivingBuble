package com.dt.android.db.dao;

import android.database.sqlite.SQLiteDatabase;

public class BaseDAO {
	protected SQLiteDatabase db;

	/**
	 * @return the db
	 */
	public SQLiteDatabase getDb() {
		return db;
	}

	/**
	 * @param db the db to set
	 */
	public void setDb(SQLiteDatabase db) {
		this.db = db;
	}
	
}
