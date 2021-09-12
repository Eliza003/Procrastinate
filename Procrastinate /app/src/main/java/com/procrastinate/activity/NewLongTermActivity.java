package com.procrastinate.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.procrastinate.R;
import com.procrastinate.database.entity.ActivityStageEntity;
import com.procrastinate.database.entity.LongTermActivityEntity;
import com.procrastinate.database.entity.LongTermActivityList;
import com.procrastinate.utils.Constants;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class NewLongTermActivity extends BaseActivity {

    EditText mNameEditText;
    EditText mStageEditText;
    EditText mStartTimeTextView;
    Button mNextStepButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_long_term);
        // init title
        setTitle(R.string.title_activity_new_long_term_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // init view
        mNameEditText = findViewById(R.id.activity_name);
        mStageEditText = findViewById(R.id.activity_stage);
        mStartTimeTextView = findViewById(R.id.start_time);
        mNextStepButton = findViewById(R.id.action_next_step);
        // set click
        mStartTimeTextView.setOnClickListener(v ->
                showDatePickerDialog(Calendar.getInstance()));
        mStageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            int maxStep = 10;

            @Override
            public void onTextChanged(CharSequence text, int start, int before, int count) {
                try {
                    int parseInt = Integer.parseInt(text.toString());
                    if (parseInt > 10) {
                        mStageEditText.setText(String.valueOf(maxStep));
                        mStageEditText.setSelection(2);
                        Toast.makeText(NewLongTermActivity.this,
                                "Allow a maximum of 10 steps",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mStageEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                String startTime = mStartTimeTextView.getText().toString().trim();
                if (TextUtils.isEmpty(startTime)) {
                    showDatePickerDialog(Calendar.getInstance());
                }
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        });
        mNextStepButton.setOnClickListener(v -> nextStep());
    }

    private void showDatePickerDialog(Calendar calendar) {
        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    Calendar instance = Calendar.getInstance();
                    instance.set(year, month, dayOfMonth);
                    String formatDate = DateFormat.getDateInstance
                            (DateFormat.MEDIUM, Locale.CANADA).format(instance.getTime());
                    mStartTimeTextView.setText(formatDate);
                }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    private void nextStep() {
        // 获取数据
        String activityName = mNameEditText.getText().toString().trim();
        String stage = mStageEditText.getText().toString().trim();
        String startTime = mStartTimeTextView.getText().toString().trim();
        // 数据验证
        if (TextUtils.isEmpty(activityName)) {
            mNameEditText.setError(String.format(getString(R.string.error_enter_null), getString(R.string.prompt_activity_name)));
            mNameEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(stage)) {
            mStageEditText.setError(String.format(getString(R.string.error_enter_null), getString(R.string.prompt_activity_stage_edit)));
            mStageEditText.requestFocus();
            return;
        }
        int step = Integer.parseInt(stage);
        if (step < 1){
            mStageEditText.setError(getString(R.string.error_stage));
            mStageEditText.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(startTime)) {
            Toast.makeText(this, String.format(getString(R.string.error_enter_null),
                    getString(R.string.prompt_start_time)), Toast.LENGTH_SHORT).show();
            showDatePickerDialog(Calendar.getInstance());
            return;
        }
        mNextStepButton.setEnabled(false);
        String userName = getIntent().getStringExtra(Constants.USER_NAME);
        // 初始化跳转参数
        LongTermActivityList activityList = new LongTermActivityList();
        activityList.setActivity(new LongTermActivityEntity(activityName,startTime,userName));
        List<ActivityStageEntity> stageList = new ArrayList<>();
        for (int i = 0; i < step; i++)
            stageList.add(new ActivityStageEntity());
        activityList.setActivityStageList(stageList);
        // 跳转
        StageActivity.startAction(this,userName,1,activityList);
        mNextStepButton.setEnabled(true);
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

    public static void startAction(Context context, String userName) {
        Intent intent = new Intent(context, NewLongTermActivity.class);
        intent.putExtra(Constants.USER_NAME, userName);
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