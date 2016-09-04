package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by isse on 01/09/2016.
 */
public class StockHistoryService extends GcmTaskService {

    private String LOG_TAG = StockHistoryService.class.getSimpleName();

    private OkHttpClient client = new OkHttpClient();
    private Context mContext;
    private StringBuilder mStoredSymbols = new StringBuilder();
    private boolean isUpdate;

    private String mSymbol;
    private String mStartDate;
    private String mEndDate;

    public StockHistoryService(){}

    public StockHistoryService(Context mContext) {
        this.mContext = mContext;
    }

    String fetchData(String url) throws IOException{

        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        return response.body().string();
    }





    @Override
    public int onRunTask(TaskParams taskParams) {

       Cursor initQueryCursor;

        if(mContext==null) {
            mContext = this;
        }

        mSymbol = taskParams.getExtras().getString("symbol");
        mStartDate = taskParams.getExtras().getString("startDate");
        mEndDate = taskParams.getExtras().getString("endDate");


        final String baseUrl = "https://query.yahooapis.com/v1/public/yql?q=";

        String query = "select * from yahoo.finance.historicaldata where symbol=\"" +
                mSymbol +
                "\" and startDate=\"" + mStartDate + "\" and endDate=\"" + mEndDate + "\"";

        final String returnFormat = "&format=json&diagnostics=true&env=store://datatables.org/alltableswithkeys&callback=";


        StringBuilder urlStringBuilder = new StringBuilder();

        try{
            urlStringBuilder.append(baseUrl);
            urlStringBuilder.append(URLEncoder.encode(query, "UTF-8"));
            urlStringBuilder.append(returnFormat);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String urlString;
        String getResponse;
        int result = GcmNetworkManager.RESULT_FAILURE;


        if(urlStringBuilder!=null){
            urlString = urlStringBuilder.toString();
            Log.e(LOG_TAG,"MY URL =============>" + urlString);

            try{
                getResponse = fetchData(urlString);
                result = GcmNetworkManager.RESULT_SUCCESS;

                mContext.getContentResolver().applyBatch(QuoteProvider.AUTHORITY, Utils.historyJsonToContentVals(mContext,getResponse));


            } catch (IOException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Error applying batch insert", e);
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }


        }

        return result;
    }
}
