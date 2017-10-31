package com.korsolution.kontin.teamusedcar.activity;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.korsolution.kontin.teamusedcar.CuteFeedJsonUtil;
import com.korsolution.kontin.teamusedcar.R;
import com.korsolution.kontin.teamusedcar.UsedCarDetailsActivity;
import com.korsolution.kontin.teamusedcar.adapter.ShowroomUsedCarAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowroomAuctionedListFragment extends Fragment {

    private SwipeRefreshLayout mRefreshView;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private TextView txtNoData;

    private AVLoadingIndicatorView avi;

    protected ArrayList<JSONObject> feedDataList;
    protected ArrayList<JSONObject> feedDataListUsedCar;

    private String UserId;
    private String CustomerId;

    final int delay = 1000;
    private boolean mIsLoading = true;
    private boolean hasLoadedAll = false;
    private int mPageIndex = 1;

    String strWebServiceUrl;


    public ShowroomAuctionedListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(UserId == null && getArguments() != null) {
            String UserId = getArguments().getString("UserId");
            this.UserId = UserId;
        }

        if(CustomerId == null && getArguments() != null) {
            String CustomerId = getArguments().getString("CustomerId");
            this.CustomerId = CustomerId;
        }

        return inflater.inflate(R.layout.fragment_showroom_auctioned_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

        setupWidgets(view);
        loadListUsedCar();

        //Toast.makeText(getActivity(), UserId + " " + CustomerId, Toast.LENGTH_LONG).show();
    }

    private void setupWidgets(View view) {

        mRefreshView = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        txtNoData = (TextView) view.findViewById(R.id.txtNoData);

        avi = (AVLoadingIndicatorView) view.findViewById(R.id.avi);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);     // VERTICAL

        mRecyclerView.addOnItemTouchListener(new ShowroomUsedCarAdapter.RecyclerTouchListener(getActivity(), mRecyclerView, new ShowroomUsedCarAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (feedDataList != null) {
                    try {
                        String carId = String.valueOf(feedDataList.get(position).getString("carId"));
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
                        String status = String.valueOf(feedDataList.get(position).getString("status"));
                        String approve = String.valueOf(feedDataList.get(position).getString("approve"));

                        //Toast.makeText(getActivity(), brand, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getActivity(), UsedCarDetailsActivity.class);
                        intent.putExtra("UserId", UserId);
                        intent.putExtra("CustomerId", CustomerId);
                        intent.putExtra("PKID", carId);
                        //intent.putExtra("EndDate", dateTime);
                        intent.putExtra("Approve", approve);
                        intent.putExtra("AuctionStatus", status);
                        intent.putExtra("sendFrom", "showroomList");
                        startActivity(intent);

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
                loadListUsedCar();
            }
        });
    }

    private void loadListUsedCar() {

        // type
        //1 = รถที่พึ่งลงและรถที่กำลังประมูล
        //2 = รถที่รอชำระเงิน
        //3 = รถที่ขาย/ประมูลแล้ว

        new FeedAsynTask().execute(strWebServiceUrl + "ListShowRoomCarByOwner", CustomerId, "3");
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

                            String _status = String.valueOf(feedDataListUsedCar.get(i).getString("status"));
                            String data = String.valueOf(feedDataListUsedCar.get(i).getString("data"));

                            if (data.equals("[]")) {
                                txtNoData.setVisibility(View.VISIBLE);
                                txtNoData.setText("No Data Available");
                            } else {
                                txtNoData.setVisibility(View.GONE);
                            }

                            feedDataList = CuteFeedJsonUtil.feed(data);
                            if (feedDataList != null) {
                                for (int j = 0; j <= feedDataList.size(); j++) {

                                    String carId = String.valueOf(feedDataList.get(j).getString("carId"));
                                    String cover = String.valueOf(feedDataList.get(j).getString("cover"));
                                    String title = String.valueOf(feedDataList.get(j).getString("title"));
                                    String year = String.valueOf(feedDataList.get(j).getString("year"));
                                    String brand = String.valueOf(feedDataList.get(j).getString("brand"));
                                    String model = String.valueOf(feedDataList.get(j).getString("model"));
                                    String sub_model = String.valueOf(feedDataList.get(j).getString("sub_model"));
                                    String km = String.valueOf(feedDataList.get(j).getString("km"));
                                    String repair = String.valueOf(feedDataList.get(j).getString("repair"));
                                    String start_price = String.valueOf(feedDataList.get(j).getString("start_price"));
                                    String bid_price = String.valueOf(feedDataList.get(j).getString("bid_price"));
                                    String buy_price = String.valueOf(feedDataList.get(j).getString("buy_price"));
                                    String created = String.valueOf(feedDataList.get(j).getString("created"));
                                    String status = String.valueOf(feedDataList.get(j).getString("status"));
                                    String approve = String.valueOf(feedDataList.get(j).getString("approve"));

                                    mAdapter = new ShowroomUsedCarAdapter(getActivity(), feedDataList);
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
                Toast.makeText(getActivity(), "Fail!!", Toast.LENGTH_LONG).show();
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

}
