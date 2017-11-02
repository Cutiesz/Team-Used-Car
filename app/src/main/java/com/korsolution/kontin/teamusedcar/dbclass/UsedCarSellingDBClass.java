package com.korsolution.kontin.teamusedcar.dbclass;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by Kontin58 on 1/11/2560.
 */

public class UsedCarSellingDBClass extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "usedcarsellingdb";

    // Table Name
    private static final String TABLE_USEDCAR_SELLING = "usedcar_selling";

    public UsedCarSellingDBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_USEDCAR_SELLING +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " Cover TEXT(100)," +
                " PKID TEXT(100)," +
                " Title TEXT(100)," +
                " Buy TEXT(100)," +
                " Bid TEXT(100)," +
                " CurrentBid TEXT(100)," +
                " Brand TEXT(100)," +
                " Model TEXT(100)," +
                " License TEXT(100)," +
                " Year TEXT(100)," +
                " Km TEXT(100)," +
                " EndDate TEXT(100)," +
                " Seller TEXT(100)," +

                " img_inner5 TEXT(100));");

        Log.d("CREATE TABLE","Create Table Successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Insert Data
    public long Insert(String strCover, String strPKID, String strTitle, String strBuy, String strBid,
                       String strCurrentBid, String strBrand, String strModel, String strLicense,
                       String strYear, String strKm, String strEndDate, String strSeller) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            SQLiteStatement insertCmd;
            String strSQL = "INSERT INTO " + TABLE_USEDCAR_SELLING + "(Cover, PKID, Title, Buy, Bid, CurrentBid, Brand, Model, License, Year, Km, EndDate, Seller) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strCover);
            insertCmd.bindString(2, strPKID);
            insertCmd.bindString(3, strTitle);
            insertCmd.bindString(4, strBuy);
            insertCmd.bindString(5, strBid);
            insertCmd.bindString(6, strCurrentBid);
            insertCmd.bindString(7, strBrand);
            insertCmd.bindString(8, strModel);
            insertCmd.bindString(9, strLicense);
            insertCmd.bindString(10, strYear);
            insertCmd.bindString(11, strKm);
            insertCmd.bindString(12, strEndDate);
            insertCmd.bindString(13, strSeller);
            return insertCmd.executeInsert();

        } catch (Exception e) {
            return -1;
        }
    }

    // Select All Data Array 2 dimention
    public String[][] SelectUsedCar(String carID) {
        // TODO Auto-generated method stub

        try {
            String arrData[][] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT  * FROM " + TABLE_USEDCAR_SELLING + " Where PKID = '" + carID + "'";
            Cursor cursor = db.rawQuery(strSQL, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getCount()][cursor.getColumnCount()];
                    /***
                     *  [x][0] = MemberID
                     *  [x][1] = Name
                     *  [x][2] = Tel
                     */
                    int i= 0;
                    do {
                        arrData[i][0] = cursor.getString(0);
                        arrData[i][1] = cursor.getString(1);
                        arrData[i][2] = cursor.getString(2);
                        arrData[i][3] = cursor.getString(3);
                        arrData[i][4] = cursor.getString(4);
                        arrData[i][5] = cursor.getString(5);
                        arrData[i][6] = cursor.getString(6);
                        arrData[i][7] = cursor.getString(7);
                        arrData[i][8] = cursor.getString(8);
                        arrData[i][9] = cursor.getString(9);
                        arrData[i][10] = cursor.getString(10);
                        arrData[i][11] = cursor.getString(11);
                        arrData[i][12] = cursor.getString(12);
                        arrData[i][13] = cursor.getString(13);
                        i++;
                    } while (cursor.moveToNext());

                }
            }
            cursor.close();

            return arrData;

        } catch (Exception e) {
            return null;
        }

    }

    // Select All Data Array 2 dimention
    public String[][] SelectAll() {
        // TODO Auto-generated method stub

        try {
            String arrData[][] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT  * FROM " + TABLE_USEDCAR_SELLING/* + " Where BlockId = '" + strBlockId + "'"*/;
            Cursor cursor = db.rawQuery(strSQL, null);

            if(cursor != null)
            {
                if (cursor.moveToFirst()) {
                    arrData = new String[cursor.getCount()][cursor.getColumnCount()];
                    /***
                     *  [x][0] = MemberID
                     *  [x][1] = Name
                     *  [x][2] = Tel
                     */
                    int i= 0;
                    do {
                        arrData[i][0] = cursor.getString(0);
                        arrData[i][1] = cursor.getString(1);
                        arrData[i][2] = cursor.getString(2);
                        arrData[i][3] = cursor.getString(3);
                        arrData[i][4] = cursor.getString(4);
                        arrData[i][5] = cursor.getString(5);
                        arrData[i][6] = cursor.getString(6);
                        arrData[i][7] = cursor.getString(7);
                        arrData[i][8] = cursor.getString(8);
                        arrData[i][9] = cursor.getString(9);
                        arrData[i][10] = cursor.getString(10);
                        arrData[i][11] = cursor.getString(11);
                        arrData[i][12] = cursor.getString(12);
                        arrData[i][13] = cursor.getString(13);
                        i++;
                    } while (cursor.moveToNext());

                }
            }
            cursor.close();

            return arrData;

        } catch (Exception e) {
            return null;
        }

    }

    // Delete Data
    public long Delete(/*String strMemberID*/) {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            // for API 11 and above
            SQLiteStatement insertCmd;
            String strSQL = "DELETE FROM " + TABLE_USEDCAR_SELLING/* + " WHERE MemberID = ? "*/;

            insertCmd = db.compileStatement(strSQL);
            /*insertCmd.bindString(1, strMemberID);*/

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }

    }
}
