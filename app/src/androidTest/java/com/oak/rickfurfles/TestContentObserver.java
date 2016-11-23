package com.oak.rickfurfles;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by Alex on 11/11/2016.
 *
 * The functions provided inside of TestProvider class use this utility class to test the
 * ContentObserver callbacks using the Polling Check class grabbed from Android CTS tests.
 *
 * Note that this only tests if onChange function is called; it doesn't tests if the correct
 * Uris are returned.
 *
 */

public class TestContentObserver extends ContentObserver {

    final HandlerThread ht;
    boolean contentChangedFlag;

    private TestContentObserver(HandlerThread ht){
        super(new Handler(ht.getLooper()));

        this.ht = ht;
    }

    protected static TestContentObserver getTestContentObserver(){
        HandlerThread ht = new HandlerThread("ContentObserverThread");
        ht.start();

        return new TestContentObserver(ht);
    }

    // On earlier versions of Android, this onChange method was called
    @Override
    public void onChange(boolean selfChange){
        onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri){
        contentChangedFlag = true;
    }

    public void waitForNotificationOrFail(){
        // Note: This PollingCheck class was taken from the Android CTS (Compatibility Test Suite)
        // It's useful to look at Android CTS source for ideas on how to test your Android
        // applications. The reason why PollingCheck works is that, by default, the JUnit
        // testing framework is not running on the main Android application thread.

    }
}
