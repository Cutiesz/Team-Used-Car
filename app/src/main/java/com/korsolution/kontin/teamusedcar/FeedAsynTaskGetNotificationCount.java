package com.korsolution.kontin.teamusedcar;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Created by Kontin58 on 30/10/2560.
 */

public class FeedAsynTaskGetNotificationCount extends AsyncTask<String, Void, String> {

    protected ArrayList<JSONObject> feedDataList;
    Context context;
    TextView textOne;

    public FeedAsynTaskGetNotificationCount(Context con, TextView textView) {
        context = con;
        textOne = textView;
    }

    @Override
    protected String doInBackground(String... strings) {

        try{

            // 1. connect server with okHttp
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build();


            // 2. assign post data
            RequestBody postData = new FormBody.Builder()
                    .add("customerId", strings[1])
                    .build();

            Request request = new Request.Builder()
                    .url(strings[0])
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
            //Toast.makeText(context, s, Toast.LENGTH_LONG).show();
            s = s.replace("<?xml version=\"1.0\" encoding=\"utf-8\"?>", "");
            s = s.replace("<string xmlns=\"http://tempuri.org/\">", "");
            s = s.replace("</string>", "");
            s = "[" + s + "]";

            feedDataList = CuteFeedJsonUtil.feed(s);
            if (feedDataList != null) {
                for (int i = 0; i <= feedDataList.size(); i++) {

                    try {

                        String status = String.valueOf(feedDataList.get(i).getString("status"));
                        String data = String.valueOf(feedDataList.get(i).getString("data"));
                        String count = String.valueOf(feedDataList.get(i).getString("count"));

                        if (count.equals("0")) {
                            textOne.setVisibility(View.GONE);
                        } else {
                            textOne.setVisibility(View.VISIBLE);
                            textOne.setText(count);
                        }

                        ArrayList<JSONObject> feedDataListData = CuteFeedJsonUtil.feed(data);
                        if (feedDataListData != null) {
                            for (int j = 0; j <= feedDataListData.size(); j++) {

                                try {

                                    String pkid = String.valueOf(feedDataListData.get(j).getString("pkid"));
                                    String text = String.valueOf(feedDataListData.get(j).getString("text"));
                                    String type = String.valueOf(feedDataListData.get(j).getString("type"));
                                    String car_id = String.valueOf(feedDataListData.get(j).getString("car_id"));
                                    String is_read = String.valueOf(feedDataListData.get(j).getString("is_read"));

                                /* type
                                รถเข้าใหม่ = 1,//send to all tent
                                มีการซื้อรถ = 2,//send to showroom
                                มีการบิด = 3,//send to showroom
                                ชนะการประมูล = 4,//send to tent
                                จบประมูล = 5, //send to showroom
                                ถ่ายรูปส่งสลิป = 6, //send to shoroom
                                ชำระสำเร็จ = 7, //send to tent
                                */

                                } catch (Exception e) {

                                }
                            }
                        }

                    } catch (Exception e) {

                    }
                }
            }

        } else {
            Toast.makeText(context, "Fail!!", Toast.LENGTH_LONG).show();
        }
    }
}
