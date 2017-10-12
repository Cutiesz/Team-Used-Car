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

public class AddUsedCar6PhotoInnerActivity extends AppCompatActivity {

    private ImageView imgCarInner1;
    private ImageView imgCarInner2;
    private ImageView imgCarInner3;
    private ImageView imgCarInner4;
    private ImageView imgCarInner5;
    private ImageView imgTakePhotoCarInner1;
    private ImageView imgTakePhotoCarInner2;
    private ImageView imgTakePhotoCarInner3;
    private ImageView imgTakePhotoCarInner4;
    private ImageView imgTakePhotoCarInner5;
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
    private String photoSelect = "Inner1";    // Inner1, Inner2, Inner3, Inner4, Inner5

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_used_car_photo_inner);

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
        Title = getIntent().getStringExtra("Title");
        Miles = getIntent().getStringExtra("Miles");
        CarDetails = getIntent().getStringExtra("CarImages");
        RepairHistory = getIntent().getStringExtra("RepairHistory");
        CheckList = getIntent().getStringExtra("CheckList");
        StrProperties = getIntent().getStringExtra("StrProperties");

        PictureUsedCarPathDB = new PictureUsedCarPathDBClass(this);

        setupWidgets();

        // Check permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            imgTakePhotoCarInner1.setEnabled(false);
            imgTakePhotoCarInner2.setEnabled(false);
            imgTakePhotoCarInner3.setEnabled(false);
            imgTakePhotoCarInner4.setEnabled(false);
            imgTakePhotoCarInner5.setEnabled(false);
            ActivityCompat.requestPermissions(this, new String[] { android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
    }

    private void setupWidgets() {

        imgCarInner1 = (ImageView) findViewById(R.id.imgCarInner1);
        imgCarInner2 = (ImageView) findViewById(R.id.imgCarInner2);
        imgCarInner3 = (ImageView) findViewById(R.id.imgCarInner3);
        imgCarInner4 = (ImageView) findViewById(R.id.imgCarInner4);
        imgCarInner5 = (ImageView) findViewById(R.id.imgCarInner5);

        imgTakePhotoCarInner1 = (ImageView) findViewById(R.id.imgTakePhotoCarInner1);
        imgTakePhotoCarInner2 = (ImageView) findViewById(R.id.imgTakePhotoCarInner2);
        imgTakePhotoCarInner3 = (ImageView) findViewById(R.id.imgTakePhotoCarInner3);
        imgTakePhotoCarInner4 = (ImageView) findViewById(R.id.imgTakePhotoCarInner4);
        imgTakePhotoCarInner5 = (ImageView) findViewById(R.id.imgTakePhotoCarInner5);

        btnNext = (Button) findViewById(R.id.btnNext);

        imgTakePhotoCarInner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Inner1";
                dialogAlertSelectPhoto();
            }
        });
        imgTakePhotoCarInner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Inner2";
                dialogAlertSelectPhoto();
            }
        });
        imgTakePhotoCarInner3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Inner3";
                dialogAlertSelectPhoto();
            }
        });
        imgTakePhotoCarInner4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Inner4";
                dialogAlertSelectPhoto();
            }
        });
        imgTakePhotoCarInner5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                photoSelect = "Inner5";
                dialogAlertSelectPhoto();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), AddUsedCar7PhotoDocActivity.class);
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
                intent.putExtra("CarImages", CarDetails);
                intent.putExtra("RepairHistory", RepairHistory);
                intent.putExtra("CheckList", CheckList);
                intent.putExtra("StrProperties", StrProperties);
                startActivity(intent);
            }
        });
    }

    public void dialogAlertSelectPhoto() {
        AlertDialog.Builder completeDialog = new AlertDialog.Builder(AddUsedCar6PhotoInnerActivity.this);

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
        switch (photoSelect) {  // Inner1, Inner2, Inner3, Inner4, Inner5
            case "Inner1":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarInner1);

                PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_1", contentUri.getPath(), "0");

                break;
            case "Inner2":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarInner2);

                PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_2", contentUri.getPath(), "0");

                break;
            case "Inner3":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarInner3);

                PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_3", contentUri.getPath(), "0");

                break;
            case "Inner4":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarInner4);

                PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_4", contentUri.getPath(), "0");

                break;
            case "Inner5":

                Glide.with(this)
                        .load(new File(contentUri.getPath()))
                        .into(imgCarInner5);

                PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_5", contentUri.getPath(), "0");

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

                    switch (photoSelect) {  // Inner1, Inner2, Inner3, Inner4, Inner5
                        case "Inner1":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarInner1);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_1", selectedImagePath, "0");

                            break;
                        case "Inner2":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarInner2);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_2", selectedImagePath, "0");

                            break;
                        case "Inner3":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarInner3);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_3", selectedImagePath, "0");

                            break;
                        case "Inner4":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarInner4);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_4", selectedImagePath, "0");

                            break;
                        case "Inner5":

                            Glide.with(this)
                                    .load(new File(selectedImagePath))
                                    .into(imgCarInner5);

                            PictureUsedCarPathDB.UpdateData("URI_CAR_INNER_5", selectedImagePath, "0");

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
                imgTakePhotoCarInner1.setEnabled(true);
                imgTakePhotoCarInner2.setEnabled(true);
                imgTakePhotoCarInner3.setEnabled(true);
                imgTakePhotoCarInner4.setEnabled(true);
                imgTakePhotoCarInner5.setEnabled(true);
            }
        }
    }
}
