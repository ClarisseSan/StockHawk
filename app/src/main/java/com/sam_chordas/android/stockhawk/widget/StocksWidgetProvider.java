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
 *
 *
 * This is governing body of App Widget. Meaning everything of App Widget is controlled from here. By control means

 Widget Update
 Widget Delete
 Widget enabled/disabled
 *
 *
 */
public class StocksWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_STOCKS_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";

    public static final String EXTRA_ITEM = "com.example.edockh.EXTRA_ITEM";

    private final String LOG_TAG = StocksWidgetProvider.class.getSimpleName();

    /*
    *onReceive() method is called when there is a broadcast intent to update the listview.
    * This intent will be sent from GCM service class.
    * When it receives the intent the onUpdate method will be called to refresh data in the listview.
     */


    @Override
    public void onReceive(Context context, Intent intent) {


        AppWidgetManager manager = AppWidgetManager.getInstance(context);

        if (intent.getAction().equals(UPDATE_STOCKS_ACTION)) {
            int appWidgetIds[] = manager.getAppWidgetIds(new ComponentName(context, StocksWidgetProvider.class));
            Log.e(LOG_TAG, "received :   " + intent.getAction());
            manager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view);
        }

        super.onReceive(context, intent);

    }

    /*
    * Called for every update of the widget. Contains the ids of appWidgetIds for which an update is needed.
     * Note that this may be all of the AppWidget instances for this provider, or just a subset of them,
     * as stated in the method’s JavaDoc. For example, if more than one widget is added to the home screen,
      * only the last one changes (until reinstall).
    * */

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        //update each app widgets with the remote adapter
        for (int i = 0; i < appWidgetIds.length; i++) {


        }
    }



    /*
    *
    Called the first time an instance of
    your widget is added to the home screen.
    * */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }


    /*
    * Called once the last instance of your widget is removed from the home screen
    * */
    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }


    /*
    * Widget instance is removed from the home screen.
    * */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
}