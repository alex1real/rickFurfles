package com.oak.rickfurfles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.oak.rickfurfles.model.dm.LiftContract;
import com.oak.rickfurfles.model.dm.LiftDbHelper;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

/**
 * Created by Alex on 26/10/2016.
 */

@RunWith(AndroidJUnit4.class)
public class TestDb {

    /******************
     * Public Methods *
     *****************/
    @Before
    public void setUp(){
        this.deleteDatabase();
    }

    /*
     * It checks:
     * - If the database was created
     * - If it's possible to open the database
     * - If it's possible to query the database
     * - If the tables required tables have been created
     */
    @Test
    public void dbCreation(){
        // A List of tables to be checked if they were created
        HashSet<String> tableNamesHashSet = new HashSet<>();
        tableNamesHashSet.add(LiftContract.ShiftEntry.TABLE_NAME);


        Context appContext = InstrumentationRegistry.getTargetContext();

        SQLiteDatabase sqLiteDatabase = new LiftDbHelper(appContext).getWritableDatabase();

        // It checks if the db is open
        Assert.assertTrue("dbCreation Error: It was not possible to open DB after creation.",
                sqLiteDatabase.isOpen());

        // Get all tables names in the database
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type = 'table'", null);

        // Check if it was possible to run a simple query against the db
        Assert.assertTrue("dbCreation Error: Cursor empty. It means that the database hasn't been created correctly",
                cursor.moveToFirst());

        // Verify if all tables has been created
        do {
            tableNamesHashSet.remove(cursor.getString(0));
        } while(cursor.moveToNext());

        Assert.assertTrue("dbCreation Error: At least one of the required tables haven't been created",
                tableNamesHashSet.isEmpty());

        cursor.close();

    }

    /*
     * It checks:
     *  - If the db could be opened
     *  - If it was possible to insert a record in the db
     *  - If the record inserted into database is with the expected values
     */
    @Test
    public void InsertShift(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        LiftDbHelper liftDbHelper = new LiftDbHelper(appContext);
        SQLiteDatabase sqLiteDatabase = liftDbHelper.getWritableDatabase();

        // Check if it was possible to open the database
        Assert.assertTrue("insertShift Error: It wasn't possible to open the db.",
                sqLiteDatabase.isOpen());

        ContentValues shiftValues = this.getDefaultShiftCV();

        long shiftId = sqLiteDatabase.insert(LiftContract.ShiftEntry.TABLE_NAME,
                null,
                shiftValues);

        Assert.assertTrue("insertShift Error: Failure to insert shift.", shiftId != -1);

        shiftValues.put(LiftContract.ShiftEntry._ID, shiftId);

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + LiftContract.ShiftEntry.TABLE_NAME, null);
        Assert.assertTrue("insertShift Error: Inconsistencies between cursor and expected values",
                TestUtilities.isValidCursor(cursor, shiftValues));

        cursor.close();
    }


    /*******************
     * Private Methods *
     ******************/
    private void deleteDatabase(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        appContext.deleteDatabase(LiftDbHelper.DATABASE_NAME);
    }

    /*
     * Generate a ContentValues object for the shift with the following characteristics:
     *  - Creation Date: Current Date
     *  - Last Update Date: Current Date
     *  - Start shift date: Yesterday 21:00:XX
     *  - End shift date: Today 05:XX:XX
     */
    private ContentValues getDefaultShiftCV(){
        Calendar shiftStartDate = new GregorianCalendar();
        shiftStartDate.add(Calendar.DAY_OF_MONTH, -1);
        shiftStartDate.set(Calendar.HOUR_OF_DAY, 21);
        shiftStartDate.set(Calendar.MINUTE, 0);

        Calendar shiftEndDate = new GregorianCalendar();
        shiftEndDate.set(Calendar.HOUR_OF_DAY, 5);

        ContentValues shiftValues = this.getShiftValues(shiftStartDate, shiftEndDate);

        return shiftValues;
    }

    /*
     * Generate a ContentValues object for the shift where:
     *  - Creation Date: Current Date
     *  - Last Update Date: Current Date
     *  - Start shift date: @param
     *  - End shift date: @param
     */
    private ContentValues getShiftValues(Calendar startDate, Calendar endDate){
        GregorianCalendar creationDate = new GregorianCalendar();

        ContentValues shiftValues = new ContentValues();
        shiftValues.put(LiftContract.ShiftEntry.COLUMN_CREATED, creationDate.getTimeInMillis());
        shiftValues.put(LiftContract.ShiftEntry.COLUMN_LAST_UPD, creationDate.getTimeInMillis());
        shiftValues.put(LiftContract.ShiftEntry.COLUMN_START_DT, startDate.getTimeInMillis());
        shiftValues.put(LiftContract.ShiftEntry.COLUMN_END_DT, endDate.getTimeInMillis());

        return shiftValues;
    }

}
