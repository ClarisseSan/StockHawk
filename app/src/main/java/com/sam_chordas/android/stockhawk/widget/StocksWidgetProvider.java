package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.sam_chordas.android.stockhawk.R;
/**
 * create MyWidgetProvider class to be a sub-class of AppWidgetProvider.
 * The AppWidgetProvider is a broadcast receiver class.
 * onReceive() method is called when there is a broadcast intent to update the listview.
 * This intent will be sent from GCM service class.
 * When it receives the intent the onUpdate method will be called to refresh data in the listview.
 */
public class StocksWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_STOCKS_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

    public static final String EXTRA_ITEM = "com.example.edockh.EXTRA_ITEM";

    private final String LOG_TAG = StocksWidgetProvider.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {


        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        if(intent.getAction().equals(UPDATE_STOCKS_ACTION)){
            int appWidgetIds[] = manager.getAppWidgetIds(new ComponentName(context,StocksWidgetProvider.class));
        Log.e(LOG_TAG, "received :   " + intent.getAction());
            manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);
        }

        super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //update each app widgets with the remote adapter
        for (int i=0; i<appWidgetIds.length; i++){


        }
    }


}