package com.wasiable.android.myreceipts;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.samples.vision.barcodereader.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by wasia on 2017/2/27.
 */

public class PrizeNumberAdapter extends RecyclerView.Adapter<PrizeNumberAdapter.PrizeNumberHolder>{
    private ArrayList<String> mDataset ;

    public PrizeNumberAdapter(ArrayList<String> myDataset) {
        mDataset = myDataset;
    }

    public class PrizeNumberHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView period;

        public PrizeNumberHolder(View v) {
            super(v);
            period = (TextView)v.findViewById(R.id.period);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(final View view) {
            final View v = view;
            int position  =   getAdapterPosition();
            Log.d("RecyclerView", "CLICK!");
            Log.d("ItemClicked:", "Selected:"+ mDataset.get(position));

            final ReceiptFile receiptFile = new ReceiptFile();
            String PrizeNumberPeriod = mDataset.get(position);
            String PeriodYear = PrizeNumberPeriod.substring(0,3);
            String PeriodMonth1 = PrizeNumberPeriod.substring(3,5);
            String PeriodMonth2 = PrizeNumberPeriod.substring(5,7);
            String ReceiptFileName1 = "";
            String ReceiptFileName2 = "";
            try {
                ReceiptFileName1 = ReceiptFile.GetReceiptFileName(PeriodYear + PeriodMonth1 + "01");
                ReceiptFileName2 = ReceiptFile.GetReceiptFileName(PeriodYear + PeriodMonth2 + "01");
            } catch (Exception e) {
                e.printStackTrace();
            }
            final String[] ReceiptFileNameList = {ReceiptFileName1, ReceiptFileName2};

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("prizenumbers");
            ref.orderByChild("period").equalTo(mDataset.get(position));

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String period;
                    Integer totalPrize = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren() ){
                        for(DataSnapshot rules : ds.child("rules").getChildren()) {
                            PrizeNumber pn = new PrizeNumber();
                            pn.number = rules.child("number").getValue().toString();
                            pn.matchs = (ArrayList<Integer>) rules.child("matchs").getValue();
                            pn.prizes = (ArrayList<Integer>) rules.child("prizes").getValue();

                            for(int i=0; i<=ReceiptFileNameList.length-1; i++) {
                                String ReceiptFileName = ReceiptFileNameList[i];
                                String ReceiptFileContent = receiptFile.ReadReceiptFileToString(view.getContext(), ReceiptFileName);
                                if (!ReceiptFileContent.isEmpty()){
                                    String jsonText = "{\"receipts\":[" + ReceiptFileContent.substring(0, ReceiptFileContent.length()-1) + "]}";
                                    try {
                                        JSONObject json = new JSONObject(jsonText);
                                        JSONArray arr = json.getJSONArray("receipts");
                                        for(int j=0; j<=arr.length()-1; j++) {
                                            String ReceiptNumber = arr.getJSONObject(j).getString("ReceiptNo");
                                            Log.d("ReceiptNumber", ReceiptNumber);

                                            int Prize = pn.CheckPrizeNumber(ReceiptNumber);
                                            if (Prize > 0) {
                                                totalPrize += Prize;
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    Toast.makeText(v.getContext(), "Total Prize is " + totalPrize, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public PrizeNumberHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View inflatedView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.prize_number_item, parent, false);

        return new PrizeNumberHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(PrizeNumberHolder holder, int position) {
        String period = (String)mDataset.get(position);
        holder.period.setText(period);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}