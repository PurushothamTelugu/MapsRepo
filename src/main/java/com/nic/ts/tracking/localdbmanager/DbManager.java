package com.nic.ts.tracking.localdbmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

public class DbManager extends SQLiteOpenHelper {

    private static final String dbName = "dbGps";

    public DbManager(@Nullable Context context) {
        super(context, dbName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String qry = "create table table_locations ( id integer primary key autoincrement, latitude TEXT, longitude TEXT, address TEXT, datentime TEXT)";
        db.execSQL(qry);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String qry = " DROP TABLE IF EXISTS table_locations";
        db.execSQL(qry);
        onCreate(db);
    }

    public String addRecord(String latitude, String longitude, String address, String datentime){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("latitude",latitude);
        contentValues.put("longitude",longitude);
        contentValues.put("address",address);
        contentValues.put("datentime",datentime);

        float result = sqLiteDatabase.insert("table_locations",null,contentValues);
        if (result == -1)
            return "failed";
        else
            return "Successfully inserted";
    }

    public String addCurrentRecord(String latlng, String address){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("current_latlng",latlng);
        contentValues.put("current_address",address);

        float result = sqLiteDatabase.insert("table_locations",null,contentValues);
        if (result == -1)
            return "failed";
        else
            return "Successfully inserted";
    }

    public Cursor readAllData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String qry = " select * from table_locations order by id desc";
        Cursor cursor = sqLiteDatabase.rawQuery(qry,null);

        return cursor;
    }
}
