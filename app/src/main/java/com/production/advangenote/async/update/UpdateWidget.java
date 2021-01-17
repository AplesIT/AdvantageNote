package com.production.advangenote.async.update;

/*@name UpdateWidget
@author vietnh
@date 1/15/21*/

import android.content.Context;
import android.os.AsyncTask;

import com.production.advangenote.BaseActivity;
import com.production.advangenote.async.bus.NotesUpdatedEvent;

import de.greenrobot.event.EventBus;

public class UpdateWidget extends AsyncTask<Void, Void, Void> {
    private Context context;

    public UpdateWidget(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        WidgetUpdateSubscriber widgetUpdateSubscriber = new WidgetUpdateSubscriber();
        return null;
    }
    class WidgetUpdateSubscriber {

        WidgetUpdateSubscriber() {
            EventBus.getDefault().register(this);
        }

        public void onEvent(NotesUpdatedEvent event) {
            BaseActivity.notifyAppWidgets(context);
            EventBus.getDefault().unregister(this);
        }
    }
}
