package com.korsolution.kontin.teamusedcar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterTermsOfAgreementActivity extends AppCompatActivity {

    private TextView txtTermsOfAgreement;
    private Button btnAgree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_terms_of_agreement);

        setupWidgets();
    }

    private void setupWidgets() {

        txtTermsOfAgreement = (TextView) findViewById(R.id.txtTermsOfAgreement);
        btnAgree = (Button) findViewById(R.id.btnAgree);

        txtTermsOfAgreement.setMovementMethod(new ScrollingMovementMethod());

        btnAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register1AccountActivity.class);
                startActivity(intent);
            }
        });
    }
}
