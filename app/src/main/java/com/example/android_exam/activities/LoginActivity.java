package com.example.android_exam.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_exam.R;
import com.example.android_exam.data.local.database.AppDatabase;
import com.example.android_exam.data.local.entity.User;
import com.example.android_exam.data.remote.LocalDataRepository;
import com.example.android_exam.data.remote.DataRepository;
import com.example.android_exam.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnSwitchToRegister;
    private TextView tvTitle, tvSwitchText;
    private ProgressBar progressBar;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra trạng thái đăng nhập bất đồng bộ
        SessionManager.checkLoginStatus(this, new SessionManager.LoginCheckCallback() {
            @Override
            public void onResult(boolean success, User user, String errorMessage) {
                if (success) {
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    runOnUiThread(() -> {
                        setContentView(R.layout.activity_login);
                        LocalDataRepository.getInstance(LoginActivity.this);
                        initViews();
                        setupClickListeners();
                    });
                }
            }
        });
    }


    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnSwitchToRegister = findViewById(R.id.btn_switch_mode);
        tvTitle = findViewById(R.id.tv_title);
        tvSwitchText = findViewById(R.id.tv_switch_text);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> {
            if (isLoginMode) {
                performLogin();
            } else {
                performRegister();
            }
        });

        btnSwitchToRegister.setOnClickListener(v -> switchMode());
    }

    private void performLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(username, password)) {
            return;
        }

        showLoading(true);

        LocalDataRepository.getInstance().login(username, password, new DataRepository.AuthCallback<User>() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    showLoading(false);
                    if (user != null) {
                        // Save session
                        SessionManager.saveUserSession(LoginActivity.this,user.id, user.username, user.getHashedPassword());

                        // Navigate to main activity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();

                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, "Lỗi đăng nhập: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void performRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!validateInput(username, password)) {
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            return;
        }

        showLoading(true);

        LocalDataRepository.getInstance().register(username, password, new DataRepository.AuthCallback<User>() {
            @Override
            public void onSuccess(User user) {
                runOnUiThread(() -> {
                    showLoading(false);

                    // Save session
                    SessionManager.saveUserSession(LoginActivity.this, user.id, user.username, user.getHashedPassword());

                    // Navigate to main activity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                    Toast.makeText(LoginActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, "Lỗi đăng ký: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean validateInput(String username, String password) {
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return false;
        }

        if (username.length() < 3) {
            etUsername.setError("Tên đăng nhập phải có ít nhất 3 ký tự");
            etUsername.requestFocus();
            return false;
        }

        return true;
    }

    private void switchMode() {
        isLoginMode = !isLoginMode;

        if (isLoginMode) {
            // Switch to Login mode
            tvTitle.setText("Đăng Nhập");
            btnLogin.setText("ĐĂNG NHẬP");
            tvSwitchText.setText("Chưa có tài khoản?");
            btnSwitchToRegister.setText("Đăng ký ngay");
        } else {
            // Switch to Register mode
            tvTitle.setText("Đăng Ký");
            btnLogin.setText("ĐĂNG KÝ");
            tvSwitchText.setText("Đã có tài khoản?");
            btnSwitchToRegister.setText("Đăng nhập");
        }

        // Clear input fields
        etUsername.setText("");
        etPassword.setText("");
        etUsername.setError(null);
        etPassword.setError(null);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnSwitchToRegister.setEnabled(!show);
        etUsername.setEnabled(!show);
        etPassword.setEnabled(!show);
    }
}