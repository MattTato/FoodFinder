package com.osu.tatoczenko.foodfinder;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


/**
 * A simple {@link Fragment} subclass.
 *
 * Lists out the different food types that a user can choose to see restaurants about.
 */
public class FoodTypeFragment extends Fragment implements OnClickListener{

    GoogleApiClient mGoogleApiClient;
    Location mLocation;
    final String browserAPIKey = "AIzaSyB6idw2Aj-V8s94RlaW92V-NyjHVFpjNAI";

    ArrayList<Place> mPlaces = new ArrayList<>();

    private AutoCompleteTextView autoComplete;

    // Adding to the list of suggestions is simple but would be tedious if it was to be made
    // all inclusive
    private static final String[] FOOD_TYPES = new String[] { "American", "Italian", "Mexican",
        "Chicken", "Hamburgers", "Greek", "Indian", "Pizza"};

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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
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
        final String idString = "place_id";
        ArrayList ids = new ArrayList<String>();
        int index = 0;
        if (json.contains(idString)) {
            index = json.indexOf(idString, index + idString.length());
            while (index != -1) {
                // from the index of the beginning of "places_id", the id starts five characters
                // after the end of "places_id" and is 27 characters long
                int start = index + idString.length() + 5;
                int end = start + 27;
                String placeID = json.substring(start, end);
                Log.d("FoodTypeFragment: ", "Place_ID: " + placeID);
                index = json.indexOf(idString, index + idString.length());
                ids.add(placeID);
            }
        }
        mapPlaces(ids);
    }

    private void mapPlaces(ArrayList<String> places) {
        // Get the places by id
        int i = 0;
        for (i = 0; i < places.size(); i++) {
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,
                    places.get(i));
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }

        // Map the places
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager.beginTransaction();
        FoodMapFragment mapFragment = new FoodMapFragment();
        mapFragment.SetupMarkerLocation(mLocation);
        mapFragment.GetFoodPlaces(mPlaces);
        fragmentTransaction.replace(R.id.mainFrameDetails, mapFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    // shamelessly taken from Matt's SearchFragment code since I don't know this places stuff as well as he does
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e("FoodTypeFragment: ", "Place query did not complete. Error: " + places.getStatus().toString());

                return;
            }
            // Get the Place object from the buffer.
            Place searchedFoodPlace = places.get(0);
            mPlaces.add(searchedFoodPlace);

            Log.i("FoodTypeFragment: ", "Place details received: " + searchedFoodPlace.getName());
        }
    };

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.clear_foodtype_search_button:
                autoComplete.setText("");
                break;
            case R.id.foodtype_back_button:
                getFragmentManager().popBackStack();
                break;
            case R.id.foodtype_map_button:
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
                break;
        }
    }

        /*
            Add the below code to whatever sort of button system you decide to implement. I assume each food type will be its own button, but implement whatever way you see fit.
            The calls I do for the Autocomplete searching are different than what you will have to do. Take a look at the PlaceAutcompleteAdapter class to see how it makes the calls.
            You'll need to do the same calls that the performFiltering method in the getFilter() method does, but just use that place data directly instead of waiting for a user to click it.
            It won't be the best and may require some manual setup, as the Places API for Android does searching based solely on name, while the Web API can do general searching.

            The other way to do this is to do the Web call to the Web Places API, which you will then need to parse JSON requests from. To do that call, you'll need to look up the calls for that API.
            You will need an API key to complete those calls. You can get that key from the strings.xml file as the only key string in there.
            This will work better for the data we get and require less manual setup, but requires parsing. Most of the code should already be online in the Google Developer Academy, but may require a bit more effort.

            The code below sends the ArrayList of Place values mPlaces to the map, and the map uses that place data to build the markers to put on the map for the food locations.
            This will probably need to be attached to each button created for this menu.
                fragmentTransaction = fragmentManager.beginTransaction();
                FoodMapFragment mapFragment = new FoodMapFragment();
                mapFragment.SetupMarkerLocation(mLocation);
                mapFragment.GetFoodPlaces(mPlaces);
                fragmentTransaction.replace(R.id.mainFrameDetails, mapFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                break;
             */
    // Thanks for the help, buddy



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
                String s = "";
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
