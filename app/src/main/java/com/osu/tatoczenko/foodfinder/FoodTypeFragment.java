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
 * Lists out the different food types that a user can choose to see restaurants about.
 */
public class FoodTypeFragment extends Fragment implements OnClickListener{


    public FoodTypeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_food_type, container, false);
        View btnBack = v.findViewById(R.id.foodtype_back_button);
        btnBack.setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.foodtype_back_button:
                getFragmentManager().popBackStack();
                break;
        }

    }


}
