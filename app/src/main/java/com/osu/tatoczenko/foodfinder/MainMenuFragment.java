package com.osu.tatoczenko.foodfinder;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment for the Main Menu. This is the first fragment that pops up when the app is opened
 * Each of the other fragments gets started from this one.
 */
public class MainMenuFragment extends Fragment implements OnClickListener{

    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    FoodTypeFragment foodTypeFragment;
    SearchFragment searchFragment;
    SavedLocationsFragment savedLocationsFragment;
    ArrayList<Place> mPlaces = new ArrayList<>();
    Place savedFoodPlace;

    private static final String FOODTYPEFRAGMENTNAME = "FoodTypeFragment";
    private static final String SEARCHFRAGMENTNAME = "SearchFragment";
    private static final String SAVEDLOCATIONSFRAGMENTNAME = "SavedLocationsFragment";

    public void UpdatedLocation(Location location){
        mLocation = location;
        FragmentManager fragmentManager = getFragmentManager();
        foodTypeFragment = (FoodTypeFragment) fragmentManager.findFragmentByTag(FOODTYPEFRAGMENTNAME);
        searchFragment = (SearchFragment) fragmentManager.findFragmentByTag(SEARCHFRAGMENTNAME);
        savedLocationsFragment = (SavedLocationsFragment) fragmentManager.findFragmentByTag(SAVEDLOCATIONSFRAGMENTNAME);
        if(foodTypeFragment != null){
            foodTypeFragment.UpdatedLocation(mLocation);
        } else {
            Log.d("Locations:", "Couldn't update Food Type location");
        }
        if(searchFragment != null){
            // Sends current user's location to be used in the map
            searchFragment.UpdateLocation(mLocation);
        } else {
            Log.d("Locations:", "Couldn't update Search location");
        }
        if(savedLocationsFragment != null){
            // Sends current user's location to be used in the map
            savedLocationsFragment.UpdatedLocation(mLocation);
        }else {
            Log.d("Locations:", "Couldn't update Saved Locations location");
        }
    }

    public void UpdateGoogleAPIClient(GoogleApiClient googleApiClient){
        mGoogleApiClient = googleApiClient;
        FragmentManager fragmentManager = getFragmentManager();
        foodTypeFragment = (FoodTypeFragment) fragmentManager.findFragmentByTag(FOODTYPEFRAGMENTNAME);
        searchFragment = (SearchFragment) fragmentManager.findFragmentByTag(SEARCHFRAGMENTNAME);
        savedLocationsFragment = (SavedLocationsFragment) fragmentManager.findFragmentByTag(SAVEDLOCATIONSFRAGMENTNAME);
        if(foodTypeFragment != null){
            // Sends Google API client to do Place Autocomplete and Map calls later
            foodTypeFragment.UpdateGoogleAPIClient(mGoogleApiClient);
        }
        if(searchFragment != null){
            // Sends Google API client to do Place Autocomplete and Map calls later
            searchFragment.UpdateGoogleApiClient(mGoogleApiClient);
        }
        if(savedLocationsFragment != null){
            // Sends Google API client to do Place Autocomplete and Map calls later
            savedLocationsFragment.UpdateGoogleAPIClient(mGoogleApiClient);
        }
        GetSavedPlaces();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Used for refreshing the list of saved places
        if(mGoogleApiClient != null){
            GetSavedPlaces();
        }
        View rootView = inflater.inflate(R.layout.main_menu, container, false);
        View btnFind = rootView.findViewById(R.id.findfood_button);
        btnFind.setOnClickListener(this);
        View btnSearch = rootView.findViewById(R.id.search_button);
        btnSearch.setOnClickListener(this);
        View btnFavorites = rootView.findViewById(R.id.favorites_button);
        btnFavorites.setOnClickListener(this);
        View btnExit = rootView.findViewById(R.id.exit_button);
        btnExit.setOnClickListener(this);
        return rootView;
    }

    private void GetSavedPlaces(){
        mPlaces.clear();
        DbOperator mDatabase = new DbOperator(getActivity());
        List<SavedFoodLocation> savedFoodLocationList = mDatabase.getAllLoc();
        for(SavedFoodLocation savedFoodLocation: savedFoodLocationList){
            String placeId = savedFoodLocation.getRestId();
            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            // Will be called after we get results from the Places API about the place with the ID we sent it
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    }

    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("PlacesAPI:", "Place query did not complete. Error: " + places.getStatus().toString());

                return;
            }
            // Get the Place object from the buffer.
            savedFoodPlace = places.get(0);
            mPlaces.add(savedFoodPlace);
        }
    };

    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        switch(v.getId()){
            case R.id.findfood_button:
                fragmentTransaction = fragmentManager.beginTransaction();
                foodTypeFragment = new FoodTypeFragment();
                if(mGoogleApiClient != null) {
                    // Sends Google API client to do Place Autocomplete and Map calls later
                    foodTypeFragment.UpdateGoogleAPIClient(mGoogleApiClient);
                }
                if(mLocation != null) {
                    // Sends current user's location to be used in the map
                    foodTypeFragment.UpdatedLocation(mLocation);
                }
                fragmentTransaction.replace(R.id.mainFrameDetails, foodTypeFragment, FOODTYPEFRAGMENTNAME);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.search_button:
                fragmentTransaction = fragmentManager.beginTransaction();
                searchFragment = new SearchFragment();
                if(mGoogleApiClient != null) {
                    // Sends Google API client to do Place Autocomplete and Map calls later
                    searchFragment.UpdateGoogleApiClient(mGoogleApiClient);
                }
                if(mLocation != null) {
                    // Sends current user's location to be used in the map
                    searchFragment.UpdateLocation(mLocation);
                }
                fragmentTransaction.replace(R.id.mainFrameDetails, searchFragment, SEARCHFRAGMENTNAME);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.favorites_button:
                fragmentTransaction = fragmentManager.beginTransaction();
                savedLocationsFragment = new SavedLocationsFragment();
                if(mGoogleApiClient != null) {
                    // Sends Google API client to do Place Autocomplete and Map calls later
                    savedLocationsFragment.UpdateGoogleAPIClient(mGoogleApiClient);
                }
                if(mLocation != null) {
                    // Sends current user's location to be used in the map
                    savedLocationsFragment.UpdatedLocation(mLocation);
                }
                // Needed because the results callback didn't happen in time for it to be done right in the OnCreateView of SavedLocationsFragment
                savedLocationsFragment.UpdatePlacesList(mPlaces);
                fragmentTransaction.replace(R.id.mainFrameDetails, savedLocationsFragment, SAVEDLOCATIONSFRAGMENTNAME);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.exit_button:
                getActivity().finish();
                break;
        }
    }
}
