package com.wasiable.android.myreceipts;


import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.samples.vision.barcodereader.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by wasia on 2017/2/25.
 */

public class ReceiptFile  {
    private static final String RECEIPT_FILE_NAME_PREFIX = "receipt";

    public static String GetReceiptFileName(String ReceiptDate) throws Exception {
        String ReceipFileName = "";
        if (ReceiptDate.length() == 7) {
            String YYYYMM = ReceiptDate.substring(0,5);
            ReceipFileName = RECEIPT_FILE_NAME_PREFIX + YYYYMM;
        } else {
            throw new Exception("Receipt date value is not correct");
        }
        return ReceipFileName;
    }

    public void WriteReceiptToFile(Context context, Receipt receipt) throws Exception {
        try {
            Gson gson = new Gson();
            String jsonReceipt = gson.toJson(receipt);

            String ReceiptFileName = GetReceiptFileName(receipt.getReceiptDate());

            if (isExternalStorageWritable()) {

                File receiptFile = getExternalReceiptFile(context, ReceiptFileName);
                FileWriter fw = new FileWriter(receiptFile, true);
                fw.write(jsonReceipt);
                fw.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String ReadReceiptFile(Context context, Receipt receipt ) throws Exception {
        String RetString = "";
        try {
            String ReceiptFileName = GetReceiptFileName(receipt.getReceiptDate());
            if (isExternalStorageWritable()) {
                File receiptFile = getExternalReceiptFile(context, ReceiptFileName);
                if (receiptFile.exists()) {
                    InputStream inputStream = new FileInputStream(receiptFile);
                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();

                        while ((receiveString = bufferedReader.readLine()) != null) {
                            stringBuilder.append(receiveString);
                        }

                        inputStream.close();
                        RetString = stringBuilder.toString();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  RetString;
    }

    /* Checks if external storage is available for read and write */
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getExternalReceiptFile(Context context, String FileName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(null), FileName);

        return file;
    }

    public boolean isReceiptDuplicated(Context context, Receipt receipt) throws Exception {
        boolean isDuplicated = false;
        try {
            String ReceiptFileContent = ReadReceiptFile(context, receipt);
            Log.i("Receipt File Content",ReceiptFileContent);
            String ReceiptNo = receipt.getReceiptNo();
            if (ReceiptFileContent.indexOf(ReceiptNo) > 0) {
                isDuplicated = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return  isDuplicated;
    }
}
