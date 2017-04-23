package com.wasiable.android.myreceipts;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.samples.vision.barcodereader.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity {
    public static final String PERIOD = "PERIOD";
    private String Period;
    private String ReceiptFileName;
    private ArrayList<JSONObject> lstJSONReceipt = new ArrayList<JSONObject>() ;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ReceiptAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewReceipt);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Period = getIntent().getStringExtra(PERIOD);
        try {
            ReceiptFileName = ReceiptFile.GetReceiptFileName(Period + "01");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ReceiptFile receiptFile = new ReceiptFile();
        String ReceiptFileContent = receiptFile.ReadReceiptFileToString(this, ReceiptFileName);
        if (!ReceiptFileContent.isEmpty()){
            String jsonText = ReceiptFile.GetReceiptJSONString(ReceiptFileContent);

            try {
                JSONObject json = new JSONObject(jsonText);
                JSONArray arr = json.getJSONArray("receipts");
                for(int j=0; j<=arr.length()-1; j++) {
                    lstJSONReceipt.add(arr.getJSONObject(j));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mAdapter = new ReceiptAdapter(lstJSONReceipt);
        mRecyclerView.setAdapter(mAdapter);
    }
}