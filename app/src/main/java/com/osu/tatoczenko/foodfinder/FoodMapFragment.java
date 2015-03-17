package com.osu.tatoczenko.foodfinder;


import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


/**
 * A simple {@link Fragment} subclass.
 *
 * The map fragment that will be used to display locations of restaurants.
 * Most of code taken from UIBasicsSample, as it handled a lot of the location setup information needed for the map already.
 */
public class FoodMapFragment extends Fragment {
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 10;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;
    private static final float SMALLEST_DISPLACEMENT = 5f;
    private GoogleMap mMap;
    private static Marker mMarker;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static Location currentLocation;
    private MapFragment mapFragment;
    private final String TAG = ((Object) this).getClass().getSimpleName();

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setupLocationClientIfNeeded();
        setupRequests();
        mGoogleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
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
        mGoogleApiClient.disconnect();
    }


    private void setupRequests(){
        if(mLocationRequest == null){
            mLocationRequest = LocationRequest.create();
            // Use high accuracy
            mLocationRequest.setPriority(
                    LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            // Set the update interval to 5 seconds
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            // Set the fastest update interval to 1 second
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            // if it doesnt work, feel free to debug it!
            mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        }

    }

    private void setupLocationClientIfNeeded(){
        if(mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API)
                            // add connection callbacks
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            PendingResult result =
                                    LocationServices
                                            .FusedLocationApi
                                            .requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
                                                @Override
                                                public void onLocationChanged(Location location) {

                                                    Toast.makeText(getActivity(), "Location updated", Toast.LENGTH_SHORT).show();
                                                    currentLocation = location;
                                                    LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                                    mMap.clear();
                                                    mMarker = mMap.addMarker(new MarkerOptions()
                                                                    .position(currentLatLng)
                                                                    .title("You Are Here")
                                                                    .snippet(location.getLatitude() +", "+ location.getLongitude())
                                                    );
                                                    mMarker.showInfoWindow();
                                                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
                                                }
                                            });
                            // do something with this result depending on the needs.
                        }

                        @Override
                        public void onConnectionSuspended(int i) {

                            // if connection is suspended, do something here

                        }
                    })
                            // add connection failed listner
                    .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult connectionResult) {

                            // handle when connection fails
                        }
                    })
                    .build();
        }
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((MapFragment) getChildFragmentManager().findFragmentById(R.id.mapFrame))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                UiSettings mapSettings = mMap.getUiSettings();
                mapSettings.setAllGesturesEnabled(false);
                mapSettings.setZoomControlsEnabled(false);
                mapSettings.setMyLocationButtonEnabled(true);
                setMarkerByLocation(currentLocation);

            }
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