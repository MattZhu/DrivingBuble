package com.dt.android.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;

import com.dt.android.db.Constants;
import com.dt.android.domainobject.AppData;
import com.dt.android.domainobject.Chapter;

public class ChapterDAO extends BaseDAO implements Constants {

	public void insertChapter( com.dt.android.serverapi.message.Chapter ch,Integer ch_type){
		db.beginTransaction();
		try {
		
			ContentValues values = new ContentValues();
			values.put("CHAPTER_ID", ch.getChapter_id());
			values.put("PARENT_ID", ch.getParent_chapter_id());
			values.put("ch_type", ch_type);
			values.put("CHAPTER", ch.getChapter_name());
			Long id=db.insert("chapter", null, values);
			printSql(ch,ch_type);
			printSqls(ch,ch_type);
			if(ch.getQuestions()!=null&&ch.getQuestions().length()>0){
				String []qIds=ch.getQuestions().split(",");
				ContentValues v = new ContentValues();
				v.put("CHAPTER_ID", id);
				for(String qId:qIds){
					v.put("question_id", qId);
					db.insert("QUESTION_CHAPTER_MAPING", null, v);
				}
			}
		db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}
	
	private void printSql(com.dt.android.serverapi.message.Chapter ch,Integer ch_type){
		System.out.println("insert into chapter (CHAPTER_ID,PARENT_ID,ch_type,CHAPTER)values("
				+ch.getChapter_id()+","+ch.getParent_chapter_id()+","+ch_type+"'"+ch.getChapter_name()+"');");
	}
	private void printSqls(com.dt.android.serverapi.message.Chapter ch,Integer ch_type){
		System.out.println("insert into QUESTION_CHAPTER_MAPING (CHAPTER_ID,question_id) select _ID,question_id " +
				"from chapter ch, QUESTION q where ch.CHAPTER_ID="
				+ch.getChapter_id()+" and ch.ch_type="+ch_type+" and q.question_id in ("+ch.getQuestions()+");");
	}
	public List<Chapter> getChaptersForStared(Integer pId) {
		List<Chapter> result = new ArrayList<Chapter>();

		String query = "select ch._id,ch.chapter_id,ch.chapter,("
				+ "select count(*) from QUESTION_CHAPTER_MAPING map,question q where "
				+ "ch._id=map.chapter_id and q.question_id=map.question_id and q.stared=1 and "
				+ getCarTypeWhere()
				+ "),(select count(*)from chapter c where c.PARENT_ID=ch.chapter_id"
				+ " and c.ch_type=" + AppData.getInstance().getqType()
				+ " )from " + "chapter ch where ch.ch_type="
				+ AppData.getInstance().getqType() + " and ch.PARENT_ID=" + pId
				+ " order by ch.chapter_id,ch.chapter";
		
		getChapters(result, query);
//		chapter = new Chapter();
//		chapter.setChapter("所有收藏");
//		chapter.setChapterId(null);
//		chapter.setQuesitonCount(total);
//		result.add(0, chapter);
		return result;
	}

	private int getChapters(List<Chapter> result, String query) {
		Cursor c = db.rawQuery(query, null);
		Chapter chapter;
		int total = 0;
		while (c.moveToNext()) {
			chapter = new Chapter();
			chapter.setId(c.getInt(0));
			chapter.setChapterId(c.getInt(1));
			chapter.setChapter(c.getString(2));
			chapter.setQuesitonCount(c.getInt(3));
			chapter.setHasChildren(c.getInt(4)>0);
			total += chapter.getQuesitonCount();
			result.add(chapter);
		}
		c.close();
		return total;
	}

	public List<Chapter> getChaptersForError(Integer pId) {
		List<Chapter> result = new ArrayList<Chapter>();
		String query = "select ch._id,ch.chapter_id,ch.chapter,("
				+ "select count(*) from QUESTION_CHAPTER_MAPING map,question q where"
				+ " ch._id=map.chapter_id and q.question_id=map.question_id and q.errored=1 and "
				+ getCarTypeWhere() + ") ,(select count(*)from chapter c where c.PARENT_ID=ch.chapter_id"
				+ " and c.ch_type=" + AppData.getInstance().getqType()
				+ " ) from "
				+ "chapter ch where ch.ch_type="
				+ AppData.getInstance().getqType() + " and ch.PARENT_ID=" + pId
				+ " order by ch.chapter_id,ch.chapter";
		
		getChapters(result, query);
//		chapter = new Chapter();
//		chapter.setChapter("所有错题");
//		chapter.setChapterId(null);
//		chapter.setQuesitonCount(total);
//		result.add(0, chapter);
		return result;
	}

	public List<Chapter> getChapters(Integer pId) {
		List<Chapter> result = new ArrayList<Chapter>();
		String query = "select ch._id, ch.chapter_id,ch.chapter,("
				+ "select count(*) from QUESTION_CHAPTER_MAPING map,question q where "
				+ "ch._id=map.chapter_id and q.question_id=map.question_id and "
				+ getCarTypeWhere() + ") ,(select count(*)from chapter c where c.PARENT_ID=ch.chapter_id"
				+ " and c.ch_type=" + AppData.getInstance().getqType()
				+ " ) from "
				+ "chapter ch where ch.ch_type="
				+ AppData.getInstance().getqType() + " and ch.PARENT_ID=" + pId
				+ " order by ch.chapter_id,ch.chapter";

		getChapters(result, query);
		return result;
	}

	private String getCarTypeWhere() {
		String qw = "";
		switch (AppData.getInstance().getCarType()) {
		case CAR_TYPE_CAR:
			qw = "q.is_c=1";
			break;
		case CAR_TYPE_BUS:
			qw = "q.is_a=1";
			break;
		case CAR_TYPE_TRUCK:
			qw = "q.is_b=1";
			break;
		}
		;
		return qw;
	}

	public void saveOrUpdateChapter(Chapter chapter) {
		ContentValues values = new ContentValues();

		values.put("CHAPTER", chapter.getChapter());
		if (chapter.getChapterId() == null) {
			db.insert(TABLE_CHAPTER, null, values);
		} else {
			db.update(TABLE_CHAPTER, values, "CHAPTER_ID =?",
					new String[] { chapter.getChapterId().toString() });
		}
	}

}
