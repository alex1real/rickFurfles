package com.oak.rickfurfles.model;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.oak.rickfurfles.model.db.LiftContract;
import com.oak.rickfurfles.model.db.LiftDbHelper;

import java.util.Calendar;

/**
 * Created by Alex on 09/11/2016.
 * Content Provider for this application
 */

public class LiftProvider extends ContentProvider {

    /*************
     * Constants *
     ************/
    private static final UriMatcher uriMatcher = buildUriMatcher();

    /*******
     * URI *
     ******/
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
    private static final int SHIFT_BY_PERIOD_WITH_SUM = 502;
    private static final int SHIFT_BY_PERIOD_SUM = 503;
    private static final int SHIFT_SUM = 504;

    /**************
     * Selections *
     *************/
    private static final String EXPENSE_BY_SHIFT_SELECTION =
            LiftContract.ExpenseEntry.TABLE_NAME + "."
            + LiftContract.ExpenseEntry.COLUMN_SHIFT_ID + " = ?";

    private static final String LIFT_BY_SHIFT_SELECTION =
            LiftContract.LiftEntry.TABLE_NAME + "." + LiftContract.LiftEntry.COLUMN_SHIFT_ID + " = ?";

    private static final String LIFT_ADDR_BY_LIFT_SELECTION =
            LiftContract.LiftAddressEntry.TABLE_NAME + "."
            + LiftContract.LiftAddressEntry.COLUMN_LIFT_ID + " = ?";

    private static final String LIFT_ADDR_BY_LIFT_AND_TYPE_SELECTION =
            LiftContract.LiftAddressEntry.TABLE_NAME + "."
            + LiftContract.LiftAddressEntry.COLUMN_LIFT_ID + " = ? AND "
            + LiftContract.LiftAddressEntry.TABLE_NAME + "."
            + LiftContract.LiftAddressEntry.COLUMN_TYPE + " = ?";

    private static final String SHIFT_BY_ID_SELECTION =
            LiftContract.ShiftEntry.TABLE_NAME + "." + LiftContract.ShiftEntry._ID + " = ?";

    private static final String SHIFT_BY_PERIOD_SELECTION =
            LiftContract.ShiftEntry.TABLE_NAME + "."
            + LiftContract.ShiftEntry.COLUMN_START_DT + " >= ? AND "
            + LiftContract.ShiftEntry.TABLE_NAME + "."
            + LiftContract.ShiftEntry.COLUMN_END_DT + " <= ?";

    /***************
     * Projections *
     **************/
    private static final String[] SHIFT_BY_PERIOD_WITH_SUM_PROJECTION =
            new String[]{LiftContract.ShiftEntry.TABLE_NAME + ".*",
            "sum(" + LiftContract.LiftEntry.TABLE_NAME + "."
                    + LiftContract.LiftEntry.COLUMN_PRICE + ") "
                    + "AS " + LiftContract.LiftEntry.FUNCTION_SUM_PRICE,
            "count(*) AS " + LiftContract.LiftEntry.FUNCTION_COUNT_LIFT};

    private static final String[] SHIFT_SUM_PROJECTION =
            new String[]{"sum(" + LiftContract.LiftEntry.TABLE_NAME
                    + "." + LiftContract.LiftEntry.COLUMN_PRICE + ") "
                    + "AS " + LiftContract.LiftEntry.FUNCTION_SUM_PRICE,
            "count(*) AS " + LiftContract.LiftEntry.FUNCTION_COUNT_LIFT};

    /****************************
     * Query Builders for Joins *
     ***************************/
    private static final SQLiteQueryBuilder shiftLiftJoinQueryBuilder;

    // This static block works for a static class in the same way as a constructor for a object.
    // It's executed only once, when the class is loaded in the JVM and no more.
    static{
        shiftLiftJoinQueryBuilder = new SQLiteQueryBuilder();

        shiftLiftJoinQueryBuilder.setTables(
                LiftContract.ShiftEntry.TABLE_NAME + " INNER JOIN "
                + LiftContract.LiftEntry.TABLE_NAME + " ON "
                + LiftContract.ShiftEntry.TABLE_NAME + "." + LiftContract.ShiftEntry._ID + " = "
                + LiftContract.LiftEntry.TABLE_NAME + "." + LiftContract.LiftEntry.COLUMN_SHIFT_ID
        );
    }

    /*************
     * Variables *
     ************/
    private LiftDbHelper liftDbHelper;

    /******************
     * Public Methods *
     *****************/
    /**************
     * Overriders *
     *************/
    @Override
    public int delete(@NonNull Uri uri,
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
    public String getType(@NonNull Uri uri){
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
    public Uri insert(@NonNull Uri uri,
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
            case EXPENSE:
                id = sqLiteDatabase.insert(LiftContract.ExpenseEntry.TABLE_NAME,
                        null,
                        contentValues);

                if(id > 0)
                        returnUri = LiftContract.AddressEntry.buildAddressUri(id);

                break;
            case LIFT:
                id = sqLiteDatabase.insert(LiftContract.LiftEntry.TABLE_NAME,
                        null,
                        contentValues);

                if(id > 0)
                    returnUri = LiftContract.LiftEntry.buildLiftUri(id);

                break;
            case LIFT_ADDR:
                id = sqLiteDatabase.insert(LiftContract.LiftAddressEntry.TABLE_NAME,
                        null,
                        contentValues);

                if(id > 0)
                    returnUri = LiftContract.LiftAddressEntry.buildLiftAddressUri(id);

                break;
            case SHIFT:
                id = sqLiteDatabase.insert(LiftContract.ShiftEntry.TABLE_NAME,
                        null,
                        contentValues);

                if(id > 0)
                    returnUri = LiftContract.ShiftEntry.buildShiftUri(id);

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
    public Cursor query(@NonNull Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder){
        Cursor returnCursor = null;
        String tableName = null;
        String groupBy = null;

        int match = uriMatcher.match(uri);

        SQLiteDatabase sqLiteDatabase = liftDbHelper.getReadableDatabase();

        switch(match){
            case ADDRESS:
                tableName = LiftContract.AddressEntry.TABLE_NAME;

                break;
            case EXPENSE:
                tableName = LiftContract.ExpenseEntry.TABLE_NAME;

                break;
            case EXPENSE_BY_SHIFT:
                tableName = LiftContract.ExpenseEntry.TABLE_NAME;
                selection = EXPENSE_BY_SHIFT_SELECTION;
                selectionArgs = getExpenseByShiftSelectionArgs(uri);

                break;
            case LIFT:
                tableName = LiftContract.LiftEntry.TABLE_NAME;

                break;
            case LIFT_BY_SHIFT:
                tableName = LiftContract.LiftEntry.TABLE_NAME;
                selection = LIFT_BY_SHIFT_SELECTION;
                selectionArgs = getLiftByIdSelectionArgs(uri);

                break;
            case LIFT_ADDR:
                tableName = LiftContract.LiftAddressEntry.TABLE_NAME;

                break;
            case LIFT_ADDR_BY_LIFT:
                tableName = LiftContract.LiftAddressEntry.TABLE_NAME;
                selection = LIFT_ADDR_BY_LIFT_SELECTION;
                selectionArgs = this.getLiftAddrByLiftSelectionArgs(uri);

                break;
            case LIFT_ADDR_BY_LIFT_AND_TYPE:
                tableName = LiftContract.LiftAddressEntry.TABLE_NAME;
                selection = LIFT_ADDR_BY_LIFT_AND_TYPE_SELECTION;
                selectionArgs = this.getLiftAddrByLiftAndTypeSelectionArgs(uri);

                break;
            case SHIFT:
                tableName = LiftContract.ShiftEntry.TABLE_NAME;

                break;
            case SHIFT_BY_PERIOD:
                tableName = LiftContract.ShiftEntry.TABLE_NAME;
                selection = SHIFT_BY_PERIOD_SELECTION;
                selectionArgs = getShiftByPeriodSelectionArgs(uri);

                break;
            case SHIFT_BY_PERIOD_WITH_SUM:
                projection = SHIFT_BY_PERIOD_WITH_SUM_PROJECTION;
                selection = SHIFT_BY_PERIOD_SELECTION;
                selectionArgs = this.getShiftByPeriodSelectionArgs(uri);
                groupBy = LiftContract.ShiftEntry.TABLE_NAME + "." + LiftContract.ShiftEntry._ID;

                break;
            case SHIFT_BY_PERIOD_SUM:
                projection = SHIFT_SUM_PROJECTION;
                selection = SHIFT_BY_PERIOD_SELECTION;
                selectionArgs = getShiftByPeriodSelectionArgs(uri);

                break;
            case SHIFT_SUM:
                projection = SHIFT_SUM_PROJECTION;
                selection = SHIFT_BY_ID_SELECTION;
                selectionArgs = getShiftByIdSelectionArgs(uri);

                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri.toString());
        }


        switch (match){
            case ADDRESS:
            case EXPENSE:
            case EXPENSE_BY_SHIFT:
            case LIFT:
            case LIFT_BY_SHIFT:
            case LIFT_ADDR:
            case LIFT_ADDR_BY_LIFT:
            case LIFT_ADDR_BY_LIFT_AND_TYPE:
            case SHIFT:
            case SHIFT_BY_PERIOD:
                // It executes non join queries
                returnCursor = sqLiteDatabase.query(tableName,
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        null,
                        sortOrder);

                break;
            case SHIFT_BY_PERIOD_WITH_SUM:
            case SHIFT_BY_PERIOD_SUM:
            case SHIFT_SUM:
                // It executes Shift-Lift join queries
                returnCursor = shiftLiftJoinQueryBuilder.query(liftDbHelper.getReadableDatabase(),
                        projection,
                        selection,
                        selectionArgs,
                        groupBy,
                        null,
                        sortOrder);

                break;
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
    public int update(@NonNull Uri uri,
                      ContentValues contentValues,
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

        numAffectedRows = sqLiteDatabase.update(tableName,
                contentValues,
                selection,
                selectionArgs);

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
                        + "/" + LiftContract.PATH_SHIFT_PERIOD + "/#/#"
                        + "/" + LiftContract.PATH_SHIFT_WITH_SUM,
                SHIFT_BY_PERIOD_WITH_SUM);

        uriMatcher.addURI(LiftContract.CONTENT_AUTHORITY,
                LiftContract.PATH_SHIFT
                        + "/#/" + LiftContract.PATH_SHIFT_SUM,
                SHIFT_SUM);

        return uriMatcher;
    }

    private String[] getExpenseByShiftSelectionArgs(Uri uri){
        long shiftId = LiftContract.ExpenseEntry.getShiftIdFromUri(uri);

        String[] selectionArgs = new String[]{Long.toString(shiftId)};

        return selectionArgs;
    }

    private String[] getLiftByIdSelectionArgs(Uri uri){
        long shiftId = LiftContract.LiftEntry.getShiftIdFromUri(uri);

        String[] selectionArgs = new String[]{Long.toString(shiftId)};

        return selectionArgs;
    }

    //LIFT_ADDR_BY_LIFT_AND_TYPE
    private String[] getLiftAddrByLiftSelectionArgs(Uri uri){
        long liftId = LiftContract.LiftAddressEntry.getLiftIdFromUri(uri);

        String[] selectionArgs = new String[]{Long.toString(liftId)};

        return selectionArgs;
    }

    private String[] getLiftAddrByLiftAndTypeSelectionArgs(Uri uri){
        long liftId = LiftContract.LiftAddressEntry.getLiftIdFromUri(uri);
        String type = LiftContract.LiftAddressEntry.getTypeFromUri(uri);

        String[] selectionArgs = new String[]{Long.toString(liftId), type};

        return selectionArgs;
    }

    private String[] getShiftByPeriodSelectionArgs(Uri uri){
        Calendar startDate =  LiftContract.ShiftEntry.getStartDateFromUri(uri);
        Calendar endDate = LiftContract.ShiftEntry.getEndDateFromUri(uri);

        long startDateMillis = startDate.getTimeInMillis();
        long endDateMillis = endDate.getTimeInMillis();

        String[] selectionArgs = new String[]{Long.toString(startDateMillis),
                Long.toString(endDateMillis)};

        return selectionArgs;
    }

    private String[] getShiftByIdSelectionArgs(Uri uri){
        long shiftId = LiftContract.ShiftEntry.getIdFromUri(uri);

        String[] selectionArgs = new String[]{Long.toString(shiftId)};

        return selectionArgs;
    }
}