package com.osu.tatoczenko.foodfinder;


import android.location.Location;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 *
 * Allows a user to search for a specific restaurant.
 */
public class SearchFragment extends Fragment implements OnClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Location mLocation;

    public SearchFragment() {
        // Required empty public constructor
    }

    public void UpdateLocation(Location location){
        mLocation = location;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        View btnMap = v.findViewById(R.id.map_button);
        btnMap.setOnClickListener(this);
        View btnBack = v.findViewById(R.id.searchback_button);
        btnBack.setOnClickListener(this);
        return v;
    }

    public void onClick(View v){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        switch(v.getId()){
            case R.id.map_button:
                fragmentTransaction = fragmentManager.beginTransaction();
                FoodMapFragment mapFragment = new FoodMapFragment();
                mapFragment.SetupMarkerLocation(mLocation);
                fragmentTransaction.replace(R.id.mainFrameDetails, mapFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
            case R.id.searchback_button:
                getFragmentManager().popBackStack();
                break;
        }
    }


}
