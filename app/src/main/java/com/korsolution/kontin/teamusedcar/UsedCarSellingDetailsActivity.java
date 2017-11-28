package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.korsolution.kontin.teamusedcar.activity.SlideshowDialogFragment;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UsedCarSellingDetailsActivity extends AppCompatActivity {

    private ImageView imgCar;
    private TextView txtCar;
    private TextView txtCurrentBid;
    private TextView txtPrice;
    private Button btnBid;
    private Button btnBuy;
    private TextView txtLastAuction;
    private TextView txtAuctionCount;
    private TextView txtDate;
    private TextView txtCountDown;
    private TextView txtYear;
    private TextView txtBrand;
    private TextView txtColorTest;
    private TextView txtColor;
    private TextView txtGearType;
    private TextView txtTitle;
    private TextView txtMiles;
    private TextView txtRepairHistory;
    private ImageView imgCar1;
    private ImageView imgCar2;
    private ImageView imgCar3;
    private ImageView imgCar4;
    private ImageView imgCar5;
    private ImageView imgCar6;
    private ImageView imgCar7;
    private ImageView imgCar8;
    private ImageView imgCar9;
    private ImageView imgCar10;
    private TextView txtProp;
    private TextView txtManagementFee;
    private TextView txtDeliveryCost;
    private ImageView imgYoutubeThumbnail;

    private String PKID;
    private String EndDate;
    private String Deposit;

    private String managementFee;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListTentDetail;

    private AccountDBClass AccountDB;

    private String strWebServiceUrl;

    long diff;
    long milliseconds;
    long endTime;

    private String bidPrice;

    private String[] mImages;

    private CarColorDBClass CarColorDB;
    protected ArrayList<JSONObject> feedDataListColor;

    private String UserId;
    private String CustomerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_car_selling_details);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        //getSupportActionBar().setTitle("");
        //getSupportActionBar().setDisplayUseLogoEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        UserId = getIntent().getStringExtra("UserId");
        CustomerId = getIntent().getStringExtra("CustomerId");
        PKID = getIntent().getStringExtra("PKID");
        EndDate = getIntent().getStringExtra("EndDate");
        Deposit = getIntent().getStringExtra("Deposit");

        AccountDB = new AccountDBClass(this);
        CarColorDB = new CarColorDBClass(this);

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        new FeedAsynTaskGetTentDetail().execute(strWebServiceUrl + "GetTentDetail", CustomerId);

        setupWidgets();

        loadUsedCarDetails();
        countDownDateTime();

        mImages = new String[11];

        new FeedAsynTaskColor().execute(strWebServiceUrl + "GetCarColor");
    }

    private void countDownDateTime() {

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy, HH:mm");
        formatter.setLenient(false);

        //String oldTime = "21.07.2017, 12:00";
        String oldTime = EndDate.replace("/", ".");   // 03/07/2017 14:29:14
        oldTime = oldTime.replace(" ", ", ");

        Date endDate;
        try {
            endDate = formatter.parse(oldTime);
            milliseconds = endDate.getTime();

        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final long[] startTime = {System.currentTimeMillis()};

        diff = milliseconds - startTime[0];

        CountDownTimer mCountDownTimer = new CountDownTimer(milliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                startTime[0] = startTime[0] - 1;
                Long serverUptimeSeconds =
                        (millisUntilFinished - startTime[0]) / 1000;

                String daysLeft = String.format("%d", serverUptimeSeconds / 86400);
                //txtBrand.setText(daysLeft);

                String hoursLeft = String.format("%d", (serverUptimeSeconds % 86400) / 3600);
                //txtTitle.setText(hoursLeft);

                String minutesLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) / 60);

                //txtMiles.setText(minutesLeft);

                String secondsLeft = String.format("%d", ((serverUptimeSeconds % 86400) % 3600) % 60);
                //txtRepairHistory.setText(secondsLeft);

                txtCountDown.setText("" + daysLeft + " วัน " + hoursLeft + " ชั่วโมง " + minutesLeft + " นาที " + secondsLeft + " วินาที");
            }

            @Override
            public void onFinish() {

                btnBid.setEnabled(false);
                btnBuy.setEnabled(false);

            }
        }.start();

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {

            loadUsedCarDetails();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    private void setupWidgets() {

        imgCar = (ImageView) findViewById(R.id.imgCar);
        txtCar = (TextView) findViewById(R.id.txtCar);
        txtCurrentBid = (TextView) findViewById(R.id.txtCurrentBid);
        txtPrice = (TextView) findViewById(R.id.txtPrice);
        btnBid = (Button) findViewById(R.id.btnBid);
        btnBuy = (Button) findViewById(R.id.btnBuy);
        txtLastAuction = (TextView) findViewById(R.id.txtLastAuction);
        txtAuctionCount = (TextView) findViewById(R.id.txtAuctionCount);
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtCountDown = (TextView) findViewById(R.id.txtCountDown);
        txtYear = (TextView) findViewById(R.id.txtYear);
        txtBrand = (TextView) findViewById(R.id.txtBrand);
        txtColorTest = (TextView) findViewById(R.id.txtColorTest);
        txtColor = (TextView) findViewById(R.id.txtColor);
        txtGearType = (TextView) findViewById(R.id.txtGearType);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        txtMiles = (TextView) findViewById(R.id.txtMiles);
        txtRepairHistory = (TextView) findViewById(R.id.txtRepairHistory);
        imgCar1 = (ImageView) findViewById(R.id.imgCar1);
        imgCar2 = (ImageView) findViewById(R.id.imgCar2);
        imgCar3 = (ImageView) findViewById(R.id.imgCar3);
        imgCar4 = (ImageView) findViewById(R.id.imgCar4);
        imgCar5 = (ImageView) findViewById(R.id.imgCar5);
        imgCar6 = (ImageView) findViewById(R.id.imgCar6);
        imgCar7 = (ImageView) findViewById(R.id.imgCar7);
        imgCar8 = (ImageView) findViewById(R.id.imgCar8);
        imgCar9 = (ImageView) findViewById(R.id.imgCar9);
        imgCar10 = (ImageView) findViewById(R.id.imgCar10);
        txtProp = (TextView) findViewById(R.id.txtProp);
        txtManagementFee = (TextView) findViewById(R.id.txtManagementFee);
        txtDeliveryCost = (TextView) findViewById(R.id.txtDeliveryCost);
        imgYoutubeThumbnail = (ImageView) findViewById(R.id.imgYoutubeThumbnail);

        btnBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialogAlertBid();

                Integer deposit = Integer.parseInt(Deposit);
                Integer ManagementFee = Integer.parseInt(managementFee);

                if (deposit >= ManagementFee) {
                    dialogAlertBid();
                } else {
                    dialogAlertDepositNotEnough();
                }

            }
        });

        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlertBuy();
            }
        });
    }

    private void loadUsedCarDetails() {

        new FeedAsynTask().execute(strWebServiceUrl + "GetCarDetail", CustomerId, PKID);
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarSellingDetailsActivity.this);
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
                        .add("customerId", params[1])
                        .add("carId", params[2])
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

                feedDataList = CuteFeedJsonUtil.feed(s);
                if (feedDataList != null) {
                    for (int i = 0; i < feedDataList.size(); i++) {
                        try {

                            String Status = String.valueOf(feedDataList.get(i).getString("status"));
                            String data = String.valueOf(feedDataList.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed("["+data+"]");
                            if (feedDataListData != null) {
                                for (int k = 0; k < feedDataListData.size(); k++) {
                                    String car = String.valueOf(feedDataListData.get(k).getString("car"));
                                    String prop_list = String.valueOf(feedDataListData.get(k).getString("prop_list"));
                                    String fee_list = String.valueOf(feedDataListData.get(k).getString("fee_list"));

                                    ArrayList<JSONObject> feedDataListCar = CuteFeedJsonUtil.feed("["+car+"]");
                                    if (feedDataListCar != null) {
                                        for (int j = 0; j < feedDataListCar.size(); j++) {
                                            String carId = String.valueOf(feedDataListCar.get(j).getString("carId"));
                                            String provId = String.valueOf(feedDataListCar.get(j).getString("provId"));
                                            String cover = String.valueOf(feedDataListCar.get(j).getString("cover"));
                                            String title = String.valueOf(feedDataListCar.get(j).getString("title"));
                                            String year = String.valueOf(feedDataListCar.get(j).getString("year"));
                                            String brand = String.valueOf(feedDataListCar.get(j).getString("brand"));
                                            String model = String.valueOf(feedDataListCar.get(j).getString("model"));
                                            String sub_model = String.valueOf(feedDataListCar.get(j).getString("sub_model"));
                                            String km = String.valueOf(feedDataListCar.get(j).getString("km"));
                                            String repair = String.valueOf(feedDataListCar.get(j).getString("repair"));
                                            String bid_price = String.valueOf(feedDataListCar.get(j).getString("bid_price"));
                                            String current_bid = String.valueOf(feedDataListCar.get(j).getString("current_bid"));
                                            String buy_price = String.valueOf(feedDataListCar.get(j).getString("buy_price"));
                                            String bid_count = String.valueOf(feedDataListCar.get(j).getString("bid_count"));
                                            String last_id = String.valueOf(feedDataListCar.get(j).getString("last_id"));
                                            String created = String.valueOf(feedDataListCar.get(j).getString("created"));
                                            String proplist = String.valueOf(feedDataListCar.get(j).getString("proplist"));
                                            String status = String.valueOf(feedDataListCar.get(j).getString("status"));
                                            String gear = String.valueOf(feedDataListCar.get(j).getString("gear"));
                                            String color = String.valueOf(feedDataListCar.get(j).getString("color"));
                                            final String img_front = String.valueOf(feedDataListCar.get(j).getString("img_front"));
                                            final String img45_v1 = String.valueOf(feedDataListCar.get(j).getString("img45_v1"));
                                            final String img45_v2 = String.valueOf(feedDataListCar.get(j).getString("img45_v2"));
                                            final String img_back = String.valueOf(feedDataListCar.get(j).getString("img_back"));
                                            final String img_engine = String.valueOf(feedDataListCar.get(j).getString("img_engine"));
                                            final String img_lever = String.valueOf(feedDataListCar.get(j).getString("img_lever"));
                                            final String img_inner1 = String.valueOf(feedDataListCar.get(j).getString("img_inner1"));
                                            final String img_inner2 = String.valueOf(feedDataListCar.get(j).getString("img_inner2"));
                                            final String img_inner3 = String.valueOf(feedDataListCar.get(j).getString("img_inner3"));
                                            final String img_inner4 = String.valueOf(feedDataListCar.get(j).getString("img_inner4"));
                                            final String img_inner5 = String.valueOf(feedDataListCar.get(j).getString("img_inner5"));
                                            final String youtube_url = String.valueOf(feedDataListCar.get(j).getString("youtube_url"));

                                            //mImages = new String[11];
                                            mImages[0] = img_front;
                                            mImages[1] = img45_v1;
                                            mImages[2] = img45_v2;
                                            mImages[3] = img_back;
                                            mImages[4] = img_engine;
                                            mImages[5] = img_lever;
                                            mImages[6] = img_inner1;
                                            mImages[7] = img_inner2;
                                            mImages[8] = img_inner3;
                                            mImages[9] = img_inner4;
                                            mImages[10] = img_inner5;

                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img_front)
                                                    .error(R.drawable.image_car)
                                                    .into(imgCar);

                                            txtCar.setText(year + " " + brand + " " + model + " " + sub_model);

                                            ///txtCurrentBid.setText(bid_price);
                                            //txtPrice.setText(buy_price);

                                            bidPrice = current_bid;

                                            //NumberFormat format = NumberFormat.getCurrencyInstance();
                                            //txtCurrentBid.setText(format.format(Double.parseDouble(bid_price)));
                                            //txtPrice.setText(format.format(Double.parseDouble(buy_price)));

                                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                                            //String yourFormattedString = formatter.format(100000);
                                            txtCurrentBid.setText(formatter.format(Integer.parseInt(current_bid)));
                                            txtPrice.setText(formatter.format(Integer.parseInt(buy_price)));

                                            txtLastAuction.setText(last_id);
                                            txtAuctionCount.setText(bid_count);


                                            txtYear.setText("ปี : " + year);
                                            txtBrand.setText("รถยนต์ : " + " " + brand + " " + model + " " + sub_model);
                                            txtTitle.setText("หัวข้อ : " + title);
                                            //txtMiles.setText("เลขไมล์ : " + km);
                                            txtMiles.setText("เลขไมล์ : " + formatter.format(Integer.parseInt(km)));
                                            txtRepairHistory.setText("ประวัติการซ่อม : " + repair);

                                            // Cut String Date Time
                                            String[] separated = created.split("-");
                                            String[] day = separated[2].split("T");
                                            String[] time = day[1].split("\\.");
                                            //String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + day[1];
                                            String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + time[0];

                                            txtDate.setText("เริ่ม : " + dateTime);

                                            String[] arrColorCode = CarColorDB.SelectColorCodeByColorName(color);
                                            if (arrColorCode != null) {
                                                txtColorTest.setBackgroundColor(Color.parseColor(arrColorCode[0]));
                                            }
                                            txtColor.setText("สี" + color);
                                            txtGearType.setText(gear);

                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img45_v1)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar1);
                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img45_v2)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar2);
                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img_back)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar3);
                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img_engine)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar4);
                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img_lever)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar5);
                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img_inner1)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar6);
                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img_inner2)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar7);
                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img_inner3)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar8);
                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img_inner4)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar9);
                                            // set Image
                                            Glide.with(UsedCarSellingDetailsActivity.this)
                                                    .load(img_inner5)
                                                    .error(R.drawable.blank_img)
                                                    .into(imgCar10);

                                            imgCar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img_front);

                                                    //dialogAlertViewImageCar(0);

                                                    goViewImages(0);
                                                }
                                            });
                                            imgCar1.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img45_v1);

                                                    //dialogAlertViewImageCar(1);

                                                    goViewImages(1);
                                                }
                                            });
                                            imgCar2.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img45_v2);

                                                    //dialogAlertViewImageCar(2);

                                                    goViewImages(2);
                                                }
                                            });
                                            imgCar3.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img_back);

                                                    //dialogAlertViewImageCar(3);

                                                    goViewImages(3);
                                                }
                                            });
                                            imgCar4.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img_engine);

                                                    //dialogAlertViewImageCar(4);

                                                    goViewImages(4);
                                                }
                                            });
                                            imgCar5.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img_lever);

                                                    //dialogAlertViewImageCar(5);

                                                    goViewImages(5);
                                                }
                                            });
                                            imgCar6.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img_inner1);

                                                    //dialogAlertViewImageCar(6);

                                                    goViewImages(6);
                                                }
                                            });
                                            imgCar7.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img_inner2);

                                                    //dialogAlertViewImageCar(7);

                                                    goViewImages(7);
                                                }
                                            });
                                            imgCar8.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img_inner3);

                                                    //dialogAlertViewImageCar(8);

                                                    goViewImages(8);
                                                }
                                            });
                                            imgCar9.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img_inner4);

                                                    //dialogAlertViewImageCar(9);

                                                    goViewImages(9);
                                                }
                                            });
                                            imgCar10.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //Toast.makeText(getApplicationContext(), img45_v1, Toast.LENGTH_LONG).show();
                                                    //dialogAlertViewCarImage(img_inner5);

                                                    //dialogAlertViewImageCar(10);

                                                    goViewImages(10);
                                                }
                                            });

                                            if (youtube_url.length() > 0) {

                                                try {

                                                    setImgThumbnail(youtube_url);

                                                } catch (Exception e) {

                                                }

                                                imgYoutubeThumbnail.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtube_url)));
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    // [ "Property 1", "Property 2" ]
                                    //Toast.makeText(getApplicationContext(), prop_list, Toast.LENGTH_LONG).show();
                                    prop_list = prop_list.replace("[", "");
                                    prop_list = prop_list.replace("]", "");
                                    prop_list = prop_list.replace("\"", "");
                                    //Toast.makeText(getApplicationContext(), prop_list, Toast.LENGTH_LONG).show();
                                    txtProp.setText("ข้อมูลรถ : " + prop_list);
                                    /*if (prop_list.contains(",")) {
                                        String[] separated = prop_list.split(",");
                                        txtProp.setText();
                                    }*/

                                    // fee1 ค่าธรรมเนียมการจัดการ, fee2 ค่าขนส่ง
                                    ArrayList<JSONObject> feedDataListFee = CuteFeedJsonUtil.feed("["+fee_list+"]");
                                    if (feedDataListFee != null) {
                                        for (int j = 0; j <= feedDataListFee.size(); j++) {
                                            String fee1 = String.valueOf(feedDataListFee.get(j).getString("fee1"));
                                            String fee2 = String.valueOf(feedDataListFee.get(j).getString("fee2"));

                                            //txtManagementFee.setText("ค่าธรรมเนียมการจัดการ : " + fee1);
                                            //txtDeliveryCost.setText("ค่าขนส่ง : " + fee2);

                                            managementFee = fee1;

                                            DecimalFormat formatter = new DecimalFormat("#,###,###");
                                            txtManagementFee.setText(formatter.format(Integer.parseInt(fee1)));
                                            txtDeliveryCost.setText(formatter.format(Integer.parseInt(fee2)));
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_LONG).show();
            }

            nDialog.dismiss();
        }
    }

    private void goViewImages(Integer position) {

        Bundle bundle = new Bundle();
        bundle.putStringArray("mImages", mImages);
        bundle.putInt("position", position);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
        newFragment.setArguments(bundle);
        newFragment.show(ft, "slideshow");
    }

    public void dialogAlertDepositNotEnough() {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(UsedCarSellingDetailsActivity.this);

        completeDialog.setTitle("เงินฝากของคุณไม่เพียงพอ!!");
        //completeDialog.setMessage(_message);
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

            }
        })/*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Fail!!" ,Toast.LENGTH_LONG).show();
            }
        })*/;
        completeDialog.show();
    }

    public void dialogAlertBid() {

        AlertDialog.Builder CheckDialog = new AlertDialog.Builder(UsedCarSellingDetailsActivity.this);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_price, (ViewGroup) findViewById(R.id.layout_dialog));

        loadUsedCarDetails();

        TextView txtNote = (TextView) Viewlayout.findViewById(R.id.txtNote);
        final EditText edtPrice = (EditText) Viewlayout.findViewById(R.id.edtPrice);

        final DecimalFormat formatter = new DecimalFormat("#,###,###");

        txtNote.setText("กรุณาใส่ราคาประมูล " + String.valueOf(formatter.format(Integer.parseInt(bidPrice) + 5000)) + " ขึ้นไป");

        CheckDialog.setTitle("เสนอราคาประมูล");
        CheckDialog.setView(Viewlayout);

        // Button OK
        CheckDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String price = edtPrice.getText().toString();

                if (price.length() > 0) {
                    Integer intBidPrice = Integer.parseInt(bidPrice);
                    Integer intPrice = Integer.parseInt(price);

                    if (intPrice >= intBidPrice + 5000) {
                        //Toast.makeText(getApplicationContext(), format.format(intPrice), Toast.LENGTH_LONG).show();

                        bidUsedCar(price);

                    } else {
                        Toast.makeText(getApplicationContext(), "กรุณาใส่ราคาประมูลมากกว่า " + String.valueOf(formatter.format(Integer.parseInt(bidPrice) + 5000)), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "กรุณาใส่ราคาที่ต้องการประมูล", Toast.LENGTH_LONG).show();
                }


            }
        })
                // Button Cancel
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                });
        CheckDialog.create();
        CheckDialog.show();
    }

    public void dialogAlertBuy() {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(UsedCarSellingDetailsActivity.this);

        completeDialog.setTitle("คุณต้องการซื้อรถด้วยใช่หรือไม่?");
        completeDialog.setMessage(txtBrand.getText().toString() + " ด้วยราคา " + txtPrice.getText().toString() + " บาท");
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                buyUsedCar();

            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Fail!!" ,Toast.LENGTH_LONG).show();
            }
        });
        completeDialog.show();
    }

    private void bidUsedCar(String price) {

        String[][] arrData = AccountDB.SelectAllAccount();
        if (arrData != null) {
            String UserId = arrData[0][1].toString();
            String CustomerId = arrData[0][2].toString();

            new FeedAsynTaskBid().execute(strWebServiceUrl + "TentBid", PKID, CustomerId, price);
        }
    }

    private void buyUsedCar() {

        String price = txtPrice.getText().toString();

        String[][] arrData = AccountDB.SelectAllAccount();
        if (arrData != null) {
            String UserId = arrData[0][1].toString();
            String CustomerId = arrData[0][2].toString();

            new FeedAsynTaskBuy().execute(strWebServiceUrl + "TentBuy", PKID, CustomerId, price);
        }
    }

    public class FeedAsynTaskBid extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarSellingDetailsActivity.this);
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
                        .add("carId", params[1])
                        .add("customerId", params[2])
                        .add("price", params[3])
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

                            String status = String.valueOf(feedDataList.get(i).getString("status"));
                            String transaction = String.valueOf(feedDataList.get(i).getString("transaction"));

                            if (status.equals("ok")) {
                                dialogAlertSuccess("ประมูลเรียบร้อย");
                            }


                        } catch (Exception e) {

                        }

                        try {

                            String status = String.valueOf(feedDataList.get(i).getString("status"));
                            String info = String.valueOf(feedDataList.get(i).getString("info"));

                            if (status.equals("fail")) {
                                dialogAlertFail("ประมูลผิดพลาด");
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

    public class FeedAsynTaskBuy extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarSellingDetailsActivity.this);
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
                        .add("carId", params[1])
                        .add("customerId", params[2])
                        .add("price", params[3])
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

                            String status = String.valueOf(feedDataList.get(i).getString("status"));
                            String transaction = String.valueOf(feedDataList.get(i).getString("transaction"));

                            if (status.equals("ok")) {
                                dialogAlertSuccess("ซื้อสำเร็จ");
                            }


                        } catch (Exception e) {

                        }

                        try {

                            String status = String.valueOf(feedDataList.get(i).getString("status"));
                            String info = String.valueOf(feedDataList.get(i).getString("info"));

                            if (status.equals("fail")) {
                                dialogAlertFail("ซื้อไม่สำเร็จ");
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

    public void dialogAlertSuccess(String title) {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(UsedCarSellingDetailsActivity.this);

        completeDialog.setTitle(title);
        //completeDialog.setMessage(_message);
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                String[][] arrData = AccountDB.SelectAllAccount();
                if (arrData != null) {
                    String UserId = arrData[0][1].toString();
                    String CustomerId = arrData[0][2].toString();

                    Intent intent = new Intent(getApplicationContext(), UsedCarSellingListActivity.class);
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

    public void dialogAlertFail(String title) {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(UsedCarSellingDetailsActivity.this);

        completeDialog.setTitle(title);
        completeDialog.setMessage("กรุณาลองใหม่อีกครั้ง!!");
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();


            }
        })/*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Fail!!" ,Toast.LENGTH_LONG).show();
            }
        })*/;
        completeDialog.show();
    }

    public void dialogAlertViewCarImage(String pathImg) {

        AlertDialog.Builder CheckDialog = new AlertDialog.Builder(UsedCarSellingDetailsActivity.this);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_car_img, (ViewGroup) findViewById(R.id.layout_dialog));

        ImageView imgCar = (ImageView) Viewlayout.findViewById(R.id.imgCar);
        ImageView imgClose = (ImageView) Viewlayout.findViewById(R.id.imgClose);

        // set Image
        Glide.with(UsedCarSellingDetailsActivity.this)
                .load(pathImg)
                .into(imgCar);

        CheckDialog.setView(Viewlayout);

        // Button OK
        CheckDialog.setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        })
                // Button Cancel
                /*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                })*/;

        CheckDialog.create();
        CheckDialog.show();
    }

    public void dialogAlertViewImageCar(Integer currentItem) {

        AlertDialog.Builder CheckDialog = new AlertDialog.Builder(UsedCarSellingDetailsActivity.this);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_img_car, (ViewGroup) findViewById(R.id.layout_dialog));

        ViewPager viewPager = (ViewPager) Viewlayout.findViewById(R.id.view_pager);
        ImagePagerAdapter adapter = new ImagePagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentItem);

        // set Image
        /*Glide.with(UsedCarSellingDetailsActivity.this)
                .load(pathImg)
                .into(imgCar);*/

        CheckDialog.setView(Viewlayout);

        // Button OK
        /*CheckDialog.setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        })
        // Button Cancel
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    }
                })*/;

        CheckDialog.create();
        CheckDialog.show();
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

                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    try {

                                        String color_name = String.valueOf(feedDataListData.get(j).getString("color_name"));
                                        String color_code = String.valueOf(feedDataListData.get(j).getString("color_code"));

                                        CarColorDB.Insert(color_name, color_code);

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

            //nDialog.dismiss();
        }
    }

    private class ImagePagerAdapter extends PagerAdapter {
        /*private int[] mImages = new int[] {
                R.drawable.blank_img,
                R.drawable.blank_img,
                R.drawable.blank_img,
                R.drawable.blank_img
        };*/

        @Override
        public int getCount() {
            return mImages.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageView) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Context context = UsedCarSellingDetailsActivity.this;
            ImageView imageView = new ImageView(context);
            int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium);
            imageView.setPadding(padding, padding, padding, padding);
            //imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //imageView.setImageResource(mImages[position]);

            imageView.setAdjustViewBounds(true);
            Glide.with(UsedCarSellingDetailsActivity.this)
                    .load(mImages[position])
                    .error(R.drawable.blank_img)
                    .into(imageView);

            ((ViewPager) container).addView(imageView, 0);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((ImageView) object);
        }
    }

    public class FeedAsynTaskGetTentDetail extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarSellingDetailsActivity.this);
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
                        .add("customerId", params[1])
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

                feedDataListTentDetail = CuteFeedJsonUtil.feed(s);
                if (feedDataListTentDetail != null) {
                    for (int i = 0; i <= feedDataListTentDetail.size(); i++) {
                        try {

                            String status = String.valueOf(feedDataListTentDetail.get(i).getString("status"));
                            String data = String.valueOf(feedDataListTentDetail.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed("[" + data + "]");
                            if (feedDataListData != null) {
                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    String showroom = String.valueOf(feedDataListData.get(j).getString("showroom"));
                                    String star = String.valueOf(feedDataListData.get(j).getString("star"));
                                    String balance = String.valueOf(feedDataListData.get(j).getString("balance"));
                                    String car_bid = String.valueOf(feedDataListData.get(j).getString("car_bid"));

                                    Deposit = balance;
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_LONG).show();
            }

            nDialog.dismiss();
        }
    }

    private void setImgThumbnail(String youtubeID) {

        youtubeID = getVideoId(youtubeID);

        final String youtubeThumnailURL = String.format("http://img.youtube.com/vi/%s/hqdefault.jpg", youtubeID);

        Glide.with(UsedCarSellingDetailsActivity.this)
                .load(youtubeThumnailURL)
                .into(imgYoutubeThumbnail);
    }

    public static String getVideoId(String watchLink){

        return watchLink.substring(watchLink.length() - 11);
    }
}
