package com.savan.photofilter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;
import jp.wasabeef.glide.transformations.CropSquareTransformation;
import jp.wasabeef.glide.transformations.CropTransformation;
import jp.wasabeef.glide.transformations.MaskTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ContrastFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.KuwaharaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SwirlFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private ImageView imageView;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void choosePhoto(View v){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");

        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK && data != null){
            try {
                Uri uri = data.getData();
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();

                image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();

                imageView.setImageBitmap(image);
            } catch (IOException e) {
                Log.e("error", "Error while loading image");
            }
        }
    }

    public void applySepia(View view) {
        applyFilter(new SepiaFilterTransformation(), view, image, imageView);
    }

    public void applyFilter(Transformation transformation, View view, Bitmap image, ImageView imageView){
        Glide.with(view).load(image)
                .apply(RequestOptions.bitmapTransform(transformation))
                .into(imageView);
    }

    public void applyToon(View view) {
        applyFilter(new ToonFilterTransformation(), view, image, imageView);
    }

    public void applySketch(View view) {
        applyFilter(new SketchFilterTransformation(), view, image, imageView);
    }

    public void applyCrop(View view) {
        int scale = 1000 / image.getWidth();
        int height = image.getHeight()*scale;
        applyFilter(new CropTransformation(1000, height), view, image, imageView);
    }

    public void applyContrast(View view) {
        applyFilter(new ContrastFilterTransformation(), view, image, imageView);
    }

    public void applyInvert(View view) {
        applyFilter(new InvertFilterTransformation(), view, image, imageView);
    }

    public void applyPixelation(View view) {
        applyFilter(new PixelationFilterTransformation(), view, image, imageView);
    }

    public void applySwirl(View view) {
        applyFilter(new SwirlFilterTransformation(), view, image, imageView);
    }

    public void applyKuwahara(View view) {
        applyFilter(new KuwaharaFilterTransformation(), view, image, imageView);
    }

    public void applyVignette(View view) {
        applyFilter(new VignetteFilterTransformation(), view, image, imageView);
    }

    public void applyBrightness(View view) {
        applyFilter(new BrightnessFilterTransformation(), view, image, imageView);
    }

    public void applyBlur(View view) {
        applyFilter(new BlurTransformation(), view, image, imageView);
    }

    public void applyMask(View view) {
//        applyFilter(new MaskTransformation(), view, image, imageView);
    }

    public void applyColorFilter(View view) {
        applyFilter(new ColorFilterTransformation(255), view, image, imageView);
    }

    public void applyCropCircle(View view) {
        applyFilter(new CropCircleTransformation(), view, image, imageView);
    }

    public void applyCropCircleWithBorder(View view) {
//        applyFilter(new CropCircleWithBorderTransformation(), view, image, imageView);
    }

    public void applyCropSquare(View view) {
        applyFilter(new CropSquareTransformation(), view, image, imageView);
    }

    public void applyRoundCorners(View view) {
        applyFilter(new RoundedCornersTransformation(image.getWidth()/2,5), view, image, imageView);
    }

    public void savePhoto(View view) {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        if(isExternalStorageAvailable() && !isExternalStorageReadOnly() && checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, System.currentTimeMillis() + "", "Filtered Image");

//            FileOutputStream outputStream = null;
//            File dir = null;
//            try {
//                dir = getExternalFilesDir("Demo");
//                File file = new File(dir, System.currentTimeMillis() + ".jpg");
//                file.createNewFile();
//                outputStream = new FileOutputStream(file);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            if(outputStream != null) {
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
//                Toast.makeText(getApplicationContext(), "Image saved to " + dir.getAbsolutePath(), Toast.LENGTH_LONG).show();
//            }
//            else {
//                Toast.makeText(getApplicationContext(), "Error while saving image...", Toast.LENGTH_SHORT).show();
//            }
//
//            try {
//                outputStream.flush();
//                outputStream.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }

    private boolean isExternalStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(externalStorageState)) {
            return true;
        }
        return false;
    }

    private boolean isExternalStorageReadOnly() {
        String externalStorageState = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(externalStorageState)) {
            return true;
        }
        return false;
    }

    public boolean checkPermission(String permission) {
        int check = ContextCompat.checkSelfPermission(this, permission);
        return (check == PackageManager.PERMISSION_GRANTED);
    }

}
