package com.procrastinate.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.procrastinate.R;
import com.procrastinate.database.repository.UserRepository;


public class LoginActivity extends BaseActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private Button mLoginButton;
    private ProgressBar mLoadingProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.title_activity_login));
        mUsernameEditText = findViewById(R.id.username);
        mPasswordEditText = findViewById(R.id.password);
        mLoginButton = findViewById(R.id.login);
        mLoadingProgressBar = findViewById(R.id.loading);
        // set click listener
        mPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                login();
                return true;
            }
            return false;
        });
        mLoginButton.setOnClickListener(v -> login());
        findViewById(R.id.create_an_account)
                .setOnClickListener(v -> RegisterActivity.startAction(this));
        findViewById(R.id.forget_password)
                .setOnClickListener(v -> ResetPasswordActivity.startAction(this));
    }

    private void login() {
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
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mLoginButton.setEnabled(false);
        new Thread(() -> {  // start verify
            boolean isSuccessful = UserRepository.getInstance(LoginActivity.this).login(userName, password);
            runOnUiThread(() -> {    // Switch to the UI thread
                if (isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(LoginActivity.this, getText(R.string.login_successfully), Toast.LENGTH_SHORT).show();
                    MainActivity.startAction(LoginActivity.this, userName);
                    finish();
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(LoginActivity.this, getText(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                }
                mLoadingProgressBar.setVisibility(View.GONE);
                mLoginButton.setEnabled(true);
            });
        }).start();
    }

}