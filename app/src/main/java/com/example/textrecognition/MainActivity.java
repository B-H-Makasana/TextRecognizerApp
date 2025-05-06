package com.example.textrecognition;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE = 3;
    private static final int READ_STORAGE = 4;
    AppCompatImageView imageView;
    AppCompatTextView takePhoto, resultView;
    AppCompatButton detectText;
    Bitmap imageBitmap;
    Uri imageuri;
    private static final String TAG = "MainActivity";
    public static final int REQUEST_IMAGE = 1;
    public static final int CAMERA_PERMISSION = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.addPhoto);
        takePhoto = findViewById(R.id.image_text);
        resultView = findViewById(R.id.resultImage);
        detectText = findViewById(R.id.detecttext);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultView.setText(" ");
                captureImage();
            }
        });
        detectText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ditectImageFromPicture();
            }
        });
    }

    private void ditectImageFromPicture() {

        InputImage image = null;
        try {
            image = InputImage.fromFilePath(this, imageuri);
        } catch (IOException e) {
            Log.d(TAG, "ditectImageFromPicture: " + e.getMessage());
        }
        TextRecognizer textRecognition = TextRecognition.getClient();
        Task<Text> task = textRecognition.process(image);
        task.addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(Text text) {
                String s = text.getText();
                resultView.setText(s);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Error :" + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void captureImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE);
            } else {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickIntent, PICK_IMAGE);
            }
        } else {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickIntent, PICK_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION)
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
       else if (requestCode == READ_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickIntent, PICK_IMAGE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                return;
            }
            imageuri = data.getData();
            imageView.setImageURI(imageuri);
            imageView.setVisibility(View.VISIBLE);
            takePhoto.setVisibility(View.GONE);
        }
        else if (requestCode == REQUEST_IMAGE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
            imageView.setVisibility(View.VISIBLE);
            takePhoto.setVisibility(View.GONE);
        }
    }
}