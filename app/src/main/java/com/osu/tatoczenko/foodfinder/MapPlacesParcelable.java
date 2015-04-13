package com.osu.tatoczenko.foodfinder;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.location.places.Place;

/**
 * Created by Matt Tatoczenko on 4/12/2015.
 */
public class MapPlacesParcelable implements Parcelable {
        Place place;

        public MapPlacesParcelable(Place place) {
            this.place = place;
        }

        private MapPlacesParcelable(Parcel in) {

        }

        public int describeContents() {
            return 0;
        }

        @Override
        public String toString() {
            return this.place.toString();
        }

        public void writeToParcel(Parcel out, int flags) {

        }

        public final Parcelable.Creator<MapPlacesParcelable> CREATOR = new Parcelable.Creator<MapPlacesParcelable>() {
            public MapPlacesParcelable createFromParcel(Parcel in) {
                return new MapPlacesParcelable(in);
            }

            public MapPlacesParcelable[] newArray(int size) {
                return new MapPlacesParcelable[size];
            }
        };
    }

