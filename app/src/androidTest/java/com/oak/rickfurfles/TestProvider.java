package com.oak.rickfurfles;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
        //ToDo: Implement crudAddress()
    }

    @Test
    public void crudExpense(){
        //ToDo: Implement crudExpense()
    }

    @Test
    public void crudExpenseByShift(){
        //ToDo: Implement crudExpenseByShift()
    }

    @Test
    public void crudLift(){
        //ToDo: Implement crudLift()
    }

    @Test
    public void crudLiftByShift(){
        //ToDo: Implement crudLiftByShift()
    }

    @Test
    public void crudShift(){
        //ToDo: Implement crudShift()
    }

    @Test
    public void crudShiftByPeriod(){
        //ToDo: Implement crudShiftByPeriod()

    }

    /*******************
     * Private Methods *
     ******************/
    private void deleteAllRecordsFromProvider(){
        //ToDo: Implement deleteAllRecordsFromProvider()
    }

    private long insertAddress(ContentValues addressValues){
        Uri addressUri  = appContext.getContentResolver().insert(LiftContract.AddressEntry.CONTENT_URI,
                addressValues);

        long addressId = ContentUris.parseId(addressUri);
        Log.v(LOG_TAG, "Address Id: " + addressId);

        return addressId;
    }

    private long insertAddressSample(){
        ContentValues addressValues = TestDb.getAddressValuesSample();

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
