package com.example.sam_chordas.stockhawk;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by anant on 2016-05-13.
 */
public class WidgetService extends RemoteViewsService {
    /**
     * To be implemented by the derived service to generate appropriate factories for
     * the data.
     *
     * @param intent
     */
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        //return remote view facory here
        return new WidgetDataProvider(intent , this);
    }
}
