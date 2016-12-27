package com.oak.rickfurfles;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.oak.rickfurfles.model.db.LiftContract;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ShiftActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shift);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_shift_new_lift);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Insert New Lift
                Uri liftUri = ShiftActivity.this.insertNewLift();

                // Navigate to Lift Activity with Create New Lift Flag
                Intent newLiftIntent = new Intent(view.getContext(), LiftActivity.class);
                newLiftIntent.setData(liftUri);

                startActivity(newLiftIntent);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /*******************
     * Private Methods *
     ******************/
    private Uri insertNewLift(){
        ContentValues liftValues = new ContentValues();

        Calendar startDate = new GregorianCalendar();

        liftValues.put(LiftContract.LiftEntry.COLUMN_START_DT, startDate.getTimeInMillis());

        Uri liftUri = getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI, liftValues);

        return liftUri;
    }
}
