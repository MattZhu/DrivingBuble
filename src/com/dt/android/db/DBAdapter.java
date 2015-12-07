package com.dt.android.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.dt.android.db.dao.AppConfDAO;
import com.dt.android.db.dao.ChapterDAO;
import com.dt.android.db.dao.LocationsDAO;
import com.dt.android.db.dao.QuestionDAO;
import com.dt.android.db.dao.SchoolDAO;
import com.dt.android.db.dao.SignDAO;
import com.dt.android.db.dao.SkillsDAO;

public class DBAdapter implements Constants {

	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;

	private ChapterDAO chapterDAO;

	private AppConfDAO appConfDAO;

	private LocationsDAO locationsDAO;

	private QuestionDAO questionDAO;
	private SignDAO signDAO;

	private SchoolDAO schoolDAO;
	private SkillsDAO skillsDAO;

	public DBAdapter(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public DBAdapter open() throws SQLException {
		// get a DB through DB assistant
		// File dbfile=FileUtil.createFileOnSDCard(context,"/db/"+
		// DATABASE_NAME);
		//
		// File folder=dbfile.getParentFile();
		// if(!folder.exists()){
		// folder.mkdirs();
		// }
		// SQLiteDatabase.op
		db = dbHelper.getWritableDatabase();// SQLiteDatabase.openOrCreateDatabase(dbfile.getAbsolutePath(),
											// null);

		return this;
	}

	public ChapterDAO getChapterDAO() {
		if (chapterDAO == null) {
			chapterDAO = new ChapterDAO();
			chapterDAO.setDb(db);
		}
		return chapterDAO;
	}

	public AppConfDAO getAppConfDAO() {
		if (appConfDAO == null) {
			appConfDAO = new AppConfDAO();
			appConfDAO.setDb(db);
		}
		return appConfDAO;
	}

	public LocationsDAO getLocationsDAO() {
		if (locationsDAO == null) {
			locationsDAO = new LocationsDAO();
			locationsDAO.setDb(db);
		}
		return locationsDAO;
	}

	public SignDAO getSignDAO() {
		if (signDAO == null) {
			signDAO = new SignDAO();
			signDAO.setDb(db);
		}
		return signDAO;
	}

	public QuestionDAO getQuestionDAO() {
		if (questionDAO == null) {
			questionDAO = new QuestionDAO();
			questionDAO.setDb(db);
		}
		return questionDAO;
	}

	public SchoolDAO getSchoolDAO() {
		if (schoolDAO == null) {
			schoolDAO = new SchoolDAO();
			schoolDAO.setDb(db);
		}
		return schoolDAO;
	}

	public SkillsDAO getSkillsDAO() {
		if (skillsDAO == null) {
			skillsDAO = new SkillsDAO();
			skillsDAO.setDb(db);
		}
		return skillsDAO;
	}

	/*
	 * close DB
	 */
	public void close() {
		// close DB through DB assistant
		db.close();
	}

	private static class DatabaseHelper extends SQLiteOpenHelper {
		private Context context;
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context=context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String[] sqls = SqlFileLoader.loadSqls("initdb.sql");
			for (String sql : sqls) {
				db.execSQL(sql);
			}

			db.beginTransaction();
			try {
				sqls = SqlFileLoader.loadSqls("sign.sql");

				for (String sql : sqls) {
					db.execSQL(sql);
				}
				sqls = SqlFileLoader.loadSqls("province.sql");

				for (String sql : sqls) {
					db.execSQL(sql);
				}
				sqls = SqlFileLoader.loadSqls("skills.sql");

				for (String sql : sqls) {
					db.execSQL(sql);
				}
				sqls = SqlFileLoader.loadSqls("questions.sql");

				for (String sql : sqls) {
					db.execSQL(sql);
				}
				db.setTransactionSuccessful();
			} finally {
				db.endTransaction();
			}
			SqlFileLoader.copyCacheFiles(context);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

}
