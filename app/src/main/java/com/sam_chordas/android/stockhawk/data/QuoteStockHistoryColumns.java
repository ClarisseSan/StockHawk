package com.sam_chordas.android.stockhawk.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

/**
 * Created by isse on 28/08/2016.
 */
public class QuoteStockHistoryColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";


    @DataType(DataType.Type.TEXT) @NotNull
    public static final String SYMBOL = "symbol";


    @DataType(DataType.Type.TEXT) @NotNull
    public static final String BID_DATE = "bid_date";

    @DataType(DataType.Type.TEXT) @NotNull
    public static final String BID_PRICE = "price";

}
