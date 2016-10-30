package com.oak.rickfurfles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.oak.rickfurfles.model.dm.LiftContract;
import com.oak.rickfurfles.model.dm.LiftDbBaseColumns;
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
        tableNamesHashSet.add(LiftContract.LiftEntry.TABLE_NAME);


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
     *  - If it was possible to insert a Shift Record in the db
     *  - If it was possible to insert 2 Lift sample Records
     *  - If the recorde inserted into Lift table are with the expected values
     */
    @Test
    public void insertLift(){
        // Open db (Create if it doesn't exist)
        SQLiteDatabase sqLiteDatabase = this.openCreateWritableDb();

        // Create a Shift
        ContentValues shiftValues = this.getDefaultShiftCV();

        long shiftId = sqLiteDatabase.insert(LiftContract.ShiftEntry.TABLE_NAME,
                null,
                shiftValues);

        // Check if the Shift was created
        Assert.assertTrue("insertLift Error: Failure to insert shift.", shiftId != -1);

        // Create a Lift
        ContentValues liftSampleValues1 = this.getSampleLiftValues1(shiftId);

        long liftId1 = sqLiteDatabase.insert(LiftContract.LiftEntry.TABLE_NAME,
                null,
                liftSampleValues1);

        // Check if the Lift was created
        Assert.assertTrue("inserLift Error: Failure to insert Lift.", liftId1 != -1);

        // Create another Lift
        ContentValues liftSampleValues2 = this.getSampleLiftValues1(shiftId);

        long liftId2 = sqLiteDatabase.insert(LiftContract.LiftEntry.TABLE_NAME,
                null,
                liftSampleValues2);

        // Check if the Lift was created
        Assert.assertTrue("insertLift Error: Failure to insert Lidt.", liftId2 != -1);

        // Query all Lifts
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + LiftContract.LiftEntry.TABLE_NAME,
                null);

        // Check if the first sample Lift is present in the cursor
        liftSampleValues1.put(LiftContract.LiftEntry._ID, liftId1);

        Assert.assertTrue("insertLift Error: Inconsistencies between cursor and expected sample values 1",
                TestUtilities.isValidCursor(cursor, liftSampleValues1));

        // Check if the second sample Lift is present in the cursor
        liftSampleValues2.put(LiftContract.LiftEntry._ID, liftId2);

        Assert.assertTrue("insertLift Error: Inconsistencies between cursor and expected sample values 2",
                TestUtilities.isValidCursor(cursor, liftSampleValues2));

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
        SQLiteDatabase sqLiteDatabase = this.openCreateWritableDb();

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
     * It generates a ContentValues object for the Lift where:
     *  - Start Date: 2 hours ago
     *  - End Date: 1:46 min ago
     *  - Price: 15
     *  - Number of Passengers: 3
     *  - Shift Id: @param
     */
    private ContentValues getSampleLiftValues1(long shiftId){
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.add(Calendar.HOUR_OF_DAY, -2);

        GregorianCalendar endDate = new GregorianCalendar();
        endDate.add(Calendar.HOUR_OF_DAY, -1);
        endDate.add(Calendar.MINUTE, -46);

        return this.getLiftValues(startDate, endDate, 15, 3, shiftId);
    }

    /*
     * It generates a ContentValues object for the Lift where:
     *  - Start Date: 1:30 hours ago
     *  - End Date: 1:23 min ago
     *  - Price: 12
     *  - Number of Passangers: 2
     *  - Shift Id: @param
     */
    private ContentValues getSampleLiftValues2(long shiftId){
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.add(Calendar.HOUR_OF_DAY, -1);
        startDate.add(Calendar.MINUTE, -30);

        GregorianCalendar endDate = new GregorianCalendar();
        endDate.add(Calendar.HOUR_OF_DAY, -1);
        endDate.add(Calendar.MINUTE, - 37);

        return this.getLiftValues(startDate, endDate, 12, 2, shiftId);
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

    /*
     * It generates a ContentValues for the Lift entity, where all data is passed as parameter.
     */
    private ContentValues getLiftValues(Calendar startDate,
                                        Calendar endDate,
                                        int price,
                                        int passengers_num,
                                        long shiftId){
        GregorianCalendar creationDate = new GregorianCalendar();

        ContentValues liftValues = new ContentValues();
        liftValues.put(LiftContract.LiftEntry.COLUMN_CREATED, creationDate.getTimeInMillis());
        liftValues.put(LiftContract.LiftEntry.COLUMN_LAST_UPD, creationDate.getTimeInMillis());
        liftValues.put(LiftContract.LiftEntry.COLUMN_START_DT, startDate.getTimeInMillis());
        liftValues.put(LiftContract.LiftEntry.COLUMN_END_DT, endDate.getTimeInMillis());
        liftValues.put(LiftContract.LiftEntry.COLUMN_PRICE, 15);
        liftValues.put(LiftContract.LiftEntry.COLUMN_PASSENGERS_NUM, 3);
        liftValues.put(LiftContract.LiftEntry.COLUMN_SHIFT_ID, shiftId);

        return liftValues;
    }

    /*
     * - Open db (Create if it doesn't exist)
     * - Check if it was possible to open the db
     */
    private SQLiteDatabase openCreateWritableDb(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        LiftDbHelper liftDbHelper = new LiftDbHelper(appContext);

        SQLiteDatabase sqLiteDatabase = liftDbHelper.getWritableDatabase();

        Assert.assertTrue("openCreateWritableDb Error: It wasn't possible to open the db.",
                sqLiteDatabase.isOpen());

        return sqLiteDatabase;
    }

}
