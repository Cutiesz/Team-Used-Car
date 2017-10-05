package com.korsolution.kontin.teamusedcar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddUsedCar3InfoActivity extends AppCompatActivity {

    private EditText edtTitle;
    private EditText edtMiles;
    private EditText edtDetail;
    private EditText edtRepair;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_used_car_info);

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

        setupWidgets();
    }

    private void setupWidgets() {

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtMiles = (EditText) findViewById(R.id.edtMiles);
        edtDetail = (EditText) findViewById(R.id.edtDetail);
        edtRepair = (EditText) findViewById(R.id.edtRepair);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = edtTitle.getText().toString();
                String miles = edtMiles.getText().toString();
                String carDetail = edtDetail.getText().toString();
                String repairHistory = edtRepair.getText().toString();

                if (miles.length() > 0) {
                    Intent intent = new Intent(getApplicationContext(), AddUsedCar4CheckListActivity.class);
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
                    intent.putExtra("Title", title);
                    intent.putExtra("Miles", miles);
                    intent.putExtra("CarDetails", carDetail);
                    intent.putExtra("RepairHistory", repairHistory);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "กรุณากรอกเลขไมล์", Toast.LENGTH_LONG).show();
                }


            }
        });
    }
}
