package com.oak.rickfurfles;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


public class LiftActivity extends AppCompatActivity {

    /*********************
     * Private Constants *
     ********************/
    private static final String LOG_TAG = LiftActivity.class.getSimpleName();

    /**************
     * Overriders *
     *************/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
