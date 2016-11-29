package com.github.tsiangleo.sr.client.dao;

/**
 * Created by tsiang on 2016/11/29.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.github.tsiangleo.sr.client.util.DBHelper;

public class AppLockDao
{
    private DBHelper dbHelper;

    public AppLockDao(Context context)
    {
        dbHelper = new DBHelper(context,"com.github.tsiangleo.sr.db",1);
    }

    /**
     * 返回所有的加锁列表：每一项是：appPackageName+"#"+appName.
     * @return
     */
    public List<String> getAllLockList(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<String> packageNames = new ArrayList<String>();
        if(db.isOpen())
        {
            Cursor cursor = db.rawQuery("select appfullname from applocklist", null);
            while(cursor.moveToNext())
            {
                String packageName = cursor.getString(0);
                packageNames.add(packageName);
            }
            cursor.close();
            db.close();
        }
        return packageNames;
    }


    public void insert(String appfullname) {
        if(find(appfullname)){
            return ;
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen())
        {
            db.execSQL("insert into applocklist (appfullname) values (?)", new String[] {appfullname});
            db.close();
        }
    }

    public boolean find(String appfullname)
    {
        boolean result = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if(db.isOpen())
        {
            Cursor cursor = db.rawQuery("select appfullname from applocklist where appfullname = ? ", new String[] {appfullname});
            if(cursor.moveToNext())
            {
                result = true;
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    public void delete(String appfullname) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(db.isOpen()) {
            db.execSQL("delete from applocklist where appfullname = ? ", new String[]{appfullname});
            db.close();
        }

    }
}
