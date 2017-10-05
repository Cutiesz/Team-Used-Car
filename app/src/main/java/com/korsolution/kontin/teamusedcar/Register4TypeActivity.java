package com.korsolution.kontin.teamusedcar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Register4TypeActivity extends AppCompatActivity {

    private Button btnShowroom;
    private Button btnShowroomSelect;
    private Button btnTent;
    private Button btnTentSelect;
    private Button btnNext;

    private String Email;
    private String ShopName;
    private String OwnerName;
    private String OwnerSurname;
    private String TelephoneNumber;
    private String Address;
    private String Province;
    private String Amphoe;
    private String District;
    private String Postcode;
    private String BankAccountName;
    private String BankAccountNumber;
    private String BankCode;

    private String supplyType = "";  // supplyType : 3 Showroom , 4 tent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_type);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        Email = getIntent().getStringExtra("Email");
        ShopName = getIntent().getStringExtra("ShopName");
        OwnerName = getIntent().getStringExtra("OwnerName");
        OwnerSurname = getIntent().getStringExtra("OwnerSurname");
        TelephoneNumber = getIntent().getStringExtra("TelephoneNumber");
        Address = getIntent().getStringExtra("Address");
        Province = getIntent().getStringExtra("Province");
        Amphoe = getIntent().getStringExtra("Amphoe");
        District = getIntent().getStringExtra("District");
        Postcode = getIntent().getStringExtra("Postcode");
        BankAccountName = getIntent().getStringExtra("BankAccountName");
        BankAccountNumber = getIntent().getStringExtra("BankAccountNumber");
        BankCode = getIntent().getStringExtra("BankCode");

        setupWidgets();
    }

    private void setupWidgets() {

        btnShowroom = (Button) findViewById(R.id.btnShowroom);
        btnShowroomSelect = (Button) findViewById(R.id.btnShowroomSelect);
        btnTent = (Button) findViewById(R.id.btnTent);
        btnTentSelect = (Button) findViewById(R.id.btnTentSelect);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnShowroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnShowroom.setVisibility(View.GONE);
                btnShowroomSelect.setVisibility(View.VISIBLE);

                btnTent.setVisibility(View.VISIBLE);
                btnTentSelect.setVisibility(View.GONE);

                supplyType = "3";
            }
        });

        btnTent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnTent.setVisibility(View.GONE);
                btnTentSelect.setVisibility(View.VISIBLE);

                btnShowroom.setVisibility(View.VISIBLE);
                btnShowroomSelect.setVisibility(View.GONE);

                supplyType = "4";
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!supplyType.equals("")) {

                    dialogAlertConfirm();

                } else {
                    Toast.makeText(getApplicationContext(), "กรุณาเลือกประเภทบัญชีผู้ใช้!",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void dialogAlertConfirm(){
        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);

        String _SupplyType = "";
        switch (supplyType) {
            case "3":
                _SupplyType = getString(R.string.showroom);
                break;
            case "4":
                _SupplyType = getString(R.string.tent);
                break;
        }

        mDialog.setTitle("คุณเลือกประเภทบัญชี \"" + _SupplyType + "\" ใช่หรือไม่?");
        //mDialog.setIcon(R.drawable.ic_action_close);
        mDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Intent intent = new Intent(getApplicationContext(), Register5LocationActivity.class);
                intent.putExtra("Email", Email);
                intent.putExtra("ShopName", ShopName);
                intent.putExtra("OwnerName", OwnerName);
                intent.putExtra("OwnerSurname", OwnerSurname);
                intent.putExtra("TelephoneNumber", TelephoneNumber);
                intent.putExtra("Address", Address);
                intent.putExtra("Province", Province);
                intent.putExtra("Amphoe", Amphoe);
                intent.putExtra("District", District);
                intent.putExtra("Postcode", Postcode);
                intent.putExtra("BankAccountName", BankAccountName);
                intent.putExtra("BankAccountNumber", BankAccountNumber);
                intent.putExtra("BankCode", BankCode);
                intent.putExtra("SupplyType", supplyType);
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
}
