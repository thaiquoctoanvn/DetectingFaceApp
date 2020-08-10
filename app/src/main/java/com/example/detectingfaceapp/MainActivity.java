package com.example.detectingfaceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private int REQUEST_CODE_PERMISSION = 1001;

    private String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private ImageView ivImageCapture;
    private PreviewView previewViewCamera;
    private Executor takePictureExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private Button btnCapture;
    private File imageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

//        ConnectView();
//        if(AskingForPermission()) {
//            StartCamera();
//        } else {
//            ActivityCompat.requestPermissions(MainActivity.this, PERMISSIONS, REQUEST_CODE_PERMISSION);
//        }
    }

//    private void ConnectView() {
//        ivImageCapture = (ImageView) findViewById(R.id.iv_imagecapture);
//        previewViewCamera = (PreviewView) findViewById(R.id.previewvew_camera);
//        takePictureExecutor = Executors.newSingleThreadExecutor();
//
//    }
//
//    private boolean AskingForPermission() {
//        for(String permission : PERMISSIONS){
//            if(ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED){
//                return false;
//            }
//        }
//        return true;
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == REQUEST_CODE_PERMISSION) {
//            if(AskingForPermission()) {
//                StartCamera();
//            }
//        }
//    }
//
//    private void StartCamera() {
//        cameraProviderFuture = ProcessCameraProvider.getInstance(MainActivity.this);
//        cameraProviderFuture.addListener(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    //
//                    ProcessCameraProvider processCameraProvider = cameraProviderFuture.get();
//                    Preview previewView = new Preview.Builder().build();
//                    // Set up trường hợp (use case) hiển thị bản xem trước
//                    previewView.setSurfaceProvider(previewViewCamera.createSurfaceProvider());
//                    // Set up trường hợp (use case) chụp ảnh
//                    imageCapture = new ImageCapture.Builder()
//                            .setTargetRotation(MainActivity.this.getWindowManager().getDefaultDisplay().getRotation())
//                            .build();
//                    // Set up camera nào sẽ chụp ảnh
//                    CameraSelector cameraSelector = new CameraSelector.Builder()
//                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//                            .build();
//                    // Set up trường hợp (use case) phân tích ảnh
//                    ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
//                    Camera camera = processCameraProvider.bindToLifecycle(
//                            MainActivity.this,
//                            cameraSelector,
//                            previewView,
//                            imageAnalysis,
//                            imageCapture);
//
//                    ivImageCapture.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyHHmmss", Locale.US);
//                            String directoryUrl = Environment.getExternalStorageDirectory().toString() + File.separator + "TestCameraX";
//                            File folder = new File(directoryUrl);
//                            if(!folder.exists()) {
//
//                                folder.mkdir();
//                                if(!folder.mkdir())
//                                    Log.e("###", "folder not exist");
//                            } else {
//                                Log.e("###", "made folder");
//                            }
//                            final File file = new File(folder, simpleDateFormat.format(new Date()) + ".jpg");
//                            ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
//                            imageCapture.takePicture(outputFileOptions, takePictureExecutor, new ImageCapture.OnImageSavedCallback() {
//                                @Override
//                                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                                    Log.e("####", "successfully");
//                                    imageFile = file;
//                                }
//
//                                @Override
//                                public void onError(@NonNull ImageCaptureException exception) {
//                                    exception.printStackTrace();
//                                    Log.e("###", "error");
//                                }
//                            });
//                        }
//                    });
//
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, ContextCompat.getMainExecutor(MainActivity.this));
//
//    }
}
