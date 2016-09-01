package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.detail_flow.StockListActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by isse on 01/09/2016.
 */
public class HistoryIntentService extends IntentService {
    public HistoryIntentService(String name) {
        super(name);
    }
    public HistoryIntentService(){
        super(StockIntentService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(HistoryIntentService.class.getSimpleName(), "History Intent Service");
        Bundle args = new Bundle();

        //get symbol from StockListActivity and put in bundle
        args.putString("symbol", intent.getStringExtra(StockListActivity.SYMBOL));


        // Load historic stock data
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDate = new Date();

        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(currentDate);
        calEnd.add(Calendar.DATE, 0);

        Calendar calStart = Calendar.getInstance();
        calStart.setTime(currentDate);
        calStart.add(Calendar.MONTH, -1);

        String startDate = dateFormat.format(calStart.getTime());
        String endDate = dateFormat.format(calEnd.getTime());

        //put startDate and endDate in bundle
        args.putString("startDate", startDate);
        args.putString("endDate", endDate);


        // We can call OnRunTask from the intent service to force it to run immediately instead of
        // scheduling a task.
       StockHistoryService stockTaskService = new StockHistoryService(this);
       stockTaskService.onRunTask(new TaskParams("args", args));

    }
}
