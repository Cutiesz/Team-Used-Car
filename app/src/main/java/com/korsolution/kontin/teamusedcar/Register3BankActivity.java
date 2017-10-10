package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Register3BankActivity extends AppCompatActivity {

    private EditText edtBankAccountName;
    private EditText edtBankAccountNumber;
    private Spinner spnBank;
    private Button btnNext;

    private String Email;
    private String ShopName;
    private String OwnerName;
    private String OwnerSurname;
    private String TelephoneNumber;
    private String Address;
    private String Province;
    private String Amphoe;
    private String District;
    private String Postcode;

    private String strWebServiceUrl;

    private BankDBClass BankDB;

    protected ArrayList<JSONObject> feedDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_bank);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        Email = getIntent().getStringExtra("Email");
        ShopName = getIntent().getStringExtra("ShopName");
        OwnerName = getIntent().getStringExtra("OwnerName");
        OwnerSurname = getIntent().getStringExtra("OwnerSurname");
        TelephoneNumber = getIntent().getStringExtra("TelephoneNumber");
        Address = getIntent().getStringExtra("Address");
        Province = getIntent().getStringExtra("Province");
        Amphoe = getIntent().getStringExtra("Amphoe");
        District = getIntent().getStringExtra("District");
        Postcode = getIntent().getStringExtra("Postcode");

        BankDB = new BankDBClass(this);

        setupWidgets();

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);
        new FeedAsynTask().execute(strWebServiceUrl + "GetBank");
    }

    private void setupWidgets() {

        edtBankAccountName = (EditText) findViewById(R.id.edtBankAccountName);
        edtBankAccountNumber = (EditText) findViewById(R.id.edtBankAccountNumber);
        spnBank = (Spinner) findViewById(R.id.spnBank);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String bankAccountName = edtBankAccountName.getText().toString();
                String bankAccountNumber = edtBankAccountNumber.getText().toString();
                String bank = spnBank.getSelectedItem().toString();

                if (bankAccountName.length() > 0) {
                    if (bankAccountNumber.length() > 0) {
                        if (!bank.equals("เลือกธนาคาร")) {

                            /*String bankCode = "";
                            String[][] arrData = BankDB.SelectData(bank);
                            if (arrData != null) {
                                bankCode = arrData[0][2].toString();
                            }

                            Intent intent = new Intent(getApplicationContext(), Register4TypeActivity.class);
                            intent.putExtra("Email", Email);
                            intent.putExtra("ShopName", ShopName);
                            intent.putExtra("OwnerName", OwnerName);
                            intent.putExtra("OwnerSurname", OwnerSurname);
                            intent.putExtra("TelephoneNumber", TelephoneNumber);
                            intent.putExtra("Address", Address);
                            intent.putExtra("Province", Province);
                            intent.putExtra("Amphoe", Amphoe);
                            intent.putExtra("District", District);
                            intent.putExtra("Postcode", Postcode);
                            intent.putExtra("BankAccountName", bankAccountName);
                            intent.putExtra("BankAccountNumber", bankAccountNumber);
                            intent.putExtra("BankCode", bankCode);
                            startActivity(intent);*/

                            dialogAlertConfirm(bankAccountName, bankAccountNumber, bank);

                        } else {
                            Toast.makeText(getApplicationContext(), "กรุณากรอกธนาคาร",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "กรุณากรอกเลขบัญชี",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "กรุณากรอกชื่อบัญชี",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public void dialogAlertConfirm(final String bankAccountName, final String bankAccountNumber, final String bank){
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);

        //mDialog.setTitle("ชื่อบัญชี \"" + bankAccountName + "\" \n เลขที่บัญชี \"" + bankAccountNumber + "\" \n ธนาคาร \"" + bank + "\" ใช่หรือไม่?");
        mDialog.setMessage("ชื่อบัญชี \"" + bankAccountName + "\" \n เลขที่บัญชี \"" + bankAccountNumber + "\" \n ธนาคาร \"" + bank + "\" \n ยืนยันข้อมูลบัญชีนี้ใช่หรือไม่?");
        //mDialog.setIcon(R.drawable.ic_action_close);
        mDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                String bankCode = "";
                String[][] arrData = BankDB.SelectData(bank);
                if (arrData != null) {
                    bankCode = arrData[0][2].toString();
                }

                Intent intent = new Intent(getApplicationContext(), Register4TypeActivity.class);
                intent.putExtra("Email", Email);
                intent.putExtra("ShopName", ShopName);
                intent.putExtra("OwnerName", OwnerName);
                intent.putExtra("OwnerSurname", OwnerSurname);
                intent.putExtra("TelephoneNumber", TelephoneNumber);
                intent.putExtra("Address", Address);
                intent.putExtra("Province", Province);
                intent.putExtra("Amphoe", Amphoe);
                intent.putExtra("District", District);
                intent.putExtra("Postcode", Postcode);
                intent.putExtra("BankAccountName", bankAccountName);
                intent.putExtra("BankAccountNumber", bankAccountNumber);
                intent.putExtra("BankCode", bankCode);
                startActivity(intent);

            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                //Toast.makeText(getBaseContext(), "Fail", Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        mDialog.show();
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(Register3BankActivity.this);
            nDialog.setMessage("Loading..");
            //nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            try{

                // 1. connect server with okHttp
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();


                // 2. assign post data
                RequestBody postData = new FormBody.Builder()
                        //.add("username", "admin")
                        //.add("password", "password")
                        //.add("provinceName", params[1])
                        .build();

                Request request = new Request.Builder()
                        .url(params[0])
                        .post(postData)
                        .build();

                // 3. transport request to server
                okhttp3.Response response = client.newCall(request).execute();
                String result = response.body().string();

                return result;

            } catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s != null) {
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();

                s = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                s = s.replace("<string xmlns=\"http://tempuri.org/\">", "");
                s = s.replace("</string>", "");
                s = "[" + s + "]";

                BankDB.Delete();

                feedDataList = CuteFeedJsonUtil.feed(s);
                if (feedDataList != null) {
                    for (int i = 0; i <= feedDataList.size(); i++) {
                        try {

                            String strstatus = String.valueOf(feedDataList.get(i).getString("status"));
                            String strdata = String.valueOf(feedDataList.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed(strdata);
                            if (feedDataListData != null) {
                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    try {

                                        String bankName = String.valueOf(feedDataListData.get(j).getString("bank_name"));
                                        String bankCode = String.valueOf(feedDataListData.get(j).getString("bank_code"));

                                        BankDB.Insert(bankName, bankCode);

                                        String[] arrCateData = BankDB.SelectName();
                                        if (arrCateData != null) {

                                            String[] arrSpinner;
                                            arrSpinner = new String[arrCateData.length+1];

                                            arrSpinner[0] = "เลือกธนาคาร";

                                            for (int k = 0; k < arrCateData.length; k++) {
                                                arrSpinner[k+1] = arrCateData[k].toString();
                                            }

                                            // Set List Spinner
                                            ArrayAdapter<String> arrAd = new ArrayAdapter<String>(Register3BankActivity.this, android.R.layout.simple_spinner_item, arrSpinner);
                                            arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spnBank.setAdapter(arrAd);
                                        }

                                    } catch (Exception e) {

                                    }
                                }
                            }

                        } catch (Exception e) {

                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No Data!!", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_LONG).show();
            }

            nDialog.dismiss();
        }
    }
}
