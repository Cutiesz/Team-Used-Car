package com.korsolution.kontin.teamusedcar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
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

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.util.ArrayList;

public class UsedCarSellingDetailsImageActivity extends AppCompatActivity {

    private ListView lvCarImage;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListCar;

    private FeedNewsListViewAdapter mAdapter;

    private String ResultUsedCarDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_used_car_selling_details_image);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        ResultUsedCarDetails = getIntent().getStringExtra("ResultUsedCarDetails");

        setupWidgets();

        loadImage();
    }

    private void setupWidgets() {

        lvCarImage = (ListView) findViewById(R.id.lvCarImage);
    }
    
    private void loadImage() {

        feedDataList = CuteFeedJsonUtil.feed(ResultUsedCarDetails);
        if (feedDataList != null) {
            for (int i = 0; i <= feedDataList.size(); i++) {
                try {

                    String Status = String.valueOf(feedDataList.get(i).getString("status"));
                    String data = String.valueOf(feedDataList.get(i).getString("data"));

                    feedDataListCar = CuteFeedJsonUtil.feed("["+data+"]");
                    if (feedDataListCar != null) {

                        mAdapter = new FeedNewsListViewAdapter();
                        lvCarImage.setAdapter(mAdapter);
                    }

                } catch (Exception e) {

                }
            }
        }
    }

    public class FeedNewsListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 11;
        }

        @Override
        public Object getItem(int position) {
            return 11;
        }

        @Override
        public long getItemId(int position) {
            return 11;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;

            if (convertView == null) {
                convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.listview_item_car_img, null);
                holder = new ViewHolder();

                holder.mImageView = (ImageView) convertView.findViewById(R.id.mImageView);

                convertView.setTag(R.id.mImageView,  holder.mImageView);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();

                holder.mImageView = (ImageView) convertView.getTag(R.id.mImageView);
            }

            // Set Data
            if (feedDataListCar != null) {
                try {

                    String img_front = String.valueOf(feedDataListCar.get(0).getString("img_front"));
                    String img45_v1 = String.valueOf(feedDataListCar.get(0).getString("img45_v1"));
                    String img45_v2 = String.valueOf(feedDataListCar.get(0).getString("img45_v2"));
                    String img_back = String.valueOf(feedDataListCar.get(0).getString("img_back"));
                    String img_engine = String.valueOf(feedDataListCar.get(0).getString("img_engine"));
                    String img_lever = String.valueOf(feedDataListCar.get(0).getString("img_lever"));
                    String img_inner1 = String.valueOf(feedDataListCar.get(0).getString("img_inner1"));
                    String img_inner2 = String.valueOf(feedDataListCar.get(0).getString("img_inner2"));
                    String img_inner3 = String.valueOf(feedDataListCar.get(0).getString("img_inner3"));
                    String img_inner4 = String.valueOf(feedDataListCar.get(0).getString("img_inner4"));
                    String img_inner5 = String.valueOf(feedDataListCar.get(0).getString("img_inner5"));

                    switch (position) {
                        case 0:
                            // set Image
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img_front)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 1:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img45_v1)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 2:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img45_v2)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 3:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img_back)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 4:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img_engine)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 5:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img_lever)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 6:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img_inner1)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 7:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img_inner2)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 8:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img_inner3)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 9:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img_inner4)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                        case 10:
                            Glide.with(UsedCarSellingDetailsImageActivity.this)
                                    .load(img_inner5)
                                    .error(R.drawable.blank_img)
                                    .into(holder.mImageView);
                            break;
                    }

                    holder.mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            
                        }
                    });

                } catch (Exception e) {

                }
            }

            return convertView;
        }

        public class ViewHolder {
            ImageView mImageView;
        }
    }
}
