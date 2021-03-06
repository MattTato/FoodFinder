package com.osu.tatoczenko.foodfinder;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 *
 * Lists out the different food types that a user can choose to see restaurants about.
 */
public class FoodTypeFragment extends Fragment implements OnClickListener{

    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    final String browserAPIKey = "AIzaSyB6idw2Aj-V8s94RlaW92V-NyjHVFpjNAI";


    // Lists of the LatLng, Name, and PlaceID (for optimization purposes
    ArrayList<LatLng> mLatLngs = new ArrayList<>();
    ArrayList<String> mNames = new ArrayList<>();
    ArrayList<String> mPlaceIDs = new ArrayList<>();

    private AutoCompleteTextView autoComplete;

    // Adding to the list of suggestions is simple but would be tedious if it was to be made
    // all inclusive
    private static final String[] FOOD_TYPES = new String[] { "American", "Italian", "Mexican",
        "Chicken", "Hamburgers", "Greek", "Indian", "Pizza", "Breakfast", "Burgers", "Burritos",
        "Ice Cream", "Smoothies", "Milkshakes", "Hotdogs"};

    public FoodTypeFragment() {
        // Required empty public constructor
    }

    public void UpdatedLocation(Location location){
        mLocation = location;
    }

    public void UpdateGoogleAPIClient(GoogleApiClient googleApiClient){
        mGoogleApiClient = googleApiClient;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_food_type, container, false);
        // Setup autocomplete field
        autoComplete = (AutoCompleteTextView) v.findViewById(R.id.autocomplete_foodtype_search);
        // Needed for landscape view of the AutoCompleteTextView, which has a Done button
        autoComplete.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Closes the keyboard in landscape view, returning the user to the regular fragment view
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        // Make an adapter for the autocomplete field based on the FOOD_TYPES string array above
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                   android.R.layout.simple_list_item_1, FOOD_TYPES);
        autoComplete.setAdapter(adapter);

        // Set the onclicklisteners
        v.findViewById(R.id.foodtype_map_button).setOnClickListener(this);
        v.findViewById(R.id.clear_foodtype_search_button).setOnClickListener(this);
        View btnBack = v.findViewById(R.id.foodtype_back_button);
        btnBack.setOnClickListener(this);
        return v;
    }


    private void parseJSONForPlaceIDs(String json) {
        // Keywords that indicate info in the json
        final String latString = "lat";
        final String lngString = "lng";
        final String nameString = "name";
        final String idString = "place_id";

        // Clear the ArrayLists
        mPlaceIDs.clear();
        mNames.clear();
        mLatLngs.clear();

        int index;
        if (json.contains(idString)) { // If this returns true, then at least one more place remains
            // In the JSON, the order of information goes: lat, lng, name, place_id
            index = json.indexOf(latString); // first occurrence of "lat"
            while (index != -1) {
                // Grab latitude
                int start = index + latString.length() + 4;
                int end = json.indexOf(",", start);
                // Lat and Lng both of format (-)##.###### (decimal not always this long)
                // Lat ended with a comma, Lng ended with a }
                String lat = json.substring(start, end);
                Log.d("FoodTypeFragment: ", "Lat: " + lat);

                // Grab longitude
                index = json.indexOf(lngString, end);
                start = index + lngString.length() + 4;
                end = json.indexOf("}", start);
                String lng = json.substring(start, end);
                Log.d("FoodTypeFragment: ", "Lng: " + lng);
                LatLng ll = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                Log.d("FoodTypeFragment: ", "LatLng: " + ll.toString());

                // Grab name, which is of format "name" : "name here"
                index = json.indexOf(nameString, end);
                start = index + nameString.length() + 5;
                end = json.indexOf("\"", start);
                String name = json.substring(start, end);
                Log.d("FoodTypeFragment: ", "Name: " + name);

                // from the index of the beginning of "places_id", the id starts five characters
                // after the end of "places_id" and is 27 characters long
                // "place_id" : "placeID here"
                index = json.indexOf(idString, end);
                start = index + idString.length() + 5;
                end = start + 27;
                String placeID = json.substring(start, end);
                Log.d("FoodTypeFragment: ", "Place_ID: " + placeID);

                // Get next occurrence of lat
                index = json.indexOf(latString, end);
                // Add all information to appropriate lists
                mPlaceIDs.add(placeID);
                mLatLngs.add(ll);
                mNames.add(name);
            }
        }
        mapPlaces();
    }

    private void mapPlaces() {
        // Map the places
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        FoodMapFragment mapFragment = new FoodMapFragment();
        mapFragment.SetupMarkerLocation(mLocation);
        mapFragment.GetFoodPlaces(mLatLngs, mNames, mPlaceIDs);
        fragmentTransaction.replace(R.id.mainFrameDetails, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.clear_foodtype_search_button:
                autoComplete.setText("");
                break;
            case R.id.foodtype_back_button:
                getFragmentManager().popBackStack();
                break;
            case R.id.foodtype_map_button:
                if(mLocation != null) {
                    // Use the Google Places Browser API to make a more general call
                    // than the other parts of the app, specifically using whatever is currently
                    // in the text field
                    // This call will search from the user's current location in a 3000 meter radius
                    String queryURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                            + mLocation.getLatitude() + "," + mLocation.getLongitude() + "&radius=3000"
                            + "&types=food&keyword=" + autoComplete.getText() + "&key="
                            + browserAPIKey;
                    Log.d("FoodTypeFragment: ", "URL: " + queryURL);
                    try {
                        new GetPlacesInfoFromWeb().execute(new URL(queryURL));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    CharSequence textToDisplay = "Please turn on GPS, Wi-Fi, or Mobile Data to get your location";
                    Toast toast = Toast.makeText(getActivity(), textToDisplay, Toast.LENGTH_LONG);
                    toast.show();
                }
                break;
        }
    }

    private class GetPlacesInfoFromWeb extends AsyncTask<URL, Integer, String> {
        @Override
        protected String doInBackground(URL... url) {
            String response = "";
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(String.valueOf(url[0]));
            try {
                HttpResponse execute = client.execute(httpGet);
                InputStream content = execute.getEntity().getContent();
                BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                String s;
                // Reading in the HTTP response to a string
                while ((s = buffer.readLine()) != null) {
                    response += s;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("FoodTypeFragment: ", "Here are the json results: " + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            parseJSONForPlaceIDs(result);
        }
    }
}
