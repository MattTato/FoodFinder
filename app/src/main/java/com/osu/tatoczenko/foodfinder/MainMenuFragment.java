package com.osu.tatoczenko.foodfinder;


import android.view.View.OnClickListener;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainMenuFragment extends Fragment implements OnClickListener{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_menu, container, false);
        View btnFind = rootView.findViewById(R.id.findfood_button);
        btnFind.setOnClickListener(this);
        View btnSearch = rootView.findViewById(R.id.search_button);
        btnSearch.setOnClickListener(this);
        View btnFavorites = rootView.findViewById(R.id.favorites_button);
        btnFavorites.setOnClickListener(this);
        View btnExit = rootView.findViewById(R.id.exit_button);
        btnExit.setOnClickListener(this);
        return rootView;
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.findfood_button:

                break;
            case R.id.search_button:

                break;
            case R.id.favorites_button:

                break;
            case R.id.exit_button:
                getActivity().finish();
                break;
        }
    }

}
