package com.oak.rickfurfles.model;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.oak.rickfurfles.model.db.LiftContract;
import com.oak.rickfurfles.model.db.LiftDbHelper;

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

    private LiftDbHelper liftDbHelper;

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
        final SQLiteDatabase sqLiteDatabase = liftDbHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        int numAffectedRows;
        String tableName;


        switch(match){
            case ADDRESS:
                tableName = LiftContract.AddressEntry.TABLE_NAME;

                break;
            case EXPENSE:
                tableName = LiftContract.ExpenseEntry.TABLE_NAME;

                break;
            case LIFT:
                tableName = LiftContract.LiftEntry.TABLE_NAME;

                break;
            case LIFT_ADDR:
                tableName = LiftContract.LiftAddressEntry.TABLE_NAME;

                break;
            case SHIFT:
                tableName = LiftContract.ShiftEntry.TABLE_NAME;

                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri.toString());
        }

        numAffectedRows = sqLiteDatabase.delete(tableName,
                selection,
                selectionArgs);

        if(numAffectedRows > 0)
            getContext().getContentResolver().notifyChange(uri,null);

        sqLiteDatabase.close();

        return numAffectedRows;
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
        final SQLiteDatabase sqLiteDatabase = liftDbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        Uri returnUri = null;
        long id;

        switch(match){
            case ADDRESS:
                id = sqLiteDatabase.insert(LiftContract.AddressEntry.TABLE_NAME,
                        null,
                        contentValues);

                if(id > 0)
                    returnUri = LiftContract.AddressEntry.buildAddressUri(id);

                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri.toString());
        }

        if(id <= 0)
            throw new android.database.SQLException("Failed to insert row into " + uri.toString());

        getContext().getContentResolver().notifyChange(uri, null);

        sqLiteDatabase.close();

        return returnUri;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder){
        Cursor returnCursor;
        int match = uriMatcher.match(uri);

        SQLiteDatabase sqLiteDatabase = liftDbHelper.getReadableDatabase();

        switch(match){
            case ADDRESS:
                returnCursor = sqLiteDatabase.query(LiftContract.AddressEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri.toString());
        }

        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return returnCursor;
    }

    @Override
    public boolean onCreate(){
        liftDbHelper = new LiftDbHelper(getContext());

        return true;
    }

    @Override
    public int update(Uri uri,
                      ContentValues contentValues,
                      String selection,
                      String[] selectionArgs){
        final SQLiteDatabase sqLiteDatabase = liftDbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        int numAffectedRows;

        switch(match){
            case ADDRESS:
                numAffectedRows = sqLiteDatabase.update(LiftContract.AddressEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs);

                break;
            default:
                numAffectedRows = 0;

                throw new UnsupportedOperationException("Unknown URI: " + uri.toString());
        }

        if(numAffectedRows > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        sqLiteDatabase.close();

        return numAffectedRows;
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