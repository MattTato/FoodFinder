package com.osu.tatoczenko.foodfinder;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Matt Tatoczenko on 4/18/2015.
 * Parcelable class in order to save alternate place info during screen rotations
 */
public class MapAlternatePlacesParcelable implements Parcelable {
    LatLng latLng;
    String name;
    String placeID;

    public MapAlternatePlacesParcelable(LatLng latLng, String name, String placeID) {
        this.latLng = latLng;
        this.name = name;
        this.placeID = placeID;
    }

    private MapAlternatePlacesParcelable(Parcel in) {

    }

    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return this.latLng.toString() + " " + this.name + " " + this.placeID;
    }

    public void writeToParcel(Parcel out, int flags) {

    }

    public final Creator<MapAlternatePlacesParcelable> CREATOR = new Creator<MapAlternatePlacesParcelable>() {
        public MapAlternatePlacesParcelable createFromParcel(Parcel in) {
            return new MapAlternatePlacesParcelable(in);
        }

        public MapAlternatePlacesParcelable[] newArray(int size) {
            return new MapAlternatePlacesParcelable[size];
        }
    };
}