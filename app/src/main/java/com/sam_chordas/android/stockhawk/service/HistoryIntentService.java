package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;

/**
 * Created by anant on 2016-04-11.
 */
public class HistoryIntentService extends IntentService {


    public HistoryIntentService(){
        super(HistoryIntentService.class.getSimpleName());

    }


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
         * @param //name Used to name the worker thread, important only for debugging.
     */


    public HistoryIntentService(String name) {
        super(name);
    }

    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        HistoryTaskService historyTaskService = new HistoryTaskService(this);
        Bundle serviceBundle = new Bundle();


        serviceBundle.putString("Symbolshortname", intent.getStringExtra("symboltag"));

        Log.e("stock value in intent" ,intent.getStringExtra("symboltag") .toString());

        historyTaskService.onRunTask(new TaskParams("Symbolshortname", serviceBundle));


    }
}
