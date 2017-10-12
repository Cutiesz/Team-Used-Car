package com.korsolution.kontin.teamusedcar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class AddUsedCar4CheckListActivity extends AppCompatActivity {

    private ListView lvCheckList;
    private Button btnNext;

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

    private FeedNewsListViewAdapter mAdapter;
    final int delay = 1000;
    private boolean mIsLoading = true;
    private boolean hasLoadedAll = false;
    private int mPageIndex = 1;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListChecklist;

    String checkList = "";
    String strProperties = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_used_car_check_list);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

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

        setupWidgets();

        loadChecklist();

        // check box
        // send id, when more id send ex. 1|2|5|6
    }

    private void setupWidgets() {

        lvCheckList = (ListView) findViewById(R.id.lvCheckList);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddUsedCar5PhotoActivity.class);
                intent.putExtra("LicensePlateFront", LicensePlateFront);
                intent.putExtra("LicensePlateBack", LicensePlateBack);
                intent.putExtra("LicensePlateProvince", LicensePlateProvince);
                intent.putExtra("ProvinceID", ProvinceID);
                intent.putExtra("CarYear", CarYear);
                intent.putExtra("CarBrand", CarBrand);
                intent.putExtra("CarGeneration", CarGeneration);
                intent.putExtra("CarSubGeneration", CarSubGeneration);
                intent.putExtra("CarColor", CarColor);
                intent.putExtra("CarGearType", CarGearType);
                intent.putExtra("Title", Title);
                intent.putExtra("Miles", Miles);
                intent.putExtra("CarImages", CarDetails);
                intent.putExtra("RepairHistory", RepairHistory);
                intent.putExtra("CheckList", checkList);
                intent.putExtra("StrProperties", strProperties);
                startActivity(intent);
            }
        });
    }

    private void loadChecklist() {

        String strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        new FeedAsynTask().execute(strWebServiceUrl + "GetCheckList");
    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(AddUsedCar4CheckListActivity.this);
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
                        //.add("customerId", params[1])
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

                feedDataListChecklist = CuteFeedJsonUtil.feed(s);
                if (feedDataListChecklist != null) {
                    for (int i = 0; i <= feedDataListChecklist.size(); i++) {
                        try {

                            String status = String.valueOf(feedDataListChecklist.get(i).getString("status"));
                            String data = String.valueOf(feedDataListChecklist.get(i).getString("data"));

                            feedDataList = CuteFeedJsonUtil.feed(data);
                            if (feedDataList != null) {

                                mAdapter = new FeedNewsListViewAdapter();
                                lvCheckList.setAdapter(mAdapter);
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
            final ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_item_checklist, null);
                holder = new ViewHolder();

                holder.cbChecklist = (CheckBox) convertView.findViewById(R.id.cbChecklist);

                convertView.setTag(R.id.cbChecklist,  holder.cbChecklist);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

                holder.cbChecklist = (CheckBox) convertView.findViewById(R.id.cbChecklist);
            }

            // Set Data
            if (feedDataList != null) {
                try {

                    final String Id = String.valueOf(feedDataList.get(position).getString("Id"));
                    final String Text = String.valueOf(feedDataList.get(position).getString("Text"));

                    holder.cbChecklist.setText(Text);

                    holder.cbChecklist.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                            if (holder.cbChecklist.isChecked()) {
                                //Toast.makeText(getBaseContext(), Id, Toast.LENGTH_SHORT).show();

                                if (checkList.equals("")) {
                                    checkList = Id;
                                    strProperties = Text;
                                } else {
                                    checkList += "|" + Id;
                                    strProperties += "," + Text;;
                                }

                            } else {

                                if (checkList.contains("|")) {
                                    if (checkList.contains("|"+Id)) {
                                        checkList = checkList.replace("|"+Id, "");
                                        strProperties = strProperties.replace(","+Text, "");
                                    } else {
                                        checkList = checkList.replace(Id+"|", "");
                                        strProperties = strProperties.replace(Text+",", "");
                                    }
                                } else {
                                    checkList = "";
                                    strProperties = "";
                                }
                            }

                            //Toast.makeText(getBaseContext(), checkList, Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e) {

                }
            }

            return convertView;
        }

        public class ViewHolder {
            CheckBox cbChecklist;
        }
    }
}
