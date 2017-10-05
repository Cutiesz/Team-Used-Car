package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class SearchDesiredCarActivity extends AppCompatActivity {

    private Spinner spnYear;
    private Spinner spnBrand;
    private Spinner spnGeneration;
    private Spinner spnColor;
    private TextView txtColorTest;
    private Button btnSearch;
    private Button btnCancle;

    protected ArrayList<JSONObject> feedDataListBrand;
    protected ArrayList<JSONObject> feedDataListGeneration;
    protected ArrayList<JSONObject> feedDataListColor;

    private String strWebServiceUrl;

    private ProgressDialog mProgressDialog;

    private CarColorDBClass CarColorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_desired_car);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        CarColorDB = new CarColorDBClass(this);

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        setupWidgets();
        loadSpinnerYear();
        new FeedAsynTaskColor().execute(strWebServiceUrl + "GetCarColor");
    }

    private void setupWidgets() {

        spnYear = (Spinner) findViewById(R.id.spnYear);
        spnBrand = (Spinner) findViewById(R.id.spnBrand);
        spnGeneration = (Spinner) findViewById(R.id.spnGeneration);
        spnColor = (Spinner) findViewById(R.id.spnColor);
        txtColorTest = (TextView) findViewById(R.id.txtColorTest);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnCancle = (Button) findViewById(R.id.btnCancle);

        spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String carYear = spnYear.getSelectedItem().toString();

                new FeedAsynTaskBrand().execute(strWebServiceUrl + "GetCarBrand", carYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String carBrand = spnBrand.getSelectedItem().toString();

                new FeedAsynTaskGeneration().execute(strWebServiceUrl + "GetCarModel", carBrand);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnGeneration.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String carGeneration = spnGeneration.getSelectedItem().toString();

                //new FeedAsynTaskSubGeneration().execute(strWebServiceUrl + "GetCarSubModel", carGeneration);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spnColor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String carColor = spnColor.getSelectedItem().toString();

                String[] arrColorCode = CarColorDB.SelectColorCodeByColorName(carColor);
                if (arrColorCode != null) {
                    txtColorTest.setBackgroundColor(Color.parseColor(arrColorCode[0]));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carYear = spnYear.getSelectedItem().toString();
                String carBrand = spnBrand.getSelectedItem().toString();
                String carGeneration = spnGeneration.getSelectedItem().toString();
                String carColor = spnColor.getSelectedItem().toString();

                if (carYear.equals("เลือกปีรถ")) {
                    carYear = "";
                }
                if (carBrand.equals("เลือกยี่ห้อรถ")) {
                    carBrand = "";
                }
                if (carGeneration.equals("เลือกรุ่นรถ")) {
                    carGeneration = "";
                }
                /*if (carSubGeneration.equals("เลือกรุ่นย่อยรถ")) {
                    carSubGeneration = "";
                }*/

                if (carColor.equals("เลือกสีรถ")) {
                    carColor = "";
                }

                Intent returnIntent = new Intent();
                returnIntent.putExtra("year", carYear);
                returnIntent.putExtra("brand", carBrand);
                returnIntent.putExtra("generation", carGeneration);
                //returnIntent.putExtra("subGeneration", carSubGeneration);
                returnIntent.putExtra("carColor", carColor);
                setResult(SearchCarActivity.RESULT_OK, returnIntent);

                overridePendingTransition(R.anim.slide_down_info, R.anim.no_change);

                finish();
            }
        });

        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent returnIntent = new Intent();
                setResult(SearchCarActivity.RESULT_CANCELED, returnIntent);

                overridePendingTransition(R.anim.slide_down_info, R.anim.no_change);

                finish();
            }
        });
    }

    private void loadSpinnerYear() {

        mProgressDialog = new ProgressDialog(SearchDesiredCarActivity.this);
        mProgressDialog.setMessage("กำลังโหลดข้อมูล ...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        // Spinner Years
        ArrayList<String> years = new ArrayList<String>();
        years.add("เลือกปีรถ");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        /*for (int i = 1900; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }*/
        for (int i = thisYear; i >= 1900; i--) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnYear.setAdapter(adapter);
    }

    public class FeedAsynTaskBrand extends AsyncTask<String, Void, String> {

        /*private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(SearchCarActivity.this);
            nDialog.setMessage("Loading..");
            //nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();

        }*/

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
                        .add("year", params[1])
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

                feedDataListBrand = CuteFeedJsonUtil.feed(s);
                if (feedDataListBrand != null) {
                    for (int i = 0; i <= feedDataListBrand.size(); i++) {
                        try {

                            String strstatus = String.valueOf(feedDataListBrand.get(i).getString("status"));
                            String strdata = String.valueOf(feedDataListBrand.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed(strdata);
                            if (feedDataListData != null) {

                                String[] arrSpinner;
                                arrSpinner = new String[feedDataListData.size()+1];
                                arrSpinner[0] = "เลือกยี่ห้อรถ";

                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    try {

                                        String Brand = String.valueOf(feedDataListData.get(j).getString("Brand"));

                                        arrSpinner[j+1] = Brand;

                                    } catch (Exception e) {

                                    }
                                }

                                // Set List Spinner
                                ArrayAdapter<String> arrAd = new ArrayAdapter<String>(SearchDesiredCarActivity.this, android.R.layout.simple_spinner_item, arrSpinner);
                                arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnBrand.setAdapter(arrAd);
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

            //nDialog.dismiss();
        }
    }

    public class FeedAsynTaskGeneration extends AsyncTask<String, Void, String> {

        /*private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(SearchCarActivity.this);
            nDialog.setMessage("Loading..");
            //nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();

        }*/

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
                        .add("brand", params[1])
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

                feedDataListGeneration = CuteFeedJsonUtil.feed(s);
                if (feedDataListGeneration != null) {
                    for (int i = 0; i <= feedDataListGeneration.size(); i++) {
                        try {

                            String strstatus = String.valueOf(feedDataListGeneration.get(i).getString("status"));
                            String strdata = String.valueOf(feedDataListGeneration.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed(strdata);
                            if (feedDataListData != null) {

                                String[] arrSpinner;
                                arrSpinner = new String[feedDataListData.size()+1];
                                arrSpinner[0] = "เลือกรุ่นรถ";

                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    try {

                                        String Model = String.valueOf(feedDataListData.get(j).getString("Model"));

                                        arrSpinner[j+1] = Model;

                                    } catch (Exception e) {

                                    }
                                }

                                // Set List Spinner
                                ArrayAdapter<String> arrAd = new ArrayAdapter<String>(SearchDesiredCarActivity.this, android.R.layout.simple_spinner_item, arrSpinner);
                                arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnGeneration.setAdapter(arrAd);
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

            //nDialog.dismiss();
        }
    }

    public class FeedAsynTaskColor extends AsyncTask<String, Void, String> {

        /*private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(AddUsedCar2ModelActivity.this);
            nDialog.setMessage("Loading..");
            //nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();

        }*/

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
                        //.add("year", params[1])
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

                CarColorDB.Delete();

                feedDataListColor = CuteFeedJsonUtil.feed(s);
                if (feedDataListColor != null) {
                    for (int i = 0; i <= feedDataListColor.size(); i++) {
                        try {

                            String strstatus = String.valueOf(feedDataListColor.get(i).getString("status"));
                            String strdata = String.valueOf(feedDataListColor.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed(strdata);
                            if (feedDataListData != null) {

                                String[] arrSpinner;
                                arrSpinner = new String[feedDataListData.size()+1];
                                arrSpinner[0] = "เลือกสีรถ";

                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    try {

                                        String color_name = String.valueOf(feedDataListData.get(j).getString("color_name"));
                                        String color_code = String.valueOf(feedDataListData.get(j).getString("color_code"));

                                        CarColorDB.Insert(color_name, color_code);

                                        arrSpinner[j+1] = color_name;

                                    } catch (Exception e) {

                                    }
                                }

                                // Set List Spinner
                                ArrayAdapter<String> arrAd = new ArrayAdapter<String>(SearchDesiredCarActivity.this, android.R.layout.simple_spinner_item, arrSpinner);
                                arrAd.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spnColor.setAdapter(arrAd);
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

            //nDialog.dismiss();
            mProgressDialog.dismiss();
        }
    }
}
