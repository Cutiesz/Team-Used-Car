package com.korsolution.kontin.teamusedcar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddUsedCar7PhotoDocActivity extends AppCompatActivity {

    private ImageView imgCarDoc1;
    private ImageView imgCarDoc2;
    private ImageView imgCarDoc3;
    private ImageView imgCarDoc4;
    private ImageView imgCarDoc5;
    private ImageView imgTakePhotoCarDoc1;
    private ImageView imgTakePhotoCarDoc2;
    private ImageView imgTakePhotoCarDoc3;
    private ImageView imgTakePhotoCarDoc4;
    private ImageView imgTakePhotoCarDoc5;
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
    private String Title;
    private String Miles;
    private String CarDetails;
    private String RepairHistory;
    private String CheckList;
    private String StrProperties;

    private static final int ACTION_TAKE_PHOTO_B = 1;
    private String mCurrentPhotoPath;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private final static int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 101;

    private PictureUsedCarPathDBClass PictureUsedCarPathDB;
    private String photoSelect = "Doc1";    // Doc1, Doc2, Doc3, Doc4, Doc5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_used_car_photo_doc);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_layout);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }

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
        CheckList = getIntent().getStringExtra("CheckList");
        StrProperties = getIntent().getStringExtra("StrProperties");
        Title = getIntent().getStringExtra("Title");
        Miles = getIntent().getStringExtra("Miles");
        CarDetails = getIntent().getStringExtra("CarDetails");
        RepairHistory = getIntent().getStringExtra("RepairHistory");

        PictureUsedCarPathDB = new PictureUsedCarPathDBClass(this);

        setupWidgets();

        // Check permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            imgTakePhotoCarDoc1.setEnabled(false);
            imgTakePhotoCarDoc2.setEnabled(false);
            imgTakePhotoCarDoc3.setEnabled(false);
            imgTakePhotoCarDoc4.setEnabled(false);
            imgTakePhotoCarDoc5.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    private void setupWidgets() {

        imgCarDoc1 = (ImageView) findViewById(R.id.imgCarDoc1);
        imgCarDoc2 = (ImageView) findViewById(R.id.imgCarDoc2);
        imgCarDoc3 = (ImageView) findViewById(R.id.imgCarDoc3);
        imgCarDoc4 = (ImageView) findViewById(R.id.imgCarDoc4);
        imgCarDoc5 = (ImageView) findViewById(R.id.imgCarDoc5);

        imgTakePhotoCarDoc1 = (ImageView) findViewById(R.id.imgTakePhotoCarDoc1);
        imgTakePhotoCarDoc2 = (ImageView) findViewById(R.id.imgTakePhotoCarDoc2);
        imgTakePhotoCarDoc3 = (ImageView) findViewById(R.id.imgTakePhotoCarDoc3);
        imgTakePhotoCarDoc4 = (ImageView) findViewById(R.id.imgTakePhotoCarDoc4);
        imgTakePhotoCarDoc5 = (ImageView) findViewById(R.id.imgTakePhotoCarDoc5);

        btnNext = (Button) findViewById(R.id.btnNext);

        imgTakePhotoCarDoc1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Doc1";
                dialogAlertSelectPhoto();
            }
        });
        imgTakePhotoCarDoc2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Doc2";
                dialogAlertSelectPhoto();
            }
        });
        imgTakePhotoCarDoc3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Doc3";
                dialogAlertSelectPhoto();
            }
        });
        imgTakePhotoCarDoc4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Doc4";
                dialogAlertSelectPhoto();
            }
        });
        imgTakePhotoCarDoc5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Doc5";
                dialogAlertSelectPhoto();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddUsedCar8PriceActivity.class);
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
                intent.putExtra("Title", Title);
                intent.putExtra("Miles", Miles);
                intent.putExtra("CarDetails", CarDetails);
                intent.putExtra("RepairHistory", RepairHistory);
                intent.putExtra("CheckList", CheckList);
                intent.putExtra("StrProperties", StrProperties);
                startActivity(intent);
            }
        });
    }

    public void dialogAlertSelectPhoto() {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(AddUsedCar7PhotoDocActivity.this);

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
        switch (photoSelect) {  // Doc1, Doc2, Doc3, Doc4, Doc5
            case "Doc1":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarDoc1);

                PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_1", contentUri.getPath(), "0");

                break;
            case "Doc2":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarDoc2);

                PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_2", contentUri.getPath(), "0");

                break;
            case "Doc3":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarDoc3);

                PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_3", contentUri.getPath(), "0");

                break;
            case "Doc4":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarDoc4);

                PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_4", contentUri.getPath(), "0");

                break;
            case "Doc5":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarDoc5);

                PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_5", contentUri.getPath(), "0");

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

                    switch (photoSelect) {  // Doc1, Doc2, Doc3, Doc4, Doc5
                        case "Doc1":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarDoc1);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_1", selectedImagePath, "0");

                            break;
                        case "Doc2":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarDoc2);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_2", selectedImagePath, "0");

                            break;
                        case "Doc3":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarDoc3);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_3", selectedImagePath, "0");

                            break;
                        case "Doc4":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarDoc4);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_4", selectedImagePath, "0");

                            break;
                        case "Doc5":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarDoc5);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_DOC_5", selectedImagePath, "0");

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
                imgTakePhotoCarDoc1.setEnabled(true);
                imgTakePhotoCarDoc2.setEnabled(true);
                imgTakePhotoCarDoc3.setEnabled(true);
                imgTakePhotoCarDoc4.setEnabled(true);
                imgTakePhotoCarDoc5.setEnabled(true);
            }
        }
    }
}
