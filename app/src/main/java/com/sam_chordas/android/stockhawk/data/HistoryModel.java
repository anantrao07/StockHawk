package com.sam_chordas.android.stockhawk.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by anant on 2016-04-25.
 */
public class HistoryModel implements  Parcelable {


    private String Symbol ;
    private String Date;
    private double High;
    private double Low;

    public HistoryModel(){

    }
    public String getSymbol() {
        return Symbol;
    }

    public String getDate() {
        return Date;
    }

    public double getHigh() {
        return High;
    }

    public double getLow() {
        return Low;
    }

    public void setSymbol(String symbol) {

        Symbol = symbol;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setHigh(double high) {
        High = high;
    }

    public void setLow(double low) {
        Low = low;
    }

    public HistoryModel(Parcel in) {
        Symbol = in.readString();
        Date = in.readString();
        High = in.readDouble();
        Low = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Symbol);
        dest.writeString(Date);
        dest.writeDouble(High);
        dest.writeDouble(Low);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<HistoryModel> CREATOR = new Parcelable.Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };
}