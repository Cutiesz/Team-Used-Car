package com.korsolution.kontin.teamusedcar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by Kontin58 on 12/11/2559.
 */

public class AccountDBClass extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "accountdb";

    // Table Name
    private static final String TABLE_ACCOUNT = "account";

    public AccountDBClass(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_ACCOUNT +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " UserId TEXT(100)," +
                " CustomerId TEXT(100)," +
                " FirstName TEXT(100)," +
                " LastName TEXT(100)," +
                " BirthDate TEXT(100)," +
                " Code TEXT(100)," +
                " Company TEXT(100)," +
                " Mobile TEXT(100)," +
                " Gender TEXT(100)," +
                " CustomerType TEXT(100)," +
                " SupplierType TEXT(100));");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    // Insert Data
    public long InsertAccount(String strUserId, String strCustomerId, String strFirstName,
                              String strLastName, String strBirthDate, String strCode,
                              String strCompany, String strMobile, String strGender,
                              String strCustomerType, String strSupplierType) {
        // TODO Auto-generated method stub

        try {
            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            SQLiteStatement insertCmd;
            String strSQL = "INSERT INTO " + TABLE_ACCOUNT
                    + "(UserId, CustomerId, FirstName, LastName, BirthDate, Code, Company, Mobile, Gender, CustomerType, SupplierType) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            insertCmd = db.compileStatement(strSQL);
            insertCmd.bindString(1, strUserId);
            insertCmd.bindString(2, strCustomerId);
            insertCmd.bindString(3, strFirstName);
            insertCmd.bindString(4, strLastName);
            insertCmd.bindString(5, strBirthDate);
            insertCmd.bindString(6, strCode);
            insertCmd.bindString(7, strCompany);
            insertCmd.bindString(8, strMobile);
            insertCmd.bindString(9, strGender);
            insertCmd.bindString(10, strCustomerType);
            insertCmd.bindString(11, strSupplierType);
            return insertCmd.executeInsert();

        } catch (Exception e) {
            return -1;
        }
    }

    // Select All Data Array 2 dimention
    public String[][] SelectAllAccount() {
        // TODO Auto-generated method stub

        try {
            String arrData[][] = null;
            SQLiteDatabase db;
            db = this.getReadableDatabase(); // Read Data

            String strSQL = "SELECT  * FROM " + TABLE_ACCOUNT/* + " Where BlockId = '" + strBlockId + "'"*/;
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
    public long DeleteAccount(/*String strMemberID*/) {
        // TODO Auto-generated method stub

        try {

            SQLiteDatabase db;
            db = this.getWritableDatabase(); // Write Data

            // for API 11 and above
            SQLiteStatement insertCmd;
            String strSQL = "DELETE FROM " + TABLE_ACCOUNT/* + " WHERE MemberID = ? "*/;

            insertCmd = db.compileStatement(strSQL);
            /*insertCmd.bindString(1, strMemberID);*/

            return insertCmd.executeUpdateDelete();

        } catch (Exception e) {
            return -1;
        }

    }
}
