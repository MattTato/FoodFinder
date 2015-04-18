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

    public static int i=-1;

    Place zPlace;
    String zPlaceId=null;

    private Marker lastMarkerClicked;

    private static Location currentLocation;
    private ArrayList<Place> mPlaces = new ArrayList<>();

    // Alternate information for the places
    private ArrayList<LatLng> mLatLngs = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mPlaceIDs = new ArrayList<>();

    // The way the code currently is, either mPlaces will have stuff in it while the others are
    // empty, or mPlaces will be empty while the others have stuff in them

    private static final String PARCELABLELIST = "MapMarkerList";

    @Override
    public void onResume() {
        super.onResume();
        //if(hasNetworkConnection()) {
            setUpMapIfNeeded();
        /*} else {
            CharSequence textToDisplay = "Please turn on Wi-Fi or Mobile Data";
            Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_LONG);
            toast.show();
        } */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        View btnFav = v.findViewById(R.id.favorite_button);
        btnFav.setOnClickListener(this);
        CloseKeyboard(v);

        if(savedInstanceState != null){
            ArrayList<MapPlacesParcelable> list = savedInstanceState.getParcelableArrayList(PARCELABLELIST);
            for(MapPlacesParcelable mapPlace : list){
                Log.d("Place stuff: ", mapPlace.toString());
                mPlaces.add(mapPlace.place);
            }
        }

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.mapFrame);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().replace(R.id.mapFrame, mapFragment).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    // Needed in order to have the map markers stay on the map when you rotate the screen
    @Override
    public void onSaveInstanceState(Bundle outState){
        ArrayList<MapPlacesParcelable> list = new ArrayList<>();
        for (Place place : mPlaces) {
            Log.d("Test: ", place.getId());
            list.add(new MapPlacesParcelable(place));
        }
        outState.putParcelableArrayList(PARCELABLELIST, list);
        super.onSaveInstanceState(outState);
    }

    public void SetupMarkerLocation(Location location) {
        currentLocation = location;
    }

    public void GetFoodPlaces(ArrayList<Place> places) {
        mPlaces = places;
    }

    public void GetFoodPlaces(ArrayList<LatLng> latlngs, ArrayList<String> names, ArrayList<String>
                              placeIDs) { mLatLngs = latlngs; mNames = names; mPlaceIDs = placeIDs; }

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
                // Add marker for the current location of the user, center it on the current location,
                // then add the searched for or saved places to the map
                if(currentLocation != null) {
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
                } else {
                    CharSequence textToDisplay = "Please turn on GPS, Wi-Fi, or Mobile Data to get your location";
                    Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        }
    }

    private void ZoomCameraIn() {
        if (mPlaces != null) {
            LatLngBounds.Builder cameraBoundsBuilder = new LatLngBounds.Builder();
            cameraBoundsBuilder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            // Only one of the following for loops will actually do anything
            // Include each LatLng that the mPlaces and mLatLngs arrays contain so that
            // the locations that were searched for will be visible on screen without
            // the need to zoom out or pan
            for (Place place : mPlaces) {
                cameraBoundsBuilder.include(place.getLatLng());
                Log.d((String)place.getName(), place.getLatLng().toString());
            }
            for (int i = 0; i < mLatLngs.size(); i++) {
                cameraBoundsBuilder.include(mLatLngs.get(i));
            }
            LatLngBounds cameraBounds = cameraBoundsBuilder.build();
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(cameraBounds, 0));
            mMap.moveCamera(CameraUpdateFactory.zoomOut());
        }
    }

    private void AddFoodPlacesToMap() {
        // If SearchFragment or SavedLocations was used, mPlaces will have info
        // If FoodTypeFragment was used, then the other three lists will have the info
        if (mPlaces != null) {
            if(mPlaces.size() > 0) {
                for (Place place : mPlaces) {
                    mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                }
            } else if (mPlaceIDs.size() > 0 && mPlaceIDs.size() == mNames.size() && mPlaceIDs.size() == mLatLngs.size()) {
                // Checking if all the arrays are the same size is probably unnecessary but whatever
                 int i;
                 for (i = 0; i < mPlaceIDs.size(); i++) {
                     mMap.addMarker(new MarkerOptions().position(mLatLngs.get(i)).title(mNames.get(i)));
                 }
            } else if(!hasNetworkConnection()){
                AskUserToTurnOnNetwork();
            } else {
                NotifyUserThatOnlyCurrentLocationIsDisplayed();
            }
        }
    }

    private void AskUserToTurnOnNetwork() {
        CharSequence textToDisplay = "Please turn on Wi-Fi or Mobile Data to find food places";
        Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_LONG);
        toast.show();
    }

    private void NotifyUserThatOnlyCurrentLocationIsDisplayed(){
        CharSequence textToDisplay = "Just showing current location";
        Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public boolean onMarkerClick(Marker m) {
        // Store the last marker clicked so it can be saved as a favorite later
        // should the user want
        lastMarkerClicked = m;
        return false;
        // return of false means nothing, just included because it needs it
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorite_button:
                if(lastMarkerClicked != null) {
                    LatLng markerLatLng = lastMarkerClicked.getPosition();
                    // Only one of the following for loops will actually do anything
                    // Depending on which arrays have info in them, the objects in said array
                    // will be checked against the LatLng of the last clicked marker to find
                    // a match, and then the matching PlaceID will be saved in the database
                    for (int p = 0; p < mPlaces.size(); p++) {
                        if (mPlaces.get(p).getLatLng().equals(markerLatLng)) {
                            zPlace = mPlaces.get(p);
                            String TAG4 = "zplaceIDis.....";
                            Log.i(TAG4, zPlace.getId());

                            //placeID of place they want to favorite
                            zPlaceId = String.valueOf(zPlace.getId());

                            addToDatabase(zPlaceId, zPlace.getName(), v);
                            break;
                        }
                    }
                    for (int i = 0; i < mPlaceIDs.size(); i++) {
                        if (mLatLngs.get(i).equals(markerLatLng)) {
                            addToDatabase(mPlaceIDs.get(i), mNames.get(i), v);
                            break;
                        }
                    }
                } else {
                    CharSequence textToDisplay = "Please click a marker to save it as a favorite.";
                    Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_SHORT);
                    toast.show();
                }
                break;
        }
    }

    private void addToDatabase(String placeID, CharSequence name, View v) {
        DbOperator db = new DbOperator(v.getContext());
        if (db.addToDatabase(placeID)) {
            CharSequence textToDisplay = name + " has been saved!";
            Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            CharSequence textToDisplay = name + " is already a favorite";
            Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void setMarkerByLocation(Location location){
        if(mMarker != null){
            mMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    // TODO: maybe some comments here just to explain? I dunno
    private	boolean hasNetworkConnection(){
        ConnectivityManager connectivityManager	=
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo	=
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isConnected;
        boolean isWifiAvailable	=	networkInfo.isAvailable();
        boolean isWifiConnected	=	networkInfo.isConnected();
        networkInfo	=
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileAvailable	=	networkInfo.isAvailable();
        boolean isMobileConnnected	=	networkInfo.isConnected();
        isConnected	=	(isMobileAvailable&&isMobileConnnected)	||
                (isWifiAvailable&&isWifiConnected);
        return(isConnected);
    }
}