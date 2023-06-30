package com.thirumalaivasa.vehiclemanagement.Helpers;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.thirumalaivasa.vehiclemanagement.Models.ImageData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class ImageHelper {
    private static final String TAG = "VehicleManagement";

    Bitmap bitmap = null;

    public void savePicture(Context context, Uri imageUri, String fileName) {

        try {
            File cacheDir = context.getCacheDir();
            File cacheFile = new File(cacheDir, fileName);

            FileOutputStream outputStream = new FileOutputStream(cacheFile);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            SharedPreferences imagePreferences = context.getSharedPreferences("Images", MODE_PRIVATE);
            SharedPreferences.Editor editor = imagePreferences.edit();
            editor.putString(fileName, cacheFile.getAbsolutePath()).apply();
            ImageData.setImage(fileName,bitmap);
            // The image is now stored in the cache file
        } catch (IOException e) {
            // Handle any errors that may occur while storing the image
            e.printStackTrace();
        }

    }


    public Task<Bitmap> downloadPicture(Context context, String fileName, StorageReference storageRef) {

        TaskCompletionSource<Bitmap> taskCompletionSource = new TaskCompletionSource<>();

        try {
            File localFile = File.createTempFile(fileName, "", context.getCacheDir());

            storageRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> {
                // When the download is successful, save the file locally and load the bitmap
                Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                SharedPreferences imagePreferences = context.getSharedPreferences("Images", MODE_PRIVATE);
                SharedPreferences.Editor editor = imagePreferences.edit();
                editor.putString(fileName, localFile.getAbsolutePath()).apply();
                editor.apply();

                taskCompletionSource.setResult(bitmap);
            }).addOnFailureListener(exception -> {
                // Handle any errors that occur during the download
                exception.printStackTrace();
                Log.i(TAG, "download failed");
                taskCompletionSource.setException(exception);
            });
        } catch (IOException e) {
            Log.e(TAG, "downloadPicture: " + e);
            taskCompletionSource.setException(e);
        }

        return taskCompletionSource.getTask();
    }

    public Task<Boolean> uploadPicture(Context context, Uri imageUri, StorageReference storageReference) {
        TaskCompletionSource<Boolean> taskCompletionSource = new TaskCompletionSource<>();
        try {
            Bitmap bmp = null;
            bmp = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (bmp != null)
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = storageReference.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskCompletionSource.setResult(true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    taskCompletionSource.setResult(false);
                }
            });


        } catch (IOException e) {
            e.printStackTrace();
            taskCompletionSource.setException(e);
            taskCompletionSource.setResult(false);
        }
        return taskCompletionSource.getTask();
    }


}
