package com.oak.rickfurfles;

import android.content.ContentValues;
import android.database.Cursor;

import com.oak.rickfurfles.model.db.LiftContract;

import junit.framework.Assert;

import java.util.Map;
import java.util.Set;

/**
 * Created by Alex on 27/10/2016.
 */

public class TestUtilities {

    /*
     * It iterates through the Cursor and try to find a record with the same Id (As a pattern, all
     * tables in this project have a Id column)
     * After find a cursor record with the same Id, try to match all values for all columns, with
     * the record passed as parameter.
     */
    public static boolean isValidCursor(Cursor cursor, ContentValues expectedValues){
        Assert.assertTrue("Empty cursor returned", cursor.moveToFirst());

        // Get the expected Id
        long expectedId = expectedValues.getAsLong(LiftContract.ShiftEntry._ID);

        // Try to find a record in the cursor with the same Id
        int columnIdIndex = cursor.getColumnIndex(LiftContract.ShiftEntry._ID);
        long cursorId = -1;

        do{
            cursorId = cursor.getLong(columnIdIndex);

            // Id founded
            if(cursorId == expectedId){
                // Check if all fields from the ContentValues match with the cursor record
                // Take the expectedValues Content Values and transform it in a Set of Map.Entry
                // In this way, every element is a pair of column name and column value
                Set<Map.Entry<String, Object>> expectedSet = expectedValues.valueSet();

                //Iterate the Expected Set to find the columns in the Cursor
                String expectedColumnName;
                String expectedValue;
                String cursorValue;
                double cursorDoubleValue;
                for(Map.Entry<String, Object> expectedEntry : expectedSet){
                    expectedColumnName = expectedEntry.getKey();

                    int cursorColumnIndex = cursor.getColumnIndex(expectedColumnName);
                    Assert.assertFalse("Expected column '" + expectedColumnName + "' wasn't found in the cursor",
                            cursorColumnIndex == -1);

                    expectedValue = expectedEntry.getValue().toString();

                    int columnType = cursor.getType(cursorColumnIndex);
                    switch (columnType) {
                        case Cursor.FIELD_TYPE_FLOAT:
                            cursorDoubleValue = cursor.getDouble(cursorColumnIndex);
                            cursorValue = Double.toString(cursorDoubleValue);

                            break;
                        default:
                            cursorValue = cursor.getString(cursorColumnIndex);

                            break;
                    }


                    //ToDo: Test if when the error msg is fired the process stops or keep testing
                    //      If it stops, the return for this method must be relocated.
                    Assert.assertEquals("Cursor value '" + cursorValue
                            + "' didn't match the expected value '" + expectedValue + "'.",
                            expectedValue,
                            cursorValue);

                }

                //ToDo: Test if when the error msg is fired the process stops or keep testing
                //      If the Assert error stops the process, this return will be reached only in
                //      case of success.
                return true;
            }

        }while(cursor.moveToNext());

        return false;
    }
}
