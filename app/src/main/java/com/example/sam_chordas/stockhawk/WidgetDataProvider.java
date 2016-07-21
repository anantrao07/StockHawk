package com.example.sam_chordas.stockhawk;

import android.annotation.TargetApi;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anant on 2016-05-13.
 */
public class WidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {


    List<String> demo = new ArrayList<String>();
    Intent intent ;
    Context mcontext;
    Cursor mCursor;


    public WidgetDataProvider(Intent intent, Context context) {
        this.intent = intent;
        this.mcontext = context;
    }

    public void init(){




    }
    /**
     * Called when your factory is first constructed. The same factory may be shared across
     * multiple RemoteViewAdapters depending on the intent passed.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onCreate() {
        mCursor = mcontext.getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI,//table
                new String[] {"_id" , "symbol","percent_change"},//columns
                null,//selection where clause
                null,//
                null,
                null
        );
       // init();
    }

    /**
     * Called when notifyDataSetChanged() is triggered on the remote adapter. This allows a
     * RemoteViewsFactory to respond to data changes by updating any internal references.
     * <p/>
     * Note: expensive tasks can be safely performed synchronously within this method. In the
     * interim, the old data will be displayed within the widget.
     *
     * @see AppWidgetManager#notifyAppWidgetViewDataChanged(int[], int)
     */
    @Override
    public void onDataSetChanged() {

      //  init();
    }

    /**
     * Called when the last RemoteViewsAdapter that is associated with this factory is
     * unbound.
     */
    @Override
    public void onDestroy() {

    }

    /**
     * See {@link //Adapter#getCount()}
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        int count = mCursor.getCount();
        Log.d("value of count",String.valueOf(count));
        return count;

    }

    /**
     * See {@link //Adapter#getView(int, View, ViewGroup)}.
     * <p/>
     * Note: expensive tasks can be safely performed synchronously within this method, and a
     * loading view will be displayed in the interim. See {@link #getLoadingView()}.
     *
     * @param position The position of the item within the Factory's data set of the item whose
     *                 view we want.
     * @return A RemoteViews object corresponding to the data at the specified position.
     */
    @Override
    public RemoteViews getViewAt(int position) {
       RemoteViews remoteViews = new RemoteViews(mcontext.getPackageName(), R.layout.list_item_view);
       mCursor.moveToPosition(position);

        int symbol_idx = mCursor.getColumnIndex(QuoteColumns.SYMBOL);

        int change_idx = mCursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE);


        remoteViews.setTextViewText(R.id.symbolview,mCursor.getString(symbol_idx));
        remoteViews.setTextViewText(R.id.changeview,mCursor.getString(change_idx));

        return remoteViews;
    }

    /**
     * This allows for the use of a custom loading view which appears between the time that
     * {@link #getViewAt(int)} is called and returns. If null is returned, a default loading
     * view will be used.
     *
     * @return The RemoteViews representing the desired loading view.
     */
    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    /**
     * See {@link //Adapter#getViewTypeCount()}.
     *
     * @return The number of types of Views that will be returned by this factory.
     */
    @Override
    public int getViewTypeCount() {
        return 0;
    }

    /**
     * See {@link //Adapter#getItemId(int)}.
     *
     * @param position The position of the item within the data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * See {@link //Adapter#hasStableIds()}.
     *
     * @return True if the same id always refers to the same object.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }
}
