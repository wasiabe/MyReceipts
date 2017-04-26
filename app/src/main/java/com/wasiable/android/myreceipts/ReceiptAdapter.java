package com.wasiable.android.myreceipts;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.samples.vision.barcodereader.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wasia on 2017/4/22.
 */

public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.ReceiptHolder> {
    private ArrayList<JSONObject> mDataset ;
    private TextView tvReceiptNo;
    private TextView tvReceiptDate;
    private TextView tvReceiptTotalAmount;

    public class ReceiptHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ReceiptHolder(View itemView) {
            super(itemView);
            tvReceiptNo = (TextView)itemView.findViewById(R.id.dtl_receipt_no);
            tvReceiptDate = (TextView)itemView.findViewById(R.id.dtl_receipt_date);
            tvReceiptTotalAmount = (TextView)itemView.findViewById(R.id.dtl_receipt_total_amount);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.d("RecyclerView", "CLICK!");
        }
    }

    public ReceiptAdapter(ArrayList<JSONObject> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public ReceiptAdapter.ReceiptHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipt_item, parent, false);
        return new ReceiptHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ReceiptHolder holder, int position) {

        try {
            JSONObject json = mDataset.get(position);
            String ReceiptNO = json.getString("ReceiptNo");
            String ReceiptDate = json.getString("ReceiptDate");
            String TotalAmount = String.valueOf(json.getInt("TotalAmount"));
            tvReceiptNo.setText(ReceiptNO);
            tvReceiptDate.setText(ReceiptDate.substring(0,3) + "/" + ReceiptDate.substring(3,5) + "/" + ReceiptDate.substring(5,7));
            tvReceiptTotalAmount.setText("$" + TotalAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {

        return mDataset.size();
    }

    public void setFilter(ArrayList<JSONObject> newList) {
        mDataset.clear();
        mDataset.addAll(newList);
        notifyDataSetChanged();
    }
}
