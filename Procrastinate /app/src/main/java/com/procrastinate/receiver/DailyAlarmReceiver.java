package com.procrastinate.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.procrastinate.R;
import com.procrastinate.activity.LongTermDetailsActivity;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityEntity;
import com.procrastinate.database.entity.LongTermActivityList;
import com.procrastinate.database.repository.LongTermActivityRepository;
import com.procrastinate.utils.Constants;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;


public class DailyAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent data) {
        // 设置明天的提醒
        addTomorrowAlarm(context,data);
        // 查询数据库，开启通知
        new Thread(() -> {
            List<LongTermActivityList> activityList = LongTermActivityRepository
                    .getInstance(context).loadUserLongTermActivityListForUserName(data.getStringExtra(Constants.USER_NAME));
            for (LongTermActivityList activity : activityList) {
                for (ActivityStageEntity stageEntity : activity.getActivityStageList()) {
                    if (stageEntity.getType() == 0) {   // 如果是正在进行，则通知，并且切换到下一条activity
                        addNotification(context, activity.getActivity(), stageEntity);
                        break;
                    }
                }
            }
        }).start();
    }


    /**
     * 添加明天的提醒
     */
    private void addTomorrowAlarm(Context context, Intent intent) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,1); // 时间加一天
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Constants.RECEIVER_DAILY_ALARM_REQUEST_CODE, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    /**
     * 添加通知
     */
    private void addNotification(Context context, LongTermActivityEntity activityEntity, ActivityStageEntity stageEntity) {
        // 设置打开页面的数据
        Intent intent = new Intent(context, LongTermDetailsActivity.class);
        Log.e("TAGGGG", "传输前 ：  addNotification: " +  activityEntity.getActivityId() );
        intent.putExtra(Constants.TITLE, activityEntity.getActivityName());
        intent.putExtra(Constants.ACTIVITY_ID, activityEntity.getActivityId());
        PendingIntent pendingIntent = PendingIntent.getActivity(context, UUID.randomUUID().hashCode(), intent, 0);
        // 通知配置
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel chan1 = new NotificationChannel("DailyAlarm", "Daily Alarm", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(chan1);
        }
        Notification notification = new NotificationCompat.Builder(context, "DailyAlarm")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(context.getString(R.string.notification_title_long_term))
                .setContentText(String.format(context.getString(R.string.notification_content_long_term)
                        , activityEntity.getActivityName(), stageEntity.getIndex(), stageEntity.getStageName()))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .build();
        // 开启通知
        manager.notify((int) stageEntity.getStageId(), notification);
    }

}