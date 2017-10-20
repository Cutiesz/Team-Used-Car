package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AddUsedCar8PriceActivity extends AppCompatActivity {

    private ImageView imgCar;
    private TextView txtLicensePlate;
    private TextView txtYear;
    private TextView txtBrand;
    private TextView txtColorTest;
    private TextView txtColor;
    private TextView txtGearType;
    private TextView txtMiles;
    private TextView txtDetails;
    private TextView txtRepairHistory;
    private TextView txtProperties;
    private TextView txtTitle;
    private EditText edtPhoneNumber;
    private EditText edtStartPrice;
    private EditText edtPrice;
    private Button btnAddUsedCar;

    private String YoutubeLink;
    private String LicensePlateFront;
    private String LicensePlateBack;
    private String LicensePlateProvince;
    private String ProvinceID;
    private String CarYear;
    private String CarBrand;
    private String CarGeneration;
    private String CarSubGeneration;
    private String CarColor;
    private String CarGearType;
    private String Title;
    private String Miles;
    private String CarDetails;
    private String RepairHistory;
    private String CheckList;
    private String StrProperties;

    private PictureUsedCarPathDBClass PictureUsedCarPathDB;

    protected ArrayList<JSONObject> feedDataList;

    private AccountDBClass AccountDB;

    ProgressDialog mProgressDialog;

    private CarColorDBClass CarColorDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_used_car_price);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        YoutubeLink = getIntent().getStringExtra("YoutubeLink");
        LicensePlateFront = getIntent().getStringExtra("LicensePlateFront");
        LicensePlateBack = getIntent().getStringExtra("LicensePlateBack");
        LicensePlateProvince = getIntent().getStringExtra("LicensePlateProvince");
        ProvinceID = getIntent().getStringExtra("ProvinceID");
        CarYear = getIntent().getStringExtra("CarYear");
        CarBrand = getIntent().getStringExtra("CarBrand");
        CarGeneration = getIntent().getStringExtra("CarGeneration");
        CarSubGeneration = getIntent().getStringExtra("CarSubGeneration");
        CarColor = getIntent().getStringExtra("CarColor");
        CarGearType = getIntent().getStringExtra("CarGearType");
        Title = getIntent().getStringExtra("Title");
        Miles = getIntent().getStringExtra("Miles");
        CarDetails = getIntent().getStringExtra("CarImages");
        RepairHistory = getIntent().getStringExtra("RepairHistory");
        CheckList = getIntent().getStringExtra("CheckList");
        StrProperties = getIntent().getStringExtra("StrProperties");

        PictureUsedCarPathDB = new PictureUsedCarPathDBClass(this);
        AccountDB = new AccountDBClass(this);
        CarColorDB = new CarColorDBClass(this);

        setupWidgets();
        initial();
    }

    private void setupWidgets() {

        imgCar = (ImageView) findViewById(R.id.imgCar);
        txtLicensePlate = (TextView) findViewById(R.id.txtLicensePlate);
        txtYear = (TextView) findViewById(R.id.txtYear);
        txtBrand = (TextView) findViewById(R.id.txtBrand);
        txtColorTest = (TextView) findViewById(R.id.txtColorTest);
        txtColor = (TextView) findViewById(R.id.txtColor);
        txtGearType = (TextView) findViewById(R.id.txtGearType);
        txtMiles = (TextView) findViewById(R.id.txtMiles);
        txtDetails = (TextView) findViewById(R.id.txtDetails);
        txtRepairHistory = (TextView) findViewById(R.id.txtRepairHistory);
        txtProperties = (TextView) findViewById(R.id.txtProperties);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        edtPhoneNumber = (EditText) findViewById(R.id.edtPhoneNumber);
        edtStartPrice = (EditText) findViewById(R.id.edtStartPrice);
        edtPrice = (EditText) findViewById(R.id.edtPrice);
        btnAddUsedCar = (Button) findViewById(R.id.btnAddUsedCar);

        btnAddUsedCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                btnAddUsedCar.setEnabled(false);

                String phoneNumber = edtPhoneNumber.getText().toString();
                String startPrice = edtStartPrice.getText().toString();
                String price = edtPrice.getText().toString();

                if (phoneNumber.length() > 0) {
                    if (startPrice.length() > 0) {
                        if (price.length() > 0) {

                            mProgressDialog = new ProgressDialog(AddUsedCar8PriceActivity.this);
                            mProgressDialog.setMessage("กำลังส่งข้อมูล ...");
                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.show();

                            addCar(phoneNumber, startPrice, price);

                        } else {
                            Toast.makeText(getApplicationContext(), "กรุณากรอกราคาขาย", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "กรุณากรอกราคาเริ่มต้น", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "กรุณากรอกหมายเลขโทรศัพท์", Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void initial() {

        String[][] arrData = PictureUsedCarPathDB.SelectAll();
        if (arrData != null) {
            String uriCarFront = arrData[0][1].toString();

            Glide.with(this)
                    .load(uriCarFront)
                    .into(imgCar);
        }

        DecimalFormat formatter = new DecimalFormat("#,###,###");

        txtLicensePlate.setText("ทะเบียน : " + LicensePlateFront + " " + LicensePlateBack + " " + LicensePlateProvince);
        txtYear.setText("ปี : " + CarYear);
        txtBrand.setText("รถยนต์ : " + CarBrand + " " + CarGeneration + " " + CarSubGeneration);
        txtColor.setText("สี" + CarColor);
        txtGearType.setText(CarGearType);
        txtMiles.setText("เลขไมค์ : " + formatter.format(Integer.parseInt(Miles)));
        txtDetails.setText("รายละเอียด : " + CarDetails);
        txtRepairHistory.setText("ประวัติการซ่อมแซม : " + RepairHistory);
        txtProperties.setText("รายละเอียด : " + StrProperties);
        txtTitle.setText("หัวข้อ : " + Title);

        String[] arrColorCode = CarColorDB.SelectColorCodeByColorName(CarColor);
        if (arrColorCode != null) {
            txtColorTest.setBackgroundColor(Color.parseColor(arrColorCode[0]));
        }

    }

    private String getImageString(String pathImage) {

        String img_str = "";

        try {

            Uri imgUri = Uri.parse("file://" + pathImage);
            Bitmap mPhotoBitMap = BitmapHelper.readBitmap(AddUsedCar8PriceActivity.this, imgUri);
            if (mPhotoBitMap != null) {
                mPhotoBitMap = BitmapHelper.shrinkBitmap(mPhotoBitMap, 500,	0);
            }
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mPhotoBitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] image = stream.toByteArray();
            img_str = Base64.encodeToString(image, 0);

        } catch (Exception e) {

        }

        return img_str;
    }

    private void addCar(String phoneNumber, String startPrice, String price) {

        String userId = "";

        String[][] arrDataAcc = AccountDB.SelectAllAccount();
        if (arrDataAcc != null) {
            userId = arrDataAcc[0][1].toString();
        }

        String imgStrCarFront = "";
        String imgStrCarFrontLeft = "";
        String imgStrCarFrontRight = "";
        String imgStrCarBack = "";
        String imgStrCarEngine = "";
        String imgStrCarBeam = "";
        String imgStrCarInner1 = "";
        String imgStrCarInner2 = "";
        String imgStrCarInner3 = "";
        String imgStrCarInner4 = "";
        String imgStrCarInner5 = "";
        String imgStrCarDoc1 = "";
        String imgStrCarDoc2 = "";
        String imgStrCarDoc3 = "";
        String imgStrCarDoc4 = "";
        String imgStrCarDoc5 = "";

        String[][] arrData = PictureUsedCarPathDB.SelectAll();
        if (arrData != null) {
            String uriCarFront = arrData[0][1].toString();
            String uriCarFrontLeft = arrData[0][2].toString();
            String uriCarFrontRight = arrData[0][3].toString();
            String uriCarBack = arrData[0][4].toString();
            String uriCarEngine = arrData[0][5].toString();
            String uriCarBeam = arrData[0][6].toString();
            String uriCarInner1 = arrData[0][7].toString();
            String uriCarInner2 = arrData[0][8].toString();
            String uriCarInner3 = arrData[0][9].toString();
            String uriCarInner4 = arrData[0][10].toString();
            String uriCarInner5 = arrData[0][11].toString();
            String uriCarDoc1 = arrData[0][12].toString();
            String uriCarDoc2 = arrData[0][13].toString();
            String uriCarDoc3 = arrData[0][14].toString();
            String uriCarDoc4 = arrData[0][15].toString();
            String uriCarDoc5 = arrData[0][16].toString();

            imgStrCarFront = getImageString(uriCarFront);
            imgStrCarFrontLeft = getImageString(uriCarFrontLeft);
            imgStrCarFrontRight = getImageString(uriCarFrontRight);
            imgStrCarBack = getImageString(uriCarBack);
            imgStrCarEngine = getImageString(uriCarEngine);
            imgStrCarBeam = getImageString(uriCarBeam);
            imgStrCarInner1 = getImageString(uriCarInner1);
            imgStrCarInner2 = getImageString(uriCarInner2);
            imgStrCarInner3 = getImageString(uriCarInner3);
            imgStrCarInner4 = getImageString(uriCarInner4);
            imgStrCarInner5 = getImageString(uriCarInner5);
            imgStrCarDoc1 = getImageString(uriCarDoc1);
            imgStrCarDoc2 = getImageString(uriCarDoc2);
            imgStrCarDoc3 = getImageString(uriCarDoc3);
            imgStrCarDoc4 = getImageString(uriCarDoc4);
            imgStrCarDoc5 = getImageString(uriCarDoc5);


            String license = LicensePlateFront + " " + LicensePlateBack + " " + LicensePlateProvince;

            String strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

            new FeedAsynTask().execute(strWebServiceUrl + "ShowRoomAdd", userId, Title, license,
                    CarBrand, CarGeneration, CarSubGeneration, CarYear, phoneNumber, CarDetails,
                    startPrice, price, CheckList, Miles, RepairHistory, ProvinceID, CarColor, CarGearType,
                    imgStrCarFront, imgStrCarFrontLeft, imgStrCarFrontRight, imgStrCarBack, imgStrCarEngine, imgStrCarBeam,
                    imgStrCarInner1, imgStrCarInner2, imgStrCarInner3, imgStrCarInner4, imgStrCarInner5,
                    imgStrCarDoc1, imgStrCarDoc2, imgStrCarDoc3, imgStrCarDoc4, imgStrCarDoc5, YoutubeLink);
        }
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        //private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*nDialog = new ProgressDialog(AddUsedCar8PriceActivity.this);
            nDialog.setMessage("Loading..");
            //nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();*/

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
                        .add("userId", params[1])
                        .add("title", params[2])
                        .add("license", params[3])
                        .add("brand", params[4])
                        .add("model", params[5])
                        .add("subModel", params[6])
                        .add("year", params[7])
                        .add("mobile", params[8])
                        .add("info", params[9])
                        .add("bidPrice", params[10])
                        .add("buyPrice", params[11])
                        .add("checkList", params[12])
                        .add("km", params[13])
                        .add("repair", params[14])
                        .add("provinceID", params[15])
                        .add("color", params[16])
                        .add("gearType", params[17])
                        .add("imgFront", params[18])
                        .add("img45v1", params[19])
                        .add("img45v2", params[20])
                        .add("imgBack", params[21])
                        .add("imgEngine", params[22])
                        .add("imgLever", params[23])
                        .add("imgInner1", params[24])
                        .add("imgInner2", params[25])
                        .add("imgInner3", params[26])
                        .add("imgInner4", params[27])
                        .add("imgInner5", params[28])
                        .add("imgDoc1", params[29])
                        .add("imgDoc2", params[30])
                        .add("imgDoc3", params[31])
                        .add("imgDoc4", params[32])
                        .add("imgDoc5", params[33])
                        .add("youtubeUrl", params[34])
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

                //{ "status": "ok", "PKID": "0cf27fdd-8aeb-4c84-be17-105510446847" }

                feedDataList = CuteFeedJsonUtil.feed(s);
                if (feedDataList != null) {
                    for (int i = 0; i <= feedDataList.size(); i++) {
                        try {

                            String strstatus = String.valueOf(feedDataList.get(i).getString("status"));
                            String strPKID = String.valueOf(feedDataList.get(i).getString("PKID"));

                            if (strstatus.equals("ok")) {
                                dialogAlertSuccess();
                            }


                        } catch (Exception e) {

                        }

                        try {

                            String strstatus = String.valueOf(feedDataList.get(i).getString("status"));
                            //String strmsg = String.valueOf(feedDataList.get(i).getString("msg"));

                            if (strstatus.equals("error")) {
                                dialogAlertFail();
                            }


                        } catch (Exception e) {

                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No Data!!", Toast.LENGTH_LONG).show();
                    btnAddUsedCar.setEnabled(true);
                }

            } else {
                Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_LONG).show();
                btnAddUsedCar.setEnabled(true);
            }

            //nDialog.dismiss();
            mProgressDialog.dismiss();
        }
    }

    public void dialogAlertSuccess() {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(AddUsedCar8PriceActivity.this);
        completeDialog.setCancelable(false);

        completeDialog.setTitle("เพิ่มรถสำเร็จ");
        //completeDialog.setMessage(_message);
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                PictureUsedCarPathDB.Delete();

                String[][] arrData = AccountDB.SelectAllAccount();
                if (arrData != null) {
                    String UserId = arrData[0][1].toString();
                    String CustomerId = arrData[0][2].toString();

                    Intent intent = new Intent(getApplicationContext(), UsedCarListActivity.class);
                    intent.putExtra("UserId", UserId);
                    intent.putExtra("CustomerId", CustomerId);
                    startActivity(intent);
                }
            }
        })/*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Fail!!" ,Toast.LENGTH_LONG).show();
            }
        })*/;
        completeDialog.show();
    }

    public void dialogAlertFail() {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(AddUsedCar8PriceActivity.this);

        completeDialog.setTitle("เพิ่มรถไม่สำเร็จ");
        completeDialog.setMessage("กรุณาลองใหม่อีกครั้ง!!");
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
                btnAddUsedCar.setEnabled(true);

            }
        })/*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Fail!!" ,Toast.LENGTH_LONG).show();
            }
        })*/;
        completeDialog.show();
    }
}
