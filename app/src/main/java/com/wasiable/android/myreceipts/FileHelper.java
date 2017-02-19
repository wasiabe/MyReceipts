package com.wasiable.android.myreceipts;

import android.os.Environment;

/**
 * Created by wasia on 2017/2/19.
 */

public class FileHelper {
    private static final String RECEIPT_FILE = "receipt.dat";

    public FileHelper(){}

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
