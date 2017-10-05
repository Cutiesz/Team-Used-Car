package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class BuyHistoryActivity extends AppCompatActivity {

    private SwipeRefreshLayout mRefreshView;
    private ListView lvBuyHistory;
    private TextView txtNoData;

    //private FeedNewsListViewAdapter mAdapter;
    final int delay = 1000;
    private boolean mIsLoading = true;
    private boolean hasLoadedAll = false;
    private int mPageIndex = 1;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListBuyHistory;

    private String UserId;
    private String CustomerId;

    private BuyHistoryDBClass BuyHistoryDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_history);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        UserId = getIntent().getStringExtra("UserId");
        CustomerId = getIntent().getStringExtra("CustomerId");

        BuyHistoryDB = new BuyHistoryDBClass(this);

        setupWidgets();

        loadData();
    }

    private void setupWidgets() {

        mRefreshView = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        lvBuyHistory = (ListView) findViewById(R.id.lvBuyHistory);
        txtNoData = (TextView) findViewById(R.id.txtNoData);

        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_bright);
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //mDataArray.clear();
                feedDataList.clear();
                //mAdapter.notifyDataSetChanged();
                mPageIndex = 1;
                hasLoadedAll = false;

                // reload
                mIsLoading = true;
                loadData();
            }
        });
    }

    private void loadData() {
        String strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        new FeedAsynTask().execute(strWebServiceUrl + "ListBuyHistoryForTent", CustomerId);
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(BuyHistoryActivity.this);
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

                            feedDataList = CuteFeedJsonUtil.feed(data);
                            if (feedDataList != null) {
                                for (int j = 0; j < feedDataList.size(); j++) {
                                    String Cover = String.valueOf(feedDataList.get(j).getString("Cover"));
                                    String PKID = String.valueOf(feedDataList.get(j).getString("PKID"));
                                    String Transaction = String.valueOf(feedDataList.get(j).getString("Transaction"));
                                    String Title = String.valueOf(feedDataList.get(j).getString("Title"));
                                    String Price = String.valueOf(feedDataList.get(j).getString("Price"));
                                    String Brand = String.valueOf(feedDataList.get(j).getString("Brand"));
                                    String Model = String.valueOf(feedDataList.get(j).getString("Model"));
                                    String SubModel = String.valueOf(feedDataList.get(j).getString("SubModel"));
                                    String Status = String.valueOf(feedDataList.get(j).getString("Status"));
                                    String Type = String.valueOf(feedDataList.get(j).getString("Type"));
                                    String By = String.valueOf(feedDataList.get(j).getString("By"));
                                    String EndDate = String.valueOf(feedDataList.get(j).getString("EndDate"));
                                    String Created = String.valueOf(feedDataList.get(j).getString("Created"));

                                    BuyHistoryDB.Insert(Cover, PKID, Transaction, Title, Price, Brand, Model, SubModel, Status, Type, By, EndDate, Created);
                                }

                                //mAdapter = new FeedNewsListViewAdapter();
                                //lvBuyHistory.setAdapter(mAdapter);
                            }

                        } catch (Exception e) {

                        }
                    }

                    String[][] arrData = BuyHistoryDB.SelectAll();
                    if (arrData != null) {
                        lvBuyHistory.setAdapter(new ImageAdapter(BuyHistoryActivity.this, arrData));
                    } else {
                        txtNoData.setVisibility(View.VISIBLE);
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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_item_buy_history, null);
                holder = new ViewHolder();

                holder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
                holder.imgCar = (ImageView) convertView.findViewById(R.id.imgCar);
                holder.txtTransaction = (TextView) convertView.findViewById(R.id.txtTransaction);
                holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
                holder.txtBrand = (TextView) convertView.findViewById(R.id.txtBrand);
                holder.txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
                holder.txtStartDate = (TextView) convertView.findViewById(R.id.txtStartDate);
                holder.txtEndDate = (TextView) convertView.findViewById(R.id.txtEndDate);
                holder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);

                convertView.setTag(R.id.layout1,  holder.layout1);
                convertView.setTag(R.id.imgCar,  holder.imgCar);
                convertView.setTag(R.id.txtTransaction,  holder.txtTransaction);
                convertView.setTag(R.id.txtTitle,  holder.txtTitle);
                convertView.setTag(R.id.txtBrand,  holder.txtBrand);
                convertView.setTag(R.id.txtPrice,  holder.txtPrice);
                convertView.setTag(R.id.txtStartDate,  holder.txtStartDate);
                convertView.setTag(R.id.txtEndDate,  holder.txtEndDate);
                convertView.setTag(R.id.txtStatus,  holder.txtStatus);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

                holder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
                holder.imgCar = (ImageView) convertView.getTag(R.id.imgCar);
                holder.txtTransaction = (TextView) convertView.getTag(R.id.txtTransaction);
                holder.txtTitle = (TextView) convertView.getTag(R.id.txtTitle);
                holder.txtBrand = (TextView) convertView.getTag(R.id.txtBrand);
                holder.txtPrice = (TextView) convertView.getTag(R.id.txtPrice);
                holder.txtStartDate = (TextView) convertView.getTag(R.id.txtStartDate);
                holder.txtEndDate = (TextView) convertView.getTag(R.id.txtEndDate);
                holder.txtStatus = (TextView) convertView.getTag(R.id.txtStatus);
            }

            // Set Data
            if (feedDataList != null) {
                try {

                    String Cover = String.valueOf(feedDataList.get(position).getString("Cover"));
                    String PKID = String.valueOf(feedDataList.get(position).getString("PKID"));
                    String Transaction = String.valueOf(feedDataList.get(position).getString("Transaction"));
                    String Title = String.valueOf(feedDataList.get(position).getString("Title"));
                    String Price = String.valueOf(feedDataList.get(position).getString("Price"));
                    String Brand = String.valueOf(feedDataList.get(position).getString("Brand"));
                    String Model = String.valueOf(feedDataList.get(position).getString("Model"));
                    String SubModel = String.valueOf(feedDataList.get(position).getString("SubModel"));
                    String Status = String.valueOf(feedDataList.get(position).getString("Status"));
                    String Type = String.valueOf(feedDataList.get(position).getString("Type"));
                    String By = String.valueOf(feedDataList.get(position).getString("By"));
                    String EndDate = String.valueOf(feedDataList.get(position).getString("EndDate"));
                    String Created = String.valueOf(feedDataList.get(position).getString("Created"));

                    // Cut String Date Time
                    String[] separated = EndDate.split("-");
                    String[] day = separated[2].split("T");
                    String[] time = day[1].split("\\.");
                    //String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + day[1];
                    String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + time[0];

                    holder.txtTransaction.setText("รหัสซื้อ-ขาย : " + Transaction);
                    holder.txtTitle.setText(Title);
                    holder.txtBrand.setText(Brand + " " + Model);
                    //holder.txtPrice.setText("ราคา : " + Price);
                    //holder.txtStartDate.setText("Created : " + Created);
                    holder.txtEndDate.setText("เวลาปิด : " + dateTime);

                    DecimalFormat formatter = new DecimalFormat("#,###,###");
                    holder.txtPrice.setText("ราคา : " + formatter.format(Integer.parseInt(Price)));

                    switch (Status) {
                        case "In Process":
                            holder.txtStatus.setText("กำลังดำเนินการ");
                            holder.txtStatus.setTextColor(Color.RED);
                            break;
                        case "Finish":
                            holder.txtStatus.setText("ซื้อสำเร็จ");
                            holder.txtStatus.setTextColor(Color.GREEN);
                            break;
                    }

                    // set Image
                    Glide.with(BuyHistoryActivity.this)
                            .load(Cover)
                            .into(holder.imgCar);

                } catch (Exception e) {

                }
            }

            return convertView;
        }

        public class ViewHolder {
            LinearLayout layout1;
            ImageView imgCar;
            TextView txtTransaction;
            TextView txtTitle;
            TextView txtBrand;
            TextView txtPrice;
            TextView txtStartDate;
            TextView txtEndDate;
            TextView txtStatus;
        }
    }

    public class ImageAdapter extends BaseAdapter {
        private Context context;
        private String[][] arrList;

        public ImageAdapter(Context c, String[][] _list)
        {
            // TODO Auto-generated method stub
            context = c;
            arrList = _list;
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return arrList.length;
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listview_item_buy_history, null);
            }

            LinearLayout layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
            ImageView imgCar = (ImageView) convertView.findViewById(R.id.imgCar);
            TextView txtTransaction = (TextView) convertView.findViewById(R.id.txtTransaction);
            TextView txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            TextView txtBrand = (TextView) convertView.findViewById(R.id.txtBrand);
            TextView txtPrice = (TextView) convertView.findViewById(R.id.txtPrice);
            TextView txtStartDate = (TextView) convertView.findViewById(R.id.txtStartDate);
            TextView txtEndDate = (TextView) convertView.findViewById(R.id.txtEndDate);
            TextView txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
            Button btnInformPayment = (Button) convertView.findViewById(R.id.btnInformPayment);

            final String ID = arrList[position][0].toString();
            final String Cover = arrList[position][1].toString();
            final String PKID = arrList[position][2].toString();
            final String Transaction = arrList[position][3].toString();
            final String Title = arrList[position][4].toString();
            final String Price = arrList[position][5].toString();
            final String Brand = arrList[position][6].toString();
            final String Model = arrList[position][7].toString();
            final String SubModel = arrList[position][8].toString();
            final String Status = arrList[position][9].toString();
            final String Type = arrList[position][10].toString();
            final String By = arrList[position][11].toString();
            final String EndDate = arrList[position][12].toString();
            final String Created = arrList[position][13].toString();

            // Cut String Date Time
            String[] separated = EndDate.split("-");
            String[] day = separated[2].split("T");
            String[] time = day[1].split("\\.");
            //String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + day[1];
            final String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + time[0];

            txtTransaction.setText("รหัส : " + Transaction);
            txtTitle.setText(Title);
            txtBrand.setText(Brand + " " + Model);
            //txtPrice.setText("ราคา : " + Price);
            //txtStartDate.setText("Created : " + Created);
            txtEndDate.setText("เวลาปิด : " + dateTime);

            DecimalFormat formatter = new DecimalFormat("#,###,###");
            txtPrice.setText("ราคา : " + formatter.format(Integer.parseInt(Price)));

            /*switch (Status) {
                case "In Process":
                    txtStatus.setText("กำลังดำเนินการ");
                    txtStatus.setTextColor(Color.RED);
                    break;
                case "Finish":
                    txtStatus.setText("ซื้อสำเร็จ");
                    txtStatus.setTextColor(Color.GREEN);
                    break;
            }*/

            // set Image
            Glide.with(BuyHistoryActivity.this)
                    .load(Cover)
                    .into(imgCar);

            layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), UsedCarDetailsActivity.class);
                    intent.putExtra("UserId", UserId);
                    intent.putExtra("CustomerId", CustomerId);
                    intent.putExtra("PKID", PKID);
                    intent.putExtra("EndDate", dateTime);
                    intent.putExtra("Approve", "");
                    intent.putExtra("AuctionStatus", Status);
                    intent.putExtra("sendFrom", "tentHistory");
                    startActivity(intent);
                }
            });

            txtStatus.setText("สถานะ : " + Status);

            if (Status.equals("รอชำระเงิน") || Status.equals("ชำระเงินไม่สำเร็จ")) {
                btnInformPayment.setVisibility(View.VISIBLE);
            } else {
                btnInformPayment.setVisibility(View.GONE);
            }

            btnInformPayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getApplicationContext(), InformPaymentActivity.class);
                    intent.putExtra("UserId", UserId);
                    intent.putExtra("CustomerId", CustomerId);
                    intent.putExtra("carId", PKID);
                    startActivity(intent);
                }
            });

            return convertView;
        }
    }
}
