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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

/**
 * Created by Alex on 26/10/2016.
 * This class:
 *    It tests all tables creation and insertion.
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
        tableNamesHashSet.add(LiftContract.AddressEntry.TABLE_NAME);


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
    
    @Test
    public void insertAddress(){
        // Open db (Create if it doesn't exist)
        SQLiteDatabase sqLiteDatabase = this.openCreateWritableDb();

        // Create an Address

        ContentValues addressValues = this.getSampleAddressValues();

        long addressId = sqLiteDatabase.insert(LiftContract.AddressEntry.TABLE_NAME,
                null,
                addressValues);

        addressValues.put(LiftContract.AddressEntry._ID, addressId);

        // Check if the Address was created
        Assert.assertTrue("InsertAddress Error: Failure to insert address.", addressId != -1);

        // Query all Address
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + LiftContract.AddressEntry.TABLE_NAME,
                null);

        // Check if the Address is present in the cursor
        Assert.assertTrue("insertAddress Error: Inconsistencies have been found beteween the cursor and expected values",
                TestUtilities.isValidCursor(cursor, addressValues));

        cursor.close();
    }

    /*
     * It checks:
     *  - If the db could be opened
     *  - If it was possible to insert a Shift Record in the db
     *  - If it was possible to insert a Expense
     *  - If the record inserted into Expense table are with the expected values
     */
    @Test
    public void insertExpense(){
        // Open db (Create if it doesn't exists)
        SQLiteDatabase sqLiteDatabase = this.openCreateWritableDb();

        // Create a Shift
        ContentValues shiftValues = this.getSampleShiftValues();

        long shiftId = sqLiteDatabase.insert(LiftContract.ShiftEntry.TABLE_NAME,
                null,
                shiftValues);

        // Check if the Shift was created
        Assert.assertTrue("insertExpense: It wasn't possible to insert the Shift", shiftId != -1);

        // Create a Expense
        ContentValues expenseValues = this.getSampleExpenseValues(shiftId);

        long expenseId = sqLiteDatabase.insert(LiftContract.ExpenseEntry.TABLE_NAME,
                null,
                expenseValues);

        // Check if the expense was created
        Assert.assertTrue("insertExpense: It was impossible to insert the Expense", expenseId != -1);

        // Query all expenses
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + LiftContract.ExpenseEntry.TABLE_NAME,
                null);

        // Check if the cursor has the inserted Expense
        expenseValues.put(LiftContract.ExpenseEntry._ID, expenseId);

        Assert.assertTrue("insertExpense: Expencted expense Values and cursor Values are not matching",
                TestUtilities.isValidCursor(cursor, expenseValues));

        cursor.close();
    }

    /*
     * It checks:
     *  - If the db could be opened
     *  - If it was possible to insert a Shift Record in the db
     *  - If it was possible to insert 2 Lift sample Records
     *  - If the record inserted into Lift table are with the expected values
     */
    @Test
    public void insertLift(){
        // Open db (Create if it doesn't exist)
        SQLiteDatabase sqLiteDatabase = this.openCreateWritableDb();

        // Create a Shift
        ContentValues shiftValues = this.getSampleShiftValues();

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
        ContentValues liftSampleValues2 = this.getSampleLiftValues2(shiftId);

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
     *  - If it was possible to insert a Shift Record in the db
     *  - If it was possible to insert a Lift Record in the db
     *  - If it was possible to insert a Address Record in the db
     *  - If it was possible to insert LiftAddress sample Record into the db
     *  - If the recorde inserted into AddressLift table are with the expected values
     */
    @Test
    public void insertLiftAddress(){
        // Open db (Create if it doesn't exist)
        SQLiteDatabase sqLiteDatabase = this.openCreateWritableDb();

        // Insert Address
        ContentValues addressValues = this.getSampleAddressValues();

        long addressId = sqLiteDatabase.insert(LiftContract.AddressEntry.TABLE_NAME,
                null,
                addressValues);

        // Check if Address was created
        Assert.assertTrue("insertLiftAddress Error: Failure to insert Address.", addressId != -1);

        // Insert Shift
        ContentValues shiftValues = this.getSampleShiftValues();

        long shiftId = sqLiteDatabase.insert(LiftContract.ShiftEntry.TABLE_NAME,
                null,
                shiftValues);

        // Check if Shift was created
        Assert.assertTrue("insertLiftAddress Error: Failure to insert Shift.", shiftId != -1);

        // Insert Lift
        ContentValues liftValues = this.getSampleLiftValues1(shiftId);

        long liftId = sqLiteDatabase.insert(LiftContract.LiftEntry.TABLE_NAME,
                null,
                liftValues);

        // Check if Lift was created
        Assert.assertTrue("insertLiftAddress Error: Failure to insert Lift.", liftId != -1);

        // Insert LiftAddress
        ContentValues liftAddressValues = this.getSampleLiftAddressValues(liftId, addressId);

        long liftAddressId = sqLiteDatabase.insert(LiftContract.LiftAddressEntry.TABLE_NAME,
                null,
                liftAddressValues);

        // Check if LiftAddress was created
        Assert.assertTrue("insertLiftAddress Error: Failure to insert LiftAddress.", liftAddressId != -1);

        // Query all LiftAddress
        Cursor cursor = sqLiteDatabase.rawQuery("select * from " + LiftContract.LiftAddressEntry.TABLE_NAME,
                null);

        // Check if the sample LiftAddress is present into the cursor
        liftAddressValues.put(LiftContract.LiftAddressEntry._ID, liftAddressId);

        Assert.assertTrue("insertLiftAddress Error: Differences between LiftAddress Cursor and " +
                "expected values has been found.",
                TestUtilities.isValidCursor(cursor, liftAddressValues));

        // Try to reinsert the LiftAddress (it shouldn't be possible)
        liftAddressId = sqLiteDatabase.insert(LiftContract.LiftAddressEntry.TABLE_NAME,
                null,
                liftAddressValues);

        // Check if it was possible to reinsert the LiftAddress
        Assert.assertEquals("insertLiftAddress Error: It was possible to violate LiftAddress Unique Key (LIFT_ID, ADDR_ID, TYPE)",
                -1, // Expected value - Fail to reinsert the record
                liftAddressId ); // Real value

        cursor.close();
    }

    /*
     * It checks:
     *  - If the db could be opened
     *  - If it was possible to insert a record in the db
     *  - If the record inserted into database is with the expected values
     */
    @Test
    public void insertShift(){
        SQLiteDatabase sqLiteDatabase = this.openCreateWritableDb();

        ContentValues shiftValues = this.getSampleShiftValues();

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

    private ContentValues getAddressValues(String place,
                                           String neighborhood,
                                           String city,
                                           String state,
                                           String country,
                                           String zipcode){
        Calendar creationDate = new GregorianCalendar();

        ContentValues addressValues = new ContentValues();
        addressValues.put(LiftContract.AddressEntry.COLUMN_CREATED, creationDate.getTimeInMillis());
        addressValues.put(LiftContract.AddressEntry.COLUMN_LAST_UPD, creationDate.getTimeInMillis());
        addressValues.put(LiftContract.AddressEntry.COLUMN_PLACE, place);
        addressValues.put(LiftContract.AddressEntry.COLUMN_NEIGHBORHOOD, neighborhood);
        addressValues.put(LiftContract.AddressEntry.COLUMN_CITY, city);
        addressValues.put(LiftContract.AddressEntry.COLUMN_STATE, state);
        addressValues.put(LiftContract.AddressEntry.COLUMN_COUNTRY, country);
        addressValues.put(LiftContract.AddressEntry.COLUMN_ZIPCODE, zipcode);

        return addressValues;
    }

    private ContentValues getExpenseValues(String name,
                                           GregorianCalendar date,
                                           BigDecimal value,
                                           long shiftId){
        GregorianCalendar creationDate = new GregorianCalendar();

        ContentValues expenseValues = new ContentValues();
        expenseValues.put(LiftContract.ExpenseEntry.COLUMN_CREATED, creationDate.getTimeInMillis());
        expenseValues.put(LiftContract.ExpenseEntry.COLUMN_LAST_UPD, creationDate.getTimeInMillis());
        expenseValues.put(LiftContract.ExpenseEntry.COLUMN_NAME, name);
        expenseValues.put(LiftContract.ExpenseEntry.COLUMN_DATE, date.getTimeInMillis());
        expenseValues.put(LiftContract.ExpenseEntry.COLUMN_VALUE, value.toString());
        expenseValues.put(LiftContract.ExpenseEntry.COLUMN_SHIFT_ID, shiftId);

        return expenseValues;
    }

    /*
     * It generates a ContentValues for the Lift entity, where all data is passed as parameter.
     */
    private ContentValues getLiftValues(Calendar startDate,
                                        Calendar endDate,
                                        BigDecimal price,
                                        int passengersNum,
                                        long shiftId){
        GregorianCalendar creationDate = new GregorianCalendar();

        ContentValues liftValues = new ContentValues();
        liftValues.put(LiftContract.LiftEntry.COLUMN_CREATED, creationDate.getTimeInMillis());
        liftValues.put(LiftContract.LiftEntry.COLUMN_LAST_UPD, creationDate.getTimeInMillis());
        liftValues.put(LiftContract.LiftEntry.COLUMN_START_DT, startDate.getTimeInMillis());
        liftValues.put(LiftContract.LiftEntry.COLUMN_END_DT, endDate.getTimeInMillis());
        liftValues.put(LiftContract.LiftEntry.COLUMN_PRICE, price.toString());
        liftValues.put(LiftContract.LiftEntry.COLUMN_PASSENGERS_NUM, passengersNum);
        liftValues.put(LiftContract.LiftEntry.COLUMN_SHIFT_ID, shiftId);

        return liftValues;
    }

    /*
     * Generate a ContentValues object for the shift where:
     *  - Lift Id: @param
     *  - Address Id: @param
     *  - Type: @param [HOP_ON/HOP_IN]
     *  - Number: @param
     *  - Latitude: @param
     *  - Longitude: @param
     *  - Point of Interest: @param
     */
    private ContentValues getLiftAddressValues(long liftId,
                                               long addressId,
                                               String type,
                                               int number,
                                               double latitude,
                                               double longitude,
                                               String pointOfInterest){
        GregorianCalendar creationDate = new GregorianCalendar();

        ContentValues liftAddressValues = new ContentValues();
        liftAddressValues.put(LiftContract.LiftAddressEntry.COLUMN_CREATED, creationDate.getTimeInMillis());
        liftAddressValues.put(LiftContract.LiftAddressEntry.COLUMN_LAST_UPD, creationDate.getTimeInMillis());
        liftAddressValues.put(LiftContract.LiftAddressEntry.COLUMN_LIFT_ID, liftId);
        liftAddressValues.put(LiftContract.LiftAddressEntry.COLUMN_ADDR_ID, addressId);
        liftAddressValues.put(LiftContract.LiftAddressEntry.COLUMN_TYPE, type);
        liftAddressValues.put(LiftContract.LiftAddressEntry.COLUMN_NUM, number);
        liftAddressValues.put(LiftContract.LiftAddressEntry.COLUMN_LAT, latitude);
        liftAddressValues.put(LiftContract.LiftAddressEntry.COLUMN_LON, longitude);
        liftAddressValues.put(LiftContract.LiftAddressEntry.COLUMN_POI, pointOfInterest);

        return liftAddressValues;
    }

    private ContentValues getSampleAddressValues(){
        String place = "Camden Street";
        String neighborhood = "Saint Kevin's";
        String city = "Dublin";
        String state = "Dublin";
        String country = "Ireland";
        String zipcode = "Dublin 2";

        return this.getAddressValues(place,
                neighborhood,
                city,
                state,
                country,
                zipcode);
    }

    /*
     * It generates a ContentValues object for the Expense where:
     *  - Name: food
     *  - Date: Yesterday 21:30:XX
     *  - Value: 6.30
     *  - Shift Id: @param
     */
    private ContentValues getSampleExpenseValues(long shiftId){
        String name = "food";

        GregorianCalendar date = new GregorianCalendar();
        date.add(Calendar.DAY_OF_YEAR, -1);
        date.set(Calendar.HOUR_OF_DAY, 21);
        date.set(Calendar.MINUTE, 30);

        BigDecimal value = new BigDecimal("6.3");

        return this.getExpenseValues(name,
                date,
                value,
                shiftId);
    }

    /*
     * It generates a ContentValues object for the Lift where:
     *  - Start Date: 2 hours ago
     *  - End Date: 1:46 min ago
     *  - Price: 15.50
     *  - Number of Passengers: 3
     *  - Shift Id: @param
     */
    private ContentValues getSampleLiftValues1(long shiftId){
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.add(Calendar.HOUR_OF_DAY, -2);

        GregorianCalendar endDate = new GregorianCalendar();
        endDate.add(Calendar.HOUR_OF_DAY, -1);
        endDate.add(Calendar.MINUTE, -46);

        //float is not precise
        BigDecimal price = new BigDecimal("15.5");
        int numberOfPassengers = 3;

        return this.getLiftValues(startDate,
                endDate,
                price,
                numberOfPassengers,
                shiftId);
    }

    /*
     * It generates a ContentValues object for the LiftAddress where:
     *  - Lift Id: @param
     *  - Address Id: @param
     *  - type: "HOP_IN"
     *  - Number: 33
     *  - Latitude: 53.349722
     *  - Longitude: -6.260278
     *  - Point of interest: Spire
     */
    private ContentValues getSampleLiftAddressValues(long liftId,
                                                     long addressId){
        String type = "HOP_IN";
        int number = 33;
        double latitude = 53.349722;
        double longitude = -6.260278;
        String pointOfInterest = "Spire";

        return getLiftAddressValues(liftId,
                addressId,
                type,
                number,
                latitude,
                longitude,
                pointOfInterest);
    }

    /*
     * It generates a ContentValues object for the Lift where:
     *  - Start Date: 1:30 hours ago
     *  - End Date: 1:23 min ago
     *  - Price: 12.20
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

        //float isn't precise
        BigDecimal price = new BigDecimal("12.2");
        int numberOfPassengers = 2;

        return this.getLiftValues(startDate,
                endDate,
                price,
                numberOfPassengers,
                shiftId);
    }

    /*
     * Generate a ContentValues object for the shift with the following characteristics:
     *  - Creation Date: Current Date
     *  - Last Update Date: Current Date
     *  - Start shift date: Yesterday 21:00:XX
     *  - End shift date: Today 05:XX:XX
     */
    private ContentValues getSampleShiftValues(){
        Calendar shiftStartDate = new GregorianCalendar();
        shiftStartDate.add(Calendar.DAY_OF_MONTH, -1);
        shiftStartDate.set(Calendar.HOUR_OF_DAY, 21);
        shiftStartDate.set(Calendar.MINUTE, 0);

        Calendar shiftEndDate = new GregorianCalendar();
        shiftEndDate.set(Calendar.HOUR_OF_DAY, 5);

        return this.getShiftValues(shiftStartDate, shiftEndDate);

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
