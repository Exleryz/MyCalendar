package com.yezhou.example.mycalendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/12/1.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String CREATE_TITLE = "create table title (" +
            "id integer primary key autoincrement," +
            "title text," +
            "date text," +
            "time text" +
            ")";    // 利用 time 与 date 实现两表连接

    public static final String CREATE_ITEM = "create table item (" +
            "id integer primary key autoincrement," +
            "title text," +
            "date text," +
            "content text," +
            "time text," +
            "remind text" +
            ")";

    private Context mContext;

    // 插入数据 title content date time remind(1 yes 0 no)到item表 及 title表
    public void insertData(String title, String date, String content, String time, String remind) {
        ContentValues itemValues = new ContentValues();
        itemValues.put("title", title);
        itemValues.put("date", date);
        itemValues.put("content", content);
        itemValues.put("time", time);
        itemValues.put("remind", remind);
        SQLiteDatabase db0 = getWritableDatabase();
        db0.insert("item", null, itemValues);
        db0.close();
        ContentValues titleValues = new ContentValues();
        titleValues.put("title", title);
        titleValues.put("date", date);
        titleValues.put("time", time);
        SQLiteDatabase db1 = getWritableDatabase();
        db1.insert("title", null, titleValues);
        db1.close();
    }

    // 更新数据
    public void updateData(String date, String time, String newDate, String newTime, String newStrTitle, String newStrContent, String newRemind) {
        ContentValues titleValues = new ContentValues();
        titleValues.put("title", newStrTitle);
        titleValues.put("date", newDate);
        titleValues.put("time", newTime);
        ContentValues itemValues = new ContentValues();
        itemValues.put("title", newStrTitle);
        itemValues.put("date", newDate);
        itemValues.put("content", newStrContent);
        itemValues.put("time", newTime);
        itemValues.put("remind",newRemind);
        SQLiteDatabase db = getWritableDatabase();
        db.update("title", titleValues, "date = ? and time = ?", new String[] { date, time});
        db.update("item", itemValues, "date = ? and time = ?", new String[] { date, time});
        db.close();
    }

    // 查询 title表 为了填充RecyclerView 利用(date time)合并的唯一性
    public void queryDataTitle(ArrayList<String> titles, ArrayList<String> dates, ArrayList<String> times) {
        titles.clear();    // 数据会重复
        dates.clear();
        times.clear();
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("title", new String[] { "title", "date", "time"}, null, null, null,null, null);
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String date = cursor.getString(cursor.getColumnIndex("date"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                titles.add(title);
                dates.add(date);
                times.add(time);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    // 用item的id查询该item的所有信息 为了填充details页面 如果Title相同。。。。 尝试利用id
    public void queryData(String strDate, String strTime, String [] strs) {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("item", new String[] { "title", "date", "content", "remind" }, "date = ? and time = ?", new String[]{ strDate, strTime}, null,null, null);
        if (cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String date = cursor.getString(cursor.getColumnIndex("date"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String remind = cursor.getString(cursor.getColumnIndex("remind"));

            strs[0] = title;
            strs[1] = date;
            strs[2] = content;
            strs[3] = remind;
        }
        cursor.close();
    }

    public boolean remind(String today,String[] strs) {    // true 有通知 false 没有通知
        Log.d("admin_dbHelper", today);
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query("item", new String[] { "title", "content", "date", "time"}, "date = ? and remind = ?", new String[]{ today, "1"}, null,null, null);
        if (cursor.moveToFirst()) {    // 今天有item 返回title content用于通知
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));

            strs[0] = title;
            strs[1] = content;
            ContentValues Values = new ContentValues();
            Values.put("remind", "0");
            db.update("item", Values, "date = ? and time = ?", new String[] { cursor.getString(cursor.getColumnIndex("date")), cursor.getString(cursor.getColumnIndex("time"))});
            db.close();
            return true;
        } else
            return false;
    }

    // 利用date和time唯一性删除数据
    public void deleteData(String strDate, String strTime) {
        SQLiteDatabase db = getWritableDatabase();
        Log.d("admin_database", strDate + strTime);
        db.delete("item", "date = ? and time = ?", new String[]{ strDate, strTime});
        db.delete("title", "date = ? and time = ?", new String[]{ strDate, strTime});
    }

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TITLE);
        db.execSQL(CREATE_ITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
