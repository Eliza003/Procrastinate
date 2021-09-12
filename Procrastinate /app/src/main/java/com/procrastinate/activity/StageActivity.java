package com.procrastinate.activity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.procrastinate.R;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityList;
import com.procrastinate.database.repository.ActivityStageRepository;
import com.procrastinate.database.repository.LongTermActivityRepository;
import com.procrastinate.utils.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StageActivity extends BaseActivity {

    private String userName;
    private String title; // 修改step时，更新alarm所需
    private int currentStep;
    private LongTermActivityList list;
    private ActivityStageEntity mStageEntity;
    private boolean isUpdate = false;

    EditText mNameEditText;
    EditText mStartTimeTextView;
    EditText mFinishTimeTextView;
    Button mNextStepButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage);
        // init title
        initIntentData();
        setTitle(String.format(getString(R.string.title_activity_stage), currentStep));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // init view
        mNameEditText = findViewById(R.id.stage_name);
        mStartTimeTextView = findViewById(R.id.start_time);
        mFinishTimeTextView = findViewById(R.id.finish_time);
        mNextStepButton = findViewById(R.id.action_next_step);
        // recovery data
        if (isUpdate) {
            mNextStepButton.setText(getText(R.string.action_done));
            mNameEditText.setText(mStageEntity.getStageName());
            mStartTimeTextView.setText(mStageEntity.getStartTime());
            mFinishTimeTextView.setText(mStageEntity.getEndTime());
        } else if (list.getActivityStageList().size() == currentStep) {
            mNextStepButton.setText(getText(R.string.action_done));
        }
        // set click
        mStartTimeTextView.setOnClickListener(v ->
                showDatePickerDialog(Calendar.getInstance(), mStartTimeTextView, getString(R.string.prompt_start_time)));
        mFinishTimeTextView.setOnClickListener(v ->
                showDatePickerDialog(Calendar.getInstance(), mFinishTimeTextView, getString(R.string.prompt_finish_time)));
        mNameEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (TextUtils.isEmpty(mStartTimeTextView.getText().toString().trim())) {
                    showDatePickerDialog(Calendar.getInstance(), mStartTimeTextView, getString(R.string.prompt_start_time));
                } else if (TextUtils.isEmpty(mFinishTimeTextView.getText().toString().trim())) {
                    showDatePickerDialog(Calendar.getInstance(), mFinishTimeTextView, getString(R.string.prompt_finish_time));
                }
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        });
        mNextStepButton.setOnClickListener(v -> nextStep());
    }

    /**
     * 开启日期选择框
     *
     * @param editText 要填充的EditText
     * @param title    dialog title
     */
    private void showDatePickerDialog(Calendar calendar, EditText editText, String title) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    showTimePickerDialog(calendar, editText, title);
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setTitle(title);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(Calendar calendar, EditText editText, String title) {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            String formatDate = new SimpleDateFormat(
                    getString(R.string.common_simple_date_format), Locale.getDefault()).format(calendar.getTime());
            editText.setText(formatDate);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setTitle(title);
        timePickerDialog.show();
    }

    /**
     * button click
     */
    private void nextStep() {
        // 获取数据
        String stageName = mNameEditText.getText().toString().trim();
        String startTime = mStartTimeTextView.getText().toString().trim();
        String finishTime = mFinishTimeTextView.getText().toString().trim();
        // 数据验证
        if (TextUtils.isEmpty(stageName)) {
            mNameEditText.setError(String.format(getString(R.string.error_enter_null), getString(R.string.prompt_activity_name)));
            mNameEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(startTime)) {
            Toast.makeText(this, String.format(getString(R.string.error_enter_null),
                    getString(R.string.prompt_start_time)), Toast.LENGTH_SHORT).show();
            showDatePickerDialog(Calendar.getInstance(), mStartTimeTextView, getString(R.string.prompt_start_time));
            return;
        }
        if (TextUtils.isEmpty(finishTime)) {
            Toast.makeText(this, String.format(getString(R.string.error_enter_null),
                    getString(R.string.prompt_finish_time)), Toast.LENGTH_SHORT).show();
            showDatePickerDialog(Calendar.getInstance(), mFinishTimeTextView, getString(R.string.prompt_finish_time));
            return;
        }
        mNextStepButton.setEnabled(false);
        // 如果是更新，就直接更新数据库
        if (isUpdate) {
            new Thread(() -> {  // start insert
                mStageEntity.setStageName(stageName);
                mStageEntity.setStartTime(startTime);
                mStageEntity.setEndTime(finishTime);
                boolean isSuccessful = ActivityStageRepository.getInstance(StageActivity.this).updateActivityStage(mStageEntity);
                runOnUiThread(() -> {    // Switch to the UI thread
                    if (isSuccessful) {
                        super.finish(); // 这里成功直接调用父类finish关闭页面，因为子类会弹出dialog
                        Toast.makeText(StageActivity.this, getText(R.string.common_succeeded), Toast.LENGTH_SHORT).show();
                        setNotificationAlarm(mStageEntity.getActivityId(),mStageEntity.getStageId(), title, mStageEntity);
                    } else { //
                        Toast.makeText(StageActivity.this, getText(R.string.common_error), Toast.LENGTH_SHORT).show();
                    }
                    mNextStepButton.setEnabled(true);
                });
            }).start();
            return;
        }
        // 如果是最后一个，就插入数据库，否则跳转
        String userName = getIntent().getStringExtra(Constants.USER_NAME);
        // 初始化跳转参数
        ActivityStageEntity stageEntity = list.getActivityStageList().get(currentStep - 1);
        stageEntity.setStageName(stageName);
        stageEntity.setStartTime(startTime);
        stageEntity.setEndTime(finishTime);
        stageEntity.setIndex(currentStep);
        if (list.getActivityStageList().size() == currentStep) { // 插入数据库
            new Thread(() -> {  // start insert
                LongTermActivityRepository.getInstance(StageActivity.this)
                        .insertLongTermActivityList(list, new LongTermActivityRepository.OnCallback() {
                            @Override
                            public void succeeded() {
                                EventBus.getDefault().post(Constants.LONG_TERM_ACTIVITY_LIST_INSERT_SUCCEEDED);
                                runOnUiThread(() -> Toast.makeText(
                                        StageActivity.this, getText(R.string.common_succeeded), Toast.LENGTH_SHORT).show());
                                for (ActivityStageEntity entity : list.getActivityStageList()) {
                                    setNotificationAlarm(entity.getActivityId(), entity.getStageId(), list.getActivity().getActivityName(), entity);
                                }
                            }

                            @Override
                            public void failure(String error) {
                                runOnUiThread(() -> {
                                    Toast.makeText(StageActivity.this, getText(R.string.common_error), Toast.LENGTH_SHORT).show();
                                    mNextStepButton.setEnabled(true);
                                });
                            }
                        });
            }).start();
        } else {    // 跳转
            StageActivity.startAction(this, userName, currentStep + 1, list);
            mNextStepButton.setEnabled(true);
        }
    }

    /**
     * 设置定时通知
     */
    private void setNotificationAlarm(Long activityId, Long stageId, String title, ActivityStageEntity stageEntity) {
        try {
            Date date = new SimpleDateFormat(getString(R.string.common_simple_date_format),
                    Locale.getDefault()).parse(stageEntity.getStartTime());
            Intent intent = new Intent();
            intent.setPackage(getPackageName());
            intent.putExtra(Constants.ACTIVITY_ID, activityId);
            intent.putExtra(Constants.STAGE_ID, stageId);
            intent.putExtra(Constants.TITLE, title);
            intent.setAction(String.valueOf(stageId));
            intent.setAction(getString(R.string.receiver_long_term_alarm));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(String.valueOf(stageId)), intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库插入成功收到通知
     *
     * @param code 通知过滤代码 LONG_TERM_ACTIVITY_LIST_INSERT_SUCCEEDED
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDatabaseInsertSucceeded(Integer code) {
        if (code == Constants.LONG_TERM_ACTIVITY_LIST_INSERT_SUCCEEDED) {
            super.finish(); // 只需要关闭此页面就行了
        }
    }

    /**
     * 获取intent数据
     */
    public void initIntentData() {
        userName = getIntent().getStringExtra(Constants.USER_NAME);
        currentStep = getIntent().getIntExtra(Constants.CURRENT_STEP, 0);
        isUpdate = getIntent().getBooleanExtra(Constants.IS_UPDATE, false);
        if (isUpdate) {
            mStageEntity = (ActivityStageEntity) getIntent().getSerializableExtra(Constants.ENTITY);
            title = getIntent().getStringExtra(Constants.TITLE);
        } else {
            list = (LongTermActivityList) getIntent().getSerializableExtra(Constants.LONG_TERM_ACTIVITY_LIST);
        }
    }

    /**
     * 重写此方法，做到退出提示
     */
    @Override
    public void finish() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Whether to go back to the previous step ?")
                .setPositiveButton("Ok", (dialog, which) -> super.finish())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * 通过此方法打开，添加数据
     */
    public static void startAction(Context context, String userName, int currentStep, LongTermActivityList list) {
        Intent intent = new Intent(context, StageActivity.class);
        intent.putExtra(Constants.USER_NAME, userName);
        intent.putExtra(Constants.CURRENT_STEP, currentStep);
        intent.putExtra(Constants.LONG_TERM_ACTIVITY_LIST, list);
        context.startActivity(intent);
    }

    /**
     * 直接通过ActivityStageEntity打开，修改数据
     */
    public static void startAction(Context context, ActivityStageEntity entity, String title) {
        Intent intent = new Intent(context, StageActivity.class);
        intent.putExtra(Constants.ENTITY, entity);
        intent.putExtra(Constants.IS_UPDATE, true);
        intent.putExtra(Constants.TITLE, title);
        context.startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

}