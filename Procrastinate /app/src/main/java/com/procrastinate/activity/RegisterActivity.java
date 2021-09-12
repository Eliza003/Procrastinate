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


public class RegisterActivity extends BaseActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mRegisterButton;
    private ProgressBar mRegisterProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(getString(R.string.title_activity_register));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mUsernameEditText = findViewById(R.id.username);
        mPasswordEditText = findViewById(R.id.password);
        mConfirmPasswordEditText = findViewById(R.id.confirm_password);
        mRegisterButton = findViewById(R.id.register);
        mRegisterProgressBar = findViewById(R.id.loading);

        mConfirmPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                register();
                return true;
            }
            return false;
        });
        mRegisterButton.setOnClickListener(v -> register());
    }

    private void register() {
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
        mRegisterProgressBar.setVisibility(View.VISIBLE);
        mRegisterButton.setEnabled(false);
        new Thread(() -> {  // start verify
            boolean isSuccessful = UserRepository.getInstance(RegisterActivity.this).register(userName, password);
            runOnUiThread(() -> {    // Switch to the UI thread
                if (isSuccessful) {
                    Toast.makeText(RegisterActivity.this, getText(R.string.registered_successfully), Toast.LENGTH_SHORT).show();
                    finish();
                } else { // Account already exists
                    Toast.makeText(RegisterActivity.this, getText(R.string.register_failed), Toast.LENGTH_SHORT).show();
                    mUsernameEditText.requestFocus();
                }
                mRegisterProgressBar.setVisibility(View.GONE);
                mRegisterButton.setEnabled(true);
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static void startAction(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

}