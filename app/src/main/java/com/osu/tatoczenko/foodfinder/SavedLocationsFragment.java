package com.osu.tatoczenko.foodfinder;


import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 *
 * Allows a user to pull up any saved locations and find them on a map again.
 */
public class SavedLocationsFragment extends Fragment implements OnClickListener {


    public SavedLocationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_saved_locations, container, false);
        View btnBack = v.findViewById(R.id.savedlocback_button);
        btnBack.setOnClickListener(this);
        return v;
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.savedlocback_button:
                getFragmentManager().popBackStack();
                break;
        }
    }


}
