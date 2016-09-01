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
import android.support.v4.util.Pools;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoryColumns;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
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

    private String mSymbol;
    private String mStartDAte;
    private int mDuration;

    private List<String> listBidPrice;
    private List<String> listDate;

    LineChart chart;
    private static final int CURSOR_LOADER_ID = 0;

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


        LineData data = new LineData(getXAxisValues(), getDataSet());
        chart.setData(data);
        chart.setDescription("YHOO Stocks");
        chart.animateXY(2000, 2000);

        //chart label color
        chart.getAxisLeft().setTextColor(Color.rgb(255, 255, 255));
        chart.getAxisRight().setTextColor(Color.rgb(255, 255, 255));
        chart.getXAxis().setTextColor(Color.rgb(255, 255, 255));
        chart.getLegend().setTextColor(Color.rgb(255, 255, 255));


        chart.invalidate();

        return view;
    }

    @Override
    public void onStart() {

        // initialize loader
        getActivity().getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

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

        switch (mDuration){
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
                    new String[]{HistoryColumns._ID,HistoryColumns.SYMBOL, HistoryColumns.BID_PRICE, HistoryColumns.BID_DATE},
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

        if (loader.getId() == CURSOR_LOADER_ID && cursor != null && cursor.moveToFirst()) {

            int columnHistoryId = cursor.getColumnIndex(HistoryColumns._ID);
            final String id = cursor.getString(columnHistoryId);

            int columnBidPriceIndex = cursor.getColumnIndex(HistoryColumns.BID_PRICE);
            final String price = cursor.getString(columnBidPriceIndex);
            //put price to bid list
            listBidPrice.add(price);

            int columnDateIndex = cursor.getColumnIndex(HistoryColumns.BID_DATE);
            final String bidDate = cursor.getString(columnDateIndex);
            //put price to bid list
            listBidPrice.add(bidDate);



        }


        for(String price:listBidPrice){
           System.out.println("PRICE ===========>" + price);
        }
        for(String bidDate:listDate){
            System.out.println("BID DATE ===========>" + bidDate);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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


    private ArrayList<LineDataSet> getDataSet() {
        ArrayList<LineDataSet> dataSets = null;

        ArrayList<Entry> valueSet1 = new ArrayList<>();
        Entry v1e1 = new Entry(110.000f, 0); // Jan
        valueSet1.add(v1e1);
        Entry v1e2 = new Entry(40.000f, 1); // Feb
        valueSet1.add(v1e2);
        Entry v1e3 = new Entry(60.000f, 2); // Mar
        valueSet1.add(v1e3);
        Entry v1e4 = new Entry(30.000f, 3); // Apr
        valueSet1.add(v1e4);
        Entry v1e5 = new Entry(90.000f, 4); // May
        valueSet1.add(v1e5);
        Entry v1e6 = new Entry(100.000f, 5); // Jun
        valueSet1.add(v1e6);

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
        xAxis.add("JAN");
        xAxis.add("FEB");
        xAxis.add("MAR");
        xAxis.add("APR");
        xAxis.add("MAY");
        xAxis.add("JUN");
        return xAxis;
    }
}
