package com.wasiable.android.myreceipts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.samples.vision.barcodereader.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wasia on 2017/3/26.
 */

public class QuickSumItemAdapter extends RecyclerView.Adapter<QuickSumItemAdapter.QuickSumItemHolder>{
    private ArrayList<String> mDataset ;
    public QuickSumItemAdapter(ArrayList<String> myDataset)  {
        mDataset = myDataset;
    }

    public class QuickSumItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tvPeriod;
        public TextView tvTotalAmount;
        public TextView tvTotalReceipts;

        public QuickSumItemHolder(View itemView) {
            super(itemView);
            tvPeriod = (TextView)itemView.findViewById(R.id.quick_sum_period);
            tvTotalAmount = (TextView)itemView.findViewById(R.id.quick_sum_total_amount);
            tvTotalReceipts = (TextView)itemView.findViewById(R.id.quick_sum_total_receipts);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {

        }
    }

    @Override
    public QuickSumItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quick_sum_item, parent, false);

        return new QuickSumItemAdapter.QuickSumItemHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(QuickSumItemHolder holder, int position) {
        try {
            String JSONString = (String)mDataset.get(position);
            JSONObject json = new JSONObject(JSONString);
            String period = json.getString("period");
            Integer total_amount = json.getInt("total_amount");
            Integer total_receipts = json.getInt("total_receipts");
            holder.tvPeriod.setText(period);
            holder.tvTotalAmount.setText(total_amount.toString());
            holder.tvTotalReceipts.setText(total_receipts.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
