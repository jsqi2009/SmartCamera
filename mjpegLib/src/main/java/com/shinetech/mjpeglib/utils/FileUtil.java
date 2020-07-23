package com.shinetech.mjpeglib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * author : Jason
 * date   : 2020/7/21 1:43 PM
 * desc   :
 */
public class FileUtil {


    public static void saveBitmap2SDCard(Context context, String filePath,  Bitmap bitmap) {

        String sdCardDir = Environment.getExternalStorageDirectory() + filePath;
        File appDir = new File(sdCardDir);

        if (!appDir.exists()) { //not exist
            appDir.mkdirs();
        }
        String fileName = "camera" + System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}


