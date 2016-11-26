package com.oak.rickfurfles;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.oak.rickfurfles.model.db.LiftContract;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by Alex on 09/11/2016.
 * This class tests LiftProvider class
 */

@RunWith(AndroidJUnit4.class)
public class TestProvider {

    /*************
     * Constants *
     ************/
    private static final String LOG_TAG = TestProvider.class.getSimpleName();
    private Context appContext;
    private SimpleDateFormat logSdf;

    /******************
     * Public Methods *
     *****************/
    /**********
     * Before *
     *********/
    @Before
    public void setUp(){
        appContext = InstrumentationRegistry.getTargetContext();
        logSdf = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss.SSS");

        this.deleteAllRecordsFromProvider();
    }

    /*********
     * Tests *
     ********/
    @Test
    public void getType(){
        // Address
        String type = appContext.getContentResolver().getType(LiftContract.AddressEntry.CONTENT_URI);

        Log.v(LOG_TAG, "LiftContract.AddressEntry.CONTENT_URI type: " + type);

        Assert.assertEquals("Error: AddressEntry CONTNT_URI should return AddressEntry.CONTENT_TYPE",
                LiftContract.AddressEntry.CONTENT_TYPE,
                type);

        // Expense
        type = appContext.getContentResolver().getType(LiftContract.ExpenseEntry.CONTENT_URI);

        Assert.assertEquals("Error: ExpenseEntry CONTENT_URI should return ExpenseEntry.CONTENT_TYPE",
                LiftContract.ExpenseEntry.CONTENT_TYPE,
                type);

        long shiftId = 1;

        type = appContext.getContentResolver().getType(LiftContract.ExpenseEntry.buildExpenseByShiftUri(shiftId));

        Assert.assertEquals("Error ExpenseEntry.buildExpenseByShiftUri should return ExpenseEntry.CONTENT_TYPE",
                LiftContract.ExpenseEntry.CONTENT_TYPE,
                type);

        // Lift
        type = appContext.getContentResolver().getType(LiftContract.LiftEntry.CONTENT_URI);

        Assert.assertEquals("Error: LiftEntry CONTENT_URI should return LiftEntry.CONTENT_TYPE",
                LiftContract.LiftEntry.CONTENT_TYPE,
                type);

        type = appContext.getContentResolver().getType(LiftContract.LiftEntry.buildLiftByShiftUri(shiftId));

        Assert.assertEquals("Error: LiftEntry.buildLiftByShiftUri should return Lift.CONTENT_TYPE",
                LiftContract.LiftEntry.CONTENT_TYPE,
                type);

        // Lift Address
        type = appContext.getContentResolver().getType(LiftContract.LiftAddressEntry.CONTENT_URI);

        Assert.assertEquals("Error: LiftAddressEntry CONTENT_URI should return LiftAddress.CONTENT_TYPE",
                LiftContract.LiftAddressEntry.CONTENT_TYPE,
                type);

        long liftId = 1;

        type = appContext.getContentResolver().getType(LiftContract.LiftAddressEntry.buildLiftAddressByLiftUri(liftId));

        Assert.assertEquals("Error: LiftAddressEntry.buildLiftAddressByLiftUri should return LiftAdress.CONTENT_TYPE",
                LiftContract.LiftAddressEntry.CONTENT_TYPE,
                type);

        String liftAddressType = "HOP_ON";

        type = appContext.getContentResolver().getType(LiftContract.LiftAddressEntry.buildLiftAddressByLiftTypeUri(liftId, liftAddressType));

        Assert.assertEquals("Error: LiftAddressEntry.buildLiftAddressByLiftTypeUri should return LiftAddress.CONTENT_ITEM_TYPE",
                LiftContract.LiftAddressEntry.CONTENT_ITEM_TYPE,
                type);

        // Shift
        type = appContext.getContentResolver().getType(LiftContract.ShiftEntry.CONTENT_URI);

        Assert.assertEquals("Error: ShiftEntry CONTENT_TYPE should return ShiftEntry.CONTENT_TYPE",
                LiftContract.ShiftEntry.CONTENT_TYPE,
                type);

        GregorianCalendar startDate = new GregorianCalendar();
        startDate.add(Calendar.DAY_OF_YEAR, -5);

        GregorianCalendar endDate = new GregorianCalendar();

        type = appContext.getContentResolver().getType(LiftContract.ShiftEntry.buildShiftPeriodUri(startDate, endDate));

        Assert.assertEquals("Error: ShiftEntry.buildShiftPeriodUri should return ShiftEntry.CONTENT_TYPE",
                LiftContract.ShiftEntry.CONTENT_TYPE,
                type);

        type = appContext.getContentResolver().getType(LiftContract.ShiftEntry.buildShiftPeriodSumUri(startDate, endDate));

        Assert.assertEquals("Error: ShiftEntry.buildShiftPeriodSumUri should return ShiftEntry.CONTENT_ITEM_TYPE",
                LiftContract.ShiftEntry.CONTENT_ITEM_TYPE,
                type);

        type = appContext.getContentResolver().getType(LiftContract.ShiftEntry.buildShiftSumUri(shiftId));

        Log.v(LOG_TAG, "LiftContract.ShiftEntry.buildShiftFromUri(1): " + LiftContract.ShiftEntry.buildShiftSumUri(1).toString());

        Assert.assertEquals("Error: ShiftEntry.buildShiftSumUri should return ShiftEntry.CONTENT_ITEM_TYPE",
                LiftContract.ShiftEntry.CONTENT_ITEM_TYPE,
                type);
    }

    @Test
    public void getIdFromShiftUri(){
        long shiftId = 3;
        Uri uri = LiftContract.ShiftEntry.buildShiftSumUri(3);

        Assert.assertEquals("Error: It wasn't possible to retrieve the currect shiftId",
                shiftId,
                LiftContract.ShiftEntry.getIdFromUri(uri));
    }

    @Test
    public void crudAddress(){
        /******************
         * Create Address *
         *****************/
        // Insert Address 1
        ContentValues addressValuesSample1 = TestDb.getAddressValuesSample1();

        // Register a ContentObserver for the insert
        TestContentObserver tco = TestContentObserver.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LiftContract.AddressEntry.CONTENT_URI,
                true,
                tco);
        Uri addressUri = appContext.getContentResolver().insert(LiftContract.AddressEntry.CONTENT_URI,
                addressValuesSample1);

        // Check if the ContentObserver was called
        // If it fails, insert location isn't calling getContext().getResolver().notifyChange(uri, null)
        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        long addressId = ContentUris.parseId(addressUri);
        addressValuesSample1.put(LiftContract.AddressEntry._ID, addressId);

        // Check if the insertion has been made
        Assert.assertTrue("Error: Fail to insert Address. Invalid Id.", addressId > 0);

        // Insert Address 2
        ContentValues addressValuesSample2 = TestDb.getAddressValuesSample2();

        addressUri = appContext.getContentResolver().insert(LiftContract.AddressEntry.CONTENT_URI,
                addressValuesSample2);

        addressId = ContentUris.parseId(addressUri);
        addressValuesSample2.put(LiftContract.AddressEntry._ID, addressId);

        Assert.assertTrue("Error: Fail to insert Address 2. Invalid returned Id.", addressId > 0);

        /****************
         * Read Address *
         ***************/
        Cursor cursor = appContext.getContentResolver().query(LiftContract.AddressEntry.CONTENT_URI,
                null,
                null,
                null,
                null);

        // Check if returned Cursor contains the inserted record
        Assert.assertTrue("Error: The returned cursor doesn't contain the first inserted address.",
                TestUtilities.isValidCursor(cursor, addressValuesSample1));

        Assert.assertTrue("Error: The returned cursor doesn't contain the second inserted address.",
                TestUtilities.isValidCursor(cursor, addressValuesSample2));

        cursor.close();

        /******************
         * Update Address *
         *****************/
        ContentValues updatedAddress2Values = new ContentValues(addressValuesSample2);
        updatedAddress2Values.put(LiftContract.AddressEntry.COLUMN_PLACE, "Different Street");

        // Create a cursor with observer to make sure that content provider is notifying the
        // observers as expected
        cursor = appContext.getContentResolver().query(LiftContract.AddressEntry.CONTENT_URI,
                null, null, null, null);

        tco = TestContentObserver.getTestContentObserver();
        cursor.registerContentObserver(tco);

        int numAffectedRows = appContext.getContentResolver().update(LiftContract.AddressEntry.CONTENT_URI,
                updatedAddress2Values,
                LiftContract.AddressEntry._ID + " = ?",
                new String[]{Long.toString(addressId)});

        Assert.assertEquals("Error: The update method failed.", 1, numAffectedRows);

        // Test to make sure if the ContentObserver was called.
        tco.waitForNotificationOrFail();

        cursor.unregisterContentObserver(tco);
        cursor.close();

        // Check if the alteration is reflected into the record
        cursor = appContext.getContentResolver().query(LiftContract.AddressEntry.CONTENT_URI,
                null, null, null, null);

        Assert.assertTrue("Error: The returned cursor doesn't contain the updated record.",
                TestUtilities.isValidCursor(cursor, updatedAddress2Values));

        cursor.close();

        /******************
         * Delete Address *
         *****************/
        cursor = appContext.getContentResolver().query(LiftContract.AddressEntry.CONTENT_URI,
                null, null, null, null);
        Assert.assertTrue("Error: The record that would be deleted couldn't be found",
                TestUtilities.isValidCursor(cursor, updatedAddress2Values));

        cursor.close();

        tco = TestContentObserver.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LiftContract.AddressEntry.CONTENT_URI,
                true,
                tco);

        appContext.getContentResolver().delete(LiftContract.AddressEntry.CONTENT_URI,
                LiftContract.AddressEntry._ID + " = ?",
                new String[]{Long.toString(addressId)});

        // Check if the ContentObserver has been called
        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        cursor = appContext.getContentResolver().query(LiftContract.AddressEntry.CONTENT_URI,
                null, null, null, null);

        // Check if the record is not present into the cursor anymore
        Assert.assertFalse("Error: The record hasn't been deleted.",
                TestUtilities.isValidCursor(cursor, updatedAddress2Values));
    }

    @Test
    public void crudExpense(){
        /**************************
         * Insert FK Dependencies *
         *************************/
        // Insert Shift 1
        // Insert Shift 2

        /******************
         * Create Expense *
         *****************/
        // Insert Expense 1
        // Insert Expense 2

        /****************
         * Read Expense *
         ***************/
        // Query all Expenses
        // Query Expenses from Shift 1

        /******************
         * Update Expense *
         *****************/
        // Update Expense 2

        /******************
         * Delete Expense *
         *****************/
        // Delete Expense 2

        //ToDo: Implement crudExpense()
    }

    @Test
    public void crudLift(){
        /************************
         * Insert FK Dependency *
         ***********************/
        // Insert Shift 1
        ContentValues shiftValues1 = TestDb.getShiftValuesSample1();

        Uri shiftUri1 = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shiftValues1);

        long shiftId1 = ContentUris.parseId(shiftUri1);
        Assert.assertTrue("Error: Invalid Id returned while trying to insert Shift 1",
                shiftId1 > 0);

        // Insert Shift 2
        ContentValues shiftValues2 = TestDb.getShiftValuesSample2();

        Uri shiftUri2 = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shiftValues2);

        long shiftId2 = ContentUris.parseId(shiftUri2);

        Assert.assertTrue("Error: Invalid Id returned while trying to insert Shift 2.",
                shiftId2 > 0);

        // Insert Shift 3
        ContentValues shiftValues3 = TestDb.getShiftValuesSample3();

        Uri shiftUri3 = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shiftValues3);

        long shiftId3 = ContentUris.parseId(shiftUri3);

        Assert.assertTrue("Error: Invalid Id returned while trying to insert Shift 3.",
                shiftId3 > 0);

        /***************
         * Create Lift *
         **************/
        // Lift 1
        // Register a ContentObserver
        TestContentObserver tco = TestContentObserver.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LiftContract.LiftEntry.CONTENT_URI,
                true,
                tco);

        // Insert Lift
        ContentValues liftValues1 = TestDb.getLiftValuesSample1(shiftId1);
        Uri liftUri1 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues1);

        // Check if the ContentObserver has been called
        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        // Check if the Lift was inserted
        long liftId1 = ContentUris.parseId(liftUri1);
        liftValues1.put(LiftContract.LiftEntry._ID, liftId1);

        Assert.assertTrue("Error: Fail to inert Lift 1. Unexpected Id returned.", liftId1 > 0);

        // Lift 2
        ContentValues liftValues2 = TestDb.getLiftValuesSample2(shiftId1);
        Uri liftUri2 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues2);

        long liftId2 = ContentUris.parseId(liftUri2);
        liftValues2.put(LiftContract.LiftEntry._ID, liftId2);

        Assert.assertTrue("Error: Fail to insert Lift 2. Unexpected Id returned.", liftId2 > 0);

        // Lift 3
        ContentValues liftValues3 = TestDb.getLiftValuesSample3(shiftId2);
        Uri liftUri3 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues3);

        long liftId3 = ContentUris.parseId(liftUri3);
        liftValues3.put(LiftContract.LiftEntry._ID, liftId3);

        Assert.assertTrue("Error: Fail to insert Lift 3. Unexpected Id returned.", liftId3 > 0);

        // Lift 4
        ContentValues liftValues4 = TestDb.getLiftValuesSample4(shiftId2);
        Uri liftUri4 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues4);

        long liftId4 = ContentUris.parseId(liftUri4);
        liftValues4.put(LiftContract.LiftEntry._ID, liftId4);

        Assert.assertTrue("Error: Fail to insert Lift 4. Unexpected Id returned.", liftId4 > 0);

        // Lift 5
        ContentValues liftValues5 = TestDb.getLiftValuesSample5(shiftId3);
        Uri liftUri5 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues5);

        long liftId5 = ContentUris.parseId(liftUri5);
        liftValues5.put(LiftContract.LiftEntry._ID, liftId5);

        Assert.assertTrue("Error: Fail to insert Lift 5. Unexpected Id returned.", liftId5 > 0);

        /*************
         * Read Lift *
         ************/
        // Query all Lifts
        Cursor cursor = appContext.getContentResolver().query(LiftContract.LiftEntry.CONTENT_URI,
                null, null, null, null);

        Assert.assertTrue("Error: Lift 1 is not present into all Lifts cursor.",
                TestUtilities.isValidCursor(cursor, liftValues1));

        Assert.assertTrue("Error: Lift 2 is not present into all Lifts cursor.",
                TestUtilities.isValidCursor(cursor, liftValues2));

        Assert.assertTrue("Error: Lift 3 is not present into all Lifts cursor.",
                TestUtilities.isValidCursor(cursor, liftValues3));

        Assert.assertTrue("Error: Lift 4 is not present into all Lifts cursor.",
                TestUtilities.isValidCursor(cursor, liftValues4));

        Assert.assertTrue("Error: Lift 5 is not present into all Lifts cursor.",
                TestUtilities.isValidCursor(cursor, liftValues5));

        cursor.close();

        // Query Lift by Shift
        Uri liftByShiftUri = LiftContract.LiftEntry.buildLiftByShiftUri(shiftId1);

        cursor = appContext.getContentResolver().query(liftByShiftUri,
                null, null, null, null);

        Assert.assertTrue("Error: Lift 1 is not present into cursor for Shift 1.",
                TestUtilities.isValidCursor(cursor, liftValues1));

        Assert.assertTrue("Error: Lift 2 is not present into cursor for Shift 1.",
                TestUtilities.isValidCursor(cursor, liftValues2));

        Assert.assertEquals("Error: Unexpected number of records returned for Lift by Shift 1 query.",
                2, cursor.getCount());

        /***************
         * Update Lift *
         **************/
        // Register a observer for the cursor to make sure that content provider is notifying the
        // observers as expected
        tco = TestContentObserver.getTestContentObserver();
        cursor.registerContentObserver(tco);

        //Update
        ContentValues updateLift1Values = new ContentValues(liftValues1);
        updateLift1Values.put(LiftContract.LiftEntry.COLUMN_PRICE, "10.5");

        int numAffectedRows = appContext.getContentResolver().update(LiftContract.LiftEntry.CONTENT_URI,
                updateLift1Values,
                LiftContract.LiftEntry._ID + " = ?",
                new String[]{Long.toString(liftId1)});

        // Check the amount of records updated
        Assert.assertEquals("Error: Unexpected amount of records updated.",
                1, numAffectedRows);

        // Check if the ContentObserver has been called.
        tco.waitForNotificationOrFail();

        cursor.unregisterContentObserver(tco);
        cursor.close();

        // Check if the update is reflected into Cursor
        cursor = appContext.getContentResolver().query(LiftContract.LiftEntry.CONTENT_URI,
                null, null, null, null);

        Assert.assertTrue("Error: The update record is not reflected into the cursor.",
                TestUtilities.isValidCursor(cursor, updateLift1Values));

        cursor.close();

        /***************
         * Delete Lift *
         **************/
        // Register a observer for to make sure that content provider is notifying the
        // observers as expected
        tco = TestContentObserver.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LiftContract.LiftEntry.CONTENT_URI,
                true,
                tco);

        numAffectedRows = appContext.getContentResolver().delete(LiftContract.LiftEntry.CONTENT_URI,
                LiftContract.LiftEntry._ID + " = ?",
                new String[]{Long.toString(liftId1)});

        Assert.assertEquals("Error: Unexpected amount of records has been deleted.",
                1,
                numAffectedRows);

        // Check if the ContentObserver has been called.
        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        // Check if the deleted record still present into db
        cursor = appContext.getContentResolver().query(LiftContract.LiftEntry.CONTENT_URI,
                null, null, null, null);

        Assert.assertFalse("Error: Lift 1 still into db after deletion.",
                TestUtilities.isValidCursor(cursor, updateLift1Values));

        cursor.close();
    }

    @Test
    public void curdLiftAddress(){
        /**************************
         * Insert FK Dependencies *
         *************************/
        // Insert Shift
        ContentValues shiftValues = TestDb.getShiftValuesSample1();

        Uri shiftUri = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shiftValues);

        long shiftId = ContentUris.parseId(shiftUri);

        Assert.assertTrue("Error: Fail to insert Shift. Unexpected id returend.",
                shiftId > 0);

        // Insert Lift 1
        ContentValues liftValues1 = TestDb.getLiftValuesSample1(shiftId);

        Uri liftUri1 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues1);

        long liftId1 = ContentUris.parseId(liftUri1);

        Assert.assertTrue("Error: Fail to insert Lift 1. Unexpected id returend.",
                liftId1 > 0);

        // Insert Lift 2
        ContentValues liftValues2 = TestDb.getLiftValuesSample2(shiftId);

        Uri liftUri2 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues2);

        long liftId2 = ContentUris.parseId(liftUri2);

        Assert.assertTrue("Error: Fail to insert Lift 2. Unexpected id returend.",
                liftId2 > 0);

        // Insert Address 1
        ContentValues addressValues1 = TestDb.getAddressValuesSample1();

        Uri addressUri1 = appContext.getContentResolver().insert(LiftContract.AddressEntry.CONTENT_URI,
                addressValues1);

        long addressId1 = ContentUris.parseId(addressUri1);

        Assert.assertTrue("Error: Fail to insert Address 1. Unexpected id returned.",
                addressId1 > 0);

        // Insert Address 2
        ContentValues addressValues2 = TestDb.getAddressValuesSample2();

        Uri addressUri2 = appContext.getContentResolver().insert(LiftContract.AddressEntry.CONTENT_URI,
                addressValues2);

        long addressId2 = ContentUris.parseId(addressUri2);

        Assert.assertTrue("Error: Fail to insert Address 2. Unexpected id returned.",
                addressId2 > 0);

        // Insert Address 3
        ContentValues addressValues3 = TestDb.getAddressValuesSample3();

        Uri addressUri3 = appContext.getContentResolver().insert(LiftContract.AddressEntry.CONTENT_URI,
                addressValues3);

        long addressId3 = ContentUris.parseId(addressUri3);

        Assert.assertTrue("Error: Fail to insert Address 3. Unexpected id returned.",
                addressId3 > 0);

        // Insert Address 4
        ContentValues addressValues4 = TestDb.getAddressValuesSample4();

        Uri addressUri4 = appContext.getContentResolver().insert(LiftContract.AddressEntry.CONTENT_URI,
                addressValues4);

        long addressId4 = ContentUris.parseId(addressUri4);

        Assert.assertTrue("Error: Fail to insert Address 4. Unexpected id returned.",
                addressId4 > 0);

        /**********************
         * Create LiftAddress *
         *********************/
        // Insert LiftAddress 1
        // Register ContentObsever
        TestContentObserver tco = TestContentObserver.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LiftContract.LiftAddressEntry.CONTENT_URI,
                true,
                tco);

        // Insert LiftAddress
        // Spire of Dublin
        ContentValues liftAddressValues1 = TestDb.getLiftAddressValuesSample1(liftId1, addressId1);

        Uri liftAddressUri1 = appContext.getContentResolver().insert(LiftContract.LiftAddressEntry.CONTENT_URI,
                liftAddressValues1);

        // Check if ContentObserver has been called
        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        // Check if LiftAddress has been inserted
        long liftAddressId1 = ContentUris.parseId(liftAddressUri1);
        liftAddressValues1.put(LiftContract.LiftAddressEntry._ID, liftAddressId1);

        Assert.assertTrue("Error: Fail to insert LiftAddress1. Unexpected id returned.",
                liftAddressId1 > 0);

        // Insert LiftAddress 2
        // Opium Rooms
        ContentValues liftAddressValues2 = TestDb.getLiftAddressValuesSample2(liftId1, addressId2);

        Uri liftAddressUri2 = appContext.getContentResolver().insert(LiftContract.LiftAddressEntry.CONTENT_URI,
                liftAddressValues2);

        long liftAddressId2 = ContentUris.parseId(liftAddressUri2);
        liftAddressValues2.put(LiftContract.LiftAddressEntry._ID, liftAddressId2);

        Assert.assertTrue("Error: Fail to insert LiftAddress 2. Unexpected id returned.",
                liftAddressId2 > 0);

        // Insert LiftAddress 3
        // The George
        ContentValues liftAddressValues3 = TestDb.getLiftAddressValuesSample3(liftId2, addressId3);

        Uri liftAddressUri3 = appContext.getContentResolver().insert(LiftContract.LiftAddressEntry.CONTENT_URI,
                liftAddressValues3);

        long liftAddressId3 = ContentUris.parseId(liftAddressUri3);
        liftAddressValues3.put(LiftContract.LiftAddressEntry._ID, liftAddressId3);

        Assert.assertTrue("Error: Fail to insert LiftAddress 3. Unexpected id returned.",
                liftAddressId3 > 0);

        // Insert LiftAddress 4
        // Dicey's
        ContentValues liftAddressValues4 = TestDb.getLiftAddressValuesSample4(liftId2, addressId4);

        Uri liftAddressUri4 = appContext.getContentResolver().insert(LiftContract.LiftAddressEntry.CONTENT_URI,
                liftAddressValues4);

        long liftAddress4Id = ContentUris.parseId(liftAddressUri4);
        liftAddressValues4.put(LiftContract.LiftAddressEntry._ID, liftAddress4Id);
        liftAddress4Id = ContentUris.parseId(liftAddressUri4);

        Assert.assertTrue("Error: Fail to insert LiftAddress 4. Unexpected id returned.",
                liftAddress4Id > 0);

        /********************
         * Read LiftAddress *
         *******************/
        // Query All LiftAddress
        Cursor cursor = appContext.getContentResolver().query(LiftContract.LiftAddressEntry.CONTENT_URI,
                null, null, null, null);

        Assert.assertEquals("Error: Query fail. Unexpected amount of records returned."
                , 4, cursor.getCount());

        // Check if LiftAddress 2 is present in cursor.
        Assert.assertTrue("Error: It wasn't possible to find LiftAddress 2 in Cursor.",
                TestUtilities.isValidCursor(cursor, liftAddressValues2));

        cursor.close();

        // Query LiftAddress by Lift
        Uri liftAddressByLiftUri = LiftContract.LiftAddressEntry.buildLiftAddressByLiftUri(liftId2);

        cursor = appContext.getContentResolver().query(liftAddressByLiftUri,
                null, null, null, null);

        Assert.assertEquals("Error: Query LiftAddress by Lift has been failed." +
                " Unexpected amount of records returned.",
                2, cursor.getCount());

        Assert.assertTrue("Error: LiftAddress 3 is not present in Cursor LiftAddress by Lift.",
                TestUtilities.isValidCursor(cursor, liftAddressValues3));

        Assert.assertTrue("Error: LiftAddress 4 is not present in Cursor LiftAddress by Lift.",
                TestUtilities.isValidCursor(cursor, liftAddressValues4));

        cursor.close();

        // Query LiftAddress by Lift and Type
        Uri liftAddressByLiftTypeUri = LiftContract.LiftAddressEntry.buildLiftAddressByLiftTypeUri(liftId1,
                "HOP_ON");

        cursor = appContext.getContentResolver().query(liftAddressByLiftTypeUri,
                null, null, null, null);

        Assert.assertEquals("Error: Fail to query LiftAddress by Lift and Type." +
                " Unexpected amount of records returned.",
                1, cursor.getCount());

        Assert.assertTrue("Error: LiftAddress 1 is not present in Cursor LiftAddress by Lift and Type.",
                TestUtilities.isValidCursor(cursor, liftAddressValues1));

        cursor.close();

        /**********************
         * Update LiftAddress *
         *********************/
        // Update LiftAddress 1
        // Register a ContentObserver
        tco = TestContentObserver.getTestContentObserver();
        cursor.registerContentObserver(tco);

        // Update LiftAddress 1
        ContentValues updatedLiftAddress1Values = new ContentValues(liftAddressValues1);
        updatedLiftAddress1Values.put(LiftContract.LiftAddressEntry.COLUMN_POI, "Quase Spire");

        int numAffectedRows = appContext.getContentResolver().update(LiftContract.LiftAddressEntry.CONTENT_URI,
                updatedLiftAddress1Values,
                LiftContract.LiftAddressEntry.TABLE_NAME
                        + "." + LiftContract.LiftAddressEntry._ID + " = ?",
                new String[]{Long.toString(liftAddressId1)});

        // Check if the ContentObserver has been called
        tco.waitForNotificationOrFail();
        cursor.unregisterContentObserver(tco);
        cursor.close();

        // Check if the update has been made
        Assert.assertEquals("Error: Fail to update LiftAddress 1. Unexpected number of records affected.",
                1, numAffectedRows);

        cursor = appContext.getContentResolver().query(liftAddressByLiftTypeUri,
                null, null, null, null);

        Assert.assertTrue("Error: Fail to update LiftAddress 1. Changes are not reflected into db.",
                TestUtilities.isValidCursor(cursor, updatedLiftAddress1Values));

        /**********************
         * Delete LiftAddress *
         *********************/
        // Delete LiftAddress 1
        // Register a ContentObserver
        tco = TestContentObserver.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LiftContract.LiftAddressEntry.CONTENT_URI,
                true,
                tco);

        numAffectedRows = appContext.getContentResolver().delete(LiftContract.LiftAddressEntry.CONTENT_URI,
                LiftContract.LiftAddressEntry.TABLE_NAME
                        + "." + LiftContract.LiftAddressEntry._ID + " = ?",
                new String[]{Long.toString(liftAddressId1)});

        // Check if the ContentObserver has been called
        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);
        cursor.close();

        // Check if the record has been deleted
        Assert.assertEquals("Error: Fail to delete LiftAddress 1. Unexpected numAffectedRows returned.",
                1, numAffectedRows);
        
        cursor = appContext.getContentResolver().query(LiftContract.LiftAddressEntry.CONTENT_URI,
                null, null, null, null);

        Assert.assertEquals("Error: Fail to delete LiftAddress 1. Unexpected number of records present in cursor.",
                3, cursor.getCount());

        Assert.assertFalse("Error: Fail to delete LiftAddress 1. Record still present in db.",
                TestUtilities.isValidCursor(cursor, updatedLiftAddress1Values));

        cursor.close();

        //ToDo: Implement crudLiftAddress()
    }

    @Test
    public void crudShift(){
        /****************
         * Create Shift *
         ***************/
        // Insert Shift 1
        //Get ContentValues
        ContentValues shift1Values = TestDb.getShiftValuesSample1();

        //Register a ContentObserver
        TestContentObserver tco = TestContentObserver.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LiftContract.ShiftEntry.CONTENT_URI,
                true,
                tco);

        // Insert address
        Uri shift1Uri = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shift1Values);

        // Check if the ContentObserver was called
        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        // Check if insertion 1 has been made
        long shift1Id = ContentUris.parseId(shift1Uri);
        shift1Values.put(LiftContract.ShiftEntry._ID, shift1Id);

        Assert.assertTrue("Error: It wasn't possible to insert Shift 1. Invalid id returned.",
                shift1Id > 0);

        // Insert Shift 2
        ContentValues shift2Values = TestDb.getShiftValuesSample2();

        Uri shift2Uri = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shift2Values);

        // Check if insertion 2 has been made
        long shift2Id = ContentUris.parseId(shift2Uri);
        shift2Values.put(LiftContract.ShiftEntry._ID, shift2Id);

        Assert.assertTrue("Error: It wasn't possible to insert Shift 2. Invalid id returned.",
                shift2Id > 0);

        // Insert Shift 3
        ContentValues shift3Values = TestDb.getShiftValuesSample3();

        Uri shift3Uri = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shift3Values);

        // Check if insertion 3 has been made
        long shift3Id = ContentUris.parseId(shift3Uri);
        shift3Values.put(LiftContract.ShiftEntry._ID, shift3Id);

        Assert.assertTrue("Error: It wasn't possible to insert Shift 3. Invalid id returned.",
                shift3Id > 0);

        /**************
         * Read Shift *
         *************/
        // Query All Shifts
        Cursor cursor = appContext.getContentResolver().query(LiftContract.ShiftEntry.CONTENT_URI,
                null, null, null, null);

        // Check if the cursor contains the inserted records
        Assert.assertTrue("Error: Shift 1 is not present into the cursor.",
                TestUtilities.isValidCursor(cursor, shift1Values));

        Assert.assertTrue("Error: Shift 2 is not present into the cursor.",
                TestUtilities.isValidCursor(cursor, shift2Values));

        Assert.assertTrue("Error: Shift 3 is not present into the cursor.",
                TestUtilities.isValidCursor(cursor, shift3Values));

        cursor.close();

        // Query Shift by period
        Calendar startDate = new GregorianCalendar();
        startDate.add(Calendar.DAY_OF_YEAR, -5);

        Calendar endDate = new GregorianCalendar();
        endDate.add(Calendar.DAY_OF_YEAR, 1);

        Uri shiftByPeriodUri = LiftContract.ShiftEntry.buildShiftPeriodUri(startDate, endDate);

        cursor = appContext.getContentResolver().query(shiftByPeriodUri,
                null, null, null, null);

        Assert.assertEquals("Error: Unexpected number of Records returned from Shift by Period query.",
                2, cursor.getCount());

        Assert.assertTrue("Error: Shift 1 should be present into Shift by Period cursor.",
                TestUtilities.isValidCursor(cursor, shift1Values));

        Assert.assertTrue("Error: Shift 2 should be present into Shift by period cursor.",
                TestUtilities.isValidCursor(cursor, shift2Values));

        Assert.assertFalse("Error: Shift 3 shouldn't be present into Shift by period cursor.",
                TestUtilities.isValidCursor(cursor, shift3Values));

        /****************
         * Update Shift *
         ***************/
        // Register a ContentObserver for the cursor to make sure that the ContentProvider is
        // notifying the observers as expected.
        tco = TestContentObserver.getTestContentObserver();
        cursor.registerContentObserver(tco);

        // Get ContentValues
        ContentValues updatedShift1Values = new ContentValues(shift1Values);
        updatedShift1Values.put(LiftContract.ShiftEntry.COLUMN_END_DT, "1479878000000");

        // Update
        int numAffectedRows = appContext.getContentResolver().update(LiftContract.ShiftEntry.CONTENT_URI,
                updatedShift1Values,
                LiftContract.ShiftEntry._ID + " = ?",
                new String[]{Long.toString(shift1Id)}
                );

        // Check if the insertion has been made
        Assert.assertEquals("Error: Problems to update Shift 1", 1, numAffectedRows);

        // Check if the ContentObserver was notified
        tco.waitForNotificationOrFail();

        cursor.unregisterContentObserver(tco);

        cursor.close();

        // Check if the record is correctly updated
        cursor = appContext.getContentResolver().query(LiftContract.ShiftEntry.CONTENT_URI,
                null, null, null, null);

        Assert.assertTrue("Error: The updated values for Shift 1 are not reflected into db.",
                TestUtilities.isValidCursor(cursor, updatedShift1Values));

        /****************
         * Delete Shift *
         ***************/
        // Check if the record that will be deleted is present into the Cursor
        Assert.assertTrue("Error: Shift that would be deleted is not present into db.",
                TestUtilities.isValidCursor(cursor, shift2Values));

        // Register a ContentObserver
        tco = TestContentObserver.getTestContentObserver();
        appContext.getContentResolver().registerContentObserver(LiftContract.ShiftEntry.CONTENT_URI,
                true,
                tco);

        // Delete Shift 2
        numAffectedRows = appContext.getContentResolver().delete(LiftContract.ShiftEntry.CONTENT_URI,
                LiftContract.ShiftEntry._ID + " = ?",
                new String[]{Long.toString(shift2Id)});

        // Check if the ContentObserver has been called
        tco.waitForNotificationOrFail();
        appContext.getContentResolver().unregisterContentObserver(tco);

        // Check the amount of deleted records
        Assert.assertEquals("Error: Unexpected amount of Shift records has been deleted.",
                1, numAffectedRows);

        // Check if the cursor still containing the Shift 2
        cursor = appContext.getContentResolver().query(LiftContract.ShiftEntry.CONTENT_URI,
                null, null, null, null);

        Assert.assertFalse("Error: The Shift 2 record still present into db after deletion.",
                TestUtilities.isValidCursor(cursor, shift2Values));

        Assert.assertEquals("Error: Unexpected number of Shift present into db after deletion.",
                2, cursor.getCount());
    }

    @Test
    public void queryShiftSum(){
        /************************
         * Insert FK dependency *
         ***********************/
        // Insert Shift 1
        ContentValues shiftValues1 = TestDb.getShiftValuesSample1();

        Uri shiftUri1 = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shiftValues1);

        long shiftId1 = ContentUris.parseId(shiftUri1);
        Assert.assertTrue("Error: Invalid Id returned while trying to insert Shift 1",
                shiftId1 > 0);

        // Insert Shift 2
        ContentValues shiftValues2 = TestDb.getShiftValuesSample2();

        Uri shiftUri2 = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shiftValues2);

        long shiftId2 = ContentUris.parseId(shiftUri2);

        Assert.assertTrue("Error: Invalid Id returned while trying to insert Shift 2.",
                shiftId2 > 0);

        // Insert Shift 3
        ContentValues shiftValues3 = TestDb.getShiftValuesSample3();

        Uri shiftUri3 = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shiftValues3);

        long shiftId3 = ContentUris.parseId(shiftUri3);

        Assert.assertTrue("Error: Invalid Id returned while trying to insert Shift 3.",
                shiftId3 > 0);

        /****************
         * Insert Lifts *
         ***************/
        // Lift 1
        // Insert Lift
        ContentValues liftValues1 = TestDb.getLiftValuesSample1(shiftId1);
        Uri liftUri1 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues1);

        // Check if the Lift was inserted
        long liftId1 = ContentUris.parseId(liftUri1);
        liftValues1.put(LiftContract.LiftEntry._ID, liftId1);

        Assert.assertTrue("Error: Fail to inert Lift 1. Unexpected Id returned.", liftId1 > 0);

        // Lift 2
        ContentValues liftValues2 = TestDb.getLiftValuesSample2(shiftId1);
        Uri liftUri2 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues2);

        long liftId2 = ContentUris.parseId(liftUri2);
        liftValues2.put(LiftContract.LiftEntry._ID, liftId2);

        Assert.assertTrue("Error: Fail to insert Lift 2. Unexpected Id returned.", liftId2 > 0);

        // Lift 3
        ContentValues liftValues3 = TestDb.getLiftValuesSample3(shiftId2);
        Uri liftUri3 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues3);

        long liftId3 = ContentUris.parseId(liftUri3);
        liftValues3.put(LiftContract.LiftEntry._ID, liftId3);

        Assert.assertTrue("Error: Fail to insert Lift 3. Unexpected Id returned.", liftId3 > 0);

        // Lift 4
        ContentValues liftValues4 = TestDb.getLiftValuesSample4(shiftId2);
        Uri liftUri4 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues4);

        long liftId4 = ContentUris.parseId(liftUri4);
        liftValues4.put(LiftContract.LiftEntry._ID, liftId4);

        Assert.assertTrue("Error: Fail to insert Lift 4. Unexpected Id returned.", liftId4 > 0);

        // Lift 5
        ContentValues liftValues5 = TestDb.getLiftValuesSample5(shiftId3);
        Uri liftUri5 = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues5);

        long liftId5 = ContentUris.parseId(liftUri5);
        liftValues5.put(LiftContract.LiftEntry._ID, liftId5);

        Assert.assertTrue("Error: Fail to insert Lift 5. Unexpected Id returned.", liftId5 > 0);

        /*********
         * Query *
         ********/
        // Shift by Period Sum

        // Shift Sum
        Uri shiftSumUri = LiftContract.ShiftEntry.buildShiftSumUri(shiftId1);

        Cursor cursor = appContext.getContentResolver().query(shiftSumUri,
                null, null, null, null);

        int sumPriceIndex = cursor.getColumnIndex(LiftContract.ShiftEntry.FUNCTION_SUM_PRICE);
        int countLiftIndex = cursor.getColumnIndex(LiftContract.ShiftEntry.FUNCTION_COUNT_LIFT);

        cursor.moveToFirst();
        double cvSumPrice = liftValues1.getAsDouble(LiftContract.LiftEntry.COLUMN_PRICE)
                + liftValues2.getAsDouble(LiftContract.LiftEntry.COLUMN_PRICE);
        double returnedSumPrice = cursor.getDouble(sumPriceIndex);

        Assert.assertEquals("Error: Unexpected sum(price).", cvSumPrice, returnedSumPrice);

        int countLift = cursor.getInt(countLiftIndex);

        Assert.assertEquals("Error: Unexpected Lifts count().", 2, countLift);

        cursor.close();
    }

    /*******************
     * Private Methods *
     ******************/
    private void deleteAllRecordsFromProvider(){
        appContext.getContentResolver().delete(LiftContract.AddressEntry.CONTENT_URI, null, null);
        appContext.getContentResolver().delete(LiftContract.ExpenseEntry.CONTENT_URI, null, null);
        appContext.getContentResolver().delete(LiftContract.LiftEntry.CONTENT_URI, null, null);
        appContext.getContentResolver().delete(LiftContract.LiftAddressEntry.CONTENT_URI,
                null,
                null);
        appContext.getContentResolver().delete(LiftContract.ShiftEntry.CONTENT_URI, null, null);
    }

    private long insertAddress(ContentValues addressValues){
        Uri addressUri  = appContext.getContentResolver().insert(LiftContract.AddressEntry.CONTENT_URI,
                addressValues);

        long addressId = ContentUris.parseId(addressUri);
        Log.v(LOG_TAG, "Address Id: " + addressId);

        return addressId;
    }

    private long insertAddressSample1(){
        ContentValues addressValues = TestDb.getAddressValuesSample1();

        return insertAddress(addressValues);
    }

    private long insertLift(ContentValues liftValues){
        Uri liftUri = appContext.getContentResolver().insert(LiftContract.LiftEntry.CONTENT_URI,
                liftValues);

        long liftId = ContentUris.parseId(liftUri);
        Log.v(LOG_TAG, "Lift Id: " + liftId);

        return liftId;
    }

    /*
     * Insert a sample Lift where
     *  - Start Date: yesterday - 21:21
     *  - End Date: yesterday - 21:29
     *  - Price: 15.50
     *  - Number of Passengers: 3
     *  - Shift Id: @param
     */
    private long insertLiftSample1(long shiftId){
        ContentValues liftValues = TestDb.getLiftValuesSample1(shiftId);

        return this.insertLift(liftValues);
    }

    /*
     * Insert a sample Lift where
     *  - Start Date: today 1:30
     *  - End Date: today 1:43
     *  - Price: 12.20
     *  - Number of Passengers: 2
     *  - Shift Id: @param
     */
    private long insertLiftSample2(long shiftId){
        ContentValues liftValues = TestDb.getLiftValuesSample2(shiftId);

        return this.insertLift(liftValues);
    }

    /*
     * Insert a sample Lift where
     *  - Start Date: 2 days ago 23:12
     *  - End Date: 2 days ago 23:17
     *  - Price: 10.0
     *  - Number of Passengers: 1
     *  - Shift Id: @param
     */
    private long insertLiftSample3(long shiftId){
        ContentValues liftValues = TestDb.getLiftValuesSample3(shiftId);

        return this.insertLift(liftValues);
    }

    /*
     * Insert a sample Lift where
     *  - Start Date: 2 days ago 23:57
     *  - End Date: 1 days ago 00:07
     *  - Price: 20.00
     *  - Number of Passengers: 1
     *  - Shift Id: @param
     */
    private long insertLiftSample4(long shiftId){
        ContentValues liftValues = TestDb.getLiftValuesSample4(shiftId);

        return this.insertLift(liftValues);
    }

    /*
     * Insert a sample Lift where
     *  - Start Date: 2 weeks ago 22:21
     *  - End Date: 2 weeks ago 22:41
     *  - Price: 10.00
     *  - Number of Passengers: 2
     *  - Shift Id: @param
     */
    private long insertLiftSample5(long shiftId){
        ContentValues liftValues = TestDb.getLiftValuesSample5(shiftId);

        return this.insertLift(liftValues);
    }

    private long insertShift(ContentValues shiftValues){
        Uri shiftUri = appContext.getContentResolver().insert(LiftContract.ShiftEntry.CONTENT_URI,
                shiftValues);

        long shiftId = ContentUris.parseId(shiftUri);
        Log.v(LOG_TAG, "Shift Id: " + shiftId);

        return shiftId;
    }

    /*
     * Insert a sample Shift where
     *  - Start Date
     *      day - yesterday
     *      hour - 21
     *      min - 00
     *      sec - current
     *      millis = current
     *  - End Date
     *      day - today
     *      hour - 5
     *      min - current
     *      sec - current
     *      millis - current
     * Returns
     *  shiftId
     */
    private long insertShiftSample1(){
        ContentValues shiftValues = TestDb.getShiftValuesSample1();

        return this.insertShift(shiftValues);
    }

    /*
 * Insert a sample Shift where
 *  - Start Date
 *      day - 2 days ago
 *      hour - 21
 *      min - 30
 *      sec - current
 *      millis = current
 *  - End Date
 *      day - yesterday
 *      hour - 4
 *      min - 23
 *      sec - current
 *      millis - current
 * Returns
 *  shiftId
 */
    private long insertShiftSample2(){
        ContentValues shiftValues = TestDb.getShiftValuesSample2();

        return this.insertShift(shiftValues);
    }

    /*
 * Insert a sample Shift where
 *  - Start Date
 *      day - 2 weeks ago
 *      hour - 21
 *      min - 20
 *      sec - current
 *      millis = current
 *  - End Date
 *      day - 13 days ago
 *      hour - 5
 *      min - 11
 *      sec - current
 *      millis - current
 * Returns
 *  shiftId
 */
    private long insertShiftSample3(){
        ContentValues shiftValues = TestDb.getShiftValuesSample3();

        return this.insertShift(shiftValues);
    }
}
