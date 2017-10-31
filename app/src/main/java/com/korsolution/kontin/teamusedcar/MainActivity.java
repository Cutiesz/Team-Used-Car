package com.korsolution.kontin.teamusedcar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.korsolution.kontin.teamusedcar.activity.ShowroomTabActivity;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    private ImageView splashImageView;

    private AccountDBClass AccountDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        AccountDB = new AccountDBClass(this);

        runSplashPage(3000);
        initial();
    }

    private void initial() {
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {

                String[][] arrData = AccountDB.SelectAllAccount();
                if (arrData != null) {

                    String UserId = arrData[0][1].toString();
                    String CustomerId = arrData[0][2].toString();
                    String SupplierType = arrData[0][11].toString();

                    Bundle extras = getIntent().getExtras();
                    if (extras != null) {
                        String valueGetIntent = extras.getString(Intent.EXTRA_TEXT);
                        if (valueGetIntent != null) {
                            /*String MSG = extras.getString("MSG");
                            if (MSG != null) {
                                valueGetIntent = MSG;
                            }*/
                            String type = extras.getString("type");
                            if (type != null) {
                                valueGetIntent = type;
                            }
                            String car_id = extras.getString("car_id");
                            if (car_id != null) {
                                valueGetIntent = car_id;
                            }

                            if (valueGetIntent.contains("youtu")) {

                                switch (SupplierType) {
                                    case "SHOWROOM":
                                        Intent intent = new Intent(getApplicationContext(), AddUsedCarYoutubeLinkActivity.class);
                                        intent.putExtra("UserId", UserId);
                                        intent.putExtra("CustomerId", CustomerId);
                                        intent.putExtra("valueGetIntent", valueGetIntent);
                                        startActivity(intent);
                                        break;
                                    case "TENT":
                                        Intent intent1 = new Intent(getApplicationContext(), UsedCarSellingListActivity.class);
                                        intent1.putExtra("UserId", UserId);
                                        intent1.putExtra("CustomerId", CustomerId);
                                        startActivity(intent1);
                                        break;
                                }

                            } else {

                                switch (SupplierType) {
                                    case "SHOWROOM":
                                        Intent intent = new Intent(getApplicationContext(), AddUsedCarYoutubeLinkActivity.class);
                                        intent.putExtra("UserId", UserId);
                                        intent.putExtra("CustomerId", CustomerId);
                                        intent.putExtra("valueGetIntent", valueGetIntent);
                                        startActivity(intent);
                                        break;
                                    case "TENT":
                                        Intent intent1 = new Intent(getApplicationContext(), UsedCarSellingListActivity.class);
                                        intent1.putExtra("UserId", UserId);
                                        intent1.putExtra("CustomerId", CustomerId);
                                        startActivity(intent1);
                                        break;
                                }
                            }
                        } else {

                            /* type
                                รถเข้าใหม่ = 1,//send to all tent
                                มีการซื้อรถ = 2,//send to showroom
                                มีการบิด = 3,//send to showroom
                                ชนะการประมูล = 4,//send to tent
                                จบประมูล = 5, //send to showroom
                                ถ่ายรูปส่งสลิป = 6, //send to shoroom
                                ชำระสำเร็จ = 7, //send to tent
                            */

                            String type = extras.getString("type");
                            String car_id = extras.getString("car_id");
                            if (type != null && car_id != null) {

                                switch (SupplierType) {
                                    case "SHOWROOM":
                                        Intent intent = new Intent(getApplicationContext(), AddUsedCarYoutubeLinkActivity.class);
                                        intent.putExtra("UserId", UserId);
                                        intent.putExtra("CustomerId", CustomerId);
                                        intent.putExtra("valueGetIntent", car_id);
                                        startActivity(intent);
                                        break;
                                    case "TENT":
                                        Intent intent1 = new Intent(getApplicationContext(), UsedCarSellingListActivity.class);
                                        intent1.putExtra("UserId", UserId);
                                        intent1.putExtra("CustomerId", CustomerId);
                                        startActivity(intent1);
                                        break;
                                }
                            } else {

                                switch (SupplierType) {
                                    case "SHOWROOM":
                                        //Intent intent = new Intent(getApplicationContext(), UsedCarListActivity.class);
                                        Intent intent = new Intent(getApplicationContext(), ShowroomTabActivity.class);
                                        intent.putExtra("UserId", UserId);
                                        intent.putExtra("CustomerId", CustomerId);
                                        startActivity(intent);
                                        break;
                                    case "TENT":
                                        Intent intent1 = new Intent(getApplicationContext(), UsedCarSellingListActivity.class);
                                        intent1.putExtra("UserId", UserId);
                                        intent1.putExtra("CustomerId", CustomerId);
                                        startActivity(intent1);
                                        break;
                                }
                            }
                        }
                    } else {

                        switch (SupplierType) {
                            case "SHOWROOM":
                                //Intent intent = new Intent(getApplicationContext(), UsedCarListActivity.class);
                                Intent intent = new Intent(getApplicationContext(), ShowroomTabActivity.class);
                                intent.putExtra("UserId", UserId);
                                intent.putExtra("CustomerId", CustomerId);
                                startActivity(intent);
                                break;
                            case "TENT":
                                Intent intent1 = new Intent(getApplicationContext(), UsedCarSellingListActivity.class);
                                intent1.putExtra("UserId", UserId);
                                intent1.putExtra("CustomerId", CustomerId);
                                startActivity(intent1);
                                break;
                        }
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }

            }
        },3000);
    }

    private void runSplashPage(int i) {
        splashImageView = (ImageView) findViewById(R.id.splashImageView);
        splashImageView.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashImageView.setVisibility(View.GONE);
            }
        }, i);
    }
}
