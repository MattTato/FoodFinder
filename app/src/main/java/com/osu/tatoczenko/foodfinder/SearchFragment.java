package com.osu.tatoczenko.foodfinder;


import android.content.res.Resources;
import android.location.Location;
import android.net.Uri;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceTypes;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 * Allows a user to search for a specific restaurant.
 * Most of the code was taken from the Places Autocomplete Sample Project, but adapted to work for our app.
 */
public class SearchFragment extends Fragment implements OnClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Location mLocation;

    private GoogleApiClient mGoogleApiClient;

    Place searchedFoodPlace;
    ArrayList<Place> mPlaces = new ArrayList<>();

    private AutoCompleteTextView mAutocompleteView;
    private AutocompleteFilter mFilter;
    private PlaceAutocompleteAdapter mAdapter;
    private static final String TAG = "PlaceAutoCompleteAdapt";
    private LatLngBounds BOUNDS_FOOD_SEARCH;

    public SearchFragment() {
        // Required empty public constructor
    }

    public void UpdateLocation(Location location){
        mLocation = location;
        BOUNDS_FOOD_SEARCH = new LatLngBounds(new LatLng(mLocation.getLatitude() - 0.1, mLocation.getLongitude() - 0.1), new LatLng(mLocation.getLatitude() + 0.1, mLocation.getLongitude() + 0.1));
    }

    public void UpdateGoogleApiClient(GoogleApiClient googleApiClient){
        mGoogleApiClient = googleApiClient;
    }

    void CreatePlaceFilters(){
        Log.d("Showing all places: ", PlaceTypes.ALL.toString());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        mAutocompleteView = (AutoCompleteTextView) v.findViewById(R.id.autocomplete_food_search);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);

        //CreatePlaceFilters();
        mAdapter = new PlaceAutocompleteAdapter(getActivity(), android.R.layout.simple_list_item_1, BOUNDS_FOOD_SEARCH, null );
        mAutocompleteView.setAdapter(mAdapter);
        mAdapter.setGoogleApiClient(mGoogleApiClient);
        mAdapter.setBounds(BOUNDS_FOOD_SEARCH);

        View btnClear = v.findViewById(R.id.clear_search_button);
        btnClear.setOnClickListener(this);
        View btnMap = v.findViewById(R.id.map_button);
        btnMap.setOnClickListener(this);
        View btnBack = v.findViewById(R.id.searchback_button);
        btnBack.setOnClickListener(this);
        return v;
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

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
              details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
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
            mPlaces.clear();
            mPlaces.add(searchedFoodPlace);

            /* Format details of the place for display and show it in a TextView.
            mPlaceDetailsText.setText(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri())); */

            Log.i(TAG, "Place details received: " + searchedFoodPlace.getName());
        }
    };

    /*private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        Log.e(TAG, res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    } */

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
