package com.sam_chordas.android.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Just consider this class as the class which tells the ListView of appwidget
 * to take what type of data. By data meaning what RemoteViewsFactory.To make it more simple,
 * if you have done ListView population,this class defines the Adapter for the ListView.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        return (new WidgetListProvider(this.getApplicationContext(), intent));
    }



}
