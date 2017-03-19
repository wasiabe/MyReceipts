package com.wasiable.android.myreceipts;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wasia on 2017/2/27.
 */

public class PrizeNumber {
    public String number;
    public ArrayList<Integer> matchs = new ArrayList<Integer>();
    public ArrayList<Integer> prizes = new ArrayList<Integer>();

    public PrizeNumber(){}

    public PrizeNumber(String number, ArrayList<Integer> matchs, ArrayList<Integer> prizes){
        this.number = number;
        this.matchs = matchs;
        this.prizes = prizes;
    }

    public int CheckPrizeNumber(String ReceiptNumber) {
        int prize = 0;
        for(int j=0; j<prizes.size(); j++) {

            Long m[] = new Long[matchs.size()];
            m = matchs.toArray(m);


            Long p[] = new Long[prizes.size()];
            p = prizes.toArray(p);

            int match =m[j].intValue();
            int idxBeginP = number.length() - match;
            String PznbrToMatch = number.substring(idxBeginP, number.length());

            int idxBeginR = ReceiptNumber.length() - match;
            String ReceiptNbrToMatch = ReceiptNumber.substring(idxBeginR, ReceiptNumber.length());

            if (PznbrToMatch.equals(ReceiptNbrToMatch)) {
                prize = p[j].intValue();
            }
        }
        return prize;
    }
}
