package com.korsolution.kontin.teamusedcar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anton46.stepsview.StepsView;
import com.bumptech.glide.Glide;
import com.korsolution.kontin.teamusedcar.R;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Kontin58 on 5/9/2560.
 */

public class ShowroomUsedCarAdapter extends RecyclerView.Adapter<ShowroomUsedCarAdapter.ViewHolder> {


    private Context mContext;

    protected ArrayList<JSONObject> feedDataList;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layout1;
        ImageView imgCar;
        TextView txtBrand;
        TextView txtLicensePlate;
        TextView txtStartPrice;
        TextView txtBidPrice;
        TextView txtPrice;
        TextView txtStatus;
        TextView txtApprove;
        TextView txtDate;
        ImageView imgSold;
        StepsView stepsView;

        public ViewHolder(View view) {
            super(view);

            layout1 = (LinearLayout) view.findViewById(R.id.layout1);
            imgCar = (ImageView) view.findViewById(R.id.imgCar);
            txtBrand = (TextView) view.findViewById(R.id.txtBrand);
            txtLicensePlate = (TextView) view.findViewById(R.id.txtLicensePlate);
            txtStartPrice = (TextView) view.findViewById(R.id.txtStartPrice);
            txtBidPrice = (TextView) view.findViewById(R.id.txtBidPrice);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtStatus = (TextView) view.findViewById(R.id.txtStatus);
            txtApprove = (TextView) view.findViewById(R.id.txtApprove);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            imgSold = (ImageView) view.findViewById(R.id.imgSold);
            stepsView = (StepsView) view.findViewById(R.id.stepsView);
        }
    }

    public ShowroomUsedCarAdapter(Context context, ArrayList<JSONObject> dataset) {
        feedDataList = dataset;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_view_showroom_usedcar_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        if (feedDataList != null) {
            try {

                final String carId = String.valueOf(feedDataList.get(position).getString("carId"));
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
                final String status = String.valueOf(feedDataList.get(position).getString("status"));
                final String approve = String.valueOf(feedDataList.get(position).getString("approve"));

                /*Glide.with(mContext)
                        .load(cover)
                        .into(viewHolder.imgCar);*/

                viewHolder.txtBrand.setText(year + " " + brand + " " + model);
                viewHolder.txtApprove.setText(approve);

                DecimalFormat formatter = new DecimalFormat("#,###,###");
                viewHolder.txtStartPrice.setText("ราคาประมูลเริ่มต้น : " + formatter.format(Integer.parseInt(start_price)));
                viewHolder.txtBidPrice.setText("ราคาประมูลปัจจุบัน : " + formatter.format(Integer.parseInt(bid_price)));
                viewHolder.txtPrice.setText("ราคาขาย : " + formatter.format(Integer.parseInt(buy_price)));

                // set Image
                Glide.with(mContext)
                        .load(cover)
                        .error(R.drawable.blank_img)
                        .into(viewHolder.imgCar);

                // status : กำลังประมูล -> รอชำระเงิน -> รถเดินทาง -> รถถึงบริษัท -> รับรถแล้ว
                String[] labels = {"รอชำระเงิน", "รถเดินทาง", "ถึงบริษัท", "รับรถแล้ว"};

                if (status.equals("กำลังประมูล")) {
                    viewHolder.txtStartPrice.setVisibility(View.VISIBLE);
                    viewHolder.txtPrice.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.txtStartPrice.setVisibility(View.GONE);
                    viewHolder.txtBidPrice.setText("ราคาปิดประมูล : " + formatter.format(Integer.parseInt(bid_price)));
                    viewHolder.txtPrice.setVisibility(View.GONE);
                }

                switch (status) {
                    case "กำลังประมูล":
                        viewHolder.txtStatus.setText("กำลังประมูล");
                        viewHolder.txtStatus.setTextColor(Color.BLUE);

                        viewHolder.stepsView.setVisibility(View.GONE);
                        break;
                    case "รอชำระเงิน":
                        viewHolder.txtStatus.setText("รอชำระเงิน");
                        viewHolder.txtStatus.setTextColor(Color.RED);

                        viewHolder.stepsView.setVisibility(View.VISIBLE);
                        viewHolder.stepsView.setCompletedPosition(labels.length - 4)
                                .setLabels(labels)
                                .setBarColorIndicator(mContext.getResources().getColor(R.color.material_blue_grey_800))
                                .setProgressColorIndicator(mContext.getResources().getColor(R.color.colorPrimary))
                                .setLabelColorIndicator(mContext.getResources().getColor(R.color.colorPrimary))
                                .drawView();
                        break;
                    case "รถเดินทาง":
                        viewHolder.txtStatus.setText("รถเดินทาง");
                        viewHolder.txtStatus.setTextColor(Color.RED);

                        viewHolder.stepsView.setVisibility(View.VISIBLE);
                        viewHolder.stepsView.setCompletedPosition(labels.length - 3)
                                .setLabels(labels)
                                .setBarColorIndicator(mContext.getResources().getColor(R.color.material_blue_grey_800))
                                .setProgressColorIndicator(mContext.getResources().getColor(R.color.colorPrimary))
                                .setLabelColorIndicator(mContext.getResources().getColor(R.color.colorPrimary))
                                .drawView();
                        break;
                    case "รถถึงบริษัท":
                        viewHolder.txtStatus.setText("รถถึงบริษัท");
                        viewHolder.txtStatus.setTextColor(Color.RED);

                        viewHolder.stepsView.setVisibility(View.VISIBLE);
                        viewHolder.stepsView.setCompletedPosition(labels.length - 2)
                                .setLabels(labels)
                                .setBarColorIndicator(mContext.getResources().getColor(R.color.material_blue_grey_800))
                                .setProgressColorIndicator(mContext.getResources().getColor(R.color.colorPrimary))
                                .setLabelColorIndicator(mContext.getResources().getColor(R.color.colorPrimary))
                                .drawView();
                        break;
                    case "รับรถแล้ว":
                        viewHolder.txtStatus.setText("รับรถแล้ว");
                        viewHolder.txtStatus.setTextColor(Color.RED);

                        viewHolder.stepsView.setVisibility(View.VISIBLE);
                        viewHolder.stepsView.setCompletedPosition(labels.length - 1)
                                .setLabels(labels)
                                .setBarColorIndicator(mContext.getResources().getColor(R.color.material_blue_grey_800))
                                .setProgressColorIndicator(mContext.getResources().getColor(R.color.colorPrimary))
                                .setLabelColorIndicator(mContext.getResources().getColor(R.color.colorPrimary))
                                .drawView();
                        break;
                    default:
                        viewHolder.txtStatus.setText(status);
                        viewHolder.stepsView.setVisibility(View.GONE);
                        break;
                }

                if (approve.equals("อนุมัติแล้ว")) {
                    viewHolder.txtApprove.setVisibility(View.GONE);
                    viewHolder.txtStatus.setVisibility(View.VISIBLE);
                } else {
                    viewHolder.txtApprove.setVisibility(View.VISIBLE);
                    viewHolder.txtStatus.setVisibility(View.GONE);
                }

                // Cut String Date Time
                String[] separated = created.split("-");
                String[] day = separated[2].split("T");
                String[] time = day[1].split("\\.");
                //String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + day[1];
                final String dateTime = day[0] + "/" + separated[1] + "/" + separated[0] + " " + time[0];
                viewHolder.txtDate.setText("วันที่ลง : " + dateTime);

            }catch (Exception e) {

            }
        }

    }


    @Override
    public int getItemCount() {
        return feedDataList.size();
    }


    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ShowroomUsedCarAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ShowroomUsedCarAdapter.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
}
