package com.korsolution.kontin.teamusedcar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by Kontin58 on 14/3/2560.
 */

public class BuyHistoryDBClass extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "buyhistorydb";

    // Table Name
    private static final String TABLE_BUY_HISTORY = "buyhistory";

    public BuyHistoryDBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + TABLE_BUY_HISTORY +
                "(ID INTEGER PRIMARY KEY  NOT NULL  UNIQUE," +
                " Cover VARCHAR," +
                " PKID VARCHAR," +
                " TransactionID VARCHAR," +
                " Title VARCHAR," +
                " Price VARCHAR," +
                " Brand VARCHAR," +
                " Model VARCHAR," +
                " SubModel VARCHAR," +
                " Status VARCHAR," +
                " Type VARCHAR," +
                " CreateBy VARCHAR," +
                " EndDate VARCHAR," +
                " CreateDate VARCHAR);");

        Log.d("CREATE TABLE","Create Table Successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Insert Data
    public long Insert(String Cover, String PKID, String Transaction, String Title, String Price,
                       String Brand, String Model, String SubModel, String Status, String Type,
                       String By, String EndDate, String Created) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            SQLiteStatement insertCmd;
            String strSQL = "INSERT INTO " + TABLE_BUY_HISTORY
                    + "(Cover, PKID, TransactionID, Title, Price, Brand, Model, SubModel, Status, Type, CreateBy, EndDate, CreateDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, Cover);
            insertCmd.bindString(2, PKID);
            insertCmd.bindString(3, Transaction);
            insertCmd.bindString(4, Title);
            insertCmd.bindString(5, Price);
            insertCmd.bindString(6, Brand);
            insertCmd.bindString(7, Model);
            insertCmd.bindString(8, SubModel);
            insertCmd.bindString(9, Status);
            insertCmd.bindString(10, Type);
            insertCmd.bindString(11, By);
            insertCmd.bindString(12, EndDate);
            insertCmd.bindString(13, Created);
            return insertCmd.executeInsert();

        } catch (Exception e) {
            return -1;
        }
    }

    // Update Data
    /*public long UpdateDataRead(String ID, String READED) {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            //for API 11 and above
            SQLiteStatement insertCmd;
            String strSQL = "UPDATE " + TABLE_BUY_HISTORY
                    + " SET READED = ? "
                    + " WHERE ID = ? ";

            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, READED);
            insertCmd.bindString(2, ID);

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }
    }*/

    // Select All Data Array 2 dimention
    public String[][] SelectAll() {
        // TODO Auto-generated method stub
        try {
            String arrData[][] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT  * FROM " + TABLE_BUY_HISTORY/* + " Where BlockId = '" + strBlockId + "'"*/ + " Order by Status DESC";
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
    public String[][] SelectByStatus() {
        // TODO Auto-generated method stub
        try {
            String arrData[][] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT  * FROM " + TABLE_BUY_HISTORY + " Where Status = 'In Process'";
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
    public long Delete(/*String logID*/) {
        // TODO Auto-generated method stub
        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            // for API 11 and above
            SQLiteStatement insertCmd;
            String strSQL = "DELETE FROM " + TABLE_BUY_HISTORY/* + " WHERE ID = ? "*/;

            insertCmd = db.compileStatement(strSQL);
            //insertCmd.bindString(1, logID);

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }
    }
}
