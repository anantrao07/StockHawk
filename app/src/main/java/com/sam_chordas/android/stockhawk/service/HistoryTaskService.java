package com.sam_chordas.android.stockhawk.service;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by anant on 2016-04-11.
 */
public class HistoryTaskService extends GcmTaskService {

    private String LOG_CAT = HistoryTaskService.class.getSimpleName();
    private OkHttpClient  historyCallClient  = new OkHttpClient();
    private Context mHistoryContext;
    private String mstartDate;
    private String mendDate;



    public HistoryTaskService(){

    }

    public HistoryTaskService(Context context){

        mHistoryContext = context;

    }
    String fetchData(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = historyCallClient.newCall(request).execute();
        return response.body().string();
    }


    @Override
    public  int onRunTask(TaskParams taskParams) {

        if(mHistoryContext == null){
            mHistoryContext = this;

        }
        String urlString;
        String getResponse;

        int result = GcmNetworkManager.RESULT_FAILURE;

        if(taskParams.getExtras().getString("Symbolshortname")!=null) {
            String symbolName = taskParams.getExtras().getString("Symbolshortname");
            StringBuilder historyUrl = new StringBuilder();
            try {
                 //mstartDate = new SimpleDateFormat("YYYY-MM-DD").format(new Date());
                //  String symbolName = taskParams.getExtras().getString("Symbolshortname");

                Log.d(LOG_CAT, symbolName);
                historyUrl.append("https://query.yahooapis.com/v1/public/yql?q=");
                historyUrl.append(URLEncoder.encode("select * from yahoo.finance.historicaldata where symbol"
                        + " in ( \'" + symbolName + " \') and startDate= \' 2016-01-01 \' and endDate=\' 2016-03-10 \' ", "UTF-8"));

                historyUrl.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");

            } catch (UnsupportedEncodingException e) {
                Log.e(LOG_CAT, "error in httpcall");
                e.printStackTrace();
            }


            //historyUrl.append("&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=");


          //  String urlString;
            //String getResponse;

            result = GcmNetworkManager.RESULT_FAILURE;

            if (historyUrl != null) {


                urlString = historyUrl.toString();

                //String url = "https://query.yahooapis.com/v1/public/yql?q=select%20%20*%20from%20yahoo.finance.historicaldata%20where%20symbol%20in%20(%20'TSLA'%20)%20and%20startDate%20%3D%20'2016-01-04'%20and%20endDate%20%3D%20'2016%20-%2004-10'&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=callback";
                Log.e(LOG_CAT, urlString);


                try {
                    getResponse = fetchData(urlString);
                    Log.e(LOG_CAT, getResponse);


                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            result = GcmNetworkManager.RESULT_SUCCESS;
        }



        return result;
    }
}
