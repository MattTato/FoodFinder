package com.osu.tatoczenko.foodfinder;

/**
 * Created by tyler_cunnington on 4/2/15.
 */
public class Location {

    //private variables
    int _id;
    String _restId;

    //empty constructor
    public Location(){

    }

    //constructor
    public Location(int id, String restId){
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
