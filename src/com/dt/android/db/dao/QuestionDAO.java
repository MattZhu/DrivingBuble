package com.dt.android.db.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.dt.android.db.Constants;
import com.dt.android.domainobject.AppData;
import com.dt.android.domainobject.Chapter;
import com.dt.android.domainobject.Question;

public class QuestionDAO extends BaseDAO implements Constants {

	private static final String SEPERATOR = "==";
	private static final String TAG = "QuestionDAO";

	public void insertQuestion(
			com.dt.android.serverapi.message.Question[] questions) {
		db.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			for (com.dt.android.serverapi.message.Question q : questions) {
				values.clear();
				values.put("question_id", q.getQuestion_id());
				values.put("is_a", q.getIs_a());
				values.put("is_b", q.getIs_b());
				values.put("is_c", q.getIs_c());
				values.put("q_type", q.getType());
				values.put("description", q.getDesc());
				values.put("picture", q.getPicture());
				values.put("answer", covertString(q.getCorrect_answer()));
				values.put("options", covertString(q.getAnswer()));
				values.put("answerDetail", q.getExplanation());
				db.insert(TABLE_QUESTION, null, values);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
	}

	private String covertString(String[] correct_answer) {
		String result = "";
		if (correct_answer == null || correct_answer.length == 0) {
			return result;
		}
		for (String s : correct_answer) {
			result += SEPERATOR + s;
		}
		return result.substring(SEPERATOR.length());
	}

	public void deleteAll() {

		db.delete(TABLE_QUESTION_CHAPTER_MAPING, null, null);
		db.delete(TABLE_CHAPTER, null, null);
		db.delete(TABLE_QUESTION, null, null);
	}

	public void updateQuestion(Question q) {
		ContentValues values = new ContentValues();
		values.put("errored", q.isErrored());
		values.put("stared", q.isStared());
		db.update(TABLE_QUESTION, values, "question_id =" + q.getId(), null);
	}

	public int getCountByType(int qtype) {
		String select = "select count(*) from " + TABLE_QUESTION
				+ " q where  q.q_type =" + qtype;
		Cursor c = db.rawQuery(select, null);
		int result = 0;
		if (c.moveToNext()) {
			result = c.getInt(0);
		}
		c.close();
		return result;
	}

	public Question[] getQuestions(String ids) {

		Question[] questions = new Question[0];
		String select = "select q.question_id,description,picture,answer,stared,errored,answerDetail,options from "
				+ TABLE_QUESTION + " q where q.question_id in (" + ids + ")";
		Cursor c = db.rawQuery(select, null);
		List<Question> list = new ArrayList<Question>();
		while (c.moveToNext()) {
			Question question = new Question();
			question.setId(c.getInt(0));
			question.setAnswer(toIntArray(c.getString(3)));
			question.setOptions(toStringList(c.getString(7)));
			question.setImage(c.getString(2));
			question.setDesciption(c.getString(1));
			question.setAnswerDetail(c.getString(6));
			question.setStared(c.getInt(4) == 1);
			question.setErrored(c.getInt(5) == 1);
			list.add(question);
		}
		c.close();
		return list.toArray(questions);
	}

	public Question[] getQuestions(Chapter ch, Integer type) {
		Question[] questions = new Question[0];
		String select = "select q.question_id,description,picture,answer,stared,errored,answerDetail,options from "
				+ TABLE_QUESTION + " q ";
		if (ch != null && ch.getChapterId() != null) {
			select += ","
					+ TABLE_QUESTION_CHAPTER_MAPING
					+ " map where q.question_id=map.question_id and map.chapter_id ="
					+ ch.getId() + "  and ";
		} else {
			select += " where ";
		}
		select += getCarTypeWhere();
		select += " and q.q_type = " + AppData.getInstance().getqType();
		if (type != null) {
			switch (type) {
			case STARED:
				select = append(select, "stared = 1");
				break;
			case ERRORED:
				select = append(select, "errored = 1");
				break;

			}
		}
		select += " order by q.question_id";
		Long start = System.currentTimeMillis();

		Cursor c = db.rawQuery(select, null);
		Log.d(TAG,
				"get questions execute query take "
						+ (System.currentTimeMillis() - start));
		List<Question> list = new ArrayList<Question>();
		while (c.moveToNext()) {
			Question question = new Question();
			question.setId(c.getInt(0));
			question.setAnswer(toIntArray(c.getString(3)));
			question.setOptions(toStringList(c.getString(7)));
			question.setImage(c.getString(2));
			question.setDesciption(c.getString(1));
			question.setAnswerDetail(c.getString(6));
			question.setStared(c.getInt(4) == 1);
			question.setErrored(c.getInt(5) == 1);
			list.add(question);
		}
		c.close();
		Log.d(TAG, "get questions take " + (System.currentTimeMillis() - start));
		return list.toArray(questions);
	}

	private Integer[] toIntArray(String str) {
		if (str == null || str.length() == 0) {
			return new Integer[0];
		}
		String[] r = str.split(SEPERATOR);
		Integer result[] = new Integer[r.length];
		for (int i = 0; i < r.length; i++) {
			result[i] = Integer.valueOf(r[i]) - 1;
		}
		return result;
	}

	private List<String> toStringList(String str) {
		if (str == null || str.length() == 0) {
			return new ArrayList<String>();
		} else {
			return Arrays.asList(str.split(SEPERATOR));
		}
	}

	private String append(String select, String string) {
		if (select == null) {
			return string;
		}

		return select + " and " + string;
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

	public void clearFlag(int type) {
		ContentValues values = new ContentValues();
		String whereClause = "";
		if (type == STARED) {
			whereClause = "stared = 1";
			values.put("stared", false);
		} else if (type == ERRORED) {
			whereClause = "errored=1";
			values.put("errored", false);
		}
		db.update(TABLE_QUESTION, values, whereClause, null);
	}

}
