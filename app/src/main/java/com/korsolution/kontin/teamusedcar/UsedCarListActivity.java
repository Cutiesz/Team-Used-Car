package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.anton46.stepsview.StepsView;
import com.bumptech.glide.Glide;
import com.korsolution.kontin.teamusedcar.activity.ShowroomTabActivity;
import com.paginate.Paginate;
import com.paginate.abslistview.LoadingListItemCreator;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import co.omise.android.models.Token;
import co.omise.android.ui.CreditCardActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UsedCarListActivity extends AppCompatActivity {

    private TextView txtName;
    private ImageView imgStar1;
    private ImageView imgStar2;
    private ImageView imgStar3;
    private ImageView imgStar4;
    private ImageView imgStar5;
    private TextView txtDeposit;
    private TextView txtAuctioned;
    private TextView txtSoldOut;
    private SwipeRefreshLayout mRefreshView;
    private ListView lvUsedCar;
    private TextView txtNoData;
    private FloatingActionButton fabAdd;
    private ImageButton imgBtnDeposit;
    private Button btnDesiredCar;

    private FeedNewsListViewAdapter mAdapter;
    final int delay = 1000;
    private boolean mIsLoading = true;
    private boolean hasLoadedAll = false;
    private int mPageIndex = 1;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListUsedCar;
    protected ArrayList<JSONObject> feedDataListShowroomDetails;

    private String UserId;
    private String CustomerId;

    private AccountDBClass AccountDB;

    String strWebServiceUrl;

    // Omise
    //private static final String OMISE_PKEY = "pkey_test_5927y4ui9k4lx17wmuz";
    private static final String OMISE_PKEY = "pkey_test_59ccnhqxthw3rt925fd";
    private static final int REQUEST_CC = 100;

    private String addDeposit;

    protected ArrayList<JSONObject> feedDataListToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_car_list);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        //getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        UserId = getIntent().getStringExtra("UserId");
        CustomerId = getIntent().getStringExtra("CustomerId");

        AccountDB = new AccountDBClass(this);

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        setupWidgets();

        loadData();
        loadShowroomDetails();

        Intent intent = new Intent(getApplicationContext(), ShowroomTabActivity.class);
        intent.putExtra("UserId", UserId);
        intent.putExtra("CustomerId", CustomerId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {

            dialogAlertLogOut();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dialogAlertLogOut(){
        AlertDialog.Builder mDialog = new AlertDialog.Builder(UsedCarListActivity.this);

        mDialog.setTitle("คุณต้องการออกจากระบบใช่หรือไม่?");
        //mDialog.setIcon(R.drawable.ic_action_close);
        mDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                AccountDB.DeleteAccount();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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

    private void setupWidgets() {

        txtName = (TextView) findViewById(R.id.txtName);
        imgStar1 = (ImageView) findViewById(R.id.imgStar1);
        imgStar2 = (ImageView) findViewById(R.id.imgStar2);
        imgStar3 = (ImageView) findViewById(R.id.imgStar3);
        imgStar4 = (ImageView) findViewById(R.id.imgStar4);
        imgStar5 = (ImageView) findViewById(R.id.imgStar5);
        txtDeposit = (TextView) findViewById(R.id.txtDeposit);
        txtAuctioned = (TextView) findViewById(R.id.txtAuctioned);
        txtSoldOut = (TextView) findViewById(R.id.txtSoldOut);
        mRefreshView = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        lvUsedCar = (ListView) findViewById(R.id.lvUsedCar);
        txtNoData = (TextView) findViewById(R.id.txtNoData);
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        imgBtnDeposit = (ImageButton) findViewById(R.id.imgBtnDeposit);
        btnDesiredCar = (Button) findViewById(R.id.btnDesiredCar);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddUsedCar1LicensePlateActivity.class);
                startActivity(intent);
            }
        });

        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_bright);
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mDataArray.clear();
                feedDataList.clear();
                mAdapter = new FeedNewsListViewAdapter();
                mAdapter.notifyDataSetChanged();
                mPageIndex = 1;
                hasLoadedAll = false;

                // reload
                mIsLoading = true;
                loadData();
                loadShowroomDetails();
            }
        });

        imgBtnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAlertDeposit();
            }
        });

        btnDesiredCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DesiredCarListShowroomActivity.class);
                intent.putExtra("UserId", UserId);
                intent.putExtra("CustomerId", CustomerId);
                startActivity(intent);
            }
        });
    }

    private void loadShowroomDetails() {

        new FeedAsynTaskGetShowroomDetail().execute(strWebServiceUrl + "GetShowroomDetail", /*"29"*/CustomerId);
    }

    private void loadData() {

        // type
        //1 = รถที่พึ่งลงและรถที่กำลังประมูล
        //2 = รถที่รอชำระเงิน
        //3 = รถที่ขาย/ประมูลแล้ว

        new FeedAsynTask().execute(strWebServiceUrl + "ListShowRoomCarByOwner", /*"29"*/CustomerId, "1");
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarListActivity.this);
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
                        .add("type", params[2])
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

                mRefreshView.setRefreshing(false);

                feedDataListUsedCar = CuteFeedJsonUtil.feed(s);
                if (feedDataListUsedCar != null) {
                    for (int i = 0; i <= feedDataListUsedCar.size(); i++) {
                        try {

                            String status = String.valueOf(feedDataListUsedCar.get(i).getString("status"));
                            String data = String.valueOf(feedDataListUsedCar.get(i).getString("data"));

                            feedDataList = CuteFeedJsonUtil.feed(data);
                            if (feedDataList != null) {

                                mAdapter = new FeedNewsListViewAdapter();
                                lvUsedCar.setAdapter(mAdapter);

                                // load more
                                Paginate.with(lvUsedCar, callbacks)
                                        .setLoadingTriggerThreshold(2)
                                        .addLoadingListItem(true)
                                        .setLoadingListItemCreator(new CustomLoadingListItemCreator())
                                        .build();

                                mIsLoading = false;
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

    public class FeedNewsListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return feedDataList.size();
        }

        @Override
        public Object getItem(int position) {
            return feedDataList.size();
        }

        @Override
        public long getItemId(int position) {
            return feedDataList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_item_used_car, null);
                holder = new ViewHolder();

                holder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
                holder.imgCar = (ImageView) convertView.findViewById(R.id.imgCar);
                holder.txtBrand = (TextView) convertView.findViewById(R.id.txtBrand);
                holder.txtLicensePlate = (TextView) convertView.findViewById(R.id.txtLicensePlate);
                holder.txtStartPrice = (TextView) convertView.findViewById(R.id.txtStartPrice);
                holder.txtBidPrice = (TextView) convertView.findViewById(R.id.txtBidPrice);
                holder.txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
                holder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
                holder.txtApprove = (TextView) convertView.findViewById(R.id.txtApprove);
                holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);
                holder.imgSold = (ImageView) convertView.findViewById(R.id.imgSold);
                holder.stepsView = (StepsView) convertView.findViewById(R.id.stepsView);

                convertView.setTag(R.id.layout1,  holder.layout1);
                convertView.setTag(R.id.imgCar,  holder.imgCar);
                convertView.setTag(R.id.txtBrand,  holder.txtBrand);
                convertView.setTag(R.id.txtLicensePlate,  holder.txtLicensePlate);
                convertView.setTag(R.id.txtStartPrice,  holder.txtStartPrice);
                convertView.setTag(R.id.txtBidPrice,  holder.txtBidPrice);
                convertView.setTag(R.id.txtPrice,  holder.txtPrice);
                convertView.setTag(R.id.txtStatus,  holder.txtStatus);
                convertView.setTag(R.id.txtApprove,  holder.txtApprove);
                convertView.setTag(R.id.txtDate,  holder.txtDate);
                convertView.setTag(R.id.imgSold,  holder.imgSold);
                convertView.setTag(R.id.stepsView,  holder.stepsView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

                holder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
                holder.imgCar = (ImageView) convertView.getTag(R.id.imgCar);
                holder.txtBrand = (TextView) convertView.getTag(R.id.txtBrand);
                holder.txtLicensePlate = (TextView) convertView.getTag(R.id.txtLicensePlate);
                holder.txtStartPrice = (TextView) convertView.getTag(R.id.txtStartPrice);
                holder.txtBidPrice = (TextView) convertView.getTag(R.id.txtBidPrice);
                holder.txtPrice = (TextView) convertView.getTag(R.id.txtPrice);
                holder.txtStatus = (TextView) convertView.getTag(R.id.txtStatus);
                holder.txtApprove = (TextView) convertView.getTag(R.id.txtApprove);
                holder.txtDate = (TextView) convertView.getTag(R.id.txtDate);
                holder.imgSold = (ImageView) convertView.getTag(R.id.imgSold);
                holder.stepsView = (StepsView) convertView.getTag(R.id.stepsView);
            }

            // Set Data
            if (feedDataList != null) {
                try {

                    final String carId = String.valueOf(feedDataList.get(position).getString("carId"));
                    String cover = String.valueOf(feedDataList.get(position).getString("cover"));
                    String title = String.valueOf(feedDataList.get(position).getString("title"));
                    String year = String.valueOf(feedDataList.get(position).getString("year"));
                    String brand = String.valueOf(feedDataList.get(position).getString("brand"));
                    String model = String.valueOf(feedDataList.get(position).getString("model"));
                    String sub_model = String.valueOf(feedDataList.get(position).getString("sub_model"));
                    String km = String.valueOf(feedDataList.get(position).getString("km"));
                    String repair = String.valueOf(feedDataList.get(position).getString("repair"));
                    String start_price = String.valueOf(feedDataList.get(position).getString("start_price"));
                    String bid_price = String.valueOf(feedDataList.get(position).getString("bid_price"));
                    String buy_price = String.valueOf(feedDataList.get(position).getString("buy_price"));
                    String created = String.valueOf(feedDataList.get(position).getString("created"));
                    final String status = String.valueOf(feedDataList.get(position).getString("status"));
                    final String approve = String.valueOf(feedDataList.get(position).getString("approve"));

                    holder.txtBrand.setText(year + " " + brand + " " + model);
                    //holder.txtLicensePlate.setText();
                    //holder.txtStartPrice.setText("ราคาประมูลเริ่มต้น : " + start_price);
                    //holder.txtBidPrice.setText("ราคาประมูลปัจจุบัน : " + bid_price);
                    //holder.txtPrice.setText("ราคาขาย : " + buy_price);
                    holder.txtApprove.setText(approve);

                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    holder.txtStartPrice.setText("ราคาประมูลเริ่มต้น : " + formatter.format(Integer.parseInt(start_price)));
                    holder.txtBidPrice.setText("ราคาประมูลปัจจุบัน : " + formatter.format(Integer.parseInt(bid_price)));
                    holder.txtPrice.setText("ราคาขาย : " + formatter.format(Integer.parseInt(buy_price)));

                    // set Image
                    Glide.with(UsedCarListActivity.this)
                            .load(cover)
                            .error(R.drawable.blank_img)
                            .into(holder.imgCar);

                    // process = กำลังประมูล, finish = ประมูลจบแล้ว
                    /*if (status.equals("finish")) {
                        holder.imgSold.setVisibility(View.VISIBLE);
                    }*/

                    // status : กำลังประมูล -> รอชำระเงิน -> รถเดินทาง -> รถถึงบริษัท -> รับรถแล้ว
                    String[] labels = {"รอชำระเงิน", "รถเดินทาง", "ถึงบริษัท", "รับรถแล้ว"};

                    if (status.equals("กำลังประมูล")) {
                        holder.txtStartPrice.setVisibility(View.VISIBLE);
                        holder.txtPrice.setVisibility(View.VISIBLE);
                    } else {
                        holder.txtStartPrice.setVisibility(View.GONE);
                        holder.txtBidPrice.setText("ราคาปิดประมูล : " + formatter.format(Integer.parseInt(bid_price)));
                        holder.txtPrice.setVisibility(View.GONE);
                    }

                    switch (status) {
                        case "กำลังประมูล":
                            holder.txtStatus.setText("กำลังประมูล");
                            holder.txtStatus.setTextColor(Color.BLUE);

                            holder.stepsView.setVisibility(View.GONE);
                            break;
                        case "รอชำระเงิน":
                            holder.txtStatus.setText("รอชำระเงิน");
                            holder.txtStatus.setTextColor(Color.RED);

                            holder.stepsView.setVisibility(View.VISIBLE);
                            holder.stepsView.setCompletedPosition(labels.length - 4)
                                    .setLabels(labels)
                                    .setBarColorIndicator(getResources().getColor(R.color.material_blue_grey_800))
                                    .setProgressColorIndicator(getResources().getColor(R.color.colorPrimary))
                                    .setLabelColorIndicator(getResources().getColor(R.color.colorPrimary))
                                    .drawView();
                            break;
                        case "รถเดินทาง":
                            holder.txtStatus.setText("รถเดินทาง");
                            holder.txtStatus.setTextColor(Color.RED);

                            holder.stepsView.setVisibility(View.VISIBLE);
                            holder.stepsView.setCompletedPosition(labels.length - 3)
                                    .setLabels(labels)
                                    .setBarColorIndicator(getResources().getColor(R.color.material_blue_grey_800))
                                    .setProgressColorIndicator(getResources().getColor(R.color.colorPrimary))
                                    .setLabelColorIndicator(getResources().getColor(R.color.colorPrimary))
                                    .drawView();
                            break;
                        case "รถถึงบริษัท":
                            holder.txtStatus.setText("รถถึงบริษัท");
                            holder.txtStatus.setTextColor(Color.RED);

                            holder.stepsView.setVisibility(View.VISIBLE);
                            holder.stepsView.setCompletedPosition(labels.length - 2)
                                    .setLabels(labels)
                                    .setBarColorIndicator(getResources().getColor(R.color.material_blue_grey_800))
                                    .setProgressColorIndicator(getResources().getColor(R.color.colorPrimary))
                                    .setLabelColorIndicator(getResources().getColor(R.color.colorPrimary))
                                    .drawView();
                            break;
                        case "รับรถแล้ว":
                            holder.txtStatus.setText("รับรถแล้ว");
                            holder.txtStatus.setTextColor(Color.RED);

                            holder.stepsView.setVisibility(View.VISIBLE);
                            holder.stepsView.setCompletedPosition(labels.length - 1)
                                    .setLabels(labels)
                                    .setBarColorIndicator(getResources().getColor(R.color.material_blue_grey_800))
                                    .setProgressColorIndicator(getResources().getColor(R.color.colorPrimary))
                                    .setLabelColorIndicator(getResources().getColor(R.color.colorPrimary))
                                    .drawView();
                            break;
                        default:
                            holder.txtStatus.setText(status);
                            holder.stepsView.setVisibility(View.GONE);
                            break;
                    }

                    /*switch (status) {
                        case "process":
                            holder.txtStatus.setText("กำลังประมูล");
                            holder.txtStatus.setTextColor(Color.BLUE);

                            holder.stepsView.setVisibility(View.GONE);
                            break;
                        case "finish":
                            holder.txtStatus.setText("ประมูลจบแล้ว");
                            holder.txtStatus.setTextColor(Color.RED);

                            holder.txtStartPrice.setVisibility(View.GONE);
                            holder.txtBidPrice.setText("ราคาปิดประมูล : " + formatter.format(Integer.parseInt(bid_price)));
                            holder.txtPrice.setVisibility(View.GONE);

                            holder.stepsView.setVisibility(View.VISIBLE);
                            break;
                    }*/

                    if (approve.equals("อนุมัติแล้ว")) {
                        holder.txtApprove.setVisibility(View.GONE);
                        holder.txtStatus.setVisibility(View.VISIBLE);
                    } else {
                        holder.txtApprove.setVisibility(View.VISIBLE);
                        holder.txtStatus.setVisibility(View.GONE);
                    }
/*
                    // steps view
                    String[] labels = {"รอชำระเงิน", "รถเดินทาง", "ถึงบริษัท", "รับรถแล้ว"};
                    holder.stepsView.setCompletedPosition(labels.length - 4)
                            .setLabels(labels)
                            .setBarColorIndicator(getResources().getColor(R.color.material_blue_grey_800))
                            .setProgressColorIndicator(getResources().getColor(R.color.colorPrimary))
                            .setLabelColorIndicator(getResources().getColor(R.color.colorPrimary))
                            .drawView();
*/
                    // Cut String Date Time
                    String[] separated = created.split("-");
                    String[] day = separated[2].split("T");
                    String[] time = day[1].split("\\.");
                    //String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + day[1];
                    final String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + time[0];
                    holder.txtDate.setText("วันที่ลง : " + dateTime);

                    holder.layout1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Toast.makeText(getBaseContext(), carId, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), UsedCarDetailsActivity.class);
                            intent.putExtra("UserId", UserId);
                            intent.putExtra("CustomerId", CustomerId);
                            intent.putExtra("PKID", carId);
                            //intent.putExtra("EndDate", dateTime);
                            intent.putExtra("Approve", approve);
                            intent.putExtra("AuctionStatus", status);
                            intent.putExtra("sendFrom", "showroomList");
                            startActivity(intent);
                        }
                    });

                } catch (Exception e) {

                }
            }

            return convertView;
        }

        public class ViewHolder {
            LinearLayout layout1;
            ImageView imgCar;
            TextView txtBrand;
            TextView txtLicensePlate;
            TextView txtStartPrice;
            TextView txtBidPrice;
            TextView txtPrice;
            TextView txtStatus;
            TextView txtApprove;
            TextView txtDate;
            ImageView imgSold;
            StepsView stepsView;
        }
    }

    Paginate.Callbacks callbacks = new Paginate.Callbacks() {
        @Override
        public void onLoadMore() {

            mIsLoading = true;

            // Delay and load more
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // load more

                    //Toast.makeText(getBaseContext(), "Load More!", Toast.LENGTH_SHORT).show();
                    //String.valueOf(mPageIndex)
                    //new FeedMore().execute("http://202.183.192.165/ws_antit/WebServiceANTIT.asmx/GET_FEED", "LYd162fYt", "", "");

                    hasLoadedAll = true;
                    mAdapter.notifyDataSetChanged();
                    mIsLoading = false;

                }
            }, delay);

        }

        @Override
        public boolean isLoading() {
            // Indicate whether new page loading is in progress or not
            return mIsLoading;
        }

        @Override
        public boolean hasLoadedAllItems() {
            // Indicate whether all data (pages) are loaded or not
            return hasLoadedAll;
        }
    };

    private class CustomLoadingListItemCreator implements LoadingListItemCreator {
        @Override
        public View newView(int position, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View view = inflater.inflate(R.layout.custom_loading_list_item, parent, false);
            return view;
        }

        @Override
        public void bindView(int position, View view) {
            // Bind custom loading row if needed
        }
    }

    public class FeedAsynTaskGetShowroomDetail extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarListActivity.this);
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

                feedDataListShowroomDetails = CuteFeedJsonUtil.feed(s);
                if (feedDataListShowroomDetails != null) {
                    for (int i = 0; i <= feedDataListShowroomDetails.size(); i++) {
                        try {

                            String status = String.valueOf(feedDataListShowroomDetails.get(i).getString("status"));
                            String data = String.valueOf(feedDataListShowroomDetails.get(i).getString("data"));

                            ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed("[" + data + "]");
                            if (feedDataListData != null) {
                                for (int j = 0; j <= feedDataListData.size(); j++) {
                                    String showroom = String.valueOf(feedDataListData.get(j).getString("showroom"));
                                    String star = String.valueOf(feedDataListData.get(j).getString("star"));
                                    String balance = String.valueOf(feedDataListData.get(j).getString("balance"));
                                    String all_car_bid = String.valueOf(feedDataListData.get(j).getString("all_car_bid"));
                                    String car_sold = String.valueOf(feedDataListData.get(j).getString("car_sold"));

                                    DecimalFormat formatter = new DecimalFormat("#,###,###");

                                    txtName.setText("ชื่อร้าน : " + showroom);
                                    txtDeposit.setText(formatter.format(Integer.parseInt(balance)) + " บาท");
                                    txtAuctioned.setText(all_car_bid + " คัน");
                                    txtSoldOut.setText(car_sold + " คัน");

                                    Integer intStar = Integer.parseInt(star);
                                    for (int k = 0; k < intStar; k++) {
                                        switch (k) {
                                            case 0:
                                                imgStar1.setVisibility(View.VISIBLE);
                                                break;
                                            case 1:
                                                imgStar2.setVisibility(View.VISIBLE);
                                                break;
                                            case 2:
                                                imgStar3.setVisibility(View.VISIBLE);
                                                break;
                                            case 3:
                                                imgStar4.setVisibility(View.VISIBLE);
                                                break;
                                            case 4:
                                                imgStar5.setVisibility(View.VISIBLE);
                                                break;
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


    public void dialogAlertDeposit() {
        AlertDialog.Builder CheckDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View Viewlayout = inflater.inflate(R.layout.dialog_deposit, (ViewGroup) findViewById(R.id.layout_dialog));

        final EditText edtDeposit = (EditText) Viewlayout.findViewById(R.id.edtDeposit);

        CheckDialog.setTitle("กรอกจำนวนเงินฝาก");
        //CheckDialog.setIcon(R.drawable.ic_action_edit);
        CheckDialog.setView(Viewlayout);

        // Button OK
        CheckDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                String strDeposit = edtDeposit.getText().toString();
                //Toast.makeText(getApplicationContext(), strDeposit, Toast.LENGTH_LONG).show();

                //showCreditCardForm();

                dialogAlertDepositConfirm(strDeposit);
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

    public void dialogAlertDepositConfirm(final String _deposit) {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(this);

        DecimalFormat formatter = new DecimalFormat("#,###,###");

        completeDialog.setTitle("เพิ่มเงินฝาก " + formatter.format(Integer.parseInt(_deposit)));
        completeDialog.setMessage("คุณต้องการเพิ่มเงินฝาก " + formatter.format(Integer.parseInt(_deposit)) + " บาทใช่หรือไม่");
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                addDeposit = _deposit;
                showCreditCardForm();
            }
        }).setNegativeButton("แก้ไขเงินฝาก", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                dialogAlertDeposit();
            }
        });
        completeDialog.show();
    }

    // Omise
    private void showCreditCardForm() {
        Intent intent = new Intent(this, CreditCardActivity.class);
        intent.putExtra(CreditCardActivity.EXTRA_PKEY, OMISE_PKEY);
        startActivityForResult(intent, REQUEST_CC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CC:
                if (resultCode == CreditCardActivity.RESULT_CANCEL) {
                    return;
                }

                Token token = data.getParcelableExtra(CreditCardActivity.EXTRA_TOKEN_OBJECT);
                // process your token here.

                String _token = data.getStringExtra(CreditCardActivity.EXTRA_TOKEN);

                Log.d("TOKEN OBJECT", String.valueOf(token));
                Log.d("TOKEN ID", _token);  //tokn_test_5931k1f99psnon0sozq

                new FeedAsynTaskPayWithOMISE().execute(strWebServiceUrl + "AddBalanceByOmise", CustomerId, _token, addDeposit);

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class FeedAsynTaskPayWithOMISE extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarListActivity.this);
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
                        .add("customerId", params[1])
                        .add("token", params[2])
                        .add("balance", params[3])
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

                // <string xmlns="http://tempuri.org/">{ "Success": true }</string>
                // { "Success": false, "Msg": "An error occurred while updating the entries. See the inner exception for details." }

                s = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
                s = s.replace("<string xmlns=\"http://tempuri.org/\">", "");
                s = s.replace("</string>", "");
                s = "[" + s + "]";

                feedDataListToken = CuteFeedJsonUtil.feed(s);
                if (feedDataListToken != null) {
                    for (int i = 0; i <= feedDataListToken.size(); i++) {
                        // Success
                        try {

                            String status = String.valueOf(feedDataListToken.get(i).getString("status"));

                            if (status.equals("ok")) {

                                dialogAlertAddBalanceByOmiseSuccess();

                            } else {
                                dialogAlertAddBalanceByOmiseFail();
                            }


                        } catch (Exception e) {

                        }

                        // Fail
                        try {

                            String status = String.valueOf(feedDataListToken.get(i).getString("status"));
                            String msg = String.valueOf(feedDataListToken.get(i).getString("msg"));

                            if (status.equals("error")) {

                                dialogAlertAddBalanceByOmiseFail();

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

    public void dialogAlertAddBalanceByOmiseSuccess() {

        AlertDialog.Builder completeDialog = new AlertDialog.Builder(this);
        completeDialog.setTitle("เพิ่มเงินฝากสำเร็จ");
        //completeDialog.setMessage("");
        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();

                // Success and reload data
                loadData();
                loadShowroomDetails();

            }
        })/*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Fail!!" ,Toast.LENGTH_LONG).show();
            }
        })*/;
        completeDialog.show();
    }

    public void dialogAlertAddBalanceByOmiseFail() {

        AlertDialog.Builder completeDialog = new AlertDialog.Builder(this);
        completeDialog.setTitle("เพิ่มเงินฝากไม่สำเร็จ");
        completeDialog.setMessage("กรุณาลองใหม่อีกครั้ง");
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("KEYCODE_BACK");

            moveTaskToBack(true);
        }
        return false;
    }
}
