package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
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

public class AddUsedCar1LicensePlateActivity extends AppCompatActivity {

    private EditText edtFront;
    private EditText edtBack;
    private Spinner spnProvince;
    private Button btnNext;

    private String YoutubeLink;

    protected ArrayList<JSONObject> feedDataListProvince;

    private ProvinceDBClass ProvinceDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_used_car_license_plate);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        YoutubeLink = getIntent().getStringExtra("YoutubeLink");

        ProvinceDB = new ProvinceDBClass(this);

        setupWidgets();

        String strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);
        new FeedAsynTaskProvince().execute(strWebServiceUrl + "GetProvince", "");
    }

    private void setupWidgets() {

        edtFront = (EditText) findViewById(R.id.edtFront);
        edtBack = (EditText) findViewById(R.id.edtBack);
        spnProvince = (Spinner) findViewById(R.id.spnProvince);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String _front = edtFront.getText().toString();
                String _back = edtBack.getText().toString();
                String _province = spnProvince.getSelectedItem().toString();
                String provinceID = "";

                if (_front.length() > 0 && _back.length() > 0) {
                    if (!_province.equals("เลือกจังหวัด")) {
                        String[][] arrData = ProvinceDB.SelectData(_province);
                        if (arrData != null) {
                            provinceID = arrData[0][1].toString();
                        }

                        Intent intent = new Intent(getApplicationContext(), AddUsedCar2ModelActivity.class);
                        intent.putExtra("YoutubeLink", YoutubeLink);
                        intent.putExtra("LicensePlateFront", _front);
                        intent.putExtra("LicensePlateBack", _back);
                        intent.putExtra("LicensePlateProvince", _province);
                        intent.putExtra("ProvinceID", provinceID);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "กรุณาเลือกจังหวัด", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "กรุณากรอกเลขทะเบียนรถ", Toast.LENGTH_LONG).show();
                }




            }
        });
    }

    public class FeedAsynTaskProvince extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(AddUsedCar1LicensePlateActivity.this);
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
                                            ArrayAdapter<String> arrAd = new ArrayAdapter<String>(AddUsedCar1LicensePlateActivity.this, android.R.layout.simple_spinner_item, arrSpinner);
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
}
