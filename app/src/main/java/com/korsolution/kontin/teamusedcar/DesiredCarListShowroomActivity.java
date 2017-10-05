package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

public class DesiredCarListShowroomActivity extends AppCompatActivity {

    private SwipeRefreshLayout mRefreshView;
    private ListView lvDesiredCar;
    private TextView txtNoData;
    private FloatingActionButton fabSearch;

    private String UserId;
    private String CustomerId;

    String strWebServiceUrl;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListDesiredCar;

    private FeedNewsListViewAdapter mAdapter;

    private CarColorDBClass CarColorDB;
    protected ArrayList<JSONObject> feedDataListColor;

    private DesiredCarDBClass DesiredCarDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desired_car_list_showroom);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        UserId = getIntent().getStringExtra("UserId");
        CustomerId = getIntent().getStringExtra("CustomerId");

        CarColorDB = new CarColorDBClass(this);
        DesiredCarDB = new DesiredCarDBClass(this);

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        setupWidgets();

        new FeedAsynTaskColor().execute(strWebServiceUrl + "GetCarColor");

        loadDesiredCarList();
    }

    private void setupWidgets() {

        mRefreshView = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        lvDesiredCar = (ListView) findViewById(R.id.lvDesiredCar);
        txtNoData = (TextView) findViewById(R.id.txtNoData);
        fabSearch = (FloatingActionButton) findViewById(R.id.fabSearch);

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), SearchDesiredCarActivity.class);
                startActivityForResult(i, 1);

                overridePendingTransition(R.anim.slide_up_info, R.anim.no_change);
            }
        });

        mRefreshView.setColorSchemeResources(android.R.color.holo_blue_bright);
        mRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedDataList.clear();
                //mAdapter.notifyDataSetChanged();
                //mPageIndex = 1;
                //hasLoadedAll = false;

                // reload
                //mIsLoading = true;
                new FeedAsynTaskColor().execute(strWebServiceUrl + "GetCarColor");
                loadDesiredCarList();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == SearchCarActivity.RESULT_OK){
                //String result = data.getStringExtra("result");
                //Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();

                String year = data.getStringExtra("year");
                String brand = data.getStringExtra("brand");
                String generation = data.getStringExtra("generation");
                //String subGeneration = data.getStringExtra("subGeneration");
                String carColor = data.getStringExtra("carColor");

                lvDesiredCar.setAdapter(null);

                loadData(year, brand, generation, carColor);

            }
            if (resultCode == SearchCarActivity.RESULT_CANCELED) {
                //Write your code if there's no result

                //Toast.makeText(getBaseContext(), "Cancle!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadData(String year, String brand, String generation, String carColor) {

        String[][] arrData = DesiredCarDB.SelectData(brand, generation, carColor, year);
        if (arrData != null) {
            lvDesiredCar.setAdapter(new ImageAdapter(DesiredCarListShowroomActivity.this, arrData));
        } else {
            txtNoData.setVisibility(View.VISIBLE);
            txtNoData.setText("ไม่มีรถที่ต้องการหา");
        }
    }

    private void loadDesiredCarList() {

        new FeedAsynTask().execute(strWebServiceUrl + "GetSearchListByShowroom", CustomerId);
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(DesiredCarListShowroomActivity.this);
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

                txtNoData.setVisibility(View.GONE);

                mRefreshView.setRefreshing(false);

                DesiredCarDB.Delete();

                feedDataListDesiredCar = CuteFeedJsonUtil.feed(s);
                if (feedDataListDesiredCar != null) {
                    for (int i = 0; i <= feedDataListDesiredCar.size(); i++) {
                        try {

                            String status = String.valueOf(feedDataListDesiredCar.get(i).getString("status"));
                            String data = String.valueOf(feedDataListDesiredCar.get(i).getString("data"));

                            if (data.equals("[]")) {
                                txtNoData.setVisibility(View.VISIBLE);
                                txtNoData.setText("ไม่มีรถที่ต้องการหา");
                            }

                            feedDataList = CuteFeedJsonUtil.feed(data);
                            if (feedDataList != null) {
                                for (int j = 0; j < feedDataList.size(); j++) {
                                    String brand = String.valueOf(feedDataList.get(j).getString("brand"));
                                    String model = String.valueOf(feedDataList.get(j).getString("model"));
                                    String color = String.valueOf(feedDataList.get(j).getString("color"));
                                    String year = String.valueOf(feedDataList.get(j).getString("year"));
                                    String date = String.valueOf(feedDataList.get(j).getString("date"));
                                    String count = String.valueOf(feedDataList.get(j).getString("count"));

                                    DesiredCarDB.Insert(brand, model, color, year, date, count);
                                }

                                String[][] arrData = DesiredCarDB.SelectAll();
                                if (arrData != null) {
                                    lvDesiredCar.setAdapter(new ImageAdapter(DesiredCarListShowroomActivity.this, arrData));
                                } else {
                                    txtNoData.setVisibility(View.VISIBLE);
                                    txtNoData.setText("ไม่มีรถที่ต้องการหา");
                                }


                                //mAdapter = new FeedNewsListViewAdapter();
                                //lvDesiredCar.setAdapter(mAdapter);

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

                        try {

                            String status = String.valueOf(feedDataListDesiredCar.get(i).getString("status"));
                            String msg = String.valueOf(feedDataListDesiredCar.get(i).getString("msg"));

                            if (status.equals("fail")) {
                                txtNoData.setVisibility(View.VISIBLE);
                                txtNoData.setText("ไม่มีรถที่ต้องการหา");
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
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_item_desired_car, null);
                holder = new ViewHolder();

                holder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
                holder.txtBrand = (TextView) convertView.findViewById(R.id.txtBrand);
                holder.txtAmount = (TextView) convertView.findViewById(R.id.txtAmount);
                holder.txtColorTest = (TextView) convertView.findViewById(R.id.txtColorTest);
                holder.txtColor = (TextView) convertView.findViewById(R.id.txtColor);
                holder.txtDate = (TextView) convertView.findViewById(R.id.txtDate);

                convertView.setTag(R.id.layout1,  holder.layout1);
                convertView.setTag(R.id.txtBrand,  holder.txtBrand);
                convertView.setTag(R.id.txtAmount,  holder.txtAmount);
                convertView.setTag(R.id.txtColorTest,  holder.txtColorTest);
                convertView.setTag(R.id.txtColor,  holder.txtColor);
                convertView.setTag(R.id.txtDate,  holder.txtDate);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

                holder.layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
                holder.txtBrand = (TextView) convertView.getTag(R.id.txtBrand);
                holder.txtAmount = (TextView) convertView.getTag(R.id.txtAmount);
                holder.txtColorTest = (TextView) convertView.getTag(R.id.txtColorTest);
                holder.txtColor = (TextView) convertView.getTag(R.id.txtColor);
                holder.txtDate = (TextView) convertView.getTag(R.id.txtDate);
            }

            // Set Data
            if (feedDataList != null) {
                try {

                    String brand = String.valueOf(feedDataList.get(position).getString("brand"));
                    String model = String.valueOf(feedDataList.get(position).getString("model"));
                    String color = String.valueOf(feedDataList.get(position).getString("color"));
                    String year = String.valueOf(feedDataList.get(position).getString("year"));
                    String date = String.valueOf(feedDataList.get(position).getString("date"));
                    String count = String.valueOf(feedDataList.get(position).getString("count"));

                    // Cut String Date Time
                    String[] separated = date.split("-");
                    String[] day = separated[2].split("T");
                    String[] time = day[1].split("\\.");
                    //String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + day[1];
                    final String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + time[0];

                    holder.txtBrand.setText(year + " " + brand + " " + model);

                    holder.txtAmount.setText("" + count + " คัน");

                    String[] arrColorCode = CarColorDB.SelectColorCodeByColorName(color);
                    if (arrColorCode != null) {
                        holder.txtColorTest.setBackgroundColor(Color.parseColor(arrColorCode[0]));
                    }
                    holder.txtColor.setText("สี" + color);

                    holder.txtDate.setText("วันที่ลง : " + dateTime);

                } catch (Exception e) {

                }
            }

            return convertView;
        }

        public class ViewHolder {
            LinearLayout layout1;
            TextView txtBrand;
            TextView txtAmount;
            TextView txtColorTest;
            TextView txtColor;
            TextView txtDate;
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
                convertView = inflater.inflate(R.layout.listview_item_desired_car, null);
            }

            LinearLayout layout1 = (LinearLayout) convertView.findViewById(R.id.layout1);
            TextView txtBrand = (TextView) convertView.findViewById(R.id.txtBrand);
            TextView txtAmount = (TextView) convertView.findViewById(R.id.txtAmount);
            TextView txtColorTest = (TextView) convertView.findViewById(R.id.txtColorTest);
            TextView txtColor = (TextView) convertView.findViewById(R.id.txtColor);
            TextView txtDate = (TextView) convertView.findViewById(R.id.txtDate);

            final String ID = arrList[position][0].toString();
            final String brand = arrList[position][1].toString();
            final String model = arrList[position][2].toString();
            final String color = arrList[position][3].toString();
            final String year = arrList[position][4].toString();
            final String date = arrList[position][5].toString();
            final String count = arrList[position][6].toString();

            // Cut String Date Time
            String[] separated = date.split("-");
            String[] day = separated[2].split("T");
            String[] time = day[1].split("\\.");
            //String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + day[1];
            final String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + time[0];

            txtBrand.setText(year + " " + brand + " " + model);

            txtAmount.setText("" + count + " คัน");

            String[] arrColorCode = CarColorDB.SelectColorCodeByColorName(color);
            if (arrColorCode != null) {
                txtColorTest.setBackgroundColor(Color.parseColor(arrColorCode[0]));
            }
            txtColor.setText("สี" + color);

            txtDate.setText("วันที่ลง : " + dateTime);

            return convertView;
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
}
