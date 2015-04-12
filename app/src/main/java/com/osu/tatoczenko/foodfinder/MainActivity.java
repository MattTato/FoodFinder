package com.osu.tatoczenko.foodfinder;

import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;


public class MainActivity extends Activity implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {
    String message = "Android_Log_Test : ";

    protected GoogleApiClient mGoogleApiClient;
    Location mLocation;
    LocationRequest mLocationRequest;
    MainMenuFragment menuFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buildGoogleApiClient();
        createLocationRequest();
        mGoogleApiClient.connect();

        //create database
        DbOperator db = new DbOperator(this);

        Log.d(message, "The onCreate() event");
        if (savedInstanceState == null) {
            menuFragment = new MainMenuFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.mainFrameDetails, menuFragment, "MainMenuFragment");
            fragmentTransaction.commit();
        } else {
            FragmentManager fragmentManager = getFragmentManager();
            menuFragment = (MainMenuFragment) fragmentManager.findFragmentByTag("MainMenuFragment");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(message, "The onStart() event");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(message, "The onResume() event");
        if(!mGoogleApiClient.isConnected()){
            Log.d(message, "Reconnecting to Google API");
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(message, "The onPause() event");
        stopLocationUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(message, "The onStop() event");
        mGoogleApiClient.disconnect();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(message, "The onDestroy() event");
    }

    /*
        We won't be doing anything with an options menu, so I commented this part out.
        If we decide to add items to the options menu, we can put this back in later
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    } */

    @Override
    public void onConnected(Bundle connectionHint){
        startLocationUpdates();
        mLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        if(menuFragment != null) {
            Log.d(message, "Updating Menu location and API client");
            menuFragment.UpdatedLocation(mLocation);
            menuFragment.UpdateGoogleAPIClient(mGoogleApiClient);
        } else {
            Log.d(message, "Couldn't update Menu Location and API client");
        }
    }

    @Override
    public void onConnectionSuspended(int i){
        // Do things
    }

    @Override
    public void onLocationChanged(Location location){
        mLocation = location;
        if(menuFragment != null) {
            menuFragment.UpdatedLocation(mLocation);
        }
    }

    protected void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void createLocationRequest() {
        if(mLocationRequest == null) {
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(10000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        }
    }

    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API).build();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        mGoogleApiClient.disconnect();
    }




}
