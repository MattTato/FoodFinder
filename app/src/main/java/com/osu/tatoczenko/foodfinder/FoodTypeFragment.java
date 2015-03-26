package com.osu.tatoczenko.foodfinder;


import android.location.Location;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;


/**
 * A simple {@link Fragment} subclass.
 *
 * Lists out the different food types that a user can choose to see restaurants about.
 */
public class FoodTypeFragment extends Fragment implements OnClickListener{

    GoogleApiClient mGoogleApiClient;
    Location mLocation;

    public FoodTypeFragment() {
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
        View v = inflater.inflate(R.layout.fragment_food_type, container, false);
        View btnBack = v.findViewById(R.id.foodtype_back_button);
        btnBack.setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.foodtype_back_button:
                getFragmentManager().popBackStack();
                break;
        }

        /*
            Add the below code to whatever sort of button system you decide to implement. I assume each food type will be its own button, but implement whatever way you see fit.
            The calls I do for the Autocomplete searching are different than what you will have to do. Take a look at the PlaceAutcompleteAdapter class to see how it makes the calls.
            You'll need to do the same calls that the performFiltering method in the getFilter() method does, but just use that place data directly instead of waiting for a user to click it.
            It won't be the best and may require some manual setup, as the Places API for Android does searching based solely on name, while the Web API can do general searching.

            The other way to do this is to do the Web call to the Web Places API, which you will then need to parse JSON requests from. To do that call, you'll need to look up the calls for that API.
            You will need an API key to complete those calls. You can get that key from the strings.xml file as the only key string in there.
            This will work better for the data we get and require less manual setup, but requires parsing. Most of the code should already be online in the Google Developer Academy, but may require a bit more effort.

            The code below sends the ArrayList of Place values mPlaces to the map, and the map uses that place data to build the markers to put on the map for the food locations.
            This will probably need to be attached to each button created for this menu.
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
