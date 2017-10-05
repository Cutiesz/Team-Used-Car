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

public class Register1AccountActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtShopName;
    private EditText edtOwnerName;
    private EditText edtOwnerSurname;
    private EditText edtTelephone;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        // register:supplyType // 3 Showroom , 4 tent

        setupWidgets();
    }

    private void setupWidgets() {

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtShopName = (EditText) findViewById(R.id.edtShopName);
        edtOwnerName = (EditText) findViewById(R.id.edtOwnerName);
        edtOwnerSurname = (EditText) findViewById(R.id.edtOwnerSurname);
        edtTelephone = (EditText) findViewById(R.id.edtTelephone);
        btnNext = (Button) findViewById(R.id.btnNext);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edtEmail.getText().toString();
                String shopName = edtShopName.getText().toString();
                String ownerName = edtOwnerName.getText().toString();
                String ownerSurname = edtOwnerSurname.getText().toString();
                String telephoneNumber = edtTelephone.getText().toString();

                if (email.length() > 0) {
                    if (shopName.length() > 0) {
                        if (ownerName.length() > 0) {
                            if (ownerSurname.length() > 0) {
                                if (telephoneNumber.length() > 0) {

                                    Intent intent = new Intent(getApplicationContext(), Register2AddressActivity.class);
                                    intent.putExtra("Email", email);
                                    intent.putExtra("ShopName", shopName);
                                    intent.putExtra("OwnerName", ownerName);
                                    intent.putExtra("OwnerSurname", ownerSurname);
                                    intent.putExtra("TelephoneNumber", telephoneNumber);
                                    startActivity(intent);

                                } else {
                                    Toast.makeText(getApplicationContext(), "Please Enter Telephone Number.",Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Please Enter Owner Surname.",Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Please Enter Owner Name.",Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Please Enter Shop Name.",Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please Enter Email.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
