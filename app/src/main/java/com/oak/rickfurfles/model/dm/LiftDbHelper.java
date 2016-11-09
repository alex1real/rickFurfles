package com.oak.rickfurfles.model.dm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Alex on 26/10/2016.
 * This Class:
 *   It creates the Database or just open it, based on the Lift Contract
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
        this.createAddressTable(sqLiteDatabase);
        this.createLiftAddressTable(sqLiteDatabase);
        this.createExpenseTable(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVerion, int newVersion){
        // Note that this only fired if you change the version number of your database
        // It does NOT depend on the version number of your application
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LiftContract.ShiftEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LiftContract.LiftEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LiftContract.AddressEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LiftContract.LiftAddressEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LiftContract.ExpenseEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    /*******************
     * Private Methods *
     ******************/
    private void createAddressTable(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_ADDR_TABLE =
                "CREATE TABLE " + LiftContract.AddressEntry.TABLE_NAME
                + " ("
                + LiftContract.AddressEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + LiftContract.AddressEntry.COLUMN_CREATED + " INTEGER NOT NULL, "
                + LiftContract.AddressEntry.COLUMN_LAST_UPD + " INTEGER NOT NULL, "
                + LiftContract.AddressEntry.COLUMN_PLACE + " TEXT NOT NULL, "
                + LiftContract.AddressEntry.COLUMN_NEIGHBORHOOD + " TEXT, "
                + LiftContract.AddressEntry.COLUMN_CITY + " TEXT NOT NULL, "
                + LiftContract.AddressEntry.COLUMN_STATE + " TEXT NOT NULL, "
                + LiftContract.AddressEntry.COLUMN_COUNTRY + " TEXT NOT NULL, "
                + LiftContract.AddressEntry.COLUMN_ZIPCODE + " TEXT, "
                + "CONSTRAINT 'UN_KEY' UNIQUE ("
                + LiftContract.AddressEntry.COLUMN_PLACE + ","
                + LiftContract.AddressEntry.COLUMN_NEIGHBORHOOD + ","
                + LiftContract.AddressEntry.COLUMN_CITY + ","
                + LiftContract.AddressEntry.COLUMN_STATE + ','
                + LiftContract.AddressEntry.COLUMN_COUNTRY + ")"
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_ADDR_TABLE);
    }

    private void createExpenseTable(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_EXPENSE_TABLE =
               "CREATE TABLE " + LiftContract.ExpenseEntry.TABLE_NAME
                + " ("
                + LiftContract.ExpenseEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + LiftContract.ExpenseEntry.COLUMN_CREATED + " INTEGER NOT NULL, "
                + LiftContract.ExpenseEntry.COLUMN_LAST_UPD + " INTEGER NOT NULL, "
                + LiftContract.ExpenseEntry.COLUMN_NAME + " TEXT NOT NULL, "
                + LiftContract.ExpenseEntry.COLUMN_DATE + " INTEGER NOT NULL, "
                + LiftContract.ExpenseEntry.COLUMN_VALUE + " REAL NOT NULL, "
                + LiftContract.ExpenseEntry.COLUMN_SHIFT_ID + " INTEGER, "
                + "CONSTRAINT 'FK_EXPENSE_SHIFT' FOREIGN KEY ('"
                + LiftContract.ExpenseEntry.COLUMN_SHIFT_ID
                + "') REFERENCES '"
                + LiftContract.ShiftEntry.TABLE_NAME
                + "' ('"
                + LiftContract.ShiftEntry._ID
                + "') ON DELETE No Action ON UPDATE No Action"
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_EXPENSE_TABLE);
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

    private void createLiftAddressTable(SQLiteDatabase sqLiteDatabase){
        final String SQL_CREATE_LIFT_ADDR_TABLE =
                "CREATE TABLE " + LiftContract.LiftAddressEntry.TABLE_NAME
                + " ("
                + LiftContract.LiftAddressEntry._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, "
                + LiftContract.LiftAddressEntry.COLUMN_CREATED + " INTEGER NOT NULL, "
                + LiftContract.LiftAddressEntry.COLUMN_LAST_UPD + " INTEGER NOT NULL, "
                + LiftContract.LiftAddressEntry.COLUMN_LIFT_ID + " INTEGER NOT NULL, "
                + LiftContract.LiftAddressEntry.COLUMN_ADDR_ID + " INTEGER NOT NULL, "
                + LiftContract.LiftAddressEntry.COLUMN_TYPE + " TEXT NOT NULL, "
                + LiftContract.LiftAddressEntry.COLUMN_NUM + " INTEGER, "
                + LiftContract.LiftAddressEntry.COLUMN_LAT + " REAL, "
                + LiftContract.LiftAddressEntry.COLUMN_LON + " REAL, "
                + LiftContract.LiftAddressEntry.COLUMN_POI + " TEXT, "
                + "CONSTRAINT 'FK_LIFT_ADDR_ADDR' FOREIGN KEY ('"
                + LiftContract.LiftAddressEntry.COLUMN_ADDR_ID
                + "') REFERENCES '"
                + LiftContract.AddressEntry.TABLE_NAME
                + "' ('"
                + LiftContract.AddressEntry._ID
                + "') ON DELETE No Action ON UPDATE No Action, "
                + "CONSTRAINT 'FK_LIFT_ADDR_LIFT' FOREIGN KEY ('"
                + LiftContract.LiftAddressEntry.COLUMN_LIFT_ID
                + "') REFERENCES '"
                + LiftContract.LiftEntry.TABLE_NAME
                + "' ('"
                + LiftContract.LiftEntry._ID
                + "') ON DELETE Cascade ON UPDATE No Action, "
                + "CONSTRAINT 'UN_KEY' UNIQUE ('"
                + LiftContract.LiftAddressEntry.COLUMN_LIFT_ID + "','"
                + LiftContract.LiftAddressEntry.COLUMN_ADDR_ID + "','"
                + LiftContract.LiftAddressEntry.COLUMN_TYPE
                + "'));";

        sqLiteDatabase.execSQL(SQL_CREATE_LIFT_ADDR_TABLE);
    }

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

}
