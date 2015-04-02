package com.osu.tatoczenko.foodfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by tyler_cunnington on 3/30/15.
 */
public class DbOperator extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MarkerLocationsSQLite";
    private static final String ROW_ID = "_id";
    private static final String KEY_REST_TAG = "rest_tag";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";
    private static final String DATABASE_TABLE = "locations";


    private static final String TABLE_CREATE = "CREATE TABLE" + DATABASE_TABLE + " (" + ROW_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_REST_TAG + " TEXT, " + KEY_LONGITUDE + "DOUBLE, " + KEY_LATITUDE + "DOUBLE)";



    public DbOperator (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);


    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);


        //not sure if I need these lines

        //onCreate(db);

    }


    public void addToDatabase(LatLng restLat,String restName){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_REST_TAG, restName);
        values.put(KEY_LONGITUDE,restLat.longitude);
        values.put(KEY_LATITUDE,restLat.latitude);

        db.insert(DATABASE_TABLE,null,values);

        db.close();





    }







}
