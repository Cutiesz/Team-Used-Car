package com.korsolution.kontin.teamusedcar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Register6PhotoActivity extends AppCompatActivity {

    private ImageView imgIdentityCard;
    private ImageView imgHouseRegistration;
    private ImageView imgVatRegistration;
    private ImageView imgStorefront;
    private ImageView imgTakePhotoIdentityCard;
    private ImageView imgTakePhotoHouseRegistration;
    private ImageView imgTakePhotoVatRegistration;
    private ImageView imgTakePhotoStorefront;
    private RadioGroup radioGroupSupplyType;
    private Button btnRegister;

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private final static int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 101;

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
    private String Latitude;
    private String Longtitude;

    private PictureProfilePathDBClass PictureProfilePathDB;
    private String photoSelect = "IdentityCard";    // IdentityCard, HouseRegistration, VatRegistration, StoreFront

    protected ArrayList<JSONObject> feedDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_photo);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

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
        Latitude = getIntent().getStringExtra("Latitude");
        Longtitude = getIntent().getStringExtra("Longtitude");

        PictureProfilePathDB = new PictureProfilePathDBClass(this);
        PictureProfilePathDB.Delete();
        PictureProfilePathDB.Insert("0", "0", "0", "0", "0");

        setupWidgets();

        // Check permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            imgTakePhotoIdentityCard.setEnabled(false);
            imgTakePhotoHouseRegistration.setEnabled(false);
            imgTakePhotoVatRegistration.setEnabled(false);
            imgTakePhotoStorefront.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    private void setupWidgets() {

        imgIdentityCard = (ImageView) findViewById(R.id.imgIdentityCard);
        imgHouseRegistration = (ImageView) findViewById(R.id.imgHouseRegistration);
        imgVatRegistration = (ImageView) findViewById(R.id.imgVatRegistration);
        imgStorefront = (ImageView) findViewById(R.id.imgStorefront);
        imgTakePhotoIdentityCard = (ImageView) findViewById(R.id.imgTakePhotoIdentityCard);
        imgTakePhotoHouseRegistration = (ImageView) findViewById(R.id.imgTakePhotoHouseRegistration);
        imgTakePhotoVatRegistration = (ImageView) findViewById(R.id.imgTakePhotoVatRegistration);
        imgTakePhotoStorefront = (ImageView) findViewById(R.id.imgTakePhotoStorefront);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        imgTakePhotoIdentityCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "IdentityCard";
                dialogAlertSelectPhoto();
            }
        });

        imgTakePhotoHouseRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "HouseRegistration";
                dialogAlertSelectPhoto();
            }
        });

        imgTakePhotoVatRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "VatRegistration";
                dialogAlertSelectPhoto();
            }
        });

        imgTakePhotoStorefront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "StoreFront";
                dialogAlertSelectPhoto();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radioGroupSupplyType.getCheckedRadioButtonId() == -1){
                    Toast.makeText(getApplicationContext(), "Please Select Type.", Toast.LENGTH_LONG).show();
                } else {

                    String[][] arrData = PictureProfilePathDB.SelectAll();
                    if (arrData != null) {
                        String identityCard = arrData[0][1].toString();
                        String houseRegistration = arrData[0][2].toString();
                        String vatRegistration = arrData[0][3].toString();
                        String storefront = arrData[0][4].toString();

                        if (identityCard.equals("0") || houseRegistration.equals("0") || vatRegistration.equals("0") || storefront.equals("0")) {
                            Toast.makeText(getApplicationContext(), "Please take all photos.", Toast.LENGTH_LONG).show();
                        } else {
                            if (isOnline()) {

                                uploadRegister();

                            } else {
                                Toast.makeText(getApplicationContext(), "No internet signal, Please try agian.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
        });
    }

    private void uploadRegister() {

        String strImgIdentityCard = "";
        String strImgHouseRegistration = "";
        String strImgVatRegistration = "";
        String strImgStorefront = "";

        String[][] arrData = PictureProfilePathDB.SelectAll();
        if (arrData != null) {
            String identityCard = arrData[0][1].toString();
            String houseRegistration = arrData[0][2].toString();
            String vatRegistration = arrData[0][3].toString();
            String storefront = arrData[0][4].toString();

            try {

                // image
                Uri imgUri = Uri.parse("file://" + identityCard);
                Bitmap mPhotoBitMap = BitmapHelper.readBitmap(Register6PhotoActivity.this, imgUri);
                if (mPhotoBitMap != null) {
                    mPhotoBitMap = BitmapHelper.shrinkBitmap(mPhotoBitMap, 500,	0);
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mPhotoBitMap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();
                final String img_str = Base64.encodeToString(image, 0);

                strImgIdentityCard = img_str;

                // image
                Uri imgUri1 = Uri.parse("file://" + houseRegistration);
                Bitmap mPhotoBitMap1 = BitmapHelper.readBitmap(Register6PhotoActivity.this, imgUri1);
                if (mPhotoBitMap1 != null) {
                    mPhotoBitMap1 = BitmapHelper.shrinkBitmap(mPhotoBitMap1, 500,	0);
                }
                ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                mPhotoBitMap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
                byte[] image1 = stream1.toByteArray();
                final String img_str1 = Base64.encodeToString(image1, 0);

                strImgHouseRegistration = img_str1;

                // image
                Uri imgUri2 = Uri.parse("file://" + vatRegistration);
                Bitmap mPhotoBitMap2 = BitmapHelper.readBitmap(Register6PhotoActivity.this, imgUri2);
                if (mPhotoBitMap2 != null) {
                    mPhotoBitMap2 = BitmapHelper.shrinkBitmap(mPhotoBitMap2, 500,	0);
                }
                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                mPhotoBitMap2.compress(Bitmap.CompressFormat.JPEG, 100, stream2);
                byte[] image2 = stream2.toByteArray();
                final String img_str2 = Base64.encodeToString(image2, 0);

                strImgVatRegistration = img_str2;

                // image
                Uri imgUri3 = Uri.parse("file://" + storefront);
                Bitmap mPhotoBitMap3 = BitmapHelper.readBitmap(Register6PhotoActivity.this, imgUri3);
                if (mPhotoBitMap3 != null) {
                    mPhotoBitMap3 = BitmapHelper.shrinkBitmap(mPhotoBitMap3, 500,	0);
                }
                ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                mPhotoBitMap3.compress(Bitmap.CompressFormat.JPEG, 100, stream3);
                byte[] image3 = stream3.toByteArray();
                final String img_str3 = Base64.encodeToString(image3, 0);

                strImgStorefront = img_str3;

            } catch (Exception e) {

            }

            String strWebServiceUrl = getResources().getString(R.string.webServiceUrlAccount);

            new FeedAsynTask().execute(strWebServiceUrl + "Register",
                    Email, ShopName, OwnerName, OwnerSurname, TelephoneNumber,
                    Address, Amphoe, District, Province, Postcode,
                    BankCode, BankAccountName, BankAccountNumber,
                    SupplyType, strImgIdentityCard, strImgHouseRegistration, strImgVatRegistration, strImgStorefront,
                    Latitude, Longtitude);
        }

    }

    public class FeedAsynTask extends AsyncTask<String, Void, String> {

        private ProgressDialog nDialog;

        String _email;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            nDialog = new ProgressDialog(Register6PhotoActivity.this);
            nDialog.setMessage("Loading..");
            //nDialog.setTitle("Checking Network");
            nDialog.setIndeterminate(false);
            nDialog.setCancelable(true);
            nDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {

            try{

                _email = params[1];

                // 1. connect server with okHttp
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();


                // 2. assign post data
                RequestBody postData = new FormBody.Builder()
                        //.add("username", "admin")
                        //.add("password", "password")
                        .add("email", params[1])
                        .add("shopName", params[2])
                        .add("firstName", params[3])
                        .add("lastName", params[4])
                        .add("mobile", params[5])
                        .add("address", params[6])
                        .add("amphur", params[7])
                        .add("district", params[8])
                        .add("provinceName", params[9])
                        .add("postcode", params[10])
                        .add("bank_code", params[11])
                        .add("bank_owner", params[12])
                        .add("bank_number", params[13])
                        .add("supplyType", params[14])
                        .add("attachID", params[15])
                        .add("attachHomeCert", params[16])
                        .add("attach20", params[17])
                        .add("attachFront", params[18])
                        .add("lat", params[19])
                        .add("lon", params[20])
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

                feedDataList = CuteFeedJsonUtil.feed(s);
                if (feedDataList != null) {
                    for (int i = 0; i <= feedDataList.size(); i++) {
                        try {

                            String strstatus = String.valueOf(feedDataList.get(i).getString("status"));
                            String strmsg = String.valueOf(feedDataList.get(i).getString("msg"));

                            if (strstatus.equals("error") || strstatus.equals("fail")) {
                                if (strmsg.equals("email duplicate")) {
                                    dialogAlertRegisterFail("มีชื่ออีเมลนี้อยู่ในระบบแล้ว");
                                } else {
                                    dialogAlertRegisterFail(strmsg);
                                }

                            }


                        } catch (Exception e) {

                        }

                        try {

                            String strstatus = String.valueOf(feedDataList.get(i).getString("status"));
                            String strUserId = String.valueOf(feedDataList.get(i).getString("UserId"));

                            if (strstatus.equals("ok")) {
                                dialogAlertRegisterSuccess(_email);
                            }


                        } catch (Exception e) {

                        }
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No Data!!", Toast.LENGTH_LONG).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Fail!!", Toast.LENGTH_LONG).show();
            }

            nDialog.dismiss();
        }
    }

    public void dialogAlertRegisterSuccess(String email) {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(Register6PhotoActivity.this);

        completeDialog.setTitle("Register Success.");
        completeDialog.setMessage("ขอบคุณสำหรับข้อมูล \nทางระบบจะทำการตรวจสอบเอกสารของท่าน เมื่อเอกสารถูกต้องครบถ้วนแล้ว ทางระบบจะแจ้งผลการสมัครไปทางอีเมล " + email);
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();

                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        })/*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Fail!!" ,Toast.LENGTH_LONG).show();
            }
        })*/;
        completeDialog.show();
    }

    public void dialogAlertRegisterFail(String msg) {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(Register6PhotoActivity.this);

        completeDialog.setTitle("Register Fail!!");
        completeDialog.setMessage(/*"Please try agian."*/msg);
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();


            }
        })/*.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                //dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Fail!!" ,Toast.LENGTH_LONG).show();
            }
        })*/;
        completeDialog.show();
    }

    public void dialogAlertSelectPhoto() {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(Register6PhotoActivity.this);

        completeDialog.setTitle("Choose photo from ");
        //completeDialog.setMessage("");
        //completeDialog.setIcon(R.drawable.ic_action_error);

        completeDialog.setPositiveButton(/*android.R.string.yes*/"Camera", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);

            }
        }).setNegativeButton(/*android.R.string.no*/"Gallery", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                startGalleryIntent();

                dialog.dismiss();
                //Toast.makeText(getApplicationContext(), "Fail!!" ,Toast.LENGTH_LONG).show();
            }
        });
        completeDialog.show();
    }

    // Photo
    private void dispatchTakePictureIntent(int actionCode) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        switch(actionCode) {
            case ACTION_TAKE_PHOTO_B:
                File f = null;

                try {
                    /*f = setUpPhotoFile();
                    mCurrentPhotoPath = f.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));*/

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
                        // Do something for lollipop and above versions

                        //Uri photoURI = Uri.fromFile( f);
                        Uri photoURI = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", createImageFile());
                        mCurrentPhotoPath = photoURI.getPath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    } else{
                        // do something for phones running an SDK before lollipop

                        f = setUpPhotoFile();
                        mCurrentPhotoPath = f.getAbsolutePath();
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    f = null;
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                break;
        } // switch

        startActivityForResult(takePictureIntent, actionCode);
    }

    private File setUpPhotoFile() throws IOException {

        File f = createImageFile();
        mCurrentPhotoPath = f.getAbsolutePath();

        return f;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

            if (storageDir != null) {
                if (! storageDir.mkdirs()) {
                    if (! storageDir.exists()){
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }

    /* Photo album for this application */
    private String getAlbumName() {
        return getString(R.string.album_profile);
    }

    private void handleBigCameraPhoto() {

        if (mCurrentPhotoPath != null) {
            galleryAddPic();
            mCurrentPhotoPath = null;
        }

    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N){
            //Toast.makeText(getApplicationContext(), contentUri.toString(), Toast.LENGTH_SHORT).show();
            contentUri = Uri.parse(String.valueOf(contentUri).replace("external_files", "storage/emulated/0"));
            //Toast.makeText(getApplicationContext(), contentUri.toString(), Toast.LENGTH_SHORT).show();
        }

        // get file name
        String path = f.getAbsolutePath();
        String filename = path.substring(path.lastIndexOf("/") + 1);

        //Log.d("URI", String.valueOf(contentUri));
        switch (photoSelect) {  // IdentityCard, HouseRegistration, VatRegistration, StoreFront
            case "IdentityCard":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgIdentityCard);

                PictureProfilePathDB.UpdateDataFront(contentUri.getPath(), "0");

                break;
            case "HouseRegistration":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgHouseRegistration);

                PictureProfilePathDB.UpdateDataBack(contentUri.getPath(), "0");

                break;
            case "VatRegistration":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgVatRegistration);

                PictureProfilePathDB.UpdateDataLeft(contentUri.getPath(), "0");

                break;
            case "StoreFront":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgStorefront);

                PictureProfilePathDB.UpdateDataRight(contentUri.getPath(), "0");

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACTION_TAKE_PHOTO_B: {
                if (resultCode == RESULT_OK) {
                    handleBigCameraPhoto();
                }
                break;
            } // ACTION_TAKE_PHOTO_B

            case GALLERY_IMAGE_ACTIVITY_REQUEST_CODE: {
                try {
                    Uri photoUri = data.getData();
                    String selectedImagePath = getImageFilePath(photoUri, this);
                    Log.d(getClass().getName(), selectedImagePath);

                    // get file name
                    //String path = f.getAbsolutePath();
                    String filename = selectedImagePath.substring(selectedImagePath.lastIndexOf("/") + 1);

                    switch (photoSelect) {  // IdentityCard, HouseRegistration, VatRegistration, StoreFront
                        case "IdentityCard":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgIdentityCard);

                            PictureProfilePathDB.UpdateDataFront(selectedImagePath, "0");

                            break;
                        case "HouseRegistration":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgHouseRegistration);

                            PictureProfilePathDB.UpdateDataBack(selectedImagePath, "0");

                            break;
                        case "VatRegistration":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgVatRegistration);

                            PictureProfilePathDB.UpdateDataLeft(selectedImagePath, "0");

                            break;
                        case "StoreFront":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgStorefront);

                            PictureProfilePathDB.UpdateDataRight(selectedImagePath, "0");

                            break;
                    }

                } catch (Exception e) {

                }
                break;

            }   // Gallery

        } // switch
    }

    // Gallery
    private void startGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public static String getImageFilePath(Uri originalUri, Activity activity) {
        // get file path in string
        String selectedImagePath = null;
        String[] projection = { MediaStore.Images.ImageColumns.DATA };
        Cursor cursor = activity.managedQuery(originalUri, projection, null,
                null, null);
        if (cursor != null) {
            int index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            selectedImagePath = cursor.getString(index);
            if (selectedImagePath == null) {

                String id = originalUri.getLastPathSegment().split(":")[1];
                final String[] imageColumns = { MediaStore.Images.Media.DATA };
                final String imageOrderBy = null;

                Uri uri = getUri();

                Cursor imageCursor = activity.managedQuery(uri, imageColumns,
                        MediaStore.Images.Media._ID + "=" + id, null,
                        imageOrderBy);

                if (imageCursor.moveToFirst()) {
                    selectedImagePath = imageCursor.getString(imageCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));
                }
                Log.e("path", selectedImagePath); // use selectedImagePath
            }
        }
        return selectedImagePath;
    }

    // By using this method get the Uri of Internal/External Storage for Media
    private static Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                imgTakePhotoIdentityCard.setEnabled(true);
                imgTakePhotoHouseRegistration.setEnabled(true);
                imgTakePhotoVatRegistration.setEnabled(true);
                imgTakePhotoStorefront.setEnabled(true);
            }
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
