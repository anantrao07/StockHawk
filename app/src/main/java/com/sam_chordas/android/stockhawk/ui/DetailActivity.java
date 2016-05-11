package com.sam_chordas.android.stockhawk.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.HistoryModel;
import com.sam_chordas.android.stockhawk.service.HistoryIntentService;
import com.sam_chordas.android.stockhawk.service.HistoryTaskService;

import java.util.ArrayList;


public class DetailActivity extends Activity {

    LineChartView lineChart;
    private Intent mHistoryIntent;
    private Bundle bundle;
    private StringBuilder msymbolname;
    private BroadcastReceiver mDataBrdCast;
    IntentFilter mDataFilter;
    ArrayList<HistoryModel> dataList;
    public final static String LOG_TAG = DetailActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        lineChart  = (LineChartView) findViewById(R.id.linechart);
        mHistoryIntent = new Intent(DetailActivity.this, HistoryIntentService.class);
        bundle = getIntent().getExtras();
        msymbolname = new StringBuilder(bundle.getString("value of symbol"));
        mHistoryIntent.putExtra("symboltag" ,msymbolname.toString() );
        Log.e(LOG_TAG , msymbolname.toString());
        //startService(mHistoryIntent);

      //  if(savedInstanceState==null){
           // mHistoryIntent.putExtra("tag" , "start");

            startService(mHistoryIntent);

       // }
        long period = 3600L;
        long flex = 10L;
        String periodicTag = "periodic";

        PeriodicTask task = new PeriodicTask.Builder()
                .setService(HistoryTaskService.class)
                .setPeriod(period)
                .setFlex(flex)
                .setTag(periodicTag)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setRequiresCharging(false)
                .build();

        GcmNetworkManager.getInstance(this).schedule(task);

    }


    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     * This is a good place to begin animations, open exclusive-access devices
     * (such as the camera), etc.
     * <p/>
     * <p>Keep in mind that onResume is not the best indicator that your activity
     * is visible to the user; a system window such as the keyguard may be in
     * front.  Use {@link #onWindowFocusChanged} to know for certain that your
     * activity is visible to the user (for example, to resume a game).
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onRestoreInstanceState
     * @see #onRestart
     * @see #onPostResume
     * @see #onPause
     */
    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Perform any final cleanup before an activity is destroyed.  This can
     * happen either because the activity is finishing (someone called
     * {@link #finish} on it, or because the system is temporarily destroying
     * this instance of the activity to save space.  You can distinguish
     * between these two scenarios with the {@link #isFinishing} method.
     * <p/>
     * <p><em>Note: do not count on this method being called as a place for
     * saving data! For example, if an activity is editing data in a content
     * provider, those edits should be committed in either {@link #onPause} or
     * {@link #onSaveInstanceState}, not here.</em> This method is usually implemented to
     * free resources like threads that are associated with an activity, so
     * that a destroyed activity does not leave such things around while the
     * rest of its application is still running.  There are situations where
     * the system will simply kill the activity's hosting process without
     * calling this method (or any others) in it, so it should not be used to
     * do things that are intended to remain around after the process goes
     * away.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onPause
     * @see #onStop
     * @see #finish
     * @see #isFinishing
     */

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(mDataBrdCast);

    }

    @Override
    protected void onStart() {
        super.onStart();


        mDataBrdCast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                 dataList = intent.getParcelableArrayListExtra("historydata");



            }
        };
        this.registerReceiver(mDataBrdCast,mDataFilter);

        //String symbolName  = bundle.getString("value of symbol");

        String[] dates = new String[dataList.size()];
        float[] valueshigh = new float[dataList.size()];
        float[] valueslow = new float[dataList.size()];
        for(int i = 0 ; i<=dataList.size() ; i++){
            dates[i] = dataList.get(i).getDate();
            valueshigh[i] = (float)dataList.get(i).getHigh();
            valueslow[i] = (float)dataList.get(i).getLow();

        }
        //   String[]  dates = {"Jan" , "Feb" , "March" , "April"};



        mDataFilter = new IntentFilter("android.intent.action.MAIN");

        LineSet lineData = new LineSet(dates,valueshigh);
        lineData.setColor(Color.parseColor("#ffffff")).setSmooth(true).setThickness(4).beginAt(0);

        lineChart.addData(lineData);
        lineChart.show();

        Toast.makeText(getApplicationContext(),msymbolname,Toast.LENGTH_SHORT).show();


    }
}
