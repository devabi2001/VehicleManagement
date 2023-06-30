package com.thirumalaivasa.vehiclemanagement.Listeners;

import android.graphics.Bitmap;

public interface DownloadListener  {
    void setOnDownloadSuccess(Bitmap bitmap);
    void setOnDownloadFailure();
}
