package com.osu.tatoczenko.foodfinder;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.ColorStateList;
import android.location.Location;
import android.util.Log;
import android.view.Gravity;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    Place savedFoodPlace;
    int numOfPlaces;

    private static final String TAG = "PlaceAutoCompleteAdapt";

    public SavedLocationsFragment() {
        // Required empty public constructor
    }

    public void UpdatedLocation(Location location){
        mLocation = location;
    }

    public void UpdateGoogleAPIClient(GoogleApiClient googleApiClient){
        mGoogleApiClient = googleApiClient;
    }

    public void UpdatePlacesList(ArrayList<Place> places){
        mPlaces = places;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_saved_locations, container, false);

        View btnBack = v.findViewById(R.id.savedlocback_button);
        btnBack.setOnClickListener(this);

        // Get the inner LinearLayout of the SavedLocationsFragment layout to fill with buttons
        LinearLayout savedLocLL = (LinearLayout)v.findViewById(R.id.savedloc_LL);
        LinearLayout.LayoutParams savedLocButtonParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // If there are places, fill the layout with buttons!
        numOfPlaces = 0;
        for(Place places : mPlaces){
            Log.d("Place name", (String)places.getName());
            Button savedLocButton = new Button(getActivity());
            savedLocButton.setLayoutParams(savedLocButtonParams);
            savedLocButton.setGravity(Gravity.CENTER_HORIZONTAL);
            savedLocButton.setText(places.getName() + ": " + places.getAddress());
            savedLocButton.setId(numOfPlaces);
            savedLocButton.setOnClickListener(this);
            numOfPlaces++;
            savedLocLL.addView(savedLocButton);
        }
        if(numOfPlaces == 0){
            TextView textView = new TextView(getActivity());
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setTextSize(20f);
            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(textParams);
            textView.setText("You don't seem to have any saved locations");
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            savedLocLL.addView(textView);
        }
        return v;
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.savedlocback_button:
                getFragmentManager().popBackStack();
                break;
            // Since I don't know the exact ID, I can't do cases for it. Send it to the default!
            default:
                // Get the number of the ID of the button clicked and add that one to the map
                int id = v.getId();
                if(id < numOfPlaces) {
                    Place place = mPlaces.get(id);
                    Log.d((String)place.getName(), "This is the place you picked");
                    ArrayList<Place> savedLocPlace = new ArrayList<>();
                    savedLocPlace.add(place);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction;
                    fragmentTransaction = fragmentManager.beginTransaction();
                    FoodMapFragment mapFragment = new FoodMapFragment();
                    mapFragment.SetupMarkerLocation(mLocation);
                    mapFragment.GetFoodPlaces(savedLocPlace);
                    fragmentTransaction.replace(R.id.mainFrameDetails, mapFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                break;
        }
    }
}
