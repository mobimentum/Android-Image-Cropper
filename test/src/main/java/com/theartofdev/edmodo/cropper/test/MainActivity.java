package com.theartofdev.edmodo.cropper.test;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.theartofdev.edmodo.cropper.PdfFile;
import com.theartofdev.edmodo.cropper.FileOperations;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

  private static final String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  /** Start pick image activity with chooser. */
  public void onSelectImageClick(View view) {
    CropImage.activity(null).setGuidelines(CropImageView.Guidelines.ON).start(this);
  }

  @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    // handle result of CropImageActivity
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      if (resultCode == 1) {
        PdfFile pdfFile = data.getParcelableExtra("pdf");
          FileOperations saveFile = new FileOperations(getApplicationContext(), pdfFile.getUri());
        try {
          Bitmap bitmap = saveFile.openPdfWithAndroidSDK(0, saveFile.getFileName(pdfFile.getUri()));
          ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageBitmap(bitmap);
        }
        catch (IOException e) {
          e.printStackTrace();
        }
      }
      else {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        if (resultCode == RESULT_OK) {
            FileOperations saveFile = new FileOperations(getApplicationContext(), result.getUri());
          ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
          Toast.makeText(
                  this, "Cropping successful, Sample: " + result.getSampleSize(), Toast.LENGTH_LONG)
                  .show();
        } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
          Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
        }
      }
    }
  }
}
