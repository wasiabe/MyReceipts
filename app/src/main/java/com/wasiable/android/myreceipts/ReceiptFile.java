package com.wasiable.android.myreceipts;


import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wasia on 2017/2/25.
 */

public class ReceiptFile  {
    private static final String RECEIPT_FILE_NAME_PREFIX = "receipt";
    private static final String RECEIPT_SEPARATOR = ",";
    public String Period = "";
    public Integer TotalAmount = 0;
    public Integer TotalReceipts = 0;
    private Map<String, Integer> mapSaler = new HashMap<String, Integer>();
    private Map<String, Integer> mapItem = new HashMap<String, Integer>();

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

    public static String GetReceiptJSONString(String ReceiptFileContent) {
        String jsonText = "{\"receipts\":[" + ReceiptFileContent.substring(0, ReceiptFileContent.length()-1) + "]}";
        return jsonText;
    }

    public static JSONArray GetReceiptsJSONArray (String ReceiptFileContent) throws JSONException {
        JSONArray arr = new JSONArray();
        try {
            String jsonText = GetReceiptJSONString(ReceiptFileContent);
            JSONObject json = new JSONObject(jsonText);
            arr = json.getJSONArray("receipts");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }

    public void  AccumReceiptSummary (Context context, String ReceiptFileName) throws JSONException {
        String ReceiptFileContent = ReadReceiptFileToString(context, ReceiptFileName);
        try {
            Period = ReceiptFileName.replace(RECEIPT_FILE_NAME_PREFIX,"").substring(0,3) + "/" + ReceiptFileName.replace(RECEIPT_FILE_NAME_PREFIX,"").substring(3,5);

            JSONArray arr = GetReceiptsJSONArray(ReceiptFileContent);
            TotalReceipts = arr.length();

            Map<String, Integer> map = new HashMap<String, Integer>();
            for(int j=0; j<=arr.length()-1; j++) {
                Integer TotalAmount = arr.getJSONObject(j).getInt("TotalAmount");
                this.TotalAmount += TotalAmount;

                // Accumulate by Saler
                String SalerEIN = arr.getJSONObject(j).getString("SalerEIN");
                if (mapSaler.containsKey(SalerEIN)) {
                    Integer subtotalBySaler = mapSaler.get(SalerEIN);
                    mapSaler.put(SalerEIN, subtotalBySaler + TotalAmount);
                } else {
                    mapSaler.put(SalerEIN, TotalAmount);
                }

                // Accumulate by Item
                JSONArray ItemContent = arr.getJSONObject(j).getJSONArray("ItemContent");
                for(int k=0;k<ItemContent.length();k++) {
                    String jsonItem =  ItemContent.get(k).toString();

                    if (isJSONValid(jsonItem)) {
                        JSONObject item = new JSONObject(jsonItem);
                        if (!item.isNull("item_name") && !item.isNull("item_quantity") && !item.isNull("unit_price")) {
                            String ItemName = item.getString("item_name");
                            Integer ItemQuantity = item.getInt("item_quantity");
                            Integer UnitPrice = item.getInt("unit_price");

                            Integer ItemAmount = 0;
                            ItemAmount = ItemQuantity * UnitPrice;

                            if (mapItem.containsKey(ItemName)) {
                                Integer subtotalByItem = mapItem.get(ItemName);
                                mapItem.put(ItemName, subtotalByItem + ItemAmount);
                            } else {
                                mapItem.put(ItemName, ItemAmount);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void WriteReceiptToFile(Context context, Receipt receipt) throws Exception {
        try {
            Gson gson = new Gson();
            String jsonReceipt = gson.toJson(receipt);

            String ReceiptFileName = GetReceiptFileName(receipt.getReceiptDate());

            if (isExternalStorageWritable()) {

                File receiptFile = getExternalReceiptFile(context, ReceiptFileName);
                FileWriter fw = new FileWriter(receiptFile, true);
                fw.write(jsonReceipt + RECEIPT_SEPARATOR);
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
            RetString = ReadReceiptFileToString(context, ReceiptFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  RetString;
    }

    public String ReadReceiptFileToString(Context context, String ReceiptFileName) {
        String RetString = "";
        try {
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
                        List<String> lstReceipt = new ArrayList<String>(Arrays.asList(stringBuilder.toString().split(RECEIPT_SEPARATOR)));
                        RetString = stringBuilder.toString();

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return RetString;
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

    public static File[] GetReceiptFiles(Context context) {
        // create new filename filter
        FilenameFilter fileNameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                if (name.startsWith(RECEIPT_FILE_NAME_PREFIX)) {
                    return true;
                }
                return false;
            }
        };

        File ReceiptFileDir = new File(String.valueOf(context.getExternalFilesDir(null)));
        File[] ReceiptFiles = ReceiptFileDir.listFiles(fileNameFilter);
        return ReceiptFiles;
    }

    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
