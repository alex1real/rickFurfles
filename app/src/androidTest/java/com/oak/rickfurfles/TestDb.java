package com.oak.rickfurfles;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.oak.rickfurfles.model.db.LiftContract;
import com.oak.rickfurfles.model.db.LiftDbHelper;

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

    /*********
     * Tests *
     ********/
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

        ContentValues addressValues = getAddressValuesSample1();

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
        ContentValues shiftValues = getShiftValuesSample1();

        long shiftId = sqLiteDatabase.insert(LiftContract.ShiftEntry.TABLE_NAME,
                null,
                shiftValues);

        // Check if the Shift was created
        Assert.assertTrue("insertExpense: It wasn't possible to insert the Shift", shiftId != -1);

        // Create a Expense
        ContentValues expenseValues = getExpenseValuesSample(shiftId);

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
        ContentValues shiftValues = getShiftValuesSample1();

        long shiftId = sqLiteDatabase.insert(LiftContract.ShiftEntry.TABLE_NAME,
                null,
                shiftValues);

        // Check if the Shift was created
        Assert.assertTrue("insertLift Error: Failure to insert shift.", shiftId != -1);

        // Create a Lift
        ContentValues liftSampleValues1 = getLiftValuesSample1(shiftId);

        long liftId1 = sqLiteDatabase.insert(LiftContract.LiftEntry.TABLE_NAME,
                null,
                liftSampleValues1);

        // Check if the Lift was created
        Assert.assertTrue("inserLift Error: Failure to insert Lift.", liftId1 != -1);

        // Create another Lift
        ContentValues liftSampleValues2 = getLiftValuesSample2(shiftId);

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
        ContentValues addressValues = getAddressValuesSample1();

        long addressId = sqLiteDatabase.insert(LiftContract.AddressEntry.TABLE_NAME,
                null,
                addressValues);

        // Check if Address was created
        Assert.assertTrue("insertLiftAddress Error: Failure to insert Address.", addressId != -1);

        // Insert Shift
        ContentValues shiftValues = getShiftValuesSample1();

        long shiftId = sqLiteDatabase.insert(LiftContract.ShiftEntry.TABLE_NAME,
                null,
                shiftValues);

        // Check if Shift was created
        Assert.assertTrue("insertLiftAddress Error: Failure to insert Shift.", shiftId != -1);

        // Insert Lift
        ContentValues liftValues = getLiftValuesSample1(shiftId);

        long liftId = sqLiteDatabase.insert(LiftContract.LiftEntry.TABLE_NAME,
                null,
                liftValues);

        // Check if Lift was created
        Assert.assertTrue("insertLiftAddress Error: Failure to insert Lift.", liftId != -1);

        // Insert LiftAddress
        ContentValues liftAddressValues = getLiftAddressValuesSample(liftId, addressId);

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

        ContentValues shiftValues = getShiftValuesSample1();

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

    /*****************************
     * Content Values Generators *
     ****************************/
    public static ContentValues getAddressValues(String place,
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

    public static ContentValues getAddressValuesSample1(){
        String place = "Camden Street";
        String neighborhood = "Saint Kevin's";
        String city = "Dublin";
        String state = "Leinster";
        String country = "Ireland";
        String zipcode = "Dublin 2";

        return getAddressValues(place,
                neighborhood,
                city,
                state,
                country,
                zipcode);
    }

    public static ContentValues getAddressValuesSample2(){
        String place = "O'Connell Street";
        String neighborhood = "Broadstone";
        String city = "Dublin";
        String state= "Leinster";
        String country = "Ireland";
        String zipcode = "Dublin 1";

        return getAddressValues(place,
                neighborhood,
                city,
                state,
                country,
                zipcode);
    }

    public static ContentValues getExpenseValues(String name,
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
     * It generates a ContentValues object for the Expense where:
     *  - Name: food
     *  - Date: Yesterday 21:30:XX
     *  - Value: 6.30
     *  - Shift Id: @param
     */
    public static ContentValues getExpenseValuesSample(long shiftId){
        String name = "food";

        GregorianCalendar date = new GregorianCalendar();
        date.add(Calendar.DAY_OF_YEAR, -1);
        date.set(Calendar.HOUR_OF_DAY, 21);
        date.set(Calendar.MINUTE, 30);

        BigDecimal value = new BigDecimal("6.3");

        return getExpenseValues(name,
                date,
                value,
                shiftId);
    }

    /*
     * It generates a ContentValues for the Lift entity, where all data is passed as parameter.
     */
    public static ContentValues getLiftValues(Calendar startDate,
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
     * It generates a ContentValues object for the Lift where:
     *  - Start Date: yesterday 23:11
     *  - End Date: yesterday 23:22
     *  - Price: 15.50
     *  - Number of Passengers: 3
     *  - Shift Id: @param
     */
    public static ContentValues getLiftValuesSample1(long shiftId){
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.add(Calendar.DAY_OF_YEAR, -1);
        startDate.set(Calendar.HOUR_OF_DAY, 23);
        startDate.set(Calendar.MINUTE, 11);

        GregorianCalendar endDate = new GregorianCalendar();
        endDate.add(Calendar.DAY_OF_YEAR, -1);
        endDate.set(Calendar.HOUR_OF_DAY, 23);
        endDate.set(Calendar.MINUTE, 22);

        //float is not precise
        BigDecimal price = new BigDecimal("15.5");
        int numberOfPassengers = 3;

        return getLiftValues(startDate,
                endDate,
                price,
                numberOfPassengers,
                shiftId);
    }

    /*
     * It generates a ContentValues object for the Lift where:
     *  - Start Date: Today 1:31
     *  - End Date: Today 1:43
     *  - Price: 12.20
     *  - Number of Passengers: 2
     *  - Shift Id: @param
     */
    public static ContentValues getLiftValuesSample2(long shiftId){
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.set(Calendar.HOUR_OF_DAY, 1);
        startDate.set(Calendar.MINUTE, 31);

        GregorianCalendar endDate = new GregorianCalendar();
        endDate.set(Calendar.HOUR_OF_DAY, 1);
        endDate.set(Calendar.MINUTE, 43);

        //float isn't precise
        BigDecimal price = new BigDecimal("12.2");
        int numberOfPassengers = 2;

        return getLiftValues(startDate,
                endDate,
                price,
                numberOfPassengers,
                shiftId);
    }

    /*
     * It generates a ContentValues object for the Lift where:
     *  - Start Date: d-2 22:13
     *  - End Date: d-2 22:22
     *  - Price: 10.00
     *  - Number of Passengers: 2
     *  - Shift Id: @param
     */
    public static ContentValues getLiftValuesSample3(long shiftId){
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.add(Calendar.DAY_OF_YEAR, -2);
        startDate.set(Calendar.HOUR_OF_DAY, 22);
        startDate.set(Calendar.MINUTE, 13);

        GregorianCalendar endDate = new GregorianCalendar();
        endDate.add(Calendar.DAY_OF_YEAR, -2);
        endDate.set(Calendar.HOUR_OF_DAY, 22);
        endDate.set(Calendar.MINUTE, 22);

        //float isn't precise
        BigDecimal price = new BigDecimal("10.0");
        int numberOfPassengers = 2;

        return getLiftValues(startDate,
                endDate,
                price,
                numberOfPassengers,
                shiftId);
    }

    /*
     * It generates a ContentValues object for the Lift where:
     *  - Start Date: d -2 11:58
     *  - End Date: d -1 00:03
     *  - Price: 5.00
     *  - Number of Passengers: 1
     *  - Shift Id: @param
     */
    public static ContentValues getLiftValuesSample4(long shiftId){
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.add(Calendar.DAY_OF_YEAR, -2);
        startDate.set(Calendar.HOUR_OF_DAY, 23);
        startDate.set(Calendar.MINUTE, 58);

        GregorianCalendar endDate = new GregorianCalendar();
        endDate.add(Calendar.DAY_OF_YEAR, -1);
        endDate.set(Calendar.HOUR_OF_DAY, 0);
        endDate.set(Calendar.MINUTE, 3);

        //float isn't precise
        BigDecimal price = new BigDecimal("5.0");
        int numberOfPassengers = 1;

        return getLiftValues(startDate,
                endDate,
                price,
                numberOfPassengers,
                shiftId);
    }

    /*
     * It generates a ContentValues object for the Lift where:
     *  - Start Date: d -14 21:21
     *  - End Date: d -14 21:36 min ago
     *  - Price: 50.00
     *  - Number of Passengers: 2
     *  - Shift Id: @param
     */
    public static ContentValues getLiftValuesSample5(long shiftId){
        GregorianCalendar startDate = new GregorianCalendar();
        startDate.add(Calendar.DAY_OF_YEAR, -14);
        startDate.set(Calendar.HOUR_OF_DAY, 1);
        startDate.set(Calendar.MINUTE, 31);

        GregorianCalendar endDate = new GregorianCalendar();
        endDate.add(Calendar.DAY_OF_YEAR, -14);
        endDate.set(Calendar.HOUR_OF_DAY, 1);
        endDate.set(Calendar.MINUTE, - 43);

        //float isn't precise
        BigDecimal price = new BigDecimal("50.0");
        int numberOfPassengers = 2;

        return getLiftValues(startDate,
                endDate,
                price,
                numberOfPassengers,
                shiftId);
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
    public static ContentValues getLiftAddressValues(long liftId,
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
    public static ContentValues getLiftAddressValuesSample(long liftId,
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
     * Generate a ContentValues object for the shift where:
     *  - Creation Date: Current Date
     *  - Last Update Date: Current Date
     *  - Start shift date: @param
     *  - End shift date: @param
     */
    public static ContentValues getShiftValues(Calendar startDate,
                                               Calendar endDate){
        GregorianCalendar creationDate = new GregorianCalendar();

        ContentValues shiftValues = new ContentValues();
        shiftValues.put(LiftContract.ShiftEntry.COLUMN_CREATED, creationDate.getTimeInMillis());
        shiftValues.put(LiftContract.ShiftEntry.COLUMN_LAST_UPD, creationDate.getTimeInMillis());
        shiftValues.put(LiftContract.ShiftEntry.COLUMN_START_DT, startDate.getTimeInMillis());
        shiftValues.put(LiftContract.ShiftEntry.COLUMN_END_DT, endDate.getTimeInMillis());

        return shiftValues;
    }

    /*
     * Generate a ContentValues object for the shift with the following characteristics:
     *  - Creation Date: Current Date
     *  - Last Update Date: Current Date
     *  - Start shift date: Yesterday 21:00:XX
     *  - End shift date: Today 05:XX:XX
     * Shift for Lift samples 1 and 2
     */
    public static ContentValues getShiftValuesSample1(){
        Calendar shiftStartDate = new GregorianCalendar();
        shiftStartDate.add(Calendar.DAY_OF_MONTH, -1);
        shiftStartDate.set(Calendar.HOUR_OF_DAY, 21);
        shiftStartDate.set(Calendar.MINUTE, 0);

        Calendar shiftEndDate = new GregorianCalendar();
        shiftEndDate.set(Calendar.HOUR_OF_DAY, 5);

        return getShiftValues(shiftStartDate, shiftEndDate);

    }

    /*
     * Generate a ContentValues object for the shift with the following characteristics:
     *  - Creation Date: Current Date
     *  - Last Update Date: Current Date
     *  - Start shift date: d-2 21:30:XX
     *  - End shift date: yesterday 04:44:XX
     * Shift for Lift samples 3 and 4
     */
    public static ContentValues getShiftValuesSample2(){
        Calendar shiftStartDate = new GregorianCalendar();
        shiftStartDate.add(Calendar.DAY_OF_YEAR, -2);
        shiftStartDate.set(Calendar.HOUR_OF_DAY, 21);
        shiftStartDate.set(Calendar.MINUTE, 30);

        Calendar shiftEndDate = new GregorianCalendar();
        shiftEndDate.add(Calendar.DAY_OF_YEAR, -1);
        shiftEndDate.set(Calendar.HOUR_OF_DAY, 4);
        shiftEndDate.set(Calendar.MINUTE, 44);

        return getShiftValues(shiftStartDate, shiftEndDate);
    }

    /*
     * Generate a ContentValues object for the shift with the following characteristics:
     *  - Creation Date: Current Date
     *  - Last Update Date: Current Date
     *  - Start shift date: 2 weeks ago 21:15:XX
     *  - End shift date: d-13 03:XX:XX
     * Shift for Lift sample 5
     */
    public static ContentValues getShiftValuesSample3(){
        Calendar shiftStartDate = new GregorianCalendar();
        shiftStartDate.add(Calendar.DAY_OF_YEAR, -14);
        shiftStartDate.set(Calendar.HOUR_OF_DAY, 21);
        shiftStartDate.set(Calendar.MINUTE, 15);

        Calendar shiftEndDate = new GregorianCalendar();
        shiftEndDate.add(Calendar.DAY_OF_YEAR, -13);
        shiftEndDate.set(Calendar.HOUR_OF_DAY, 3);

        return getShiftValues(shiftStartDate, shiftEndDate);
    }

    /*******************
     * Private Methods *
     ******************/
    private void deleteDatabase(){
        Context appContext = InstrumentationRegistry.getTargetContext();

        appContext.deleteDatabase(LiftDbHelper.DATABASE_NAME);
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
