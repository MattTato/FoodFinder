package com.osu.tatoczenko.foodfinder;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by tyler_cunnington on 4/15/15.
 * Used only for J Unit test cases
 */
public class TestFragmentActivity extends FragmentActivity {

    @Override
    protected void onCreate (Bundle arg0){
        super.onCreate(arg0);
        setContentView(R.layout.activity_test_fragment);
    }
}