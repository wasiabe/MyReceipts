package com.wasiable.android.myreceipts;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AlertDialog;
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
    private Context context;

    public PrizeNumberAdapter(Context cntxt, ArrayList<String> myDataset) {
        context = cntxt;
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
            int position = getAdapterPosition();
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
            ref.orderByChild("period").equalTo(mDataset.get(position)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String period;
                    Integer totalPrize = 0;
                    ArrayList<String> PrizeList = new ArrayList<String>();

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
                                    String jsonText = ReceiptFile.GetReceiptJSONString(ReceiptFileContent);
                                    //String jsonText = "{\"receipts\":[" + ReceiptFileContent.substring(0, ReceiptFileContent.length()-1) + "]}";
                                    try {
                                        JSONObject json = new JSONObject(jsonText);
                                        JSONArray arr = json.getJSONArray("receipts");
                                        for(int j=0; j<=arr.length()-1; j++) {
                                            String ReceiptNumber = arr.getJSONObject(j).getString("ReceiptNo");
                                            String ReceiptDate = arr.getJSONObject(j).getString("ReceiptDate");

                                            int Prize = pn.CheckPrizeNumber(ReceiptNumber);
                                            if (Prize > 0) {
                                                totalPrize += Prize;
                                                PrizeList.add(ReceiptNumber + " " + ReceiptDate + " $" + String.valueOf(Prize));
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    AlertDialog.Builder alertPrize = new AlertDialog.Builder(context);
                    String alertPrizeTitle = context.getString(R.string.title_prize_number);
                    String alertPrizePostiveBtn = context.getString(R.string.igotit);
                    if (totalPrize>0) {
                        String Congratulations = String.format(context.getString(R.string.prize_check_result), totalPrize);
                        Toast.makeText(v.getContext(), Congratulations, Toast.LENGTH_LONG).show();

                        String[] PrizeArray = PrizeList.toArray(new String[PrizeList.size()]);
                        alertPrize
                            .setTitle(alertPrizeTitle)
                            .setItems(PrizeArray, null)
                            .setPositiveButton(alertPrizePostiveBtn,null)
                            .show();
                    } else {
                        String no_prize = context.getString(R.string.msg_no_prize_for_this_period);
                        alertPrize
                                .setMessage(no_prize)
                                .setPositiveButton(alertPrizePostiveBtn,null)
                                .show();
                    }
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
        String periodYear = period.substring(0,3);
        String periodMonth1 = period.substring(3,5);
        String periodMonth2 = period.substring(5,7);
        String periodDisp = periodYear + "年" + periodMonth1 + "-" + periodMonth2 + "月";
        holder.period.setText(periodDisp);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}