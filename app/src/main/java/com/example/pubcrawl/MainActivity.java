package com.example.pubcrawl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;

import com.example.pubcrawl.fragments.EventsFragment;

public class MainActivity extends AppCompatActivity {

    final String TAG = "MAIN_ACTIVITY";
    final FragmentManager fragmentManager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // temporary hack just to display events fragment
        Fragment fragment = new EventsFragment();
        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
        Log.i(TAG, "Created EventsFragment");
    }
}
