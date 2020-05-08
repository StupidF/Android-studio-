package com.zzj.mpchart_bluetooth;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class beanopenhelper extends SQLiteOpenHelper{
	

	public beanopenhelper(Context context, String name) {
		super(context, name, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table if not exists roast(_id integer primary key autoincrement,name text,date text,curves text not null,changedata text not null,roaster text,note text,flavor integer,body integer,clean integer,sour integer,sweet integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
