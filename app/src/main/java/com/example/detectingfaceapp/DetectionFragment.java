package com.example.detectingfaceapp;

import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.detectingfaceapp.helper.GraphicOverlay;
import com.example.detectingfaceapp.helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceContour;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetectionFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int REQUEST_CODE_PERMISSION = 1001;

    private String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    private View view;
    private ImageView ivImageCapture, ivImageDetection;
    private ImageButton ibtnClose;
    private TextView tvNext;
    private PreviewView previewViewCamera;
    private Executor takePictureExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ImageCapture imageCapture;
    private ProcessCameraProvider processCameraProvider;
    private ImageAnalysis imageAnalysis;
    private CameraSelector cameraSelector;
    private Camera camera;
    private Preview previewView;
    private Button btnCapture;
    private File imageFile;
    private AlertDialog alertDialog;
    private InputImage inputImage;
    private int rotationDegrees;
    private GraphicOverlay graphicOverlay;
    private Realm realm;

    public DetectionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetectionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetectionFragment newInstance(String param1, String param2) {
        DetectionFragment fragment = new DetectionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_detection, container, false);
        ConnectView();
        if(AskingForPermission()) {
            StartCamera();
        } else {
            requestPermissions(PERMISSIONS, REQUEST_CODE_PERMISSION);
        }
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void ConnectView() {
        ivImageCapture = (ImageView) view.findViewById(R.id.iv_imagecapture);
        previewViewCamera = (PreviewView) view.findViewById(R.id.previewvew_camera);
        ibtnClose = (ImageButton) view.findViewById(R.id.ibtn_close);
        tvNext = (TextView) view.findViewById(R.id.tv_next);
        graphicOverlay = view.findViewById(R.id.graphicoverlay);
        alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage("Loading...")
                .setCancelable(false)
                .create();
        takePictureExecutor = Executors.newSingleThreadExecutor();
        ibtnClose.setOnClickListener(DetectionFragment.this);
        tvNext.setOnClickListener(DetectionFragment.this);
        ivImageCapture.setOnClickListener(DetectionFragment.this);

        Realm.init(getActivity());
        realm = Realm.getDefaultInstance();
    }

    private boolean AskingForPermission() {
        for(String permission : PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_PERMISSION) {
            if(AskingForPermission()) {
                StartCamera();
            }
        }
    }

    private void StartCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(getActivity());
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // Đối tượng điểu khiển camera
                    processCameraProvider = cameraProviderFuture.get();
                    previewView = new Preview.Builder().build();
                    // Set up trường hợp (use case) hiển thị bản xem trước
                    previewView.setSurfaceProvider(previewViewCamera.createSurfaceProvider());
                    // Set up trường hợp (use case) chụp ảnh
                    imageCapture = new ImageCapture.Builder()
                            .setTargetRotation(getActivity().getWindowManager().getDefaultDisplay().getRotation())
                            .build();
                    // Set up camera nào sẽ chụp ảnh
                    cameraSelector = new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build();
                    // Set up trường hợp (use case) phân tích ảnh
                    imageAnalysis = new ImageAnalysis.Builder().build();
                    // Kết nối các thành phần vào đổi tượng điều khiển provider
                    camera = processCameraProvider.bindToLifecycle(
                            getActivity(),
                            cameraSelector,
                            previewView,
                            imageAnalysis,
                            imageCapture);

                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(getActivity()));

    }

    private void ProcessingFaceDetection(Bitmap bitmap, ImageAnalysis imageAnalysis) {

        //Lấy rotationDegree cho phân tích
        imageAnalysis.setAnalyzer(takePictureExecutor, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy imageProxy) {
                rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                imageProxy.close();
            }
        });

        //Nạp đầu vào để phân tích
        inputImage = InputImage.fromBitmap(bitmap, rotationDegrees);

        FaceDetectorOptions faceDetectorOptions = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE) //Nhận diện chính xác
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL) //Nhận diện mắt, tai, mũi
                .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                .build();
        FaceDetector faceDetector = FaceDetection.getClient(faceDetectorOptions);
        Log.e("###", "xxx");
        faceDetector.process(inputImage)
                .addOnSuccessListener(new OnSuccessListener<List<Face>>() {
            @Override
            public void onSuccess(List<Face> faces) {
                Log.e("###", "preparing display faces");
                DisplayFaceDetection(faces);
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("DetectionError", "No face is detected");
                        alertDialog.dismiss();
                        Toast.makeText(getActivity(), "None of face is found, please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void DisplayFaceDetection(List<Face> list) {
        int counter = 0;
        Random random = new Random();
        for(Face face : list) {
            Log.e("###", "processing");
            List<PointF> leftEyeContour = face.getContour(FaceContour.LEFT_EYE).getPoints();
            List<PointF> rightEyeContour = face.getContour(FaceContour.RIGHT_EYE).getPoints();
            List<PointF> noseBottomContour = face.getContour(FaceContour.NOSE_BOTTOM).getPoints();
            List<PointF> upperLipTopContour = face.getContour(FaceContour.UPPER_LIP_TOP).getPoints();
            List<PointF> lowerLipBottomContour = face.getContour(FaceContour.LOWER_LIP_BOTTOM).getPoints();
            List<PointF> faceContour = face.getContour(FaceContour.FACE).getPoints();

            Log.e("ppp", leftEyeContour.toString());

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    FaceDetectionObject item = new FaceDetectionObject();

                    item.setName(String.format("%d.jpg", System.currentTimeMillis()) + random.nextInt(100));
                    item.setLeftEyeContour(ConvertPointFToPointt(leftEyeContour));
                    item.setRightEyeContour(ConvertPointFToPointt(rightEyeContour));
                    item.setNoseBottomContour(ConvertPointFToPointt(noseBottomContour));
                    item.setUpperLipTopContour(ConvertPointFToPointt(upperLipTopContour));
                    item.setLowerLipBottomContour(ConvertPointFToPointt(lowerLipBottomContour));
                    item.setFaceContour(ConvertPointFToPointt(faceContour));

                    realm.copyToRealm(item);
                }
            });

            //Lấy viền bao khuôn mặt
            Rect rect = face.getBoundingBox();
            RectOverlay rectOverlay = new RectOverlay(graphicOverlay, rect);
            graphicOverlay.add(rectOverlay);
            counter++;
        }

        //Dừng camare để lấy frame cuối cùng
        processCameraProvider.unbind(previewView);
        alertDialog.dismiss();
        ivImageCapture.setClickable(false);
        tvNext.setVisibility(View.VISIBLE);
        Log.e("###", "done");
        Log.e("counter", String.valueOf(counter));
    }

    private void NextClick() {
        processCameraProvider.unbindAll();
        ivImageCapture.setClickable(true);
        tvNext.setVisibility(View.GONE);
        StartCamera();
    }

    private void CaptureFace() {

        graphicOverlay.clear();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyHHmmss", Locale.US);
        String directoryUrl = Environment.getExternalStorageDirectory().toString() + File.separator + "TestCameraX";
        File folder = new File(directoryUrl);
        if(!folder.exists()) {

            folder.mkdir();
            if(!folder.mkdir())
                Log.e("###", "folder not exist");
        } else {
            Log.e("###", "made folder");
        }
        final File file = new File(folder, simpleDateFormat.format(new Date()) + ".jpg");
        alertDialog.show();
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, takePictureExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                Log.e("####", "preparing scan");

                Bitmap bitmap = previewViewCamera.getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap, previewViewCamera.getWidth(), previewViewCamera.getHeight(), false);
                ProcessingFaceDetection(bitmap, imageAnalysis);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                exception.printStackTrace();
                Log.e("###", "error");
            }
        });
    }

    private RealmList<Pointt> ConvertPointFToPointt(List<PointF> pointFList) {
        RealmList<Pointt> realmList = new RealmList<>();
        for(PointF p : pointFList) {
            Pointt pointt = new Pointt();
            pointt.setX(p.x);
            Log.e("ppp", "Converting");
            pointt.setY(p.y);
        }

        return realmList;
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.ibtn_close:
                NavHostFragment.findNavController(DetectionFragment.this).navigate(R.id.action_detectionFragment_to_homeFragment);
                break;
            case R.id.tv_next:
                NextClick();
                break;
            case R.id.iv_imagecapture:
                CaptureFace();
                break;
        }
    }
}
