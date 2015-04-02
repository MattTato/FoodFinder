package com.osu.tatoczenko.foodfinder;


import android.location.Location;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 * Allows a user to pull up any saved locations and find them on a map again.
 */
public class SavedLocationsFragment extends Fragment implements OnClickListener {

    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    ArrayList<Place> mPlaces = new ArrayList<>();



    public SavedLocationsFragment() {
        // Required empty public constructor
    }

    public void UpdatedLocation(Location location){
        mLocation = location;
    }

    public void UpdateGoogleAPIClient(GoogleApiClient googleApiClient){
        mGoogleApiClient = googleApiClient;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_saved_locations, container, false);

        View btnBack = v.findViewById(R.id.savedlocback_button);
        btnBack.setOnClickListener(this);


        return v;


    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.savedlocback_button:
                getFragmentManager().popBackStack();
                break;
            //case R.id.favorite_button:
                //Button myButton = new Button(this);
                //myButton.setText("NEW Added Button");
                //LinearLayout ll = (LinearLayout)findViewById(R.id.buttonlayout);
                //LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                //ll.addView(myButton, lp);



                //break;

            /*
            Add the below code to whatever sort of button system you decide to implement. You may want each saved location to be its own button, but that's just my thought on it.
            If each saved place is it's own button, you should then save and load the Place object of the location in the database, making this part much easier.
            If you just save the name and the latitude and longitude, then you'll have to manually build the Place object.
            In the FoodMapFragment, you will need to add something to the markers displayed for the food that allows a function call to save that place data in a database.
            The code below sends the ArrayList of Place values mPlaces to the map, and the map uses that place data to build the markers to put on the map for the food locations.
                fragmentTransaction = fragmentManager.beginTransaction();
                FoodMapFragment mapFragment = new FoodMapFragment();
                mapFragment.SetupMarkerLocation(mLocation);
                mapFragment.GetFoodPlaces(mPlaces);
                fragmentTransaction.replace(R.id.mainFrameDetails, mapFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
             */
        }
    }


}
