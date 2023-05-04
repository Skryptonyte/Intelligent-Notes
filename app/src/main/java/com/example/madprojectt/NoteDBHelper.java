package com.example.madprojectt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDBHelper extends SQLiteOpenHelper {

    NoteDBHelper(Context context, String dbname) {

        super(context, dbname+".sql", null, 1);
        String name = dbname.toLowerCase();

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists notes(noteid integer primary key autoincrement, title varchar(10), descr varchar(50))");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
