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
import android.view.Menu;
import android.view.MenuItem;

import com.oak.rickfurfles.model.db.LiftContract;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_period_new_shift);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Add a new Shift
                Uri shiftUri = MainActivity.this.insertNewShift();

                // Navigate to the Shift's view
                Intent shiftIntent = new Intent(view.getContext(), ShiftActivity.class);
                shiftIntent.setData(shiftUri);

                startActivity(shiftIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_period, menu);
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

    /*******************
     * Private Methods *
     ******************/
    private Uri insertNewShift(){

        ContentValues shiftValues = new ContentValues();

        Calendar startDate = new GregorianCalendar();

        shiftValues.put(LiftContract.ShiftEntry.COLUMN_START_DT, startDate.getTimeInMillis());

        Uri shiftUri = getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shiftValues);

        return shiftUri;
    }
}
