package com.procrastinate.receiver;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.procrastinate.R;
import com.procrastinate.activity.LongTermDetailsActivity;
import com.procrastinate.activity.OngoingActivity;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityList;
import com.procrastinate.database.entity.OneTimeActivityEntity;
import com.procrastinate.database.repository.ActivityStageRepository;
import com.procrastinate.database.repository.LongTermActivityRepository;
import com.procrastinate.database.repository.OneTimeActivityRepository;
import com.procrastinate.utils.Constants;

import java.util.UUID;


public class LongTermAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent data) {
        new Thread(() -> {
            Intent intent = new Intent(context, LongTermDetailsActivity.class);
            intent.putExtra(Constants.TITLE, data.getStringExtra(Constants.TITLE));
            intent.putExtra(Constants.ACTIVITY_ID, data.getLongExtra(Constants.ACTIVITY_ID,0));
            ActivityStageEntity entity = ActivityStageRepository.getInstance(context)
                    .loadActivityStageListForId(data.getLongExtra(Constants.STAGE_ID, 0)).get(0);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, 0);
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel chan1 = new NotificationChannel("LongTermAlarm", "Long-Term Alarm", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(chan1);
            }
            Notification notification = new NotificationCompat.Builder(context, "LongTermAlarm")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(context.getString(R.string.notification_title_long_term))
                    .setContentText(String.format(context.getString(R.string.notification_content_long_term)
                            , data.getStringExtra(Constants.TITLE), entity.getIndex(),entity.getStageName()))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .build();
            manager.notify((int) data.getLongExtra(Constants.STAGE_ID, 0), notification);
        }).start();
    }
}