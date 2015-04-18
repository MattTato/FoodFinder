package com.osu.tatoczenko.foodfinder;

/**
 * Created by tyler_cunnington on 4/2/15.
 * Used as the container for information stored in the database
 */
public class SavedFoodLocation {

    //private variables
    int _id;
    String _restId;

    //empty constructor
    public SavedFoodLocation(){

    }

    //constructor
    public SavedFoodLocation(int id, String restId){
        this._id=id;
        this._restId=restId;
    }

    //getting id
    public int getId(){
        return this._id;
    }

    //setting id
    public void setId(int id){
        this._id=id;
    }

    //getting restaurant ID
    public String getRestId(){
        return this._restId;
    }

    //setting restaurant ID
    public void setRestId(String restId){

        this._restId = restId;

    }



}
