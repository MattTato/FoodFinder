package com.osu.tatoczenko.foodfinder;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

/**
 * Created by tyler_cunnington on 4/14/15.
 */

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity>{

    MainActivity activity;

    public MainActivityTest(){

        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    public void testTextViewNotNull(){
        TextView textView = (TextView) activity.findViewById(R.id.textView);
        assertNotNull(textView);
    }

    public void testTextViewIsCorrect(){
        TextView textView = (TextView) activity.findViewById(R.id.textView);
        String text = (String) textView.getText();
        assertEquals("Food Finder!",text);


    }






}
