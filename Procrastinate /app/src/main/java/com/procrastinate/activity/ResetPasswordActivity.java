package com.procrastinate.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.procrastinate.R;
import com.procrastinate.database.repository.UserRepository;


public class ResetPasswordActivity extends BaseActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mResetButton;
    private ProgressBar mResetProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        setTitle(getString(R.string.title_activity_reset_password));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsernameEditText = findViewById(R.id.username);
        mPasswordEditText = findViewById(R.id.password);
        mConfirmPasswordEditText = findViewById(R.id.confirm_password);
        mResetButton = findViewById(R.id.reset);
        mResetProgressBar = findViewById(R.id.loading);

        mConfirmPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                confirm();
                return true;
            }
            return false;
        });
        mResetButton.setOnClickListener(v -> confirm());
    }

    private void confirm() {
        String userName = mUsernameEditText.getText().toString().trim().toLowerCase();
        if (TextUtils.isEmpty(userName)) {
            mUsernameEditText.setError(getString(R.string.error_user_name));
            mUsernameEditText.requestFocus();
            return;
        }
        String password = mPasswordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            mPasswordEditText.setError(getString(R.string.error_password));
            mPasswordEditText.requestFocus();
            return;
        }
        String secondaryPassword = mConfirmPasswordEditText.getText().toString().trim();
        if (!password.equals(secondaryPassword)) {
            mConfirmPasswordEditText.setError(getString(R.string.error_passwords_differ));
            mConfirmPasswordEditText.requestFocus();
            return;
        }
        mResetProgressBar.setVisibility(View.VISIBLE);
        mResetButton.setEnabled(false);
        new Thread(() -> {  // start verify
            boolean isSuccessful = UserRepository.getInstance(ResetPasswordActivity.this).resetPassword(userName, password);
            runOnUiThread(() -> {    // Switch to the UI thread
                if (isSuccessful) {
                    Toast.makeText(ResetPasswordActivity.this, getText(R.string.reset_successfully), Toast.LENGTH_SHORT).show();
                    finish();
                }else { //  Account does not exist
                    Toast.makeText(ResetPasswordActivity.this, getText(R.string.reset_failed), Toast.LENGTH_SHORT).show();
                    mUsernameEditText.requestFocus();
                }
                mResetProgressBar.setVisibility(View.GONE);
                mResetButton.setEnabled(true);
            });
        }).start();
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void startAction(Context context) {
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        context.startActivity(intent);
    }

}