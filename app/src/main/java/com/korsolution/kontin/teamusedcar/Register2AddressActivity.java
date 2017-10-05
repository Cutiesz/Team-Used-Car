package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class Register2AddressActivity extends AppCompatActivity {

    private EditText edtAddress;
    private Spinner spnProvince;
    private Spinner spnAmphoe;
    private Spinner spnDistrict;
    private EditText edtPostcode;
    private Button btnNext;

    private String strWebServiceUrl;

    protected ArrayList<JSONObject> feedDataListProvince;
    protected ArrayList<JSONObject> feedDataListAmphoe;
    protected ArrayList<JSONObject> feedDataListDistrict;

    private ProvinceDBClass ProvinceDB;
    private AmphoeDBClass AmphoeDB;
    private DistrictDBClass DistrictDB;

    private String Email;
    private String ShopName;
    private String OwnerName;
    private String OwnerSurname;
    private String TelephoneNumber;

    String provinceName = "";
    String[] emptyAmphoe = {"เลือกอำเภอ"};
    String[] emptyDistrict = {"เลือกตำบล"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_address);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        Email = getIntent().getStringExtra("Email");
        ShopName = getIntent().getStringExtra("ShopName");
        OwnerName = getIntent().getStringExtra("OwnerName");
        OwnerSurname = getIntent().getStringExtra("OwnerSurname");
        TelephoneNumber = getIntent().getStringExtra("TelephoneNumber");

        ProvinceDB = new ProvinceDBClass(this);
        AmphoeDB = new AmphoeDBClass(this);
        DistrictDB = new DistrictDBClass(this);

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        setupWidgets();
        initial();
    }

    private void setupWidgets() {

        edtAddress = (EditText) findViewById(R.id.edtAddress);
        spnProvince = (Spinner) findViewById(R.id.spnProvince);
        spnAmphoe = (Spinner) findViewById(R.id.spnAmphoe);
        spnDistrict = (Spinner) findViewById(R.id.spnDistrict);
        edtPostcode = (EditText) findViewById(R.id.edtPostcode);
        btnNext = (Button) findViewById(R.id.btnNext);

        spnProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String _province = spnProvince.getSelectedItem().toString();

                new FeedAsynTaskAmphoe().execute(strWebServiceUrl + "GetAmphur", _province, "");

                provinceName = _province;

                // Set List Spinner
                ArrayAdapter<String> arrAd = new ArrayAdapter<String>(Register2AddressActivity.this, android.R.layout.simple_spinner_item, emptyDistrict);
                arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spnDistrict.setAdapter(arrAd);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnAmphoe.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String _amphoe = spnAmphoe.getSelectedItem().toString();

                new FeedAsynTaskDistrict().execute(strWebServiceUrl + "GetDistrict", provinceName, _amphoe, "");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String address = edtAddress.getText().toString();
                String province = spnProvince.getSelectedItem().toString();
                String amphoe = spnAmphoe.getSelectedItem().toString();
                String district = spnDistrict.getSelectedItem().toString();
                String postcode = edtPostcode.getText().toString();

                if (address.length() > 0) {
                    if (!province.equals("เลือกจังหวัด")) {
                        if (!amphoe.equals("เลือกอำเภอ")) {
                            if (!district.equals("เลือกตำบล")) {
                                if (postcode.length() == 5) {

                                    Intent intent = new Intent(getApplicationContext(), Register3BankActivity.class);
                                    intent.putExtra("Email", Email);
                                    intent.putExtra("ShopName", ShopName);
                                    intent.putExtra("OwnerName", OwnerName);
                                    intent.putExtra("OwnerSurname", OwnerSurname);
                                    intent.putExtra("TelephoneNumber", TelephoneNumber);
                                    intent.putExtra("Address", address);
                                    intent.putExtra("Province", province);
                                    intent.putExtra("Amphoe", amphoe);
                                    intent.putExtra("District", district);
                                    intent.putExtra("Postcode", postcode);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "กรุณากรอกรหัสไปรษณี",Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "กรุณาเลือกตำบล.",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "กรุณาเลือกอำเภอ.",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "กรุณาเลือกจังหวัด.",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "กรุณากรอกที่อยู่",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initial() {
        if (isOnline()) {

            new FeedAsynTaskProvince().execute(strWebServiceUrl + "GetProvince", "");

            // Set List Spinner
            ArrayAdapter<String> arrAd1 = new ArrayAdapter<String>(Register2AddressActivity.this, android.R.layout.simple_spinner_item, emptyAmphoe);
            arrAd1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnAmphoe.setAdapter(arrAd1);

            // Set List Spinner
            ArrayAdapter<String> arrAd2 = new ArrayAdapter<String>(Register2AddressActivity.this, android.R.layout.simple_spinner_item, emptyDistrict);
            arrAd2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnDistrict.setAdapter(arrAd2);

        } else {
            Toast.makeText(getApplicationContext(), "No internet signal, Please try agian.", Toast.LENGTH_LONG).show();
        }
    }

    public class FeedAsynTaskProvince extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(Register2AddressActivity.this);
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
                        .add("provinceName", params[1])
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

                ProvinceDB.Delete();

                feedDataListProvince = CuteFeedJsonUtil.feed(s);
                if (feedDataListProvince != null) {
                    for (int i = 0; i <= feedDataListProvince.size(); i++) {
                        try {

                            String strstatus = String.valueOf(feedDataListProvince.get(i).getString("status"));
                            String strdata = String.valueOf(feedDataListProvince.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed(strdata);
                            if (feedDataListData != null) {
                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    try {

                                        String strId = String.valueOf(feedDataListData.get(j).getString("Id"));
                                        String strProvince_Name = String.valueOf(feedDataListData.get(j).getString("Province_Name"));

                                        ProvinceDB.Insert(strId, strProvince_Name);

                                        String[] arrCateData = ProvinceDB.SelectName();
                                        if (arrCateData != null) {

                                            String[] arrSpinner;
                                            arrSpinner = new String[arrCateData.length+1];

                                            arrSpinner[0] = "เลือกจังหวัด";

                                            for (int k = 0; k < arrCateData.length; k++) {
                                                arrSpinner[k+1] = arrCateData[k].toString();
                                            }

                                            // Set List Spinner
                                            ArrayAdapter<String> arrAd = new ArrayAdapter<String>(Register2AddressActivity.this, android.R.layout.simple_spinner_item, arrSpinner);
                                            arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spnProvince.setAdapter(arrAd);
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

    public class FeedAsynTaskAmphoe extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(Register2AddressActivity.this);
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
                        .add("provinceName", params[1])
                        .add("amphurName", params[2])
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

                AmphoeDB.Delete();

                feedDataListAmphoe = CuteFeedJsonUtil.feed(s);
                if (feedDataListAmphoe != null) {
                    for (int i = 0; i <= feedDataListAmphoe.size(); i++) {
                        try {

                            String strstatus = String.valueOf(feedDataListAmphoe.get(i).getString("status"));
                            String strdata = String.valueOf(feedDataListAmphoe.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed(strdata);
                            if (feedDataListData != null) {
                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    try {

                                        String strAmphur_ID = String.valueOf(feedDataListData.get(j).getString("Amphur_ID"));
                                        String strAmphur_Name = String.valueOf(feedDataListData.get(j).getString("Amphur_Name"));

                                        AmphoeDB.Insert(strAmphur_ID, strAmphur_Name);

                                        String[] arrCateData = AmphoeDB.SelectName();
                                        if (arrCateData != null) {

                                            String[] arrSpinner;
                                            arrSpinner = new String[arrCateData.length+1];

                                            arrSpinner[0] = "เลือกอำเภอ";

                                            for (int k = 0; k < arrCateData.length; k++) {
                                                arrSpinner[k+1] = arrCateData[k].toString();
                                            }

                                            // Set List Spinner
                                            ArrayAdapter<String> arrAd = new ArrayAdapter<String>(Register2AddressActivity.this, android.R.layout.simple_spinner_item, arrSpinner);
                                            arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spnAmphoe.setAdapter(arrAd);
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

    public class FeedAsynTaskDistrict extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(Register2AddressActivity.this);
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
                        .add("provinceName", params[1])
                        .add("amphurName", params[2])
                        .add("districtName", params[3])
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

                DistrictDB.Delete();

                feedDataListDistrict = CuteFeedJsonUtil.feed(s);
                if (feedDataListDistrict != null) {
                    for (int i = 0; i <= feedDataListDistrict.size(); i++) {
                        try {

                            String strstatus = String.valueOf(feedDataListDistrict.get(i).getString("status"));
                            String strdata = String.valueOf(feedDataListDistrict.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed(strdata);
                            if (feedDataListData != null) {
                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    try {

                                        String strDistrict_ID = String.valueOf(feedDataListData.get(j).getString("District_ID"));
                                        String strDistrict_Name = String.valueOf(feedDataListData.get(j).getString("District_Name"));

                                        DistrictDB.Insert(strDistrict_ID, strDistrict_Name);

                                        String[] arrCateData = DistrictDB.SelectName();
                                        if (arrCateData != null) {

                                            String[] arrSpinner;
                                            arrSpinner = new String[arrCateData.length+1];

                                            arrSpinner[0] = "เลือกตำบล";

                                            for (int k = 0; k < arrCateData.length; k++) {
                                                arrSpinner[k+1] = arrCateData[k].toString();
                                            }

                                            // Set List Spinner
                                            ArrayAdapter<String> arrAd = new ArrayAdapter<String>(Register2AddressActivity.this, android.R.layout.simple_spinner_item, arrSpinner);
                                            arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                            spnDistrict.setAdapter(arrAd);
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

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
