package com.korsolution.kontin.teamusedcar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.korsolution.kontin.teamusedcar.activity.ShowroomTabActivity;

public class AddUsedCarYoutubeLinkActivity extends AppCompatActivity {

    private EditText edtYoutubeLink;
    private ImageView imgThumbnail;
    private Button btnSkip;
    private Button btnNext;

    private String UserId;
    private String CustomerId;
    private String valueGetIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_used_car_youtube_link);

        UserId = getIntent().getStringExtra("UserId");
        CustomerId = getIntent().getStringExtra("CustomerId");
        valueGetIntent = getIntent().getStringExtra("valueGetIntent");

        //Toast.makeText(getApplicationContext(), valueGetIntent, Toast.LENGTH_LONG).show();

        setupWidgets();

        if (valueGetIntent != null) {
            edtYoutubeLink.setText(valueGetIntent);
            setImgThumbnail(valueGetIntent);
        }
    }

    private void setupWidgets() {

        edtYoutubeLink = (EditText) findViewById(R.id.edtYoutubeLink);
        imgThumbnail = (ImageView) findViewById(R.id.imgThumbnail);
        btnSkip = (Button) findViewById(R.id.btnSkip);
        btnNext = (Button) findViewById(R.id.btnNext);

        edtYoutubeLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edtYoutubeLink.length() > 0) {
                    btnNext.setEnabled(true);
                } else {
                    btnNext.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                String youtubeID = edtYoutubeLink.getText().toString();

                if (youtubeID.length() > 0) {

                    try {

                        setImgThumbnail(youtubeID);

                    } catch (Exception e) {

                    }
                }
            }
        });

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), AddUsedCar1LicensePlateActivity.class);
                intent.putExtra("YoutubeLink", "");
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imgThumbnail.getDrawable() != null) {

                    String youtubeLink = edtYoutubeLink.getText().toString();

                    Intent intent = new Intent(getApplicationContext(), AddUsedCar1LicensePlateActivity.class);
                    intent.putExtra("YoutubeLink", youtubeLink);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "ลิ้งค์ youtube ไม่ถูกต้อง!!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setImgThumbnail(String youtubeID) {

        youtubeID = getVideoId(youtubeID);

        final String youtubeThumnailURL = String.format("http://img.youtube.com/vi/%s/hqdefault.jpg", youtubeID);

        Glide.with(AddUsedCarYoutubeLinkActivity.this)
                .load(youtubeThumnailURL)
                .into(imgThumbnail);
    }

    public static String getVideoId(String watchLink){

        return watchLink.substring(watchLink.length() - 11);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            System.out.println("KEYCODE_BACK");

            Intent intent = new Intent(getApplicationContext(), ShowroomTabActivity.class);
            intent.putExtra("UserId", UserId);
            intent.putExtra("CustomerId", CustomerId);
            startActivity(intent);
        }
        return false;
    }
}
