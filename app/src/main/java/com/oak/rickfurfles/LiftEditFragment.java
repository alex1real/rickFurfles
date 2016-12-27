package com.oak.rickfurfles;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oak.rickfurfles.model.db.LiftContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class LiftEditFragment extends Fragment {

    /********************
     * Public Constants *
     *******************/
    public static final String LIFT_EDIT_FRAGMENT_TAG = "LEFT";

    public static final String[] LIFT_PROJECTION = {
            LiftContract.LiftEntry.TABLE_NAME + "." + LiftContract.LiftEntry.COLUMN_START_DT,
            LiftContract.LiftEntry.TABLE_NAME + "." + LiftContract.LiftEntry.COLUMN_END_DT,
            LiftContract.LiftEntry.TABLE_NAME + "." + LiftContract.LiftEntry.COLUMN_PRICE,
            LiftContract.LiftEntry.TABLE_NAME + "." + LiftContract.LiftEntry.COLUMN_PASSENGERS_NUM
    };

    /****************
     * Constructors *
     ***************/
    public LiftEditFragment() {
    }

    /**************
     * Overriders *
     *************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lift_edit, container, false);
    }
}
