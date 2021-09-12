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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.procrastinate.R;
import com.procrastinate.database.entity.OneTimeActivityEntity;
import com.procrastinate.database.repository.OneTimeActivityRepository;
import com.procrastinate.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewOneTimeActivity extends BaseActivity {

    EditText mNameEditText;
    EditText mStartTimeTextView;
    EditText mLocationEditText;
    EditText mDescriptionEditText;
    Button mDoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_one_time);
        // init title
        setTitle(R.string.title_activity_new_one_time_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // init view
        mNameEditText = findViewById(R.id.activity_name);
        mStartTimeTextView = findViewById(R.id.start_time);
        mLocationEditText = findViewById(R.id.location);
        mDescriptionEditText = findViewById(R.id.description);
        mDoneButton = findViewById(R.id.action_done);
        // recovery data
        if (getIntent().getBooleanExtra(Constants.IS_UPDATE, false)) {
            OneTimeActivityEntity entity = (OneTimeActivityEntity) getIntent().getSerializableExtra(Constants.ENTITY);
            mNameEditText.setText(entity.getActivityName());
            mStartTimeTextView.setText(entity.getStartTime());
            mLocationEditText.setText(entity.getLocation());
            mDescriptionEditText.setText(entity.getDescription());
        }
        // set click
        mStartTimeTextView.setOnClickListener(v ->
                showDatePickerDialog(Calendar.getInstance()));
        mNameEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                String startTime = mStartTimeTextView.getText().toString().trim();
                if (TextUtils.isEmpty(startTime)) {
                    showDatePickerDialog(Calendar.getInstance());
                }
            }
            return false;
        });
        mDescriptionEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                done();
                return true;
            }
            return false;
        });
        mDoneButton.setOnClickListener(v -> done());
    }

    private void showDatePickerDialog(Calendar calendar) {
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    showTimePickerDialog(calendar);
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void showTimePickerDialog(Calendar calendar) {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            String formatDate = new SimpleDateFormat(
                    getString(R.string.common_simple_date_format), Locale.getDefault()).format(calendar.getTime());
            mStartTimeTextView.setText(formatDate);
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }

    private void done() {
        // 获取数据
        String activityName = mNameEditText.getText().toString().trim();
        String startTime = mStartTimeTextView.getText().toString().trim();
        String location = mLocationEditText.getText().toString().trim();
        String description = mDescriptionEditText.getText().toString().trim();
        // 数据验证
        if (TextUtils.isEmpty(activityName)) {
            mNameEditText.setError(String.format(getString(R.string.error_enter_null), getString(R.string.prompt_activity_name)));
            mNameEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(startTime)) {
            Toast.makeText(this, String.format(getString(R.string.error_enter_null),
                    getString(R.string.prompt_start_time)), Toast.LENGTH_SHORT).show();
            showDatePickerDialog(Calendar.getInstance());
            return;
        }
        if (TextUtils.isEmpty(location)) {
            mLocationEditText.setError(String.format(getString(R.string.error_enter_null), getString(R.string.prompt_location)));
            mLocationEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            mDescriptionEditText.setError(String.format(getString(R.string.error_enter_null), getString(R.string.prompt_description)));
            mDescriptionEditText.requestFocus();
            return;
        }
        mDoneButton.setEnabled(false);
        String userName = getIntent().getStringExtra(Constants.USER_NAME);
        // 数据库操作
        new Thread(() -> {  // start insert
            boolean isSuccessful;
            if (getIntent().getBooleanExtra(Constants.IS_UPDATE, false)) {
                OneTimeActivityEntity entity = (OneTimeActivityEntity) getIntent().getSerializableExtra(Constants.ENTITY);
                entity.setActivityName(activityName);
                entity.setStartTime(startTime);
                entity.setLocation(location);
                entity.setDescription(description);
                if (isSuccessful = OneTimeActivityRepository.getInstance(NewOneTimeActivity.this).updateActivity(entity)) {
                    setNotificationAlarm(entity.getActivityId(),userName, startTime);
                }
            } else {
                OneTimeActivityEntity entity = new OneTimeActivityEntity(activityName, startTime, location, description, userName);
                long id = OneTimeActivityRepository.getInstance(NewOneTimeActivity.this).insertActivity(entity);
                if (isSuccessful = id != -1) {
                    setNotificationAlarm(id,userName, startTime);
                }
            }
            boolean f_isSuccessful = isSuccessful;
            runOnUiThread(() -> {    // Switch to the UI thread
                if (f_isSuccessful) {
                    super.finish(); // 这里成功直接调用父类finish关闭页面，因为子类会弹出dialog
                    Toast.makeText(NewOneTimeActivity.this, getText(R.string.common_succeeded), Toast.LENGTH_SHORT).show();
                } else { //
                    Toast.makeText(NewOneTimeActivity.this, getText(R.string.common_error), Toast.LENGTH_SHORT).show();
                }
                mDoneButton.setEnabled(true);
            });
        }).start();
    }

    /**
     * 设置定时通知
     */
    private void setNotificationAlarm(Long activityId,String userName, String startTime) {
        try {
            Date date = new SimpleDateFormat(getString
                    (R.string.common_simple_date_format), Locale.getDefault()).parse(startTime);
            Intent intent = new Intent();
            intent.setPackage(getPackageName());
            intent.putExtra(Constants.ACTIVITY_ID, activityId);
            intent.putExtra(Constants.USER_NAME, userName);
            intent.setAction(String.valueOf(activityId));
            intent.setAction(getString(R.string.receiver_one_time_alarm));
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this,  Integer.parseInt(String.valueOf(activityId)), intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            alarmManager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 重写此方法，做到退出提示
     */
    @Override
    public void finish() {
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Are you sure not to save exit ?")
                .setPositiveButton("Ok", (dialog, which) -> super.finish())
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * 新建activity ： 通过userName打开
     */
    public static void startAction(Context context, String userName) {
        Intent intent = new Intent(context, NewOneTimeActivity.class);
        intent.putExtra(Constants.USER_NAME, userName);
        context.startActivity(intent);
    }

    /**
     * 修改activity，直接通过entity打开
     */
    public static void startAction(Context context, OneTimeActivityEntity entity) {
        Intent intent = new Intent(context, NewOneTimeActivity.class);
        intent.putExtra(Constants.USER_NAME, entity.getUserName());
        intent.putExtra(Constants.ENTITY, entity);
        intent.putExtra(Constants.IS_UPDATE, true);
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