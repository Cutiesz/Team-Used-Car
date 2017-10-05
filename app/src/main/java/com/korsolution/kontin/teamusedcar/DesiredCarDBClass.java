package com.korsolution.kontin.teamusedcar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

/**
 * Created by Kontin58 on 28/8/2560.
 */

public class DesiredCarDBClass extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "desiredcardb";

    // Table Name
    private static final String TABLE_DESIRED_CAR = "desiredcar";

    public DesiredCarDBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_DESIRED_CAR +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " CarBrand TEXT(100)," +
                " CarModel TEXT(100)," +
                " CarColor TEXT(100)," +
                " CarYear TEXT(100)," +
                " UpdateDate DATETIME," +
                " CarCount TEXT(100));");

        Log.d("CREATE TABLE","Create Table Successfully.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Insert Data
    public long Insert(String strCarBrand, String strCarModel, String strCarColor, String strCarYear, String strUpdateDate, String strCarCount) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            SQLiteStatement insertCmd;
            String strSQL = "INSERT INTO " + TABLE_DESIRED_CAR
                    + "(CarBrand, CarModel, CarColor, CarYear, UpdateDate, CarCount) VALUES (?, ?, ?, ?, ?, ?)";
            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strCarBrand);
            insertCmd.bindString(2, strCarModel);
            insertCmd.bindString(3, strCarColor);
            insertCmd.bindString(4, strCarYear);
            insertCmd.bindString(5, strUpdateDate);
            insertCmd.bindString(6, strCarCount);
            return insertCmd.executeInsert();

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

            String strSQL = "SELECT  * FROM " + TABLE_DESIRED_CAR/* + " Where BlockId = '" + strBlockId + "'"*/;
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
    public String[][] SelectData(String strCarBrand, String strCarModel, String strCarColor, String strCarYear) {
        // TODO Auto-generated method stub

        String strBand = "";
        String strModel = "";
        String strColor = "";
        String strYear = "";

        if (!strCarBrand.equals("")) {
            strBand = " AND CarBrand = '" + strCarBrand + "' ";
        }
        if (!strCarModel.equals("")) {
            strModel = " AND CarModel = '" + strCarModel + "' ";
        }
        if (!strCarColor.equals("")) {
            strColor = " AND CarColor = '" + strCarColor + "' ";
        }
        if (!strCarYear.equals("")) {
            strYear = " AND CarYear = '" + strCarYear + "' ";
        }

        try {
            String arrData[][] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT  * FROM " + TABLE_DESIRED_CAR + " Where (1=1) " + strBand + strModel + strColor + strYear;
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
            String strSQL = "DELETE FROM " + TABLE_DESIRED_CAR/* + " WHERE MemberID = ? "*/;

            insertCmd = db.compileStatement(strSQL);
            /*insertCmd.bindString(1, strMemberID);*/

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }

    }
}
