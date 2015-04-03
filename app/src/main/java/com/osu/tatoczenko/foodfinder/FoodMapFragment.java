package com.osu.tatoczenko.foodfinder;


import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 * The map fragment that will be used to display locations of restaurants.
 * Most of code taken from UIBasicsSample, as it handled a lot of the location setup information needed for the map already.
 */
public class FoodMapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private GoogleMap mMap;
    private static Marker mMarker;

    private static String TAG4 = "zplaceIDis.....";
    public static int i=-1;

    Place zPlace;
    String zPlaceId=null;

    private Marker lastMarkerClicked;

    private static Location currentLocation;
    private ArrayList<Place> mPlaces = new ArrayList<>();

    private MapFragment mapFragment;
    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        View btnFav = v.findViewById(R.id.favorite_button);
        btnFav.setOnClickListener(this);
        CloseKeyboard(v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mapFragment = (MapFragment) fm.findFragmentById(R.id.mapFrame);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.mapFrame, mapFragment).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void SetupMarkerLocation(Location location) {
        currentLocation = location;
    }

    public void GetFoodPlaces(ArrayList<Place> places) {
        mPlaces = places;
    }

    private void CloseKeyboard(View v) {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.mapFrame))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setOnMarkerClickListener(this);
                UiSettings mapSettings = mMap.getUiSettings();
                mapSettings.setAllGesturesEnabled(true);
                mapSettings.setZoomControlsEnabled(true);
                mapSettings.setMyLocationButtonEnabled(true);
                setMarkerByLocation(currentLocation);
                mMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude())).title("Your location"));
                mMarker.showInfoWindow();
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 14f));
                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                    @Override
                    public void onMapLoaded() {
                        AddFoodPlacesToMap();
                        ZoomCameraIn();
                    }
                });
            }
        }
    }

    private void ZoomCameraIn() {
        if (mPlaces != null) {
            LatLngBounds.Builder cameraBoundsBuilder = new LatLngBounds.Builder();
            cameraBoundsBuilder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            for (Place place : mPlaces) {
                cameraBoundsBuilder.include(place.getLatLng());
                Log.d((String)place.getName(), place.getLatLng().toString());
            }
            LatLngBounds cameraBounds = cameraBoundsBuilder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(cameraBounds, 0));
            mMap.moveCamera(CameraUpdateFactory.zoomOut());
        }
    }

    private void AddFoodPlacesToMap() {
        if (mPlaces != null) {
            for (Place place : mPlaces) {
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker m) {
    //lastClickedMarker
        lastMarkerClicked = m;
        return false;
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_button:
                int p = 0;
                for (p = 0; p < mPlaces.size(); p++) {
                    LatLng markerLatLng = lastMarkerClicked.getPosition();
                    if (mPlaces.get(p).getLatLng().equals(markerLatLng)) {
                        zPlace = mPlaces.get(p);

                        Log.i(TAG4, zPlace.getId());
                        break;
                    }
                }
                //placeID of place they want to favorite
                zPlaceId = String.valueOf(zPlace.getId());

                DbOperator db = new DbOperator(v.getContext());
                if(db.addToDatabase(zPlaceId)){
                    CharSequence textToDisplay = zPlace.getName() + " has been saved!";
                    Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    CharSequence textToDisplay = zPlace.getName() + " is already a favorite";
                    Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    private void setMarkerByLocation(Location location){
        if(mMarker != null){
            mMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    // if you want to check connectivity, use this method. you can disable the map if network is not available.
    private boolean networkIsAvailable() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }
}