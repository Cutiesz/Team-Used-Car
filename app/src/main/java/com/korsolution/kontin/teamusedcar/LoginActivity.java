package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;
    private TextView txtAlert;
    private TextView txtRegister;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListToken;

    private AccountDBClass AccountDB;

    private TokenDBClass TokenDB;
    private String _token;

    private String strWebServiceUrl;

    // check permission location
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().hide();

        AccountDB = new AccountDBClass(this);
        TokenDB = new TokenDBClass(this);

        String[][] arrData = TokenDB.SelectAll();
        if (arrData != null) {
            _token = arrData[0][1].toString();
        }

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        setupWidgets();

        // tent1 : 123456 , showroom1 : 123456

        //String shareBody = "กินข้าวและ";
        //Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        //sharingIntent.setType("text/plain");
        //sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject Here");
        //sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        //startActivity(Intent.createChooser(sharingIntent, "shared"/*getResources().getString(R.string.share_using)*/));

        // Check permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            btnLogin.setEnabled(false);
            txtRegister.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }

        // check permission Location
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
    }

    private void setupWidgets() {

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        txtAlert = (TextView) findViewById(R.id.txtAlert);
        txtRegister = (TextView) findViewById(R.id.txtRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String _email = edtEmail.getText().toString();
                String _password = edtPassword.getText().toString();

                if (isOnline()) {
                    if (_email.length() > 0) {
                        if (_password.length() > 0) {

                            // Log in
                            login(_email, _password, "Android");

                        } else {
                            txtAlert.setVisibility(View.VISIBLE);
                            txtAlert.setText("Please Enter Password!!");
                        }
                    } else {
                        txtAlert.setVisibility(View.VISIBLE);
                        txtAlert.setText("Please Enter Username!!");
                    }
                } else {
                    txtAlert.setVisibility(View.VISIBLE);
                    txtAlert.setText("No Internet signal, Please try agian!!");
                }
            }
        });

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register1AccountActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login(String _email, String _password, String _device) {

        new FeedAsynTask().execute(strWebServiceUrl + "Login", /*"supplier1", "123456"*/_email, _password, _device);
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(LoginActivity.this);
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
                        .add("email", params[1])
                        .add("password", params[2])
                        .add("device", params[3])
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
                //Toast.makeText(getActivity().getApplicationContext(), s, Toast.LENGTH_LONG).show();

                s = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                s = s.replace("<string xmlns=\"http://tempuri.org/\">", "");
                s = s.replace("</string>", "");
                s = "[" + s + "]";

                AccountDB.DeleteAccount();

                feedDataList = CuteFeedJsonUtil.feed(s);
                if (feedDataList != null) {

                    // Log in fail
                    try {
                        String _status = String.valueOf(feedDataList.get(0).getString("status"));

                        //txtLogInFail.setVisibility(View.VISIBLE);
                        //txtLogInFail.setText(loginStatus);

                        if (_status.equals("error") || _status.equals("fail")) {
                            txtAlert.setVisibility(View.VISIBLE);
                            txtAlert.setText("E-mail or Password wrong!!");
                        }

                    } catch (Exception e) {

                    }

                    // Log in success
                    for (int i = 0; i <= feedDataList.size(); i++) {
                        try {

                            final String strstatus = String.valueOf(feedDataList.get(i).getString("status"));
                            final String strdata = String.valueOf(feedDataList.get(i).getString("data"));

                            if (strstatus.equals("ok")) {

                                ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed("[" + strdata + "]");
                                if (feedDataListData != null) {
                                    for (int j = 0; j <= feedDataListData.size(); j++) {
                                        try {

                                            String _UserId = String.valueOf(feedDataListData.get(j).getString("UserId"));
                                            String _CustomerId = String.valueOf(feedDataListData.get(j).getString("CustomerId"));
                                            String _Customer = String.valueOf(feedDataListData.get(j).getString("Customer"));

                                            ArrayList<JSONObject> feedDataListCustomer = CuteFeedJsonUtil.feed("[" + _Customer + "]");
                                            if (feedDataListCustomer != null) {
                                                for (int k = 0; k <= feedDataListCustomer.size(); k++) {
                                                    try {

                                                        String strFirstName = String.valueOf(feedDataListCustomer.get(k).getString("FirstName"));
                                                        String strLastName = String.valueOf(feedDataListCustomer.get(k).getString("LastName"));
                                                        String strBirthDate = String.valueOf(feedDataListCustomer.get(k).getString("BirthDate"));
                                                        String strCode = String.valueOf(feedDataListCustomer.get(k).getString("Code"));
                                                        String strCompany = String.valueOf(feedDataListCustomer.get(k).getString("Company"));
                                                        String strMobile = String.valueOf(feedDataListCustomer.get(k).getString("Mobile"));
                                                        String strGender = String.valueOf(feedDataListCustomer.get(k).getString("Gender"));
                                                        String strCustomerType = String.valueOf(feedDataListCustomer.get(k).getString("CustomerType"));
                                                        String strSupplierType = String.valueOf(feedDataListCustomer.get(k).getString("SupplierType"));

                                                        AccountDB.InsertAccount(_UserId, _CustomerId, strFirstName, strLastName, strBirthDate, strCode, strCompany, strMobile, strGender, strCustomerType, strSupplierType);

                                                        new FeedAsynTaskUpdateToken().execute(strWebServiceUrl + "UpdateTokenFCM", _UserId, _token);

                                                        switch (strSupplierType) {
                                                            case "SHOWROOM":
                                                                Intent intent = new Intent(getApplicationContext(), UsedCarListActivity.class);
                                                                intent.putExtra("UserId", _UserId);
                                                                intent.putExtra("CustomerId", _CustomerId);
                                                                startActivity(intent);
                                                                break;
                                                            case "TENT":
                                                                Intent intent1 = new Intent(getApplicationContext(), UsedCarSellingListActivity.class);
                                                                intent1.putExtra("UserId", _UserId);
                                                                intent1.putExtra("CustomerId", _CustomerId);
                                                                startActivity(intent1);
                                                                break;
                                                        }

                                                    }catch (Exception e) {

                                                    }
                                                }
                                            }

                                        }catch (Exception e) {

                                        }
                                    }
                                }

                            } else {
                                txtAlert.setVisibility(View.VISIBLE);
                                txtAlert.setText("E-mail or Password wrong!!");
                            }

                        } catch (Exception e) {

                        }
                    }

                } else {
                    //Toast.makeText(getActivity().getApplicationContext(), "User or Password worng!!", Toast.LENGTH_LONG).show();
                    txtAlert.setVisibility(View.VISIBLE);
                    txtAlert.setText("E-mail or Password wrong!!");


                }

            } else {
                //Toast.makeText(getActivity().getApplicationContext(), "Log in Fail!!", Toast.LENGTH_LONG).show();
                txtAlert.setVisibility(View.VISIBLE);
                txtAlert.setText("Log in Fail!!");
            }

            nDialog.dismiss();
        }
    }

    public class FeedAsynTaskUpdateToken extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(LoginActivity.this);
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
                        .add("userId", params[1])
                        .add("firebaseToken", params[2])
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

                feedDataListToken = CuteFeedJsonUtil.feed(s);
                if (feedDataListToken != null) {
                    for (int i = 0; i <= feedDataListToken.size(); i++) {

                        try {

                            String status = String.valueOf(feedDataListToken.get(i).getString("status"));

                            if (status.equals("ok")) {
                                Toast.makeText(getApplicationContext(), "Register token Success.", Toast.LENGTH_LONG).show();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("KEYCODE_BACK");

            moveTaskToBack(true);
        }
        return false;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //btnLogin.setEnabled(false);

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                btnLogin.setEnabled(true);
                txtRegister.setEnabled(true);
            }
        }

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //btnLogin.setEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}
