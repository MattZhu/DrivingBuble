package com.dt.android.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import com.dt.android.db.Constants;
import com.dt.android.domainobject.SignCategory;
import com.dt.android.domainobject.SignContent;

public class SignDAO extends BaseDAO implements Constants {

	public List<SignCategory> getSignCategorys(Integer parentId) {
		String sql = "select sign_category_id,name,parent_id,(select count(*) from "
				+ TABLE_SIGN_CONTENT
				+ " content where content.category_id=category.sign_category_id or "
				+ "content.category_id in (select c1.sign_category_id from  "
				+ TABLE_SIGN_CATEGORY
				+ " c1 where c1.parent_id= category.sign_category_id) ),"
				+ "(select count(*) from "
				+ TABLE_SIGN_CONTENT
				+ " c2 where c2.category_id=category.sign_category_id) "
				+ " from "
				+ TABLE_SIGN_CATEGORY + " category";

		if (parentId != null) {
			sql += " where parent_id=" + parentId;
		} else {
			sql += " where parent_id is null";
		}
		sql += " order by sign_category_id";

		Cursor c = db.rawQuery(sql, null);

		List<SignCategory> result = new ArrayList<SignCategory>();
		SignCategory sc;
		while (c.moveToNext()) {
			sc = new SignCategory();
			sc.setDirectCount(c.getInt(4));
			sc.setCount(c.getInt(3));
			sc.setId(c.getInt(0));
			sc.setParent_id(c.getInt(2));
			sc.setName(c.getString(1));
			result.add(sc);
		}
		c.close();
		return result;
	}

	public List<SignContent> getSignContent(Integer categoryId) {
		List<SignContent> result = new ArrayList<SignContent>();

		Cursor c = db.query(TABLE_SIGN_CONTENT, new String[] {
				"sign_content_id", "category_id", "name", "picture" },
				"category_id=" + categoryId, null, null, null,
				"sign_content_id");
		SignContent sc;
		while (c.moveToNext()) {
			sc = new SignContent();
			sc.setCategoryId(c.getInt(1));
			sc.setId(c.getInt(0));
			Log.d("SignDAO", "content name:" + c.getString(2));
			sc.setName(c.getString(2).replaceAll("\\\\n", "\n"));
			sc.setPicture(c.getString(3));
			result.add(sc);
		}
		c.close();
		return result;
	}
}
