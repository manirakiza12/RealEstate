package com.elite.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.elite.item.Property;

import java.util.ArrayList;

public class DatabaseHelperRecent extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "propertyrecent.db";
    public static final String TABLE_FAVOURITE_NAME = "recent";

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_PRICE = "price";
    public static final String KEY_ADDRESS = "location";
    public static final String KEY_AUTO_ID = "auto_id";


    public DatabaseHelperRecent(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_FAVOURITE_TABLE = "CREATE TABLE " + TABLE_FAVOURITE_NAME + "("
                + KEY_ID + " INTEGER,"
                + KEY_AUTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT,"
                + KEY_IMAGE + " TEXT,"
                + KEY_PRICE + " TEXT,"
                + KEY_ADDRESS + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVOURITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVOURITE_NAME);
        // Create tables again
        onCreate(db);
    }

    public boolean getRecentById(String story_id) {
        boolean count = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String[] args = new String[]{story_id};
        Cursor cursor = db.rawQuery("SELECT id FROM recent WHERE id=? ", args);
        if (cursor.moveToFirst()) {
            count = true;
        }
        cursor.close();
        db.close();
        return count;
    }

    public void removeRecentById(String _id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM  recent " + " WHERE " + KEY_ID + " = " + _id);
        db.close();
    }

    public long addRecent(String TableName, ContentValues contentvalues, String s1) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(TableName, s1, contentvalues);
    }

    public ArrayList<Property> getRecent() {
        ArrayList<Property> chapterList = new ArrayList<>();
        String selectQuery = "SELECT *  FROM "
                + TABLE_FAVOURITE_NAME +" ORDER BY auto_id DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Property contact = new Property();
                contact.setPropertyId(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID)));
                contact.setPropertyTitle(cursor.getString(cursor.getColumnIndexOrThrow(KEY_TITLE)));
                contact.setPropertyImage(cursor.getString(cursor.getColumnIndexOrThrow(KEY_IMAGE)));
                contact.setPropertyPrice(cursor.getInt(cursor.getColumnIndexOrThrow(KEY_PRICE)));
                contact.setPropertyAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)));
                chapterList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return chapterList;
    }
}
