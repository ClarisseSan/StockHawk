package com.sam_chordas.android.stockhawk.detail_flow;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.ui.ChartFragment;

import java.util.ArrayList;

/**
 * A fragment representing a single Stock detail screen.
 * This fragment is either contained in a {@link StockListActivity}
 * in two-pane mode (on tablets) or a {@link StockDetailActivity}
 * on handsets.
 */
public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_SYMBOL = "symbol";
    private static final int CURSOR_LOADER_ID = 0;

    private String mSymbol = "";


    private TextView txt_symbol;
    private TextView txt_company;
    private TextView txt_price;
    private TextView txt_percent;
    private TextView txt_change_amt;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StockDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_SYMBOL)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mSymbol = getArguments().getString(ARG_SYMBOL);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.stock_detail, container, false);

        txt_symbol = (TextView) rootView.findViewById(R.id.txt_symbol);
        txt_symbol.setText(mSymbol);

        txt_company = (TextView) rootView.findViewById(R.id.txt_company);
        txt_price = (TextView) rootView.findViewById(R.id.txt_price);
        txt_percent = (TextView) rootView.findViewById(R.id.txt_percent);
        txt_change_amt = (TextView) rootView.findViewById(R.id.txt_change_amt);

        setupTabs(rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // initialize loader
        getActivity().getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
        super.onStart();

    }

    private void setupTabs(View rootView) {

        //for tab layout
        int numberOfTabs = 3;

        ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.view_pager);

        // Assigning ViewPager View and setting the adapter
        SamplePagerAdapter adapter = new SamplePagerAdapter(getFragmentManager(), numberOfTabs);
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        /*
        * In revision 4 of the Support Package, a method was added to ViewPager
        * which allows you to specify the number of offscreen pages to use, rather than the default which is 1.
        In your case, you want to specify 2, so that when you are on the third page, the first one is not destroyed, and vice-versa.
        * */
        mViewPager.setOffscreenPageLimit(2);

    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // Attach loader to our flavors database query
        // run when loader is initialized

        CursorLoader loader = null;

        if(id==CURSOR_LOADER_ID){

            loader =  new CursorLoader(getContext(), QuoteProvider.Quotes.CONTENT_URI,
                    new String[]{QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.NAME, QuoteColumns.BIDPRICE,
                            QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE
                    },
                    QuoteColumns.SYMBOL + " = \"" + mSymbol + "\"",
                    null,
                    QuoteColumns._ID + " DESC");


        }


        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Set the cursor in our CursorAdapter once the Cursor is loaded

        if (loader.getId() == CURSOR_LOADER_ID && cursor != null && cursor.moveToFirst()) {

            int columnCompanyIndex = cursor.getColumnIndex(QuoteColumns.NAME);
            final String company = cursor.getString(columnCompanyIndex);
            txt_company.setText(company);

            int columnPriceIndex = cursor.getColumnIndex(QuoteColumns.BIDPRICE);
            final String price = cursor.getString(columnPriceIndex);
            txt_price.setText(price);

            int columnPercentIndex = cursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE);
            final String percent = cursor.getString(columnPercentIndex);
            txt_percent.setText(percent);

            int columnChangeIndex = cursor.getColumnIndex(QuoteColumns.CHANGE);
            final String change = cursor.getString(columnChangeIndex);
            txt_change_amt.setText(change);
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }


    public class SamplePagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"1 week", "2 weeks", "1 month"};

        private final ArrayList<String> mTitles;

        public SamplePagerAdapter(FragmentManager fm, int numberOfTabs) {
            super(fm);
            mTitles = new ArrayList<>();
            for (int i = 0; i < numberOfTabs; i++) {
                mTitles.add(TITLES[i]);
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            //create bundle to pass movieId to the fragments
            // Bundle bundle = generateBundle();
            //Context context = MovieDetailActivity.this;

            if (position == 0) {
                fragment = new ChartFragment();
            }
            if (position == 1) {
                fragment = new ChartFragment();
            }
            if (position == 2) {
                fragment = new ChartFragment();
            }

            return fragment;
        }
    }
}
