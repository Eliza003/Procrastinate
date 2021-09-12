package com.procrastinate.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.procrastinate.R;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityList;
import com.procrastinate.database.entity.OneTimeActivityEntity;
import com.procrastinate.database.repository.LongTermActivityRepository;
import com.procrastinate.database.repository.OneTimeActivityRepository;
import com.procrastinate.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseActivity {

    TextView mActivitiesPrompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // set title
        String userName = getIntent().getStringExtra(Constants.USER_NAME);
        setTitle(String.format(getString(R.string.title_activity_main), userName));
        // init view
        mActivitiesPrompt = findViewById(R.id.activities_prompt);
        mActivitiesPrompt.setText(String.format(getString(R.string.prompt_activities_main), "--"));
        // init click
        findViewById(R.id.action_setting_new_activity).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.dialog_selective_type))
                    .setItems(R.array.dialog_selective_type_array, (dialog, which) -> {
                        if (which == 0) { // One-time
                            NewOneTimeActivity.startAction(this, userName);
                        } else if (which == 1) {  // Long-term
                            NewLongTermActivity.startAction(this, userName);
                        }
                    })
                    .show();
        });
        findViewById(R.id.action_ongoing_activities).setOnClickListener(v -> {
            OngoingActivity.startAction(this, userName);
        });
        findViewById(R.id.action_finished_activities).setOnClickListener(v -> {
            FinishedActivity.startAction(this, userName);
        });
        // init data
        OneTimeActivityRepository.getInstance(this)
                .loadUserOneTimeActivityListForType(userName, 0)
                .observe(this, oneTimeActivityEntities -> {
                    mOneTimeActivityEntity = oneTimeActivityEntities;
                    initActivitiesPrompt();
                });
        LongTermActivityRepository.getInstance(this)
                .loadUserLongTermActivityList(userName)
                .observe(this, longTermActivityLists -> {
                    mLongTermActivityEntity = longTermActivityLists;
                    initActivitiesPrompt();
                });
        // 开启每天9点定时提醒
        setNotificationAlarm(userName);
    }

    private List<OneTimeActivityEntity> mOneTimeActivityEntity;
    private List<LongTermActivityList> mLongTermActivityEntity;

    /**
     * 初始化主页提示
     */
    private void initActivitiesPrompt() {
        if (mOneTimeActivityEntity == null
                || mLongTermActivityEntity == null) {
            return; // 数据不全
        }
        int ongoingCount = mOneTimeActivityEntity.size();
        for (LongTermActivityList activityList : mLongTermActivityEntity) {
            for (ActivityStageEntity stageEntity : activityList.getActivityStageList()) {
                if (stageEntity.getType() == 0) {
                    // 只要有正在进行的，就可以添加
                    ongoingCount++;
                    break;
                }
            }
        }
        mActivitiesPrompt.setText(String.format(getString(R.string.prompt_activities_main), String.valueOf(ongoingCount)));
    }

    /**
     * 设置定时通知 ： 每日
     */
    private void setNotificationAlarm(String userName) {
            // 设置9点整的时间
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 9);
            calendar.set(Calendar.MINUTE, 0);
            // 和当前时间比较 , 看看需不需要加一天
            if (calendar.before(Calendar.getInstance())){
                calendar.add(Calendar.DAY_OF_MONTH,1);
            }
//            Log.e("TAG", "setNotificationAlarm: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime()));
            Intent intent = new Intent();
            intent.setPackage(getPackageName());
            intent.putExtra(Constants.USER_NAME, userName);
            intent.setAction(getString(R.string.receiver_daily_alarm));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Constants.RECEIVER_DAILY_ALARM_REQUEST_CODE, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);     // 每次都要取消重新开启，防止切换帐号
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

    public static void startAction(Context context, String userName) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(Constants.USER_NAME, userName);
        context.startActivity(intent);
    }


}