package com.example.furnishar.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Toast;

import com.example.furnishar.DataModel;
import com.example.furnishar.R;
import com.example.furnishar.SessionManager;
import com.example.furnishar.utils.PhotoUtils;
import com.google.android.material.snackbar.Snackbar;

import com.example.furnishar.adapters.PhotosAdapter;

import java.util.List;
import java.util.SplittableRandom;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private String userId;

    @BindView(R.id.photos_recyclerview)
    RecyclerView photosRecyclerView;

    PhotosAdapter photosAdapter;
    List<String> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        userId = SessionManager.getInstance(this).getFirebaseUserId();
        setupRecyclerView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        photoList = DataModel.getInstance().getPhotoList(userId);
        photosAdapter.updatePhotoList(photoList);
    }

    void setupRecyclerView() {
        photosAdapter = new PhotosAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        photosRecyclerView.setLayoutManager(layoutManager);

        photoList = DataModel.getInstance().getPhotoList(userId);
        photosAdapter.updatePhotoList(photoList);

//        swipeRefreshLayout.setOnRefreshListener(() -> {
//            photosAdapter.updatePhotoList(photoList);
//            swipeRefreshLayout.setRefreshing(false);
//        });
    }

    @OnClick(R.id.camera_btn)
    void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }
        else startCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        // If request is cancelled, the result arrays are empty.
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)){

                    Snackbar.make(findViewById(R.id.container_main), R.string.camera_permission_grant, Snackbar.LENGTH_LONG)
                            .setAction("Grant", v -> gotoAppPermissions())
                            .show();
                }
                else
                    Toast.makeText(getBaseContext(), R.string.camera_permission, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startCamera() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap;
            if (extras != null) {
                imageBitmap = (Bitmap) extras.get("data");
                if (imageBitmap != null) {
                    String encodedPhoto = PhotoUtils.convertToBase64(imageBitmap);
                    String photoName = "nume poza"; //TODO generator de nume random
                    DataModel.getInstance().addPhoto(userId, photoName, encodedPhoto);
                }
            }
        }
    }

    private void gotoAppPermissions() {
        Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);
    }
}
