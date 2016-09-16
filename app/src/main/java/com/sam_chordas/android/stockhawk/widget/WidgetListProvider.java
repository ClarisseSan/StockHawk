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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;

/**
 * If you are familiar with Adapter of ListView,this is the same as adapter
 * with few changes
 * <p>
 * <p>
 * tutorial: https://laaptu.wordpress.com/2013/07/19/android-app-widget-with-listview/
 */

public class WidgetListProvider implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext = null;
    private int appWidgetId;
    private Cursor data = null;

    private static final String[] QUOTE_COLUMNS = {
            QuoteColumns._ID,
            QuoteColumns.SYMBOL,
            QuoteColumns.BIDPRICE,
            QuoteColumns.CHANGE,
            QuoteColumns.PERCENT_CHANGE,
            QuoteColumns.ISUP
    };


    // these indices must match the projection
    static final int INDEX_STOCK_ID = 0;
    static final int INDEX_STOCK_SYMBOL = 1;
    static final int INDEX_STOCK_BIDPRICE = 2;
    static final int INDEX_STOCK_CHANGE = 3;
    static final int INDEX_STOCK_PERCENT_CHANGE = 4;
    static final int INDEX_STOCK_ISUP = 5;

    public WidgetListProvider(Context context, Intent intent) {
        this.mContext = context;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }


    @Override
    public void onCreate() {
        //nothing to do

    }

    @Override
    public void onDataSetChanged() {
        if (data != null) {
            data.close();
        }
        // This method is called by the app hosting the widget (e.g., the launcher)
        // However, our ContentProvider is not exported so it doesn't have access to the
        // data. Therefore we need to clear (and finally restore) the calling identity so
        // that calls use our process and permission
        final long identityToken = Binder.clearCallingIdentity();
        data = mContext.getContentResolver().query(
                QuoteProvider.Quotes.CONTENT_URI,
                QUOTE_COLUMNS,
                QuoteColumns.ISCURRENT + " = ?",
                new String[]{"1"},
                null);
        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    /*
    *Similar to getView of Adapter where instead of View
    *we return RemoteViews
    */

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION ||
                data == null || !data.moveToPosition(position)) {
            return null;
        }

        final RemoteViews views = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);


        int quoteID = data.getInt(INDEX_STOCK_ID);
        String quoteSymbol = data.getString(INDEX_STOCK_SYMBOL);
        String quoteBidPrice = data.getString(INDEX_STOCK_BIDPRICE);
        String quoteChange = data.getString(INDEX_STOCK_CHANGE);
        String quotePercent = data.getString(INDEX_STOCK_PERCENT_CHANGE);
        //int quoteIsUp = data.getInt(INDEX_STOCK_ISUP);


        views.setTextViewText(R.id.stock_symbol, quoteSymbol);
        views.setTextViewText(R.id.bid_price, quoteBidPrice);
        views.setTextViewText(R.id.change, quoteChange);
        views.setTextViewText(R.id.percent_change, quotePercent);

        final Intent fillInIntent = new Intent();
        Uri quoteUri = QuoteProvider.Quotes.withSymbol(quoteSymbol);
        fillInIntent.setData(quoteUri);
        views.setOnClickFillInIntent(R.id.widget_item, fillInIntent);


        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_list_item);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (data.moveToPosition(position))
            return data.getLong(INDEX_STOCK_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
