package com.osu.tatoczenko.foodfinder;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tyler_cunnington on 3/30/15.
 */
public class DbOperator extends SQLiteOpenHelper {

    //database version
    private static final int DATABASE_VERSION = 1;
    //database name
    private static final String DATABASE_NAME = "LocationsSQLite";
    //table name
    private static final String DATABASE_TABLE = "locations";
    //table columns
    private static final String KEY_ID = "id";
    private static final String KEY_REST_ID = "restID";





    private static final String[] COLUMNS = {KEY_ID,KEY_REST_ID};


    public DbOperator (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String TABLE_CREATE = "CREATE TABLE " + DATABASE_TABLE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_REST_ID + " TEXT" + ")";
        db.execSQL(TABLE_CREATE);
    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);


        //not sure if I need these lines

        onCreate(db);

    }

    //add stuff to database
    public boolean addToDatabase(String restaurantId){
        List<SavedFoodLocation> savedLocList = getAllLoc();
        boolean canAddToDatabase = true;
        for(SavedFoodLocation savedLoc : savedLocList){
            // somehow my SavedLocations got a null pointer in them and I don't know how to
            // delete my database so I guess I'll just add this check in - Marshall
            if (savedLoc.getRestId() != null) {
                if (savedLoc.getRestId().equals(restaurantId)) {
                    canAddToDatabase = false;
                }
            }
        }
        if(canAddToDatabase) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(KEY_REST_ID, restaurantId);
            db.insert(DATABASE_TABLE, null, values);
            db.close();
        }
        return canAddToDatabase;
    }


    //read a single row in database
    public SavedFoodLocation getFromTable(int id){

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DATABASE_TABLE, new String[] {KEY_ID,KEY_REST_ID},KEY_ID + "=?", new String[]{String.valueOf(id)},null,null,null,null);
        if (cursor !=null)
            cursor.moveToFirst();

        SavedFoodLocation location = new SavedFoodLocation(Integer.parseInt(cursor.getString(0)),cursor.getString(1));
        return location;

    }

    // will return all rows from database in an array list

    public List<SavedFoodLocation> getAllLoc(){
        List<SavedFoodLocation> locList = new ArrayList<SavedFoodLocation>();
        //select all query
        String selectQuery = "SELECT * FROM " + DATABASE_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);

        if (cursor.moveToFirst()){
            do{
                SavedFoodLocation location = new SavedFoodLocation();
                location.setId(Integer.parseInt(cursor.getString(0)));
                location.setRestId(cursor.getString(1));
                locList.add(location);
            } while (cursor.moveToNext());


        }
        //return list
        return locList;
    }
}
