package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.bumptech.glide.Glide;
import com.korsolution.kontin.teamusedcar.activity.NotificationTentListActivity;
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

public class UsedCarSellingListActivity extends AppCompatActivity {

    private TextView txtName;
    private ImageView imgStar1;
    private ImageView imgStar2;
    private ImageView imgStar3;
    private ImageView imgStar4;
    private ImageView imgStar5;
    private TextView txtDeposit;
    private TextView txtAuctioned;
    private SwipeRefreshLayout mRefreshView;
    private ListView lvUsedCarSelling;
    private TextView txtNoData;
    private FloatingActionButton fabSearch;
    private ImageButton imgBtnDeposit;
    private Button btnDesiredCar;

    private FeedNewsListViewAdapter mAdapter;
    final int delay = 1000;
    private boolean mIsLoading = true;
    private boolean hasLoadedAll = false;
    private int mPageIndex = 1;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListUsedCar;
    protected ArrayList<JSONObject> feedDataListBuyHistory;
    protected ArrayList<JSONObject> feedDataListHistory;
    protected ArrayList<JSONObject> feedDataListTentDetail;

    private String UserId;
    private String CustomerId;

    private AccountDBClass AccountDB;

    // Notification on ActionBar
    static ImageButton btnNotification;
    static TextView textOne1;
    static int mNotifCount1 = 10;

    static ImageButton btnCar;
    static TextView textOne;
    static int mNotifCount = 0;

    private BuyHistoryDBClass BuyHistoryDB;

    String strWebServiceUrl;

    private String deposit;

    // Omise
    //private static final String OMISE_PKEY = "pkey_test_5927y4ui9k4lx17wmuz";
    private static final String OMISE_PKEY = "pkey_test_59ccnhqxthw3rt925fd";
    private static final int REQUEST_CC = 100;

    private String addDeposit;

    protected ArrayList<JSONObject> feedDataListToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_car_selling_list);

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
        BuyHistoryDB = new BuyHistoryDBClass(this);

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        setupWidgets();

        loadData("", "", "", "");

        loadTentDetails();

        /*CountDownTimer cdt = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Tick
            }

            public void onFinish() {
                // Finish
            }
        }.start();*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == SearchCarActivity.RESULT_OK){
                //String result = data.getStringExtra("result");
                //Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();

                String year = data.getStringExtra("year");
                String brand = data.getStringExtra("brand");
                String generation = data.getStringExtra("generation");
                String subGeneration = data.getStringExtra("subGeneration");

                lvUsedCarSelling.setAdapter(null);

                loadData(year, brand, generation, subGeneration);

            }
            if (resultCode == SearchCarActivity.RESULT_CANCELED) {
                //Write your code if there's no result

                //Toast.makeText(getBaseContext(), "Cancle!", Toast.LENGTH_LONG).show();
            }
        }

        // Omise
        switch (requestCode) {
            case REQUEST_CC:
                if (resultCode == CreditCardActivity.RESULT_CANCEL) {
                    return;
                }

                Token token = data.getParcelableExtra(CreditCardActivity.EXTRA_TOKEN_OBJECT);
                // process your token here.

                String _token = data.getStringExtra(CreditCardActivity.EXTRA_TOKEN);

                Log.d("TOKEN OBJECT", String.valueOf(token));
                Log.d("TOKEN ID", _token);

                new FeedAsynTaskPayWithOMISE().execute(strWebServiceUrl + "AddBalanceByOmise", CustomerId, _token, addDeposit);

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }//onActivityResult

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tent, menu);

        MenuItem itemNoti = menu.findItem(R.id.action_buy_history);
        View count = menu.findItem(R.id.action_buy_history).getActionView();
        btnCar = (ImageButton) count.findViewById(R.id.btnCar);
        textOne = (TextView) count.findViewById(R.id.textOne);
        //textOne.setText(String.valueOf(mNotifCount));
        //textOne.setVisibility(View.VISIBLE);

        String[][] arrData = BuyHistoryDB.SelectByStatus();
        if (arrData != null) {
            textOne.setText(String.valueOf(arrData.length));
            textOne.setVisibility(View.VISIBLE);
        } else {
            textOne.setVisibility(View.GONE);
        }

        btnCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), BuyHistoryActivity.class);
                intent.putExtra("UserId", UserId);
                intent.putExtra("CustomerId", CustomerId);
                startActivity(intent);

            }
        });

        View count1 = menu.findItem(R.id.action_notifications).getActionView();
        btnNotification = (ImageButton) count1.findViewById(R.id.btnNotification);
        textOne1 = (TextView) count1.findViewById(R.id.textOne);
        textOne1.setText(String.valueOf(mNotifCount1));
        textOne1.setVisibility(View.GONE);
        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), NotificationTentListActivity.class);
                intent.putExtra("UserId", UserId);
                intent.putExtra("CustomerId", CustomerId);
                intent.putExtra("Deposit", deposit);
                startActivity(intent);
            }
        });

        new FeedAsynTaskGetNotificationCount(this, textOne1).execute(strWebServiceUrl + "GetNotificationByCustomer", /*"29"*/CustomerId);

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

            loadData("", "", "", "");
            loadTentDetails();

            return true;
        }

        if (id == R.id.action_logout) {

            dialogAlertLogOut();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void dialogAlertLogOut(){
        AlertDialog.Builder mDialog = new AlertDialog.Builder(UsedCarSellingListActivity.this);

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
        mRefreshView = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        lvUsedCarSelling = (ListView) findViewById(R.id.lvUsedCarSelling);
        txtNoData = (TextView) findViewById(R.id.txtNoData);
        fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);
        imgBtnDeposit = (ImageButton) findViewById(R.id.imgBtnDeposit);
        btnDesiredCar = (Button) findViewById(R.id.btnDesiredCar);

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), SearchCarActivity.class);
                startActivityForResult(i, 1);

                overridePendingTransition(R.anim.slide_up_info, R.anim.no_change);
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
                loadData("", "", "", "");
                loadTentDetails();
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
                Intent intent = new Intent(getApplicationContext(), DesiredCarListTentActivity.class);
                intent.putExtra("UserId", UserId);
                intent.putExtra("CustomerId", CustomerId);
                startActivity(intent);
            }
        });
    }

    private void loadTentDetails() {

        new FeedAsynTaskGetTentDetail().execute(strWebServiceUrl + "GetTentDetail", /*"29"*/CustomerId);
    }

    private void loadData(String year, String brand, String generation, String subGeneration) {

        new FeedAsynTask().execute(strWebServiceUrl + "ListCar", CustomerId, year, brand, generation, subGeneration);

        new FeedAsynTaskBuyHistory().execute(strWebServiceUrl + "ListBuyHistoryForTent", CustomerId);
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarSellingListActivity.this);
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

                txtNoData.setVisibility(View.GONE);

                mRefreshView.setRefreshing(false);

                feedDataListUsedCar = CuteFeedJsonUtil.feed(s);
                if (feedDataListUsedCar != null) {
                    for (int i = 0; i <= feedDataListUsedCar.size(); i++) {
                        try {

                            String status = String.valueOf(feedDataListUsedCar.get(i).getString("status"));
                            String data = String.valueOf(feedDataListUsedCar.get(i).getString("data"));

                            if (data.equals("[]")) {
                                txtNoData.setVisibility(View.VISIBLE);
                                txtNoData.setText("ไม่มีรถประมูล");
                            }

                            feedDataList = CuteFeedJsonUtil.feed(data);
                            if (feedDataList != null) {

                                String PKID = String.valueOf(feedDataList.get(i).getString("PKID"));

                                mAdapter = new FeedNewsListViewAdapter();
                                lvUsedCarSelling.setAdapter(mAdapter);

                                // load more
                                /*Paginate.with(lvUsedCarSelling, callbacks)
                                        .setLoadingTriggerThreshold(2)
                                        .addLoadingListItem(true)
                                        .setLoadingListItemCreator(new CustomLoadingListItemCreator())
                                        .build();

                                mIsLoading = false;*/
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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_item_used_car_selling, null);
                holder = new ViewHolder();

                holder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
                holder.imgCar = (ImageView) convertView.findViewById(R.id.imgCar);
                holder.txtBrand = (TextView) convertView.findViewById(R.id.txtBrand);
                holder.txtBid = (TextView) convertView.findViewById(R.id.txtBid);
                holder.txtCurrentBid = (TextView) convertView.findViewById(R.id.txtCurrentBid);
                holder.txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
                holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);

                convertView.setTag(R.id.layout1,  holder.layout1);
                convertView.setTag(R.id.imgCar,  holder.imgCar);
                convertView.setTag(R.id.txtBrand,  holder.txtBrand);
                convertView.setTag(R.id.txtBid,  holder.txtBid);
                convertView.setTag(R.id.txtCurrentBid,  holder.txtCurrentBid);
                convertView.setTag(R.id.txtPrice,  holder.txtPrice);
                convertView.setTag(R.id.txtDate,  holder.txtDate);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

                holder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
                holder.imgCar = (ImageView) convertView.getTag(R.id.imgCar);
                holder.txtBrand = (TextView) convertView.getTag(R.id.txtBrand);
                holder.txtBid = (TextView) convertView.getTag(R.id.txtBid);
                holder.txtCurrentBid = (TextView) convertView.getTag(R.id.txtCurrentBid);
                holder.txtPrice = (TextView) convertView.getTag(R.id.txtPrice);
                holder.txtDate = (TextView) convertView.getTag(R.id.txtDate);
            }

            // Set Data
            if (feedDataList != null) {
                try {

                    final String Cover = String.valueOf(feedDataList.get(position).getString("Cover"));
                    final String PKID = String.valueOf(feedDataList.get(position).getString("PKID"));
                    String Title = String.valueOf(feedDataList.get(position).getString("Title"));
                    String Buy = String.valueOf(feedDataList.get(position).getString("Buy"));
                    String Bid = String.valueOf(feedDataList.get(position).getString("Bid"));
                    String CurrentBid = String.valueOf(feedDataList.get(position).getString("CurrentBid"));
                    String Brand = String.valueOf(feedDataList.get(position).getString("Brand"));
                    String Model = String.valueOf(feedDataList.get(position).getString("Model"));
                    String License = String.valueOf(feedDataList.get(position).getString("License"));
                    String Year = String.valueOf(feedDataList.get(position).getString("Year"));
                    String Km = String.valueOf(feedDataList.get(position).getString("Km"));
                    final String End = String.valueOf(feedDataList.get(position).getString("End"));
                    String Seller = String.valueOf(feedDataList.get(position).getString("Seller"));

                    // Cut String Date Time
                    String[] separated = End.split("-");
                    String[] day = separated[2].split("T");
                    String[] time = day[1].split("\\.");
                    //String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + day[1];
                    final String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + time[0];

                    holder.txtBrand.setText(Year + " " + Brand + " " + Model);
                    //holder.txtBid.setText("ราคาเริ่มต้น : " + Bid);
                    //holder.txtCurrentBid.setText("การเสนอราคาปัจจุบัน : " + CurrentBid);
                    //holder.txtPrice.setText("ราคาขาย : " + Buy);
                    holder.txtDate.setText("เวลาปิด : " + dateTime);

                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    holder.txtBid.setText("ราคาเริ่มต้น : " + formatter.format(Integer.parseInt(Bid)));
                    holder.txtCurrentBid.setText("การเสนอราคาปัจจุบัน : " + formatter.format(Integer.parseInt(CurrentBid)));
                    holder.txtPrice.setText("ราคาขาย : " + formatter.format(Integer.parseInt(Buy)));

                    // set Image
                    Glide.with(UsedCarSellingListActivity.this)
                            .load(Cover)
                            .error(R.drawable.blank_img)
                            .into(holder.imgCar);

                    holder.layout1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //Toast.makeText(getBaseContext(), PKID, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), UsedCarSellingDetailsActivity.class);
                            intent.putExtra("UserId", UserId);
                            intent.putExtra("CustomerId", CustomerId);
                            intent.putExtra("PKID", PKID);
                            intent.putExtra("EndDate", dateTime);
                            intent.putExtra("Deposit", deposit);
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
            TextView txtBid;
            TextView txtCurrentBid;
            TextView txtPrice;
            TextView txtDate;
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

    public class FeedAsynTaskBuyHistory extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarSellingListActivity.this);
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

                mRefreshView.setRefreshing(false);

                BuyHistoryDB.Delete();

                feedDataListBuyHistory = CuteFeedJsonUtil.feed(s);
                if (feedDataListBuyHistory != null) {
                    for (int i = 0; i < feedDataListBuyHistory.size(); i++) {
                        try {

                            String status = String.valueOf(feedDataListBuyHistory.get(i).getString("status"));
                            String data = String.valueOf(feedDataListBuyHistory.get(i).getString("data"));

                            feedDataListHistory = CuteFeedJsonUtil.feed(data);
                            if (feedDataListHistory != null) {
                                for (int j = 0; j < feedDataListHistory.size(); j++) {
                                    String Cover = String.valueOf(feedDataListHistory.get(j).getString("Cover"));
                                    String PKID = String.valueOf(feedDataListHistory.get(j).getString("PKID"));
                                    String Transaction = String.valueOf(feedDataListHistory.get(j).getString("Transaction"));
                                    String Title = String.valueOf(feedDataListHistory.get(j).getString("Title"));
                                    String Price = String.valueOf(feedDataListHistory.get(j).getString("Price"));
                                    String Brand = String.valueOf(feedDataListHistory.get(j).getString("Brand"));
                                    String Model = String.valueOf(feedDataListHistory.get(j).getString("Model"));
                                    String SubModel = String.valueOf(feedDataListHistory.get(j).getString("SubModel"));
                                    String Status = String.valueOf(feedDataListHistory.get(j).getString("Status"));
                                    String Type = String.valueOf(feedDataListHistory.get(j).getString("Type"));
                                    String By = String.valueOf(feedDataListHistory.get(j).getString("By"));
                                    String EndDate = String.valueOf(feedDataListHistory.get(j).getString("EndDate"));
                                    String Created = String.valueOf(feedDataListHistory.get(j).getString("Created"));

                                    BuyHistoryDB.Insert(Cover, PKID, Transaction, Title, Price, Brand, Model, SubModel, Status, Type, By, EndDate, Created);
                                }

                                //mAdapter = new FeedNewsListViewAdapter();
                                //lvBuyHistory.setAdapter(mAdapter);
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

    public class FeedAsynTaskGetTentDetail extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarSellingListActivity.this);
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

                                    deposit = balance;

                                    DecimalFormat formatter = new DecimalFormat("#,###,###");

                                    txtName.setText("ชื่อร้าน : " + showroom);
                                    txtDeposit.setText(formatter.format(Integer.parseInt(balance)) + " บาท");
                                    txtAuctioned.setText(car_bid + " คัน");

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
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CC:
                if (resultCode == CreditCardActivity.RESULT_CANCEL) {
                    return;
                }

                Token token = data.getParcelableExtra(CreditCardActivity.EXTRA_TOKEN_OBJECT);
                // process your token here.

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
*/

    public class FeedAsynTaskPayWithOMISE extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(UsedCarSellingListActivity.this);
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
                loadData("", "", "", "");
                loadTentDetails();

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
