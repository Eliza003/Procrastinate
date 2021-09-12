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
import com.procrastinate.activity.OngoingActivity;
import com.procrastinate.database.entity.OneTimeActivityEntity;
import com.procrastinate.database.repository.OneTimeActivityRepository;
import com.procrastinate.utils.Constants;

import java.util.UUID;


public class OneTimeAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent data) {
        new Thread(() -> {
            Intent intent = new Intent(context, OngoingActivity.class);
            intent.putExtra(Constants.USER_NAME, data.getStringExtra(Constants.USER_NAME));
            OneTimeActivityEntity entity = OneTimeActivityRepository.getInstance(context)
                    .loadOneTimeActivityForId(data.getLongExtra(Constants.ACTIVITY_ID, 0)).get(0);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, 0);
            NotificationManagerCompat manager = NotificationManagerCompat.from(context);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel chan1 = new NotificationChannel("OneTimeAlarm", "One-Time Alarm", NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(chan1);
            }
            Notification notification = new NotificationCompat.Builder(context, "OneTimeAlarm")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentTitle(context.getString(R.string.notification_title_one_time))
                    .setContentText(String.format(context.getString(R.string.notification_content_one_time)
                            , entity.getLocation(), entity.getActivityName()))
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .build();
            manager.notify((int) data.getLongExtra(Constants.ACTIVITY_ID, 0), notification);
        }).start();
    }
}