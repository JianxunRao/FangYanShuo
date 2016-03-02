package com.trojx.regularpractice.sqllitetest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**MyOpenHelper类
 * Created by Administrator on 2016/2/28.
 */
public class MyOpenHelper extends SQLiteOpenHelper {
    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table newtable (id integer primary key autoincrement,key text,value text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
