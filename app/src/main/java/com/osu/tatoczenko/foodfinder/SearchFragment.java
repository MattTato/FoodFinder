package com.osu.tatoczenko.foodfinder;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 *
 * Allows a user to search for a specific restaurant.
 * Most of the code was taken from the Places Autocomplete Sample Project, but adapted to work for our app.
 */
public class SearchFragment extends Fragment implements OnClickListener{
    Location mLocation;

    private GoogleApiClient mGoogleApiClient;

    Place searchedFoodPlace;
    ArrayList<Place> mPlaces = new ArrayList<>();

    private AutoCompleteTextView mAutocompleteView;
    private AutocompleteFilter mFilter;
    private PlaceAutocompleteAdapter mAdapter;
    private static final String TAG = "PlaceAutoCompleteAdapt";
    private static final String TAG2 = "TYLER";
    private static final String PARCELABLELIST = "PlaceList";

    private LatLngBounds BOUNDS_FOOD_SEARCH;

    public SearchFragment() {
        // Required empty public constructor
    }

    public void UpdateLocation(Location location){
        mLocation = location;
        if(mLocation != null) {
            BOUNDS_FOOD_SEARCH = new LatLngBounds(new LatLng(mLocation.getLatitude() - 0.1, mLocation.getLongitude() - 0.1), new LatLng(mLocation.getLatitude() + 0.1, mLocation.getLongitude() + 0.1));
        } else {
            // Will cover all of the 48 contiguous US states
            BOUNDS_FOOD_SEARCH = new LatLngBounds(new LatLng(18.005611, -124.626080), new LatLng(48.987386, -62.361014));
        }
    }

    public void UpdateGoogleApiClient(GoogleApiClient googleApiClient){
        mGoogleApiClient = googleApiClient;
    }

    void CreatePlaceFilters(){
        List<Integer> placeList = new ArrayList<>();
        /*
            So Autocomplete only filters by certain things. So I can't filter by FOOD or RESTAURANT, but ESTABLISHMENT works.
            Here's the info: https://developers.google.com/places/supported_types
            For now, it's better than nothing, but not exactly what I want. May look into further filtering.
          */
        placeList.add(Place.TYPE_ESTABLISHMENT);
        mFilter = AutocompleteFilter.create(placeList);
        Log.d("Set has these places: ", mFilter.toString());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mAutocompleteView = (AutoCompleteTextView) v.findViewById(R.id.autocomplete_food_search);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAutocompleteView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        CreatePlaceFilters();
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, BOUNDS_FOOD_SEARCH, mFilter);
        mAutocompleteView.setAdapter(mAdapter);
        mAdapter.setGoogleApiClient(mGoogleApiClient);
        mAdapter.setBounds(BOUNDS_FOOD_SEARCH);

        View btnClear = v.findViewById(R.id.clear_search_button);
        btnClear.setOnClickListener(this);
        View btnMap = v.findViewById(R.id.map_button);
        btnMap.setOnClickListener(this);
        View btnBack = v.findViewById(R.id.searchback_button);
        btnBack.setOnClickListener(this);

        if(savedInstanceState != null){
            ArrayList<MapPlacesParcelable> list = savedInstanceState.getParcelableArrayList(PARCELABLELIST);
            for(MapPlacesParcelable mapPlace : list){
                Log.d("Place stuff: ", mapPlace.toString());
                mPlaces.add(mapPlace.place);
            }
        }

        return v;
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

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a PlaceAutocomplete object from which we
             read the place ID.
              */

            final PlaceAutocompleteAdapter.PlaceAutocomplete item = mAdapter.getItem(position);

            final String placeId = String.valueOf(item.placeId);
            Log.i(TAG, "Autocomplete item selected: " + item.description);
            Log.i(TAG, "Autocomplete item selected: " + placeId);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            // Will be called after we get results from the Places API about the place with the ID we sent it
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

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
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());

                return;
            }
            // Get the Place object from the buffer.
            searchedFoodPlace = places.get(0);
            Log.i(TAG2,"test" + searchedFoodPlace);
            // Add this Place object into the ArrayList for easy passing into the FoodMapFragment
            mPlaces.clear();
            mPlaces.add(searchedFoodPlace);

            Log.i(TAG, "Place details received: " + searchedFoodPlace.getName());
        }
    };

    public void onClick(View v){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        switch(v.getId()){
            case R.id.clear_search_button:
                mAutocompleteView.setText("");
                break;
            case R.id.map_button:
                fragmentTransaction = fragmentManager.beginTransaction();
                FoodMapFragment mapFragment = new FoodMapFragment();
                mapFragment.SetupMarkerLocation(mLocation);
                mapFragment.GetFoodPlaces(mPlaces);
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
