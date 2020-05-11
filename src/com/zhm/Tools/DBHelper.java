package com.zhm.Tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	private static final String DATABASE_NAME = "snake.db";
	private static final int VERSION = 1;

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql = "create table scoreTbl(" +
				"id integer primary key autoincrement, " +
				"name varchar(20) not null, " +
				"score integer);";
		db.execSQL(sql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql = "drop table if exists scoreTbl";
		db.execSQL(sql);
		onCreate(db);
	}

}
