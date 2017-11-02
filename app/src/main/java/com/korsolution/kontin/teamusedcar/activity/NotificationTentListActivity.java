package com.korsolution.kontin.teamusedcar.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.korsolution.kontin.teamusedcar.BuyHistoryActivity;
import com.korsolution.kontin.teamusedcar.CuteFeedJsonUtil;
import com.korsolution.kontin.teamusedcar.R;
import com.korsolution.kontin.teamusedcar.UsedCarSellingDetailsActivity;
import com.korsolution.kontin.teamusedcar.UsedCarSellingListActivity;
import com.korsolution.kontin.teamusedcar.adapter.NotificationAdapter;
import com.korsolution.kontin.teamusedcar.dbclass.UsedCarSellingDBClass;
import com.paginate.Paginate;
import com.paginate.abslistview.LoadingListItemCreator;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NotificationTentListActivity extends AppCompatActivity {

    private SwipeRefreshLayout mRefreshView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView txtNoData;
    private AVLoadingIndicatorView avi;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListNoti;
    protected ArrayList<JSONObject> feedDataListNotificationRead;
    protected ArrayList<JSONObject> feedDataListUsedCar;
    protected ArrayList<JSONObject> feedDataListUsedCarData;

    final int delay = 1000;
    private boolean mIsLoading = true;
    private boolean hasLoadedAll = false;
    private int mPageIndex = 1;

    private String UserId;
    private String CustomerId;

    String strWebServiceUrl;

    private UsedCarSellingDBClass UsedCarSellingDB;

    private String Deposit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_tent_list);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        UserId = getIntent().getStringExtra("UserId");
        CustomerId = getIntent().getStringExtra("CustomerId");
        Deposit = getIntent().getStringExtra("Deposit");

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        UsedCarSellingDB = new UsedCarSellingDBClass(this);

        setupWidgets();
        loadNotificationList();
        loadDataListCar("", "", "", "");
    }

    private void setupWidgets() {

        mRefreshView = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        txtNoData = (TextView) findViewById(R.id.txtNoData);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);     // VERTICAL

        mRecyclerView.addOnItemTouchListener(new NotificationAdapter.RecyclerTouchListener(this, mRecyclerView, new NotificationAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (feedDataList != null) {
                    try {
                        String pkid = String.valueOf(feedDataList.get(position).getString("pkid"));
                        String text = String.valueOf(feedDataList.get(position).getString("text"));
                        String type = String.valueOf(feedDataList.get(position).getString("type"));
                        String car_id = String.valueOf(feedDataList.get(position).getString("car_id"));
                        String is_read = String.valueOf(feedDataList.get(position).getString("is_read"));

                        new FeedAsynTaskMakeNotificationRead().execute(strWebServiceUrl + "MakeNotificationRead", pkid);

                        String CarId = "";
                        String EndDate = "";
                        String arrData[][] = UsedCarSellingDB.SelectUsedCar(car_id);
                        if (arrData != null) {
                            CarId = arrData[0][2].toString();
                            EndDate = arrData[0][12].toString();
                        }

                        /* type
                        รถเข้าใหม่ = 1,//send to all tent
                        มีการซื้อรถ = 2,//send to showroom
                        มีการบิด = 3,//send to showroom
                        ชนะการประมูล = 4,//send to tent
                        จบประมูล = 5, //send to showroom
                        ถ่ายรูปส่งสลิป = 6, //send to shoroom
                        ชำระสำเร็จ = 7, //send to tent
                        */

                        //Toast.makeText(getApplicationContext(), type, Toast.LENGTH_LONG).show();

                        switch (type) {
                            case "1":
                                Intent intent = new Intent(getApplicationContext(), UsedCarSellingDetailsActivity.class);
                                intent.putExtra("PKID", CarId);
                                intent.putExtra("EndDate", EndDate);
                                intent.putExtra("Deposit", Deposit);
                                startActivity(intent);
                                break;
                            case "4":
                                Intent intent4 = new Intent(getApplicationContext(), BuyHistoryActivity.class);
                                intent4.putExtra("UserId", UserId);
                                intent4.putExtra("CustomerId", CustomerId);
                                startActivity(intent4);
                                break;
                            case "7":
                                Intent intent7 = new Intent(getApplicationContext(), BuyHistoryActivity.class);
                                intent7.putExtra("UserId", UserId);
                                intent7.putExtra("CustomerId", CustomerId);
                                startActivity(intent7);
                                break;
                            default:
                                Intent intent1 = new Intent(getApplicationContext(), UsedCarSellingListActivity.class);
                                intent1.putExtra("UserId", UserId);
                                intent1.putExtra("CustomerId", CustomerId);
                                startActivity(intent1);
                                break;
                        }

                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_bright);
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mDataArray.clear();
                feedDataList.clear();
                //mAdapter = new FeedNewsListViewAdapter();
                mAdapter.notifyDataSetChanged();
                mPageIndex = 1;
                hasLoadedAll = false;

                // reload
                mIsLoading = true;
                loadNotificationList();
            }
        });
    }

    private void loadNotificationList() {

        new FeedAsynTask().execute(strWebServiceUrl + "GetNotificationByCustomer", CustomerId);
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        //private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*nDialog = new ProgressDialog(getActivity());
            nDialog.setMessage("Loading..");
            //nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();*/

            avi.show();
            txtNoData.setVisibility(View.VISIBLE);
            txtNoData.setText("Downloading..");
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

                mRefreshView.setRefreshing(false);

                feedDataListNoti = CuteFeedJsonUtil.feed(s);
                if (feedDataListNoti != null) {
                    for (int i = 0; i <= feedDataListNoti.size(); i++) {
                        try {

                            String _status = String.valueOf(feedDataListNoti.get(i).getString("status"));
                            String data = String.valueOf(feedDataListNoti.get(i).getString("data"));
                            String count = String.valueOf(feedDataListNoti.get(i).getString("count"));

                            if (data.equals("[]")) {
                                txtNoData.setVisibility(View.VISIBLE);
                                txtNoData.setText("No Data Available");
                            } else {
                                txtNoData.setVisibility(View.GONE);
                            }

                            feedDataList = CuteFeedJsonUtil.feed(data);
                            if (feedDataList != null) {
                                for (int j = 0; j <= feedDataList.size(); j++) {

                                    String pkid = String.valueOf(feedDataList.get(j).getString("pkid"));
                                    String text = String.valueOf(feedDataList.get(j).getString("text"));
                                    String type = String.valueOf(feedDataList.get(j).getString("type"));
                                    String car_id = String.valueOf(feedDataList.get(j).getString("car_id"));
                                    String is_read = String.valueOf(feedDataList.get(j).getString("is_read"));

                                    mAdapter = new NotificationAdapter(NotificationTentListActivity.this, feedDataList);
                                    mRecyclerView.setAdapter(mAdapter);

                                    // load more
                                    Paginate.with(mRecyclerView, callbacks)
                                            .setLoadingTriggerThreshold(2)
                                            .addLoadingListItem(true)
                                            .setLoadingListItemCreator((com.paginate.recycler.LoadingListItemCreator) new CustomLoadingListItemCreator())
                                            .build();

                                    mIsLoading = false;

                                    txtNoData.setVisibility(View.GONE);
                                }
                            }

                        } catch (Exception e) {

                        }
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_LONG).show();
                txtNoData.setVisibility(View.VISIBLE);
                txtNoData.setText("Load Data Fail!, Please try agian.");
            }

            //nDialog.dismiss();
            avi.hide();
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

    public class FeedAsynTaskMakeNotificationRead extends AsyncTask<String, Void, String> {

        //private ProgressDialog nDialog;

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(NotificationTentListActivity.this);
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
                        .add("noti_id", params[1])
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

                feedDataListNotificationRead = CuteFeedJsonUtil.feed(s);
                if (feedDataListNotificationRead != null) {
                    for (int i = 0; i <= feedDataListNotificationRead.size(); i++) {
                        // Success
                        try {

                            String status = String.valueOf(feedDataListNotificationRead.get(i).getString("status"));

                            if (status.equals("ok")) {

                                //

                            } else {
                                //
                            }


                        } catch (Exception e) {

                        }

                        // Fail
                        try {

                            String status = String.valueOf(feedDataListNotificationRead.get(i).getString("status"));
                            String msg = String.valueOf(feedDataListNotificationRead.get(i).getString("msg"));

                            if (status.equals("error")) {

                                //

                            }


                        } catch (Exception e) {

                        }
                    }

                } else {
                    //Toast.makeText(getApplicationContext(), "No Data!!", Toast.LENGTH_LONG).show();
                }

            } else {
                //Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_LONG).show();
            }

            //nDialog.dismiss();
        }
    }

    private void loadDataListCar(String year, String brand, String generation, String subGeneration) {

        new FeedAsynTaskListCar().execute(strWebServiceUrl + "ListCar", CustomerId, year, brand, generation, subGeneration);
    }

    public class FeedAsynTaskListCar extends AsyncTask<String, Void, String> {

        //private ProgressDialog nDialog;

        /*@Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(NotificationTentListActivity.this);
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
                        .add("customerId", params[1])
                        .add("year", params[2])
                        .add("brand", params[3])
                        .add("model", params[4])
                        .add("subModel", params[5])
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

                UsedCarSellingDB.Delete();

                feedDataListUsedCar = CuteFeedJsonUtil.feed(s);
                if (feedDataListUsedCar != null) {
                    for (int i = 0; i <= feedDataListUsedCar.size(); i++) {
                        try {

                            String status = String.valueOf(feedDataListUsedCar.get(i).getString("status"));
                            String data = String.valueOf(feedDataListUsedCar.get(i).getString("data"));

                            feedDataListUsedCarData = CuteFeedJsonUtil.feed(data);
                            if (feedDataListUsedCarData != null) {
                                for (int j = 0; j <= feedDataListUsedCarData.size(); j++) {

                                    try {

                                        String Cover = String.valueOf(feedDataListUsedCarData.get(j).getString("Cover"));
                                        String PKID = String.valueOf(feedDataListUsedCarData.get(j).getString("PKID"));
                                        String Title = String.valueOf(feedDataListUsedCarData.get(j).getString("Title"));
                                        String Buy = String.valueOf(feedDataListUsedCarData.get(j).getString("Buy"));
                                        String Bid = String.valueOf(feedDataListUsedCarData.get(j).getString("Bid"));
                                        String CurrentBid = String.valueOf(feedDataListUsedCarData.get(j).getString("CurrentBid"));
                                        String Brand = String.valueOf(feedDataListUsedCarData.get(j).getString("Brand"));
                                        String Model = String.valueOf(feedDataListUsedCarData.get(j).getString("Model"));
                                        String License = String.valueOf(feedDataListUsedCarData.get(j).getString("License"));
                                        String Year = String.valueOf(feedDataListUsedCarData.get(j).getString("Year"));
                                        String Km = String.valueOf(feedDataListUsedCarData.get(j).getString("Km"));
                                        String End = String.valueOf(feedDataListUsedCarData.get(j).getString("End"));
                                        String Seller = String.valueOf(feedDataListUsedCarData.get(j).getString("Seller"));

                                        UsedCarSellingDB.Insert(Cover, PKID, Title, Buy, Bid, CurrentBid, Brand, Model, License, Year, Km, End, Seller);

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
}
