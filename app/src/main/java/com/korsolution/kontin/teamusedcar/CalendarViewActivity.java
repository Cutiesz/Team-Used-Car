package com.korsolution.kontin.teamusedcar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarViewActivity extends AppCompatActivity implements OnDateSelectedListener, OnMonthChangedListener {

    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();

    MaterialCalendarView widget;
    private TextView txtDate;
    private Button btnOK;

    private String dateSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        setupWidgets();
        initLoad();

    }

    private void initLoad() {
        widget.setOnDateChangedListener(this);
        widget.setOnMonthChangedListener(this);

        Calendar instance = Calendar.getInstance();
        widget.setSelectedDate(instance.getTime());

        // get current date time
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String DateTime_Current_Internet = sdf.format(new Date());
        //txtDate.setText(DateTime_Current_Internet);

        //Setup initial text
        txtDate.setText(getSelectedDatesString());

        dateSelected = DateTime_Current_Internet;
    }

    private void setupWidgets() {

        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        txtDate = (TextView) findViewById(R.id.txtDate);
        btnOK = (Button) findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent returnIntent = new Intent();
                returnIntent.putExtra("dateSelected", dateSelected);
                setResult(CalendarViewActivity.RESULT_OK, returnIntent);

                overridePendingTransition(R.anim.slide_down_info, R.anim.no_change);

                finish();

            }
        });
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String DateTime_Current_Internet = sdf.format(date.getDate());
        //txtDate.setText(DateTime_Current_Internet);
        dateSelected = DateTime_Current_Internet;

        txtDate.setText(getSelectedDatesString());
    }

    @Override
    public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

    }

    private String getSelectedDatesString() {
        CalendarDay date = widget.getSelectedDate();
        if (date == null) {
            return "No Selection";
        }
        return FORMATTER.format(date.getDate());
    }
}
