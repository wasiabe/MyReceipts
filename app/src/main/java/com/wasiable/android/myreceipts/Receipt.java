package com.wasiable.android.myreceipts;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;

import java.util.ArrayList;

/**
 * Created by wasia on 2017/2/25.
 */

public class Receipt implements Parcelable {
    protected Receipt(Parcel in) {
        ReceiptNo = in.readString();
        ReceiptDate = in.readString();
        RandomCode = in.readString();
        SalesAmountHex = in.readString();
        SalesAmount = in.readInt();
        TotalAmountHex = in.readString();
        TotalAmount = in.readInt();
        BuyerEIN = in.readString();
        SalerEIN = in.readString();
        EncryptCode = in.readString();
        SalerData = in.readString();
        ItemCount = in.readInt();
        ItemContent = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Receipt> CREATOR = new Creator<Receipt>() {
        @Override
        public Receipt createFromParcel(Parcel in) {
            return new Receipt(in);
        }

        @Override
        public Receipt[] newArray(int size) {
            return new Receipt[size];
        }
    };

    public static String getReceiptNoKey() {
        return RECEIPT_NO_KEY;
    }

    public static String getReceiptDateKey() {
        return RECEIPT_DATE_KEY;
    }

    public static String getRandomCodeKey() {
        return RANDOM_CODE_KEY;
    }

    public static String getSalesAmountHexKey() {
        return SALES_AMOUNT_HEX_KEY;
    }

    public static String getSalesAmountKey() {
        return SALES_AMOUNT_KEY;
    }

    public static String getTotalAmountHexKey() {
        return TOTAL_AMOUNT_HEX_KEY;
    }

    public static String getTotalAmountKey() {
        return TOTAL_AMOUNT_KEY;
    }

    public static String getEinBuyerKey() {
        return EIN_BUYER_KEY;
    }

    public static String getEinSalerKey() {
        return EIN_SALER_KEY;
    }

    public static String getEncryptCodeKey() {
        return ENCRYPT_CODE_KEY;
    }

    public static String getSalerDataKey() {
        return SALER_DATA_KEY;
    }

    public static String getItemCountKey() {
        return ITEM_COUNT_KEY;
    }

    public static String getTotalItemsKey() {
        return TOTAL_ITEMS_KEY;
    }

    public static String getChineseEncodeKey() {
        return CHINESE_ENCODE_KEY;
    }

    public static String getItemContentKey() {
        return ITEM_CONTENT_KEY;
    }

    public static String getItmeNameKey() {
        return ITME_NAME_KEY;
    }

    public static String getItemQuantityKey() {
        return ITEM_QUANTITY_KEY;
    }

    public static String getUnitPriceKey() {
        return UNIT_PRICE_KEY;
    }

    private static final String RECEIPT_NO_KEY = "receipt_no";
    private static final String RECEIPT_DATE_KEY = "receipt_date";
    private static final String RANDOM_CODE_KEY = "random_code";
    private static final String SALES_AMOUNT_HEX_KEY = "sales_amount_hex";
    private static final String SALES_AMOUNT_KEY = "sales_amount";
    private static final String TOTAL_AMOUNT_HEX_KEY = "total_amount_hex";
    private static final String TOTAL_AMOUNT_KEY = "total_amount";
    private static final String EIN_BUYER_KEY = "buyer_ein";
    private static final String EIN_SALER_KEY = "saler_ein";
    private static final String ENCRYPT_CODE_KEY = "encrypt_code";
    private static final String SALER_DATA_KEY = "saler_data";
    private static final String ITEM_COUNT_KEY = "item_count";
    private static final String TOTAL_ITEMS_KEY = "total_item_count";
    private static final String CHINESE_ENCODE_KEY = "chinese_encode";
    private static final String ITEM_CONTENT_KEY = "item_content";
    private static final String ITME_NAME_KEY = "item_name";
    private static final String ITEM_QUANTITY_KEY = "item_quantity";
    private static final String UNIT_PRICE_KEY = "unit_price";

    public String getReceiptNo() {
        return ReceiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        ReceiptNo = receiptNo;
    }

    public String getReceiptDate() {
        return ReceiptDate;
    }

    public void setReceiptDate(String receiptDate) {
        ReceiptDate = receiptDate;
    }

    public String getRandomCode() {
        return RandomCode;
    }

    public void setRandomCode(String randomCode) {
        RandomCode = randomCode;
    }

    public String getSalesAmountHex() {
        return SalesAmountHex;
    }

    public void setSalesAmountHex(String salesAmountHex) {
        SalesAmountHex = salesAmountHex;
    }

    public int getSalesAmount() {
        return SalesAmount;
    }

    public void setSalesAmount(int salesAmount) {
        SalesAmount = salesAmount;
    }

    public String getTotalAmountHex() {
        return TotalAmountHex;
    }

    public void setTotalAmountHex(String totalAmountHex) {
        TotalAmountHex = totalAmountHex;
    }

    public int getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        TotalAmount = totalAmount;
    }

    public String getBuyerEIN() {
        return BuyerEIN;
    }

    public void setBuyerEIN(String buyerEIN) {
        BuyerEIN = buyerEIN;
    }

    public String getSalerEIN() {
        return SalerEIN;
    }

    public void setSalerEIN(String salerEIN) {
        SalerEIN = salerEIN;
    }

    public String getEncryptCode() {
        return EncryptCode;
    }

    public void setEncryptCode(String encryptCode) {
        EncryptCode = encryptCode;
    }

    public String getSalerData() {
        return SalerData;
    }

    public void setSalerData(String salerData) {
        SalerData = salerData;
    }

    public int getItemCount() {
        return ItemCount;
    }

    public void setItemCount(int itemCount) {
        ItemCount = itemCount;
    }

    public ArrayList<String> getItemContent() {
        return ItemContent;
    }

    public void setItemContent(ArrayList<String> itemContent) {
        ItemContent.addAll(itemContent);
    }

    private String ReceiptNo = "";
    private String ReceiptDate = "";
    private String RandomCode = "";
    private String SalesAmountHex = "";
    private int SalesAmount = 0;
    private String TotalAmountHex = "";
    private int TotalAmount = 0;
    private String BuyerEIN = "";
    private String SalerEIN = "";
    private String EncryptCode = "";
    private String SalerData = "";
    private int ItemCount = 0;
    private ArrayList<String> ItemContent;

    public Receipt() {
        ItemContent = new ArrayList<String>();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ReceiptNo);
        dest.writeString(ReceiptDate);
        dest.writeString(RandomCode);
        dest.writeString(SalesAmountHex);
        dest.writeInt(SalesAmount);
        dest.writeString(TotalAmountHex);
        dest.writeInt(TotalAmount);
        dest.writeString(BuyerEIN);
        dest.writeString(SalerEIN);
        dest.writeString(EncryptCode);
        dest.writeString(SalerData);
        dest.writeInt(ItemCount);
        dest.writeStringList(ItemContent);
    }
}
