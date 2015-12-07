package com.dt.android.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import com.dt.android.db.Constants;
import com.dt.android.domainobject.Skill;

public class SkillsDAO extends BaseDAO implements Constants {

	public List<Skill> getSkills() {
		String sql = "select id,name,file_name from "
				+ TABLE_SKILLS+ " order by id";		

		Cursor c = db.rawQuery(sql, null);

		List<Skill> result = new ArrayList<Skill>();
		Skill skill;
		while (c.moveToNext()) {
			skill = new Skill();
			skill.setId(c.getInt(0));
			skill.setFileName(c.getString(2));
			skill.setName(c.getString(1));
			result.add(skill);
		}
		c.close();
		return result;
	}
	
	public List<Skill> getLaws(){
		String sql = "select id,name,file_name from "
			+ TABLE_LAWS+ " order by id";		

	Cursor c = db.rawQuery(sql, null);

	List<Skill> result = new ArrayList<Skill>();
	Skill skill;
	while (c.moveToNext()) {
		skill = new Skill();
		skill.setId(c.getInt(0));
		skill.setFileName(c.getString(2));
		skill.setName(c.getString(1));
		result.add(skill);
	}
	c.close();
	return result;
	}
}
