package com.osu.tatoczenko.foodfinder;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainMenuFragment extends Fragment implements OnClickListener{

    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    FoodTypeFragment foodTypeFragment;
    SearchFragment searchFragment;
    SavedLocationsFragment savedLocationsFragment;

    public void UpdatedLocation(Location location){
        mLocation = location;
    }

    public void UpdateGoogleAPIClient(GoogleApiClient googleApiClient){
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

    public void onClick(View v) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        switch(v.getId()){
            case R.id.findfood_button:
                fragmentTransaction = fragmentManager.beginTransaction();
                foodTypeFragment = new FoodTypeFragment();
                // Sends Google API client to do Place Autocomplete and Map calls later
                foodTypeFragment.UpdateGoogleAPIClient(mGoogleApiClient);
                // Sends current user's location to be used in the map
                foodTypeFragment.UpdatedLocation(mLocation);
                fragmentTransaction.replace(R.id.mainFrameDetails, foodTypeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.search_button:
                fragmentTransaction = fragmentManager.beginTransaction();
                searchFragment = new SearchFragment();
                searchFragment.UpdateLocation(mLocation);
                searchFragment.UpdateGoogleApiClient(mGoogleApiClient);
                fragmentTransaction.replace(R.id.mainFrameDetails, searchFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.favorites_button:
                fragmentTransaction = fragmentManager.beginTransaction();
                savedLocationsFragment = new SavedLocationsFragment();
                // Sends Google API client to do Place Autocomplete and Map calls later
                savedLocationsFragment.UpdateGoogleAPIClient(mGoogleApiClient);
                // Sends current user's location to be used in the map
                savedLocationsFragment.UpdatedLocation(mLocation);
                fragmentTransaction.replace(R.id.mainFrameDetails, savedLocationsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.exit_button:
                getActivity().finish();
                break;

        }
    }
}
