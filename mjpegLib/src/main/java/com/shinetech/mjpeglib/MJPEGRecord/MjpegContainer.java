package com.shinetech.mjpeglib.MJPEGRecord;

import android.graphics.Bitmap;

public class MjpegContainer {
    Bitmap bitmap;
    public byte [] data;

    MjpegContainer (Bitmap bitmap, byte [] data) {
        this.bitmap = bitmap;
        this.data = data;
    }
}
