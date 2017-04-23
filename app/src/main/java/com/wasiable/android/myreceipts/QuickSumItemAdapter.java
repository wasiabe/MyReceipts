package com.wasiable.android.myreceipts;

import android.content.Context;
import android.content.Intent;
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
 * Created by wasia on 2017/3/26.
 */

public class QuickSumItemAdapter extends RecyclerView.Adapter<QuickSumItemAdapter.QuickSumItemHolder>{
    private ArrayList<MonthlySummary> mDataset ;
    private Context context;

    public QuickSumItemAdapter(Context cntxt, ArrayList<MonthlySummary> myDataset)  {
        mDataset = myDataset;
        context = cntxt;
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
            final View v = view;
            int position = getAdapterPosition();
            Log.d("ItemClicked:", "Selected:"+ mDataset.get(position));

            MonthlySummary ms = (MonthlySummary)mDataset.get(position);
            String Period = ms.Period.replace("/","");

            Intent intent = new Intent(context, ReceiptActivity.class);
            intent.putExtra(ReceiptActivity.PERIOD, Period);
            context.startActivity(intent);
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
            MonthlySummary ms = (MonthlySummary)mDataset.get(position);
            holder.tvPeriod.setText(ms.Period);
            holder.tvTotalAmount.setText("$" + ms.TotalAmount.toString());
            holder.tvTotalReceipts.setText(ms.TotoaReceipts.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}
