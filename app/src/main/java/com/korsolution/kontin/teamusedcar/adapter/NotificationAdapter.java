package com.korsolution.kontin.teamusedcar.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.korsolution.kontin.teamusedcar.R;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Kontin58 on 31/10/2560.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context mContext;
    protected ArrayList<JSONObject> feedDataList;

    public NotificationAdapter(Context context, ArrayList<JSONObject> dataset) {
        feedDataList = dataset;
        mContext = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout layout1;
        TextView txtNotification;

        public ViewHolder(View view) {
            super(view);

            layout1 = (RelativeLayout) view.findViewById(R.id.layout1);
            txtNotification = (TextView) view.findViewById(R.id.txtNotification);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.recycler_view_notification_row, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (feedDataList != null) {
            try {

                String pkid = String.valueOf(feedDataList.get(position).getString("pkid"));
                String text = String.valueOf(feedDataList.get(position).getString("text"));
                String type = String.valueOf(feedDataList.get(position).getString("type"));
                String car_id = String.valueOf(feedDataList.get(position).getString("car_id"));
                String is_read = String.valueOf(feedDataList.get(position).getString("is_read"));

                holder.txtNotification.setText(text);

                switch (is_read) {
                    case "true":
                        holder.layout1.setBackgroundColor(Color.LTGRAY);
                        break;
                    case "false":
                        holder.layout1.setBackgroundColor(Color.WHITE);
                        break;
                    default:
                        holder.layout1.setBackgroundColor(Color.WHITE);
                        break;
                }

            } catch (Exception e) {

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
        private NotificationAdapter.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final NotificationAdapter.ClickListener clickListener) {
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
