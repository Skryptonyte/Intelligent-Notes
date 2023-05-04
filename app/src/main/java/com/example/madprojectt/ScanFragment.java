package com.example.madprojectt;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScanFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Camera c;
    Camera.PictureCallback pc;
    View root;
    String dbname;
    int initalize = 0;
    public ScanFragment(String dbname) {
        this.dbname = dbname;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ScanFragment.
     */
    // TODO: Rename and change types and number of parameters

    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    ActivityResultLauncher<String> requestPermissionLauncher;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        pc = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera c) {
                Toast.makeText(getContext(),"Picture clicked! OCR model may take a while to load!",Toast.LENGTH_LONG);
                Camera.Parameters params = c.getParameters(); // mCamera is a Camera object
                List<Camera.Size> sizes = params.getSupportedPreviewSizes();
                int width = (int) sizes.get(0).width;
                int height = (int) sizes.get(0).height;

                Bitmap bmp= BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                InputImage image = InputImage.fromBitmap(bmp,0);
                scanImage(image);

            }
        };

        pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    if (uri != null) {
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                            InputImage image = InputImage.fromBitmap(bitmap,0);
                            scanImage(image);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        requestPermissionLauncher =
                registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        initializeCamera();
                        // Permission is granted. Continue the action or workflow in your
                        // app.
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),"Camera requires permissions!",Toast.LENGTH_LONG);
                        // Explain to the user that the feature is unavailable because the
                        // feature requires a permission that the user has denied. At the
                        // same time, respect the user's decision. Don't link to system
                        // settings in an effort to convince the user to change their
                        // decision.

                    }
                });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root=  inflater.inflate(R.layout.fragment_scan, container, false);

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),android.Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            /*
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.CAMERA)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 1242);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 1242);
            }
            */
            //ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.CAMERA}, 1242);
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
        else {
            initializeCamera();
        }

        return root;
    }

    void initializeCamera()
    {
        SurfaceView sv = root.findViewById(R.id.surfaceView);
        sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

                if (c == null)
                {
                    c = Camera.open();
                }
                try {

                    c.setPreviewDisplay(sv.getHolder());
                }
                catch (Exception e){
                    e.printStackTrace();
                    AlertDialog.Builder ad = new AlertDialog.Builder(root.getContext());
                    ad.setTitle("Camera error!");
                    ad.setMessage(("Failed to set up camera!"));
                    ad.setPositiveButton("OK",null);

                    AlertDialog a = ad.create();
                    a.show();

                    return;
                }
                c.startPreview();
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

            }
        });

        ImageButton b = root.findViewById(R.id.takePictureBtn);
        b.setOnClickListener(this::clickCamera);

        ImageButton ib = root.findViewById(R.id.galleryButton);
        ib.setOnClickListener(this::scanImageFromGallery);

        sv.setAlpha(1.0f);

    }
    void scanImageFromGallery(View v)
    {

        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    void scanImage(InputImage image)
    {
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        Task<Text> result =
                recognizer.process(image)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                Toast.makeText(getActivity().getApplicationContext(), visionText.getText(), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getActivity().getApplicationContext(), NewNoteActivity.class);
                                intent.putExtra("title", "Scan Text");
                                intent.putExtra("desc", visionText.getText());
                                intent.putExtra("dbname",dbname);
                                startActivity(intent);
                                if (c != null)
                                    c.startPreview();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getActivity().getApplicationContext(), "Failed to scan!", Toast.LENGTH_LONG).show();
                                if (c != null)
                                    c.startPreview();
                            }
                        });
    }



    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        if (c != null)
            c.release();
    }
    void clickCamera(View v)
    {
        if (c != null)
            c.takePicture(null,null,pc);
    }
    @Override
    public void onResume()
    {
        super.onResume();

    }


    @Override
    public void onPause()
    {
        super.onPause();
        if (c != null) {
            c.stopPreview();
            c.release();
            c = null;
        }
    }

}