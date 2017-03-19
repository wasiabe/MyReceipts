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

            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("prizenumbers");
            ref.orderByChild("period").equalTo(mDataset.get(position));

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String period;
                    for (DataSnapshot ds : dataSnapshot.getChildren() ){
                        for(DataSnapshot rules : ds.child("rules").getChildren()) {
                            PrizeNumber pn = new PrizeNumber();
                            pn.number = rules.child("number").getValue().toString();
                            pn.matchs = (ArrayList<Integer>) rules.child("matchs").getValue();
                            pn.prizes = (ArrayList<Integer>) rules.child("prizes").getValue();
                            String ReceiptNumber = "SR12340000";
                            int Prize = pn.CheckPrizeNumber(ReceiptNumber);
                            if (Prize > 0) {
                                Toast.makeText(v.getContext(), "Prize is " + Prize, Toast.LENGTH_LONG).show();
                            }
                        }
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
        holder.period.setText(period);

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

}