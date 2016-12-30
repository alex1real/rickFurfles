package com.oak.rickfurfles;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oak.rickfurfles.model.db.LiftContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class ShiftActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    /*********************
     * Private Constants *
     ********************/
    private static final int SHIFT_LOADER_ID = 0;
    private static final int LIFT_SUM_LOADER_ID = 1;
    private static final int LIFT_LOADER_ID = 2;

    // Projections
    // Shift by Id Projection
    private static final String[] SHIFT_BY_ID_PROJECTION = new String[]{
            LiftContract.ShiftEntry.TABLE_NAME + "." + LiftContract.ShiftEntry._ID,
            LiftContract.ShiftEntry.TABLE_NAME + "." + LiftContract.ShiftEntry.COLUMN_START_DT,
            LiftContract.ShiftEntry.TABLE_NAME + "." + LiftContract.ShiftEntry.COLUMN_END_DT
    };

    // Shift by Id Projection Indexes
    private static final int COL_SHIFT_ID = 0;
    private static final int COL_SHIFT_START_DT = 1;
    private static final int COL_SHIFT_END_DT = 2;

    /*********************
     * Private Variables *
     ********************/
    private Uri uri;

    /****************
     * Constructors *
     ***************/
    public ShiftActivityFragment() {
    }

    /***********************
     * Fragment Overriders *
     **********************/
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        LoaderManager lm = getLoaderManager();
        lm.initLoader(SHIFT_LOADER_ID, null, this);
        lm.initLoader(LIFT_SUM_LOADER_ID, null, this);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        this.uri = getActivity().getIntent().getData();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shift, container, false);
    }

    /********************************************
     * LoaderManager.LoaderCallbacks Overriders *
     *******************************************/
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch(id){
            case SHIFT_LOADER_ID:
                if(this.uri != null){
                    return new CursorLoader(getActivity(),
                            this.uri,
                            ShiftActivityFragment.SHIFT_BY_ID_PROJECTION, null, null, null);
                }

                break;
            case LIFT_SUM_LOADER_ID:
                if(uri != null){
                    long shiftId = ContentUris.parseId(uri);
                    Uri liftByShiftSumUri = LiftContract.LiftEntry.buildLiftByShiftSumUri(shiftId);

                    return new CursorLoader(getActivity(),
                            liftByShiftSumUri,
                            null, null, null, null);
                }

                break;
            case LIFT_LOADER_ID:
                // ToDo: Implement Lift Loader.onCreateLoader
                break;
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        int loaderId = loader.getId();

        switch(loaderId){
            case SHIFT_LOADER_ID:
                this.populateShiftViews(cursor);

                break;
            case LIFT_SUM_LOADER_ID:
                this.populateLiftSumViews(cursor);

                break;
            case LIFT_LOADER_ID:
                // ToDo: Implement Lift Loader.onLoadFinished
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /*******************
     * Private Methods *
     ******************/
    private void populateShiftViews(Cursor cursor){
        cursor.moveToFirst();

        // Get the root view for Fragment's layout
        View rootView = getView();

        // Start Date
        long startDateInMillis = cursor.getLong(COL_SHIFT_START_DT);
        // ToDo: Change for a cleaner formatting solution
        SimpleDateFormat sdf;
        if(startDateInMillis != 0) {
            Calendar startDate = new GregorianCalendar();
            startDate.setTimeInMillis(startDateInMillis);
            sdf = new SimpleDateFormat("EEEE dd MMM YYYY HH:mm");
            TextView startDateView = (TextView) rootView.findViewById(R.id.shift_full_start_date_tv);
            startDateView.setText(sdf.format(startDate.getTime()));
        }

        // End Date
        long endDateInMillis = cursor.getLong(COL_SHIFT_END_DT);
        // ToDo: Change for a cleaner formatting solution
        if(endDateInMillis != 0) {
            Calendar endDate = new GregorianCalendar();
            endDate.setTimeInMillis(endDateInMillis);
            sdf = new SimpleDateFormat(" - HH:mm");
            TextView endDateView = (TextView) rootView.findViewById(R.id.shift_end_time_tv);
            endDateView.setText(sdf.format(endDate.getTime()));
        }
    }

    public void populateLiftSumViews(Cursor cursor){
        cursor.moveToFirst();

        // Get the root view for Fragment's layout
        View rootView = getView();

        // Lift count()
        int columnIndex = cursor.getColumnIndex(LiftContract.LiftEntry.FUNCTION_SUM_PRICE);
        int liftCount = cursor.getInt(columnIndex);
        TextView liftCountView = (TextView)rootView.findViewById(R.id.shift_lifts_tv);
        liftCountView.setText(Integer.toString(liftCount));

        // Balance (Lift sum())
        columnIndex = cursor.getColumnIndex(LiftContract.LiftEntry.FUNCTION_SUM_PRICE);
        double balance = cursor.getDouble(columnIndex);
        TextView balanceView = (TextView)rootView.findViewById(R.id.shift_balance_tv);
        // ToDo: Format the balance
        balanceView.setText(Double.toString(balance));
    }
}
