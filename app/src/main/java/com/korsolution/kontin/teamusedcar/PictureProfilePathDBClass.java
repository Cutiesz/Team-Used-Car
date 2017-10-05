package com.korsolution.kontin.teamusedcar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by Kontin58 on 2/11/2559.
 */

public class PictureProfilePathDBClass extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "pictureprofilepathdb";

    // Table Name
    private static final String TABLE_PICTURE_PATH = "picture_profile_path";

    public PictureProfilePathDBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PICTURE_PATH +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " URI_IDENTITY_CARD TEXT(100)," +
                " URI_HOUSE_REGISTRATION TEXT(100)," +
                " URI_VAT_REGISTRATION TEXT(100)," +
                " URI_STOREFRONT TEXT(100)," +
                " NUMBER_ID TEXT(100));");

        Log.d("CREATE TABLE","Create Table Successfully.");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Insert Data
    public long Insert(String strURI_IDENTITY_CARD, String strURI_HOUSE_REGISTRATION, String strURI_VAT_REGISTRATION, String strURI_STOREFRONT, String strNUMBER_ID) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            SQLiteStatement insertCmd;
            String strSQL = "INSERT INTO " + TABLE_PICTURE_PATH
                    + "(URI_IDENTITY_CARD, URI_HOUSE_REGISTRATION, URI_VAT_REGISTRATION, URI_STOREFRONT, NUMBER_ID) VALUES (?, ?, ?, ?, ?)";
            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strURI_IDENTITY_CARD);
            insertCmd.bindString(2, strURI_HOUSE_REGISTRATION);
            insertCmd.bindString(3, strURI_VAT_REGISTRATION);
            insertCmd.bindString(4, strURI_STOREFRONT);
            insertCmd.bindString(5, strNUMBER_ID);
            return insertCmd.executeInsert();

        } catch (Exception e) {
            return -1;
        }
    }

    // Update Data
    public long UpdateDataFront(String strURI_IDENTITY_CARD, String strNUMBER_ID) {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            //for API 11 and above
            SQLiteStatement insertCmd;
            String strSQL = "UPDATE " + TABLE_PICTURE_PATH
                    + " SET URI_IDENTITY_CARD = ? "
                    //+ " , LanguageName = ? "
                    + " WHERE NUMBER_ID = ? ";

            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strURI_IDENTITY_CARD);
            insertCmd.bindString(2, strNUMBER_ID);

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }
    }

    // Update Data
    public long UpdateDataBack(String strURI_HOUSE_REGISTRATION, String strNUMBER_ID) {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            //for API 11 and above
            SQLiteStatement insertCmd;
            String strSQL = "UPDATE " + TABLE_PICTURE_PATH
                    + " SET URI_HOUSE_REGISTRATION = ? "
                    //+ " , LanguageName = ? "
                    + " WHERE NUMBER_ID = ? ";

            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strURI_HOUSE_REGISTRATION);
            insertCmd.bindString(2, strNUMBER_ID);

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }
    }

    // Update Data
    public long UpdateDataLeft(String strURI_VAT_REGISTRATION, String strNUMBER_ID) {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            //for API 11 and above
            SQLiteStatement insertCmd;
            String strSQL = "UPDATE " + TABLE_PICTURE_PATH
                    + " SET URI_VAT_REGISTRATION = ? "
                    //+ " , LanguageName = ? "
                    + " WHERE NUMBER_ID = ? ";

            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strURI_VAT_REGISTRATION);
            insertCmd.bindString(2, strNUMBER_ID);

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }
    }

    // Update Data
    public long UpdateDataRight(String strURI_STOREFRONT, String strNUMBER_ID) {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            //for API 11 and above
            SQLiteStatement insertCmd;
            String strSQL = "UPDATE " + TABLE_PICTURE_PATH
                    + " SET URI_STOREFRONT = ? "
                    //+ " , LanguageName = ? "
                    + " WHERE NUMBER_ID = ? ";

            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strURI_STOREFRONT);
            insertCmd.bindString(2, strNUMBER_ID);

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }
    }

    // Select All Data Array 2 dimention
    public String[][] SelectAll() {
        // TODO Auto-generated method stub

        try {
            String arrData[][] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT  * FROM " + TABLE_PICTURE_PATH/* + " Where BlockId = '" + strBlockId + "'"*/;
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
            String strSQL = "DELETE FROM " + TABLE_PICTURE_PATH/* + " WHERE MemberID = ? "*/;

            insertCmd = db.compileStatement(strSQL);
            /*insertCmd.bindString(1, strMemberID);*/

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }

    }
}
