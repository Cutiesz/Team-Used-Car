package com.korsolution.kontin.teamusedcar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by Kontin58 on 27/6/2560.
 */

public class PictureUsedCarPathDBClass extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "pictureusedcarpathdb";

    // Table Name
    private static final String TABLE_PICTURE_PATH = "picture_usedcar_path";

    public PictureUsedCarPathDBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_PICTURE_PATH +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " URI_CAR_FRONT TEXT(100)," +
                " URI_CAR_FRONT_LEFT TEXT(100)," +
                " URI_CAR_FRONT_RIGHT TEXT(100)," +
                " URI_CAR_BACK TEXT(100)," +
                " URI_CAR_ENGINE TEXT(100)," +
                " URI_CAR_BEAM TEXT(100)," +
                " URI_CAR_INNER_1 TEXT(100)," +
                " URI_CAR_INNER_2 TEXT(100)," +
                " URI_CAR_INNER_3 TEXT(100)," +
                " URI_CAR_INNER_4 TEXT(100)," +
                " URI_CAR_INNER_5 TEXT(100)," +
                " URI_CAR_DOC_1 TEXT(100)," +
                " URI_CAR_DOC_2 TEXT(100)," +
                " URI_CAR_DOC_3 TEXT(100)," +
                " URI_CAR_DOC_4 TEXT(100)," +
                " URI_CAR_DOC_5 TEXT(100)," +
                " NUMBER_ID TEXT(100));");

        Log.d("CREATE TABLE","Create Table Successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Insert Data
    public long Insert(String strURI_CAR_FRONT, String strURI_CAR_FRONT_LEFT, String strURI_CAR_FRONT_RIGHT,
                       String strURI_CAR_BACK, String strURI_CAR_ENGINE, String strURI_CAR_BEAM,
                       String strURI_CAR_INNER_1, String strURI_CAR_INNER_2, String strURI_CAR_INNER_3,
                       String strURI_CAR_INNER_4, String strURI_CAR_INNER_5, String strURI_CAR_DOC_1,
                       String strURI_CAR_DOC_2, String strURI_CAR_DOC_3, String strURI_CAR_DOC_4,
                       String strURI_CAR_DOC_5, String strNUMBER_ID) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            SQLiteStatement insertCmd;
            String strSQL = "INSERT INTO " + TABLE_PICTURE_PATH
                    + "(URI_CAR_FRONT, URI_CAR_FRONT_LEFT, URI_CAR_FRONT_RIGHT, URI_CAR_BACK, URI_CAR_ENGINE, URI_CAR_BEAM, URI_CAR_INNER_1, URI_CAR_INNER_2, URI_CAR_INNER_3, URI_CAR_INNER_4, URI_CAR_INNER_5, URI_CAR_DOC_1, URI_CAR_DOC_2, URI_CAR_DOC_3, URI_CAR_DOC_4, URI_CAR_DOC_5, NUMBER_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strURI_CAR_FRONT);
            insertCmd.bindString(2, strURI_CAR_FRONT_LEFT);
            insertCmd.bindString(3, strURI_CAR_FRONT_RIGHT);
            insertCmd.bindString(4, strURI_CAR_BACK);
            insertCmd.bindString(5, strURI_CAR_ENGINE);
            insertCmd.bindString(6, strURI_CAR_BEAM);
            insertCmd.bindString(7, strURI_CAR_INNER_1);
            insertCmd.bindString(8, strURI_CAR_INNER_2);
            insertCmd.bindString(9, strURI_CAR_INNER_3);
            insertCmd.bindString(10, strURI_CAR_INNER_4);
            insertCmd.bindString(11, strURI_CAR_INNER_5);
            insertCmd.bindString(12, strURI_CAR_DOC_1);
            insertCmd.bindString(13, strURI_CAR_DOC_2);
            insertCmd.bindString(14, strURI_CAR_DOC_3);
            insertCmd.bindString(15, strURI_CAR_DOC_4);
            insertCmd.bindString(16, strURI_CAR_DOC_5);
            insertCmd.bindString(17, strNUMBER_ID);
            return insertCmd.executeInsert();

        } catch (Exception e) {
            return -1;
        }
    }

    // Update Data
    public long UpdateData(String strColumnName, String strURI, String strNUMBER_ID) {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            //for API 11 and above
            SQLiteStatement insertCmd;
            String strSQL = "UPDATE " + TABLE_PICTURE_PATH
                    + " SET " + strColumnName + " = ? "
                    //+ " , LanguageName = ? "
                    + " WHERE NUMBER_ID = ? ";

            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strURI);
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
                        arrData[i][6] = cursor.getString(6);
                        arrData[i][7] = cursor.getString(7);
                        arrData[i][8] = cursor.getString(8);
                        arrData[i][9] = cursor.getString(9);
                        arrData[i][10] = cursor.getString(10);
                        arrData[i][11] = cursor.getString(11);
                        arrData[i][12] = cursor.getString(12);
                        arrData[i][13] = cursor.getString(13);
                        arrData[i][14] = cursor.getString(14);
                        arrData[i][15] = cursor.getString(15);
                        arrData[i][16] = cursor.getString(16);
                        arrData[i][17] = cursor.getString(17);
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
