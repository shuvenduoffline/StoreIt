package com.example.storeit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class Database extends SQLiteOpenHelper {

    public static String NAME = "scanrecord";
    public static int VERSION = 1;

    String CREATE_SCAN_RECORDS_TABLE = "CREATE TABLE records(_id INTEGER PRIMARY KEY,details TEXT, item INTEGER, quantity INTEGER, scan_at INTEGER )";
    public static String SCAN_RECORDS_TABLE = "scanrecord";
    public static String SCAN_RECORDS_TABLE_ID = "_id";
    public static String SCAN_RECORDS_TABLE_DETAILS = "details";
    public static String SCAN_RECORDS_TABLE_ITEM = "item";
    public static String SCAN_RECORDS_TABLE_START_QUANTITY = "quantity";
    public static String SCAN_RECORDS_TABLE_SCAN_AT = "scan_at";



    private static Database instance;

    public static synchronized Database getInstance(Context context) {
        if (instance == null) {
            instance = new Database(context.getApplicationContext());
        }
        return instance;
    }

    private Database(Context context) {
        super(context, Database.NAME, null, Database.VERSION);
    }

    @Override
    public synchronized void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_SCAN_RECORDS_TABLE);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    private Items getAllItems(Cursor cursor) {
        Items item = new Items();


        // String[] columnNames = cursor.getColumnNames();

        int index = cursor.getColumnIndex(SCAN_RECORDS_TABLE_ID);
        item.setId(cursor.getInt(index));

        index = cursor.getColumnIndex(SCAN_RECORDS_TABLE_ITEM);
        item.setItem(cursor.getString(index));

        index = cursor.getColumnIndex(SCAN_RECORDS_TABLE_DETAILS);
        item.setDetails(cursor.getString(index));

        index = cursor.getColumnIndex(SCAN_RECORDS_TABLE_START_QUANTITY);
        item.setQuantity(cursor.getInt(index));

        index = cursor.getColumnIndex(SCAN_RECORDS_TABLE_SCAN_AT);
        item.setScanat(new Date(cursor.getInt(index)));

        return item;
    }


    public synchronized ArrayList<Items> getAllItems() {
        ArrayList<Items> array_list = new ArrayList<Items>();

        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Cursor cursor = db.rawQuery("select * from " + Database.SCAN_RECORDS_TABLE, null);
            cursor.moveToFirst();
            while (cursor.isAfterLast() == false) {
                Items item = getAllItems(cursor);
                array_list.add(item);
                cursor.moveToNext();
            }
            Collections.reverse(array_list);
            return array_list;
        } finally {
            db.close();
        }
    }


    public synchronized boolean addScanResult(Items item) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {

            ContentValues contentValues = new ContentValues();

            contentValues.put(SCAN_RECORDS_TABLE_DETAILS, item.getDetails());
            contentValues.put(SCAN_RECORDS_TABLE_ITEM, item.getItem());
            contentValues.put(SCAN_RECORDS_TABLE_SCAN_AT, ( item.getScanat().getTime()));
            contentValues.put(SCAN_RECORDS_TABLE_START_QUANTITY, item.getQuantity());
            long rowId = db.insert(Database.SCAN_RECORDS_TABLE, null, contentValues);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }



    public synchronized boolean removeItem(Items item) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.delete(Database.SCAN_RECORDS_TABLE, SCAN_RECORDS_TABLE_ID + "=" + item.getId(), null);
            return true;
        } finally {
            db.close();
        }
    }

    public synchronized int count() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            int numRows = (int) DatabaseUtils.queryNumEntries(db, Database.SCAN_RECORDS_TABLE);
            return numRows;
        } finally {
            db.close();
        }
    }




}