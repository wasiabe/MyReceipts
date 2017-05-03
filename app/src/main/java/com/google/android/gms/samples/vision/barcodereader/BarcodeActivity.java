/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.samples.vision.barcodereader;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.util.ArrayList;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wasiable.android.myreceipts.*;



/**
 * Main activity demonstrating how to pass extra parameters to an activity that
 * reads barcodes.
 */
public class BarcodeActivity extends Activity implements View.OnClickListener {

    // use a compound button so either checkbox or switch widgets work.
    private CompoundButton autoFocus;
    private CompoundButton useFlash;
    private CompoundButton showReceipt;
    private TextView statusMessage;
    private TextView barcodeValue;
    private String ReceiptNo = "";

    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    private static final int LEFT_BARCODE_VALUE_BASIC_LENGTH=77;

    private Receipt receipt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        statusMessage = (TextView)findViewById(R.id.status_message);
        barcodeValue = (TextView)findViewById(R.id.barcode_value);

        autoFocus = (CompoundButton) findViewById(R.id.auto_focus);
        useFlash = (CompoundButton) findViewById(R.id.use_flash);
        showReceipt = (CompoundButton) findViewById(R.id.show_receipt);

        findViewById(R.id.read_barcode).setOnClickListener(this);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.read_barcode) {
            // launch barcode activity.
            Intent intent = new Intent(this, BarcodeCaptureActivity.class);
            intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus.isChecked());
            intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash.isChecked());

            startActivityForResult(intent, RC_BARCODE_CAPTURE);
        }
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<String> lstLeft = new ArrayList<>();
        ArrayList<String> lstRight = new ArrayList<>();
        ReceiptNo = "";

        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    statusMessage.setText(R.string.barcode_success);
                    TextView tvBarcodeValue = (TextView)findViewById(R.id.barcode_value);
                    tvBarcodeValue.setText("");

                    receipt = new Receipt();

                    Barcode barcodeLeft = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObjectLeft);
                    if (barcodeLeft==null) {
                        tvBarcodeValue.setText(R.string.barcode_left_null);
                        Toast.makeText(this, R.string.barcode_left_null, Toast.LENGTH_LONG);
                        return;
                    }

                    if (barcodeLeft!=null) {
                        lstLeft = parseBarcodeValueLeft(barcodeLeft.displayValue);
                        Log.d(TAG, "Left Barcode read: " + barcodeLeft.displayValue);
                    }
                    Barcode barcodeRight = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObjectRight);
                    if(barcodeRight!=null) {
                        lstRight = parseBarcodeValueRight(barcodeRight.displayValue);
                        Log.d(TAG, "Right Barcode read: " + barcodeRight.displayValue);
                    }
                    //barcodeValue.setText("Left:" + barcodeLeft.displayValue + "/n Right:" + barcodeRight.displayValue);
                    lstLeft.addAll(lstRight);
                    String[] lstReceiptInfo = lstLeft.toArray(new String[lstLeft.size()]);

                    // Check Prize Number
                    ReceiptNo = receipt.getReceiptNo();
                    tvBarcodeValue.setText(getString(R.string.receipt_no) + ":" + ReceiptNo);

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("prizenumbers");
                    ref.orderByChild("period").equalTo(receipt.getPeriod()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            TextView tvBarcodeValue  = (TextView) findViewById(R.id.barcode_value);
                            String orgValue = (String) tvBarcodeValue.getText();
                            tvBarcodeValue.setText(orgValue + "  " + getString(R.string.msg_no_prize));
                            boolean didntWin = true;

                            if (!dataSnapshot.exists()) {
                                statusMessage.setText(R.string.prize_number_not_exist);
                            }
                            for (DataSnapshot ds : dataSnapshot.getChildren() ) {
                                for (DataSnapshot rules : ds.child("rules").getChildren()) {
                                    PrizeNumber pn = new PrizeNumber();
                                    pn.number = rules.child("number").getValue().toString();
                                    pn.matchs = (ArrayList<Integer>) rules.child("matchs").getValue();
                                    pn.prizes = (ArrayList<Integer>) rules.child("prizes").getValue();
                                    int Prize = pn.CheckPrizeNumber(ReceiptNo);
                                    if (Prize > 0) {
                                        didntWin = false;
                                        tvBarcodeValue.setText(orgValue + "  " + String.format( getString(R.string.prize_check_result),Prize));
                                        Toast.makeText(BarcodeActivity.this, String.format( getString(R.string.prize_check_result),Prize), Toast.LENGTH_LONG);
                                    }
                                }
                            }
                            if (didntWin) {
                                Toast.makeText(BarcodeActivity.this, getString(R.string.msg_no_prize), Toast.LENGTH_LONG);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    // Write to receipt file
                    ReceiptFile receiptFile = new ReceiptFile();
                    try {
                        if (receiptFile.isReceiptDuplicated(this, receipt) ) {
                            Log.i("isReceiptDuplicated","YES");

                            //tvBarcodeValue.setText(getString(R.string.receipt_duplicated));
                            Toast.makeText(this, getString(R.string.receipt_duplicated),  Toast.LENGTH_LONG).show();
                        } else {
                            receiptFile.WriteReceiptToFile(this, receipt);
                        }
                        if (showReceipt.isChecked()) {
                            AlertDialog diagReceiptInfo = new AlertDialog.Builder(this)
                                    .setTitle("Receipt Information")
                                    .setItems(lstReceiptInfo, null)
                                    .setPositiveButton(R.string.ok, null)
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    statusMessage.setText(R.string.barcode_failure);
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                statusMessage.setText(String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)));
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private ArrayList<String> parseBarcodeValueRight(String barcodeValue) {
        barcodeValue = barcodeValue.trim();
        if (barcodeValue.equals("**")) {
            barcodeValue = "";
        } else {
            barcodeValue = barcodeValue.substring(2);
            if (barcodeValue.substring(0,1).equals(":")) {
                barcodeValue = barcodeValue.substring(1);
            }
        }
        ArrayList<String> lstItemJSON = parseItemInfo(barcodeValue);
        receipt.setItemContent(lstItemJSON);

        return lstItemJSON;
    }

    private ArrayList<String> parseItemInfo (String ItemInfo) {
        int i = 0;
        ArrayList<String> lstItemJSON = new ArrayList<String>();
        String itemName = "";
        String itemQuantity = "";
        String unitPrice = "";
        String itemNameDisp = "";
        String itemQuantityDisp = "";
        String unitPriceDisp = "";

        String[] lstItemInfo = ItemInfo.split(":");
        for(i=0; i<lstItemInfo.length; i++) {
            switch (i % 3) {
                case 0:
                    if (!lstItemInfo[i].isEmpty()) {
                        itemName = Receipt.getItmeNameKey() + ":" + lstItemInfo[i];
                        itemNameDisp = getString(R.string.item_name) + ":" + lstItemInfo[i];
                    }
                    break;
                case 1:
                    itemQuantity = Receipt.getItemQuantityKey() + ":" + lstItemInfo[i].replaceAll("\\D+","");
                    itemQuantityDisp = getString(R.string.item_quantity) + ":" + lstItemInfo[i].replaceAll("\\D+","");
                    break;
                case 2:
                    unitPrice = Receipt.getUnitPriceKey() + ":" + lstItemInfo[i].replaceAll("\\D+","");
                    unitPriceDisp = getString(R.string.unit_price) + ":" + lstItemInfo[i].replaceAll("\\D+","");
                    lstItemJSON.add("{"+itemName+","+itemQuantity+","+unitPrice+"}");
                    break;
            }
        }
        return lstItemJSON;
    }

    private ArrayList<String> parseBarcodeValueLeft(String barcodeValue) {
        barcodeValue = barcodeValue.trim();
        String receiptNo = barcodeValue.substring(0,10);         // 10 chars
        String receiptDate = barcodeValue.substring(10,17);     // 7 chars
        String randomCode =  barcodeValue.substring(17,21);     // 4 chars
        String salesAmountHex =  barcodeValue.substring(21,29);     //8 chars
        String totalSalesAmountHex  = barcodeValue.substring(29,37);  // 8 chars
        String buyerEIN  = barcodeValue.substring(37,45);  // 8 chars
        String salerEIN  = barcodeValue.substring(45,53);  // 8 chars
        String encryptCode  = barcodeValue.substring(53,77);  // 24 chars

        String salerData = "";
        String itemCount = "";
        String ttlItemCount = "";
        String chineseEncode = "";

        ArrayList<String> lstItemJSON = new ArrayList<String>();
        if (barcodeValue.length() > LEFT_BARCODE_VALUE_BASIC_LENGTH) {
            String receiptDetail = barcodeValue.substring(LEFT_BARCODE_VALUE_BASIC_LENGTH);
            String[] lstReceiptDetail = receiptDetail.split(":");
            salerData = lstReceiptDetail[1];
            itemCount = lstReceiptDetail[2];
            ttlItemCount = lstReceiptDetail[3];
            chineseEncode = lstReceiptDetail[4];

            String itemInfo = "";
            if (lstReceiptDetail.length > 5) {
                for (int i=5;i<lstReceiptDetail.length;i++) {
                    itemInfo = itemInfo + lstReceiptDetail[i] + ":";
                }
            }
            lstItemJSON = parseItemInfo(itemInfo);
        }

        // Convert Hex  amount to decimal
        String SalesAmount = String.valueOf(Integer.parseInt(salesAmountHex,16));
        String TotalAmount = String.valueOf(Integer.parseInt(totalSalesAmountHex,16));

        ArrayList<String> lstReceiptInfo = new ArrayList<String>();
        lstReceiptInfo.add(getString( R.string.receipt_no )+ ":" + receiptNo);
        lstReceiptInfo.add(getString( R.string.receipt_date )+ ":" + receiptDate);
        lstReceiptInfo.add(getString( R.string.random_code )+ ":" + randomCode);
        lstReceiptInfo.add(getString( R.string.sales_amount_hex )+ ":" + salesAmountHex);
        lstReceiptInfo.add(getString( R.string.sales_amount )+ ":" + SalesAmount);
        lstReceiptInfo.add(getString( R.string.total_amount_hex )+ ":" + totalSalesAmountHex);
        lstReceiptInfo.add(getString( R.string.total_amount )+ ":" + TotalAmount);
        lstReceiptInfo.add(getString( R.string.buyer_ein )+ ":" + buyerEIN);
        lstReceiptInfo.add(getString( R.string.saler_ein )+ ":" + salerEIN);
        lstReceiptInfo.add(getString( R.string.encrypt_code )+ ":" + encryptCode);
        lstReceiptInfo.add(getString( R.string.saler_data )+ ":" + salerData);
        lstReceiptInfo.add(getString( R.string.item_count )+ ":" + itemCount);
        lstReceiptInfo.add(getString( R.string.total_item_count )+ ":" + ttlItemCount);
        lstReceiptInfo.add(getString( R.string.chinese_encode )+ ":" + chineseEncode);
        lstReceiptInfo.addAll(lstItemJSON);

        // Set value to  receipt object
        receipt.setReceiptNo(receiptNo);
        receipt.setReceiptDate(receiptDate);
        receipt.setRandomCode(randomCode);
        receipt.setSalesAmountHex(salesAmountHex);
        receipt.setSalesAmount(Integer.valueOf(SalesAmount));
        receipt.setTotalAmountHex(totalSalesAmountHex);
        receipt.setTotalAmount(Integer.valueOf(TotalAmount));
        receipt.setBuyerEIN(buyerEIN);
        receipt.setSalerEIN(salerEIN);
        receipt.setEncryptCode(encryptCode);
        receipt.setSalerData(salerData);
        receipt.setItemContent(lstItemJSON);

        return lstReceiptInfo;
    }
}