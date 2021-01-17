package com.production.advangenote.helpers.notifications;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.os.Build;

import com.production.advangenote.AdvantageNotes;
import com.production.advangenote.R;

import java.util.EnumMap;
import java.util.Map;

import static com.production.advangenote.utils.Constants.CHANNEL_BACKUPS_ID;
import static com.production.advangenote.utils.Constants.CHANNEL_PINNED_ID;
import static com.production.advangenote.utils.Constants.CHANNEL_REMINDERS_ID;

/**
 * @author vietnh
 * @name NotificationChannels
 * @date 10/1/20
 **/
@TargetApi(Build.VERSION_CODES.O)
public class NotificationChannels {


    protected static final Map<NotificationChannelNames, NotificationChannel> channels;

    static {
        channels = new EnumMap<>(NotificationChannelNames.class);
        channels.put(NotificationChannelNames.BACKUPS, new NotificationChannel(
                NotificationManager.IMPORTANCE_DEFAULT,
                AdvantageNotes.getAppContext().getString(R.string.channel_backups_name),
                AdvantageNotes.getAppContext().getString(R.string.channel_backups_description),
                CHANNEL_BACKUPS_ID));
        channels.put(NotificationChannelNames.REMINDERS, new NotificationChannel(
                NotificationManager.IMPORTANCE_DEFAULT,
                AdvantageNotes.getAppContext().getString(R.string.channel_reminders_name),
                AdvantageNotes.getAppContext().getString(R.string.channel_reminders_description),
                CHANNEL_REMINDERS_ID));
        channels.put(NotificationChannelNames.PINNED, new NotificationChannel(
                NotificationManager.IMPORTANCE_DEFAULT,
                AdvantageNotes.getAppContext().getString(R.string.channel_pinned_name),
                AdvantageNotes.getAppContext().getString(R.string.channel_pinned_description),
                CHANNEL_PINNED_ID));
    }

    public enum NotificationChannelNames {
        BACKUPS, REMINDERS, PINNED
    }

}