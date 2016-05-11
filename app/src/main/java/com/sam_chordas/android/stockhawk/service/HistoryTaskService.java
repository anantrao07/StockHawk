package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.rest.Utils;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Scanner;

/**
 * Created by anant on 2016-04-11.
 */
public class HistoryTaskService extends GcmTaskService {

    private String LOG_CAT = HistoryTaskService.class.getSimpleName();
    private OkHttpClient  historyCallClient  = new OkHttpClient();
    private Context mHistoryContext;
    private CharSequence mstartDate;
    private CharSequence mendDate;
    Utils utility = new Utils();
    private Context mContext;





    public HistoryTaskService(){

    }

    public HistoryTaskService(Context context){

        mHistoryContext = context;

    }


    InputStream fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = historyCallClient.newCall(request).execute();

        return response.body().byteStream();
    }


    @Override
    public  int onRunTask(TaskParams taskParams) {

        if(mHistoryContext == null){
            mHistoryContext = this;

        }
        String urlString;
        String getResponse;

        int result = GcmNetworkManager.RESULT_FAILURE;
        mstartDate = Utils.getStartDate();
        mendDate = Utils.getEndDate();

        if(taskParams.getExtras().getString("Symbolshortname")!=null) {
            String symbolName = taskParams.getExtras().getString("Symbolshortname");
            StringBuilder historyUrl = new StringBuilder();
            try {

                Log.e("getstartdate",Utils.getStartDate());
                Log.e("getenddate",Utils.getEndDate());

                Log.d(LOG_CAT, symbolName);
                historyUrl.append("https://query.yahooapis.com/v1/public/yql?q=");
                historyUrl.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol=","UTF-8"));
                     //   + " in ( \'" + symbolName + " \') and startDate= \' 2016-01-01 \' and endDate=\' 2016-03-10 \' ", "UTF-8"));
                historyUrl.append(URLEncoder.encode("\'"+symbolName+"\'"+" and startDate=\'"+mstartDate+"\' and endDate=\'"+mendDate+"\'", "UTF-8"));
                historyUrl.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");


            } catch (UnsupportedEncodingException e) {
                Log.e(LOG_CAT, "error in httpcall");
                e.printStackTrace();
            }

            result = GcmNetworkManager.RESULT_FAILURE;

            if (historyUrl != null) {



                urlString = historyUrl.toString();

                //String url = "https://query.yahooapis.com/v1/public/yql?q=select%20%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(%20'TSLA'%20)%20and%20startDate%20%3D%20'2016-01-04'%20and%20endDate%20%3D%20'2016%20-%2004-10'&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=callback";
                Log.e(LOG_CAT, urlString);


                try {
                    //getResponse = fetchData((urlString)).toString();
                    InputStreamReader in = new InputStreamReader(fetchData((urlString)));
                    String inString = new Scanner(in).next();
                    Log.e(LOG_CAT, inString);
                    utility.readHistory(fetchData((urlString)));

                        Intent dataIntent = new Intent("android.intent.action.MAIN");//"android.intent.action.MAIN");

                        if(dataIntent!=null)
                            dataIntent.putParcelableArrayListExtra("historydata" , utility.hm);
                    mHistoryContext.sendBroadcast(dataIntent);

                    } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            result = GcmNetworkManager.RESULT_SUCCESS;
        }



        return result;
    }
}
