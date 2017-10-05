package com.korsolution.kontin.teamusedcar;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Kontin58 on 29/6/2560.
 */

public class CarDetailsDBClass extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "productdb";

    // Table Name
    private static final String TABLE_PRODUCT = "product";

    private static final String TABLE_PRODUCT_PRICE = "PRODUCT_PRICE";
    private static final String TABLE_PRODUCT_PRODUCT = "PRODUCT_PRODUCT";
    private static final String TABLE_PRODUCT_IMAGE = "PRODUCT_IMAGE";
    private static final String TABLE_PRODUCT_INFO_TH = "PRODUCT_INFO_TH";
    private static final String TABLE_PRODUCT_INFO_EN = "PRODUCT_INFO_EN";
    private static final String TABLE_PRODUCT_PROMOTION = "PRODUCT_PROMOTION";
    private static final String TABLE_PRODUCT_FLASH_DEAL = "PRODUCT_FLASH_DEAL";

    public CarDetailsDBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PRODUCT +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " carId TEXT(100)," +
                " cover TEXT(100)," +
                " title TEXT(100)," +
                " year TEXT(100)," +
                " brand TEXT(100)," +
                " model TEXT(100)," +
                " sub_model TEXT(100)," +
                " km TEXT(100)," +
                " repair TEXT(100)," +
                " bid_price TEXT(100)," +
                " buy_price TEXT(100)," +
                " created TEXT(100)," +
                " status TEXT(100)," +
                " img_front TEXT(100)," +
                " img45_v1 TEXT(100)," +
                " img45_v2 TEXT(100)," +
                " img_back TEXT(100)," +
                " img_engine TEXT(100)," +
                " img_lever TEXT(100)," +
                " img_inner1 TEXT(100)," +
                " img_inner2 TEXT(100)," +
                " img_inner3 TEXT(100)," +
                " img_inner4 TEXT(100)," +
                " img_inner5 TEXT(100));");

        Log.d("CREATE TABLE","Create Table Successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
