package com.korsolution.kontin.teamusedcar;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Register5LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mMapView;

    private EditText edtLatitude;
    private EditText edtLongtitude;
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
    private String SupplyType;  // supplyType : 3 Showroom , 4 tent

    // check permission location
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_location);

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
        SupplyType = getIntent().getStringExtra("SupplyType");

        // set Map
        mMapView = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mMapView);
        mMapView.getMapAsync(this);

        setupWidgets();

        // check permission Location
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
    }

    private void setupWidgets() {

        edtLatitude = (EditText) findViewById(R.id.edtLatitude);
        edtLongtitude = (EditText) findViewById(R.id.edtLongtitude);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String latitude = edtLatitude.getText().toString();
                String longtitude = edtLongtitude.getText().toString();

                if (latitude.length() > 0 && longtitude.length() > 0) {

                    Intent intent = new Intent(getApplicationContext(), Register6PhotoActivity.class);
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
                    intent.putExtra("SupplyType", SupplyType);
                    intent.putExtra("Latitude", latitude);
                    intent.putExtra("Longtitude", longtitude);
                    startActivity(intent);

                } else {
                    Toast.makeText(getApplicationContext(), "กรุณาระบุที่ตั้งก่อนทำขั้นตอนถัดไป!!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        LatLng currentlocation = new LatLng(13.7246005, 100.6331108);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentlocation, 5));

        // check permission location
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //User has previously accepted this permission
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            //Not in api-23, no need to prompt
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                LatLng Point = new LatLng(latLng.latitude, latLng.longitude);

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(String.valueOf(latLng.latitude) + ", " + String.valueOf(latLng.longitude))).showInfoWindow();

                edtLatitude.setText(String.valueOf(latLng.latitude));
                edtLongtitude.setText(String.valueOf(latLng.longitude));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            //btnLogin.setEnabled(false);

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                //  TODO: Prompt with explanation!

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    if (ActivityCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //btnLogin.setEnabled(true);

                        // check permission location
                        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            //User has previously accepted this permission
                            if (ActivityCompat.checkSelfPermission(this,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                mMap.setMyLocationEnabled(true);
                            }
                        } else {
                            //Not in api-23, no need to prompt
                            mMap.setMyLocationEnabled(true);
                        }
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}
