package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ValueFormatter;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoryColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChartFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_START_DATE = "startDate";
    private static final String ARG_SYMBOL = "symbol";
    private static final String ARG_DURATION = "duration";
    private static final int CURSOR_LOADER_ID = 0;
    LineChart chart;
    private String mSymbol;
    private String mStartDAte;
    private int mDuration;
    private List<String> listBidPrice;
    private List<String> listDate;
    private OnFragmentInteractionListener mListener;

    public ChartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static ChartFragment newInstance(String symbol, String startDate, String duration) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_SYMBOL, symbol);
        args.putString(ARG_START_DATE, startDate);
        args.putString(ARG_DURATION, duration);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSymbol = getArguments().getString(ARG_SYMBOL);
            mStartDAte = getArguments().getString(ARG_START_DATE);
            mDuration = getArguments().getInt(ARG_DURATION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chart, container, false);

        chart = (LineChart) view.findViewById(R.id.chart);

        chart.setDescription(mSymbol + getString(R.string.stock));
        chart.animateXY(2000, 2000);

        //chart label color
        chart.getAxisLeft().setTextColor(Color.rgb(255, 255, 255));
        chart.getAxisRight().setTextColor(Color.rgb(255, 255, 255));
        chart.getXAxis().setTextColor(Color.rgb(255, 255, 255));
        chart.getLegend().setTextColor(Color.rgb(255, 255, 255));

        //refresh chart
        chart.invalidate();

        return view;
    }

    @Override
    public void onStart() {

        // initialize loader
        getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onStart();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        String sortOrder = null;

        switch (mDuration) {
            case 0:
                //1 week
                sortOrder = HistoryColumns._ID + " ASC LIMIT 5";
                break;
            case 1:
                //2 weeks
                sortOrder = HistoryColumns._ID + " ASC LIMIT 10";
                break;
            case 2:
                //1 month
                sortOrder = HistoryColumns._ID + " ASC LIMIT 22";
                break;

        }

        if (id == CURSOR_LOADER_ID) {

            loader = new CursorLoader(getContext(),
                    QuoteProvider.QuotesHistory.CONTENT_URI,
                    new String[]{HistoryColumns._ID, HistoryColumns.SYMBOL, HistoryColumns.BID_PRICE, HistoryColumns.BID_DATE},
                    HistoryColumns.SYMBOL + " = ?",
                    new String[]{mSymbol},
                    sortOrder);
        }

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        listBidPrice = new ArrayList<>();
        listDate = new ArrayList<>();
        // Set the cursor in our CursorAdapter once the Cursor is loaded

        if (cursor != null && cursor.moveToFirst()) {

            Log.e("CURSOR SIZE=========>", String.valueOf(cursor.getCount()));

            int columnHistoryId = cursor.getColumnIndex(HistoryColumns._ID);
            int columnBidPriceIndex = cursor.getColumnIndex(HistoryColumns.BID_PRICE);
            int columnDateIndex = cursor.getColumnIndex(HistoryColumns.BID_DATE);

            String id = cursor.getString(columnHistoryId);

            String price = cursor.getString(columnBidPriceIndex);
            //put price to bid list
            listBidPrice.add(price);

            String bidDate = cursor.getString(columnDateIndex);
            //put price to bid list
            listDate.add(bidDate);

            while (cursor.moveToNext()) {
                id = cursor.getString(columnHistoryId);

                price = cursor.getString(columnBidPriceIndex);
                //put price to bid list
                listBidPrice.add(price);

                bidDate = cursor.getString(columnDateIndex);
                //put price to bid list
                listDate.add(bidDate);
            }

            //set data to chart
            LineData data = new LineData(getXAxisValues(), getDataSet());
            chart.setData(data);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            chart.notifyDataSetChanged();

            // format values
            data.setValueFormatter(new MyValueFormatter());


            //refresh chart
            chart.invalidate();
        }


        for (String price : listBidPrice) {
            System.out.println("PRICE ===========>" + price);
        }
        for (String bidDate : listDate) {
            System.out.println("BID DATE ===========>" + bidDate);
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private ArrayList<LineDataSet> getDataSet() {
        ArrayList<LineDataSet> dataSets = null;

        ArrayList<Entry> valueSet1 = new ArrayList<>();

        Collections.reverse(listBidPrice);
        for (int i = 0; i < listBidPrice.size(); i++) {
            float bidPrice = Float.valueOf(listBidPrice.get(i));
            Entry e = new Entry(bidPrice, i);
            valueSet1.add(e);

        }



        /* Dataset --> The set of data you have to draw in your chart */

        LineDataSet lineDataSet1 = new LineDataSet(valueSet1, getString(R.string.price));
        lineDataSet1.setColor(Color.rgb(0, 155, 0));
        //lineDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
        lineDataSet1.setDrawFilled(true);

        dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        return dataSets;
    }

    private ArrayList<String> getXAxisValues() {
        ArrayList<String> xAxis = new ArrayList<>();

        Collections.reverse(listDate);
        for (String bidDate : listDate
                ) {
            xAxis.add(bidDate);

        }
        return xAxis;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

/*
*
* The IValueFormatter interface can be used to create custom-made formatter classes that allow
 * to format values within the chart (from DataSets) in a specific way before drawing them.
 */

    public class MyValueFormatter implements ValueFormatter {
        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("###,###,##0.00"); // use two decimal
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value);
        }
    }
}
