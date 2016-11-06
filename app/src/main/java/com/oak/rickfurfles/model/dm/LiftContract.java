package com.oak.rickfurfles.model.dm;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.util.Calendar;

/**
 * Created by Alex on 26/10/2016.
 */

public class LiftContract {

    /*************
     * Constants *
     ************/
    // The "Content Authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website. A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device
    public static final String CONTENT_AUTHORITY = "com.oak.rickfurfles.app";

    // Use CONTENT_AUTHORITY to create a base of all URI's which apps will use to caontact the
    // content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URIs)
    // For instance, content://com.oak.rickfurfles.app/shift/ is a valid path for looking at shift
    // data
    public static final String PATH_SHIFT = "shift";
    public static final String PATH_LIFT = "lift";
    public static final String PATH_ADDR = "Address";
    public static final String PATH_LIFT_ADDR = "LiftAddress";
    public static final String PATH_EXPENSE = "Expense";

    /*******************
     * Private Methods *
     ******************/
    private static long getDayFirstMillis(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    private static long getDayLastMillis(Calendar calendar){
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTimeInMillis();
    }

    /*****************
     * Inner Classes *
     ****************/
    public static final class ShiftEntry implements BaseColumns, LiftDbBaseColumns{
        /*************
         * Constants *
         ************/
        /*******
         * Uri *
         ******/
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHIFT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIFT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIFT;

        /*********
         * Table *
         ********/
        public static final String TABLE_NAME = "SHIFT";

        /***********
         * Columns *
         **********/
        //It stores the date when the shift has started
        public static final String COLUMN_START_DT = "START_DT";

        //It stores the date when the shift has been finished
        public static final String COLUMN_END_DT = "END_DT";

        /******************
         * Public Methods *
         *****************/
        /****************
         * Uri Builders *
         ***************/
        public static Uri buildShiftUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildShiftPeriodUri(Calendar startDate, Calendar endDate){
            long startDateMillis = getDayFirstMillis(startDate);
            long endDateMillis = getDayLastMillis(endDate);

            return CONTENT_URI.buildUpon()
                    .appendPath("period")
                    .appendPath(Long.toString(startDateMillis))
                    .appendPath(Long.toString(endDateMillis))
                    .build();
        }
    }


    public static final class LiftEntry implements BaseColumns, LiftDbBaseColumns{
        /*************
         * Constants *
         ************/
        /*******
         * URI *
         ******/
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LIFT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIFT;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LIFT;

        /*********
         * Table *
         ********/
        public static final String TABLE_NAME = "LIFT";

        /**********
         * Column *
         *********/
        // It stores the Lift's start time
        public static final String COLUMN_START_DT = "START_DT";

        // It stores the Lift's end time
        public static final String COLUMN_END_DT = "END_DT";

        //It stores the Lift's price
        public static final String COLUMN_PRICE = "PRICE";

        // It stores the Lift's number of passengers
        public static final String COLUMN_PASSENGERS_NUM = "PASSENGERS_NUM";

        // It stores the Shift's foreign key
        public static final String COLUMN_SHIFT_ID = "SHIFT_ID";


        /******************
         * Public Methods *
         *****************/
        /****************
         * Uri Builders *
         ***************/
        public static Uri buildLiftUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildLiftByShiftUri(long shiftId){
            return CONTENT_URI.buildUpon().appendPath(Long.toString(shiftId)).build();
        }
    }

    // Address Catalog
    public static final class AddressEntry implements BaseColumns, LiftDbBaseColumns{
        /*************
         * Constants *
         ************/
        /*******
         * URI *
         ******/
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ADDR).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ADDR;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ADDR;

        /*********
         * Table *
         ********/
        public static final String TABLE_NAME = "ADDR";

        /***********
         * Columns *
         **********/
        // It stores the Place Name
        public static final String COLUMN_PLACE = "PLACE";

        // It stores the Neighborhood name
        public static final String COLUMN_NEIGHBORHOOD = "NEIGHBORHOOD";

        // It stores the City name
        public static final String COLUMN_CITY = "CITY";

        // It stores the State name
        public static final String COLUMN_STATE = "STATE";

        // It stores the Country name
        public static final String COLUMN_COUNTRY = "COUNTRY";

        // It stores the zipcode
        public static final String COLUMN_ZIPCODE = "ZIPCODE";

        /******************
         * Public Methods *
         *****************/
        /****************
         * URI Builders *
         ***************/
        public static Uri buildAddressUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    //ToDo: Implement LiftAddressEntry inner class
    //ToDo: Implement ExpenseEntry inner class
}
