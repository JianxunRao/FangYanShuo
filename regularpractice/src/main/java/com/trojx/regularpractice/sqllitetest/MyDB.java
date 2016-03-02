package com.trojx.regularpractice.sqllitetest;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**将一些常用的数据库操作封装起来
 * Created by Administrator on 2016/2/28.
 */
public class MyDB {
    private static MyDB myDB;
    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     * @param context
     */
    private MyDB(Context context){
        MyOpenHelper myOpenHelper=new MyOpenHelper(context,"newDB",null,1);
        db=myOpenHelper.getWritableDatabase();
    }

    /**
     * 获取本类的实例
     * @param context
     * @return
     */
    public synchronized static MyDB getInstance(Context context){
        if(myDB==null){
            myDB=new MyDB(context);
            return  myDB;
        }
        return myDB;
    }

    /**
     * 添加新记录的示例
     * @param key 键
     * @param value 值
     */
    public void addNewRecord(String key,String value){
        ContentValues values=new ContentValues();
        values.put(key,value);
        db.insert("newDB",null,values);
    }
    /*
    ...诸多数据库操作方法
     */
}
