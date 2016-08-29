package com.sam_chordas.android.stockhawk.widget;

import android.app.LauncherActivity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;

import java.util.ArrayList;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 *
 *
 * tutorial: https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/
 */

public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
     private ArrayList listItemList = new ArrayList();
     private Context context = null;
     private int appWidgetId;

     public WidgetListProvider(Context context, Intent intent) {
         this.context = context;
         appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                 AppWidgetManager.INVALID_APPWIDGET_ID);


     }



    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return listItemList.size()!=0 ? listItemList.size():0;
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    *
    */

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(
                context.getPackageName(), R.layout.list_item_quote);
        LauncherActivity.ListItem listItem = (LauncherActivity.ListItem) listItemList.get(position);
        //remoteView.setTextViewText(R.id.stock_symbol, listItem.heading);
        //remoteView.setTextViewText(R.id.change, listItem.content);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
