package com.osu.tatoczenko.foodfinder;

import android.app.FragmentTransaction;
import android.app.FragmentManager;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;


public class MainActivity extends Activity {
    String message = "Android_Log_Test : ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(message, "The onCreate() event");
        if (savedInstanceState == null) {
            MainMenuFragment menuFragment = new MainMenuFragment();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.mainFrameDetails, menuFragment);
            fragmentTransaction.commit();
        }
    } // Testing if I can push
    // You can!


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(message, "The onStart() event");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(message, "The onResume() event");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(message, "The onPause() event");
    }


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
    }
}
