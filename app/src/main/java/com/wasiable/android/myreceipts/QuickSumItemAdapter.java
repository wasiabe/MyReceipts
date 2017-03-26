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
        public TextView qs_rv_item;
        public TextView qs_rv_item_amount;

        public QuickSumItemHolder(View itemView) {
            super(itemView);
            qs_rv_item = (TextView)itemView.findViewById(R.id.quick_sum_rv_item);
            qs_rv_item_amount = (TextView)itemView.findViewById(R.id.quick_sum_rv_item_amount);
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
            String item = json.getString("item");
            Integer amount = json.getInt("amount");
            holder.qs_rv_item.setText(item);
            holder.qs_rv_item_amount.setText(amount.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
