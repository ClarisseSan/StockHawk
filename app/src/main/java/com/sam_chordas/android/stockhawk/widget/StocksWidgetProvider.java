/*
* Copyright 2016 Angela Sanchez

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
* */

package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.StockDetailActivity;
import com.sam_chordas.android.stockhawk.ui.StockListActivity;

/**
 * create MyWidgetProvider class to be a sub-class of AppWidgetProvider.
 * The AppWidgetProvider is a broadcast receiver class.
 * <p>
 * <p>
 * This is governing body of App Widget. Meaning everything of App Widget is controlled from here. By control means
 * <p>
 * Widget Update
 * Widget Delete
 * Widget enabled/disabled
 */
public class StocksWidgetProvider extends AppWidgetProvider {

    public static final String UPDATE_STOCKS_ACTION = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String UPDATE_STOCKS_ACTION_DATE = "android.intent.action.DATE_CHANGED";
    public static final String UPDATE_STOCKS_ACTION_DATABASE = "com.sam_chordas.android.stockhawk.DATABASE_CHANGED";



    private final String LOG_TAG = StocksWidgetProvider.class.getSimpleName();

    /*
    *onReceive() method is called when there is a broadcast intent to update the listview.
    * This intent will be sent from GCM service class.
    * When it receives the intent the onUpdate method will be called to refresh data in the listview.
     */


    @Override
    public void onReceive(Context context, Intent intent) {


        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        String action = intent.getAction();

        if (action.equals(UPDATE_STOCKS_ACTION) || action.equals(UPDATE_STOCKS_ACTION_DATE) || action.equals(UPDATE_STOCKS_ACTION_DATABASE)) {
            int appWidgetIds[] = manager.getAppWidgetIds(new ComponentName(context, StocksWidgetProvider.class));
            Log.e(LOG_TAG, "received :   " + intent.getAction());
            this.onUpdate(context,manager,appWidgetIds);
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

        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_stocks);


            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, StockListActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);


            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }

            boolean useDetailActivity = context.getResources()
                    .getBoolean(R.bool.use_detail_activity);
            Intent clickIntentTemplate = useDetailActivity
                    ? new Intent(context, StockDetailActivity.class)
                    : new Intent(context, StockListActivity.class);

            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.list_view, clickPendingIntentTemplate);
            views.setEmptyView(R.id.list_view, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.list_view,
                new Intent(context, WidgetRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.list_view,
                new Intent(context, WidgetRemoteViewsService.class));
    }


    /*
    *    Called the first time an instance of
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