package com.korsolution.kontin.teamusedcar.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.korsolution.kontin.teamusedcar.AccountDBClass;
import com.korsolution.kontin.teamusedcar.AddUsedCarYoutubeLinkActivity;
import com.korsolution.kontin.teamusedcar.CuteFeedJsonUtil;
import com.korsolution.kontin.teamusedcar.DesiredCarListShowroomActivity;
import com.korsolution.kontin.teamusedcar.LoginActivity;
import com.korsolution.kontin.teamusedcar.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import co.omise.android.models.Token;
import co.omise.android.ui.CreditCardActivity;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ShowroomTabActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private TextView tabOne;
    private TextView tabTwo;
    private TextView tabThree;

    private TextView txtName;
    private ImageView imgStar1;
    private ImageView imgStar2;
    private ImageView imgStar3;
    private ImageView imgStar4;
    private ImageView imgStar5;
    private TextView txtDeposit;
    private TextView txtAuctioned;
    private TextView txtSoldOut;
    private FloatingActionButton fabAdd;
    private ImageButton imgBtnDeposit;
    private Button btnDesiredCar;

    private String UserId;
    private String CustomerId;

    private AccountDBClass AccountDB;

    String strWebServiceUrl;

    // Omise
    //private static final String OMISE_PKEY = "pkey_test_5927y4ui9k4lx17wmuz";
    private static final String OMISE_PKEY = "pkey_test_59ccnhqxthw3rt925fd";
    private static final int REQUEST_CC = 100;

    private String addDeposit;

    protected ArrayList<JSONObject> feedDataListShowroomDetails;
    protected ArrayList<JSONObject> feedDataListToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showroom_tab);

        UserId = getIntent().getStringExtra("UserId");
        CustomerId = getIntent().getStringExtra("CustomerId");

        AccountDB = new AccountDBClass(this);

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);

        //tabLayout = (TabLayout) findViewById(R.id.tabs);
        //tabLayout.setupWithViewPager(mViewPager);


        createViewPager(mViewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        //tabLayout.setBackgroundColor(Color.BLACK);
        createTabIcons();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                switch(tab.getPosition()) {
                    case 0:
                        tabOne.setTextColor(Color.parseColor("#FFFFFF"));
                        tabTwo.setTextColor(Color.parseColor("#9E9E9E"));
                        tabThree.setTextColor(Color.parseColor("#9E9E9E"));
                        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car, 0, 0);
                        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car_unselected, 0, 0);
                        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car_unselected, 0, 0);
                        break;
                    case 1:
                        tabOne.setTextColor(Color.parseColor("#9E9E9E"));
                        tabTwo.setTextColor(Color.parseColor("#FFFFFF"));
                        tabThree.setTextColor(Color.parseColor("#9E9E9E"));
                        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car_unselected, 0, 0);
                        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car, 0, 0);
                        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car_unselected, 0, 0);
                        break;
                    case 2:
                        tabOne.setTextColor(Color.parseColor("#9E9E9E"));
                        tabTwo.setTextColor(Color.parseColor("#9E9E9E"));
                        tabThree.setTextColor(Color.parseColor("#FFFFFF"));
                        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car_unselected, 0, 0);
                        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car_unselected, 0, 0);
                        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car, 0, 0);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        setupWidgets();
        loadShowroomDetails();
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
        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        imgBtnDeposit = (ImageButton) findViewById(R.id.imgBtnDeposit);
        btnDesiredCar = (Button) findViewById(R.id.btnDesiredCar);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddUsedCarYoutubeLinkActivity.class);
                startActivity(intent);
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

    private void createTabIcons() {

        //TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setTextColor(Color.parseColor("#FFFFFF"));
        tabOne.setText("รถกำลังประมูล");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        //TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setTextColor(Color.parseColor("#9E9E9E"));
        tabTwo.setText("รอชำระเงิน");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car_unselected, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        //TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setTextColor(Color.parseColor("#9E9E9E"));
        tabThree.setText("ขาย/ประมูลจบแล้ว");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_action_car_unselected, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    private void createViewPager(ViewPager viewPager) {

        ShowroomAuctioningListFragment mFragmentAuctioning = new ShowroomAuctioningListFragment();
        ShowroomPendingListFragment mFragmentPending = new ShowroomPendingListFragment();
        ShowroomAuctionedListFragment mFragmentAuctioned = new ShowroomAuctionedListFragment();

        Bundle bundle = new Bundle();
        bundle.putString("UserId", UserId);
        bundle.putString("CustomerId", CustomerId);

        mFragmentAuctioning.setArguments(bundle);
        mFragmentPending.setArguments(bundle);
        mFragmentAuctioned.setArguments(bundle);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //adapter.addFrag(new ShowroomAuctioningListFragment(), "รถกำลังประมูล");
        //adapter.addFrag(new ShowroomPendingListFragment(), "รอชำระเงิน");
        //adapter.addFrag(new ShowroomAuctionedListFragment(), "ขาย/ประมูลจบแล้ว");
        adapter.addFrag(mFragmentAuctioning, "รถกำลังประมูล");
        adapter.addFrag(mFragmentPending, "รอชำระเงิน");
        adapter.addFrag(mFragmentAuctioned, "ขาย/ประมูลจบแล้ว");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
        AlertDialog.Builder mDialog = new AlertDialog.Builder(ShowroomTabActivity.this);

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


    private void loadShowroomDetails() {

        new FeedAsynTaskGetShowroomDetail().execute(strWebServiceUrl + "GetShowroomDetail", /*"29"*/CustomerId);
    }

    public class FeedAsynTaskGetShowroomDetail extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(ShowroomTabActivity.this);
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
            nDialog = new ProgressDialog(ShowroomTabActivity.this);
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
