package com.gomdev.gles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

public class GLESFileUtils {
    private static final String CLASS = "GLESFileUtils";
    private static final String TAG = GLESConfig.TAG + " " + CLASS;
    private static final boolean DEBUG = GLESConfig.DEBUG;

    private static boolean sExternalStorageAvailable = false;
    private static boolean sExternalStorageWriteable = false;

    public static boolean checkExternalStorageState() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            sExternalStorageAvailable = sExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            sExternalStorageAvailable = true;
            sExternalStorageWriteable = false;
        } else {
            sExternalStorageAvailable = sExternalStorageWriteable = false;
        }

        return true;
    }
    
    public static boolean isExternalStorageAvaiable() {
        return sExternalStorageAvailable;
    }
    
    public static boolean isExternalStorageWriable() {
        return sExternalStorageWriteable;
    }

    public static boolean write(String path, String str) {
        String parentDirPath = getParentDirectoryPath(path);
        File dir = new File(parentDirPath);
        dir.mkdir();
        
        File file = new File(path);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            byte[] data = str.getBytes();
            fos.write(data, 0, data.length);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, "write() File is not existed!!!");
            e.printStackTrace();

            return false;
        } catch (IOException e) {
            Log.d(TAG, "write() write fails");
            e.printStackTrace();

            return false;
        }

        return true;
    }
    
    public static String getParentDirectoryPath(String path) {
        int index = path.lastIndexOf(File.separator);
        String parentDirectory = path.substring(0, index);
        return parentDirectory;
    }

    public static String read(String path) {
        byte[] data = null;

        try {
            FileInputStream fis = new FileInputStream(path);
            int size = fis.available();
            data = new byte[size];
            while (fis.read(data) != -1) {
                ;
            }
            fis.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "read() file is not existed!!!");
            e.printStackTrace();

            return null;
        } catch (IOException e) {
            Log.d(TAG, "read() read fails");
            e.printStackTrace();

            return null;
        }

        return new String(data);
    }
}
