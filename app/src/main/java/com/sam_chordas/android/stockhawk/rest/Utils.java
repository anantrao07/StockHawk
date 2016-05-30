package com.sam_chordas.android.stockhawk.rest;

import android.content.ContentProviderOperation;
import android.util.Log;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.sam_chordas.android.stockhawk.data.HistoryModel;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by sam_chordas on 10/8/15.
 */
public class Utils {

  private static String LOG_TAG = Utils.class.getSimpleName();



  Object o = new Object();
  public static boolean showPercent = true;
  public static boolean status = true;
  public static boolean historyStatus = true;
  public ArrayList<HistoryModel> hm = new ArrayList<>();
  public HistoryModel historyDataObj ;

  private int resultCount;

  public static ArrayList quoteJsonToContentVals(String JSON) {
    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
    JSONObject jsonObject = null;
    JSONArray resultsArray = null;
    try {
      jsonObject = new JSONObject(JSON);
      if (jsonObject != null && jsonObject.length() != 0) {
        jsonObject = jsonObject.getJSONObject("query");
        int count = Integer.parseInt(jsonObject.getString("count"));
        if (count == 1) {
          jsonObject = jsonObject.getJSONObject("results")
                  .getJSONObject("quote");

          if (!(jsonObject.getString("Bid").equals("null") && jsonObject.getString("ChangeinPercent").equals("null"))) {
            batchOperations.add(buildBatchOperation(jsonObject));
          } else {
            status = false;

          }
        } else {
          resultsArray = jsonObject.getJSONObject("results").getJSONArray("quote");


          if (resultsArray != null && resultsArray.length() != 0) {
            for (int i = 0; i < resultsArray.length(); i++) {
              jsonObject = resultsArray.getJSONObject(i);
              batchOperations.add(buildBatchOperation(jsonObject));
            }
          }
        }
      }
    } catch (JSONException e) {
      Log.e(LOG_TAG, "String to JSON failed: " + e);
    }
    return batchOperations;
  }

  public static String truncateBidPrice(String bidPrice) {
    Log.e("value of bidprice ", bidPrice);

    if (bidPrice != null) {
      bidPrice = String.format("%.2f", Float.parseFloat(bidPrice));
    }
    return bidPrice;

  }

  public static String truncateChange(String change, boolean isPercentChange) {
    String weight = change.substring(0, 1);
    String ampersand = "";
    if (isPercentChange) {
      ampersand = change.substring(change.length() - 1, change.length());
      change = change.substring(0, change.length() - 1);
    }
    change = change.substring(1, change.length());
    double round = (double) Math.round(Double.parseDouble(change) * 100) / 100;
    change = String.format("%.2f", round);
    StringBuffer changeBuffer = new StringBuffer(change);
    changeBuffer.insert(0, weight);
    changeBuffer.append(ampersand);
    change = changeBuffer.toString();
    return change;
  }

  public static ContentProviderOperation buildBatchOperation(JSONObject jsonObject) {
    ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(
            QuoteProvider.Quotes.CONTENT_URI);
    try {
      String bid = jsonObject.getString("Bid");
      String change = jsonObject.getString("Change");
      Log.e("value of bid price is", change);
      if (!(bid.equals("null"))) {

        builder.withValue(QuoteColumns.SYMBOL, jsonObject.getString("symbol").toUpperCase());
        builder.withValue(QuoteColumns.BIDPRICE, truncateBidPrice(jsonObject.getString("Bid")));
        builder.withValue(QuoteColumns.PERCENT_CHANGE, truncateChange(
                jsonObject.getString("ChangeinPercent"), true));
        builder.withValue(QuoteColumns.CHANGE, truncateChange(change, false));
        builder.withValue(QuoteColumns.ISCURRENT, 1);

      }
      if (change.charAt(0) == '-') {
        builder.withValue(QuoteColumns.ISUP, 0);
      } else {
        builder.withValue(QuoteColumns.ISUP, 1);
      }

    } catch (JSONException e) {
      e.printStackTrace();
    }
    return builder.build();
  }


  public static String getEndDate() {
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd ");
    Calendar endcal = Calendar.getInstance();

    String currentDate = sdf.format(endcal.getTime());

    return currentDate;

  }

  public static String getStartDate() {
    DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    Calendar startcal = Calendar.getInstance();

    startcal.add(Calendar.MONTH, -4);
    String startDate = sdf.format(startcal.getTime());


    return startDate;
  }

  public ArrayList<HistoryModel> readHistory(InputStream in) throws IOException {

    JsonReader historyJson = new JsonReader(new InputStreamReader(in, "UTF-8"));

    try {
      return readMessage(historyJson);

    } finally {
      historyJson.close();
    }

  }

  public ArrayList<HistoryModel> readMessage(JsonReader reader) throws IOException {

    List messages = new ArrayList();

    String iponame;
    String date;
    double high;
    double low;

    reader.beginObject();

    String first = reader.nextName();
   // Log.e("value of readernextname" , first);
    reader.beginObject();

    while (reader.hasNext() != false && reader.peek()!= JsonToken.END_DOCUMENT) {


      JsonToken jtk = reader.peek();
      Log.e("value of tkn" , jtk.toString());
      if(jtk==JsonToken.NAME){
        String name = reader.nextName();

      //  Log.e("value of next reader" , name);

        if(name.equals("count")) {
          resultCount = reader.nextInt();
         // Log.e("value of count is ", String.valueOf(resultCount));

        }

            JsonToken resultToken =reader.peek();

          if(resultCount >0 && name.equals("results")){

            reader.beginObject();
            String arrayTokenNAme = reader.nextName();
           // Log.d("value of value of token" , arrayTokenNAme);
            reader.beginArray();
            while ((reader.hasNext() != false)){

              //JsonToken resultToken = reader.peek();
              //reader.beginObject();
             // Log.e("value of token " , jtk.toString());
              historyDataObj = new HistoryModel();
              reader.beginObject();
              while (reader.hasNext() && reader.peek()!= JsonToken.END_DOCUMENT){


                String resultArraynames = reader.nextName();
                if(resultArraynames.equals("Symbol")){
                  historyDataObj.setSymbol(reader.nextString());
                //  Log.d("Symbol" ,historyDataObj.getSymbol() );
                }
                else if(resultArraynames.equals("Date")) {
                  historyDataObj.setDate(reader.nextString());
                }
                else if(resultArraynames.equals("High")) {
                  historyDataObj.setHigh(Double.parseDouble(reader.nextString()));
                }
                else if(resultArraynames.equals("Low")) {
                  historyDataObj.setLow(Double.parseDouble(reader.nextString()));
                }
                else{
                  reader.skipValue();
                }

                //Log.d(LOG_TAG, resultArraynames);
                hm.add(historyDataObj);

              }
              reader.endObject();

            }

            reader.endArray();
          }
      }

      else {
        reader.skipValue();
      }
    }

    reader.endObject();

    return hm;
    }


  }

