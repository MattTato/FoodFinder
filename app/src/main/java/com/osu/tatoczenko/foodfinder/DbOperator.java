package com.osu.tatoczenko.foodfinder;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tyler_cunnington on 3/30/15.
 */
public class DbOperator extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "MarkerLocations";
    private static final String KEY_REST_NAME = "name";
    private static final String KEY_LONGITUDE = "longitude";
    private static final String KEY_LATITUDE = "latitude";

    private static final String TABLE_CREATE = "CREATE TABLE" + DATABASE_NAME + " (" + KEY_REST_NAME + "TEXT, "
            + KEY_LONGITUDE + "FLOAT, " + KEY_LATITUDE + "FLOAT);";


    DbOperator (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);


        //not sure if I need this line below

        onCreate(db);

    }
}
