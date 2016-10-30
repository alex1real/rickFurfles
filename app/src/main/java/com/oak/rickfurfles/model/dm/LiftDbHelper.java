package com.oak.rickfurfles.model.dm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alex on 26/10/2016.
 */

public class LiftDbHelper extends SQLiteOpenHelper {

    /*************
     * Constants *
     ************/
    // Database file name
    public static final String DATABASE_NAME = "lift.db";

    // If you change the database schema, you must increment the database version
    private static final int DATABASE_VERSION = 1;

    /****************
     * Constructors *
     ***************/
    public LiftDbHelper(Context context){
        //(Context context, String databaseName, SQLiteDatabase.CursorFactory factory, int DatabaseVersion)
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /******************
     * Public Methods *
     *****************/
    /**************
     * Overriders *
     *************/
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        this.createShiftTable(sqLiteDatabase);
        this.createLiftTable(sqLiteDatabase);
        //ToDo: call all create*Table methods
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVerion, int newVersion){
        // Note that this only fired if you change the version number of your database
        // It does NOT depend on the version number of your application
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LiftContract.ShiftEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LiftContract.LiftEntry.TABLE_NAME);
        //ToDo: Drop all created tables

        onCreate(sqLiteDatabase);
    }

    /*******************
     * Private Methods *
     ******************/
    private void createShiftTable(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_SHIFT_TABLE =
                "CREATE TABLE " + LiftContract.ShiftEntry.TABLE_NAME
                + " ("
                + LiftContract.ShiftEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + LiftContract.ShiftEntry.COLUMN_CREATED + " INTEGER NOT NULL, "
                + LiftContract.ShiftEntry.COLUMN_LAST_UPD + " INTEGER NOT NULL,"
                + LiftContract.ShiftEntry.COLUMN_START_DT + " INTEGER NOT NULL, "
                + LiftContract.ShiftEntry.COLUMN_END_DT + " INTEGER NOT NULL "
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_SHIFT_TABLE);
    }

    private void createLiftTable(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_LIFT_TABLE =
                "CREATE TABLE " + LiftContract.LiftEntry.TABLE_NAME
                + "("
                + LiftContract.LiftEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + LiftContract.LiftEntry.COLUMN_CREATED + " INTEGER NOT NULL, "
                + LiftContract.LiftEntry.COLUMN_LAST_UPD + " INTEGER NOT NULL, "
                + LiftContract.LiftEntry.COLUMN_START_DT + " INTEGER NOT NULL, "
                + LiftContract.LiftEntry.COLUMN_END_DT + " INTEGER, "
                + LiftContract.LiftEntry.COLUMN_PRICE + " REAL NOT NULL, "
                + LiftContract.LiftEntry.COLUMN_PASSENGERS_NUM + " INTEGER, "
                + LiftContract.LiftEntry.COLUMN_SHIFT_ID + " INTEGER, "
                + "CONSTRAINT 'FK_LIFT_SHIFT' FOREIGN KEY ("
                + LiftContract.LiftEntry.COLUMN_SHIFT_ID + ") "
                + "REFERENCES '" + LiftContract.ShiftEntry.TABLE_NAME + "' ("
                + LiftContract.ShiftEntry._ID + ") ON DELETE No Action ON UPDATE No Action"
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_LIFT_TABLE);
    }

    //ToDo: Implement createAddressTable method
    //ToDo: Implement createLiftAddressTable method
    //Todo: Implement createExpenseTable method

}
