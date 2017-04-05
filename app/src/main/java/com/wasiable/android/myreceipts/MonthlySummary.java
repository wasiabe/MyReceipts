package com.wasiable.android.myreceipts;

import android.support.annotation.NonNull;

/**
 * Created by wasia on 2017/4/5.
 */

public class MonthlySummary implements Comparable<MonthlySummary> {
    public String Period="";
    public Integer TotalAmount=0;
    public Integer TotoaReceipts=0;
    public MonthlySummary() {}

    @Override
    public int compareTo(MonthlySummary ms ) {
        return this.Period.compareTo(ms.Period);
    }
}
