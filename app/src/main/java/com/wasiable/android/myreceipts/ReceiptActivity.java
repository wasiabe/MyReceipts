package com.wasiable.android.myreceipts;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.samples.vision.barcodereader.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ReceiptActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
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

        Toolbar Toolbar = (Toolbar) findViewById(R.id.receipt_toolbar);
        setSupportActionBar(Toolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_receipt,menu);
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint(getString(R.string.search_title_receipt));
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toUpperCase();
        ArrayList<JSONObject> newList = new ArrayList<JSONObject>();
        Integer ResultCount = 0;
        Integer ResultTotalAmount = 0;
        String  ResultString = "";
        try {
            for(JSONObject receipt: lstJSONReceipt) {
                if (receipt.getString("ReceiptNo").contains(newText)
                        || receipt.getString("ReceiptDate").contains(newText.replace("/",""))
                        || receipt.getString("ItemContent").contains(newText)
                        ) {
                    newList.add(receipt);
                    ResultCount += 1;
                    ResultTotalAmount += Integer.valueOf(receipt.getString("TotalAmount"));
                }
            }
            mAdapter = new ReceiptAdapter(newList);
            mRecyclerView.setAdapter(mAdapter);
            ResultString = String.format(getString(R.string.search_result_text),ResultTotalAmount);
            Toast.makeText(this, ResultString, Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
        e.printStackTrace();
    }


        return false;
    }
}
