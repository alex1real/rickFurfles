package com.oak.rickfurfles.model;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

import com.oak.rickfurfles.model.db.LiftContract;

/**
 * Created by Alex on 09/11/2016.
 */

public class LiftProvider extends ContentProvider {

    /*************
     * Constants *
     ************/
    private static final UriMatcher uriMatcher = buildUriMatcher();

    private static final int ADDRESS = 100;
    private static final int EXPENSE = 200;
    private static final int EXPENSE_BY_SHIFT = 201;
    private static final int LIFT = 300;
    private static final int LIFT_BY_SHIFT = 301;
    private static final int LIFT_ADDR = 400;
    private static final int LIFT_ADDR_BY_LIFT = 401;
    private static final int LIFT_ADDR_BY_LIFT_AND_TYPE = 402;
    private static final int SHIFT = 500;
    private static final int SHIFT_BY_PERIOD = 501;
    private static final int SHIFT_BY_PERIOD_SUM = 502;
    private static final int SHIFT_SUM = 503;

    /******************
     * Public Methods *
     *****************/
    /**************
     * Overriders *
     *************/
    @Override
    public int delete(Uri uri,
                      String selection,
                      String[] selectionArgs){
        return 0;
    }

    @Override
    public String getType(Uri uri){
        final int match = uriMatcher.match(uri);

        switch(match){
            case ADDRESS:
                return LiftContract.AddressEntry.CONTENT_TYPE;
            case EXPENSE:
                return LiftContract.ExpenseEntry.CONTENT_TYPE;
            case EXPENSE_BY_SHIFT:
                return LiftContract.ExpenseEntry.CONTENT_TYPE;
            case LIFT:
                return LiftContract.LiftEntry.CONTENT_TYPE;
            case LIFT_BY_SHIFT:
                return LiftContract.LiftEntry.CONTENT_TYPE;
            case LIFT_ADDR:
                return LiftContract.LiftAddressEntry.CONTENT_TYPE;
            case LIFT_ADDR_BY_LIFT:
                return LiftContract.LiftAddressEntry.CONTENT_TYPE;
            case LIFT_ADDR_BY_LIFT_AND_TYPE:
                return LiftContract.LiftAddressEntry.CONTENT_ITEM_TYPE;
            case SHIFT:
                return LiftContract.ShiftEntry.CONTENT_TYPE;
            case SHIFT_BY_PERIOD:
                return LiftContract.ShiftEntry.CONTENT_TYPE;
            case SHIFT_BY_PERIOD_SUM:
                return LiftContract.ShiftEntry.CONTENT_ITEM_TYPE;
            case SHIFT_SUM:
                return LiftContract.ShiftEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri,
                      ContentValues contentValues){
        return null;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder){
        return null;
    }

    @Override
    public boolean onCreate(){
        return true;
    }

    @Override
    public int update(Uri uri,
                      ContentValues contentValues,
                      String selection,
                      String[] selectionArgs){
        return 0;
    }

    /*******************
     * Private Methods *
     ******************/
    private static UriMatcher buildUriMatcher(){
        // The code passed into the constructor represents the code to return for the root URI.
        // It's common to use NO_MATCH as the code for this case.
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Address
        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_ADDR,
                ADDRESS);

        // Expense
        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_EXPENSE,
                EXPENSE);

        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_EXPENSE
                        + "/" + LiftContract.PATH_SHIFT + "/#",
                EXPENSE_BY_SHIFT);

        // Lift
        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_LIFT,
                LIFT);

        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_LIFT
                        + "/" + LiftContract.PATH_SHIFT + "/#",
                LIFT_BY_SHIFT);

        // Lift Address
        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_LIFT_ADDR,
                LIFT_ADDR);

        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_LIFT_ADDR
                        + "/" + LiftContract.PATH_LIFT + "/#",
                LIFT_ADDR_BY_LIFT);

        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_LIFT_ADDR
                        + "/" + LiftContract.PATH_LIFT + "/#"
                        + "/" + LiftContract.PATH_LIFT_ADDR_TYPE + "/*",
                LIFT_ADDR_BY_LIFT_AND_TYPE);

        // Shift
        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_SHIFT,
                SHIFT);

        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_SHIFT
                        + "/" + LiftContract.PATH_SHIFT_PERIOD + "/#/#",
                SHIFT_BY_PERIOD);

        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_SHIFT
                        + "/" + LiftContract.PATH_SHIFT_PERIOD + "/#/#"
                        + "/" + LiftContract.PATH_SHIFT_SUM,
                SHIFT_BY_PERIOD_SUM);

        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_SHIFT
                        + "/#/" + LiftContract.PATH_SHIFT_SUM,
                SHIFT_SUM);

        return uriMatcher;
    }
}