package com.example.android_exam.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android_exam.R;
import com.example.android_exam.data.api.ApiManager;
import com.example.android_exam.data.api.AuthCallback;
import com.example.android_exam.data.dto.AuthResponseDto;
import com.example.android_exam.data.dto.response.ApiResponse;
import com.example.android_exam.data.dto.user.LoginDto;
import com.example.android_exam.data.dto.user.RegisterDto;
import com.example.android_exam.data.models.base.User;
import com.example.android_exam.utils.SessionManager;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail;
    private Button btnLogin, btnSwitchToRegister;
    private TextView tvTitle, tvSwitchText;
    private ProgressBar progressBar;
    private TextInputLayout tilEmail;

    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ApiManager.getInstance().setAuthToken(null);
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etEmail = findViewById(R.id.et_email);
        btnLogin = findViewById(R.id.btn_login);
        btnSwitchToRegister = findViewById(R.id.btn_switch_mode);
        tvTitle = findViewById(R.id.tv_title);
        tvSwitchText = findViewById(R.id.tv_switch_text);
        progressBar = findViewById(R.id.progress_bar);
        tilEmail = findViewById(R.id.til_email);
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

        if (validateInput(username, password, null)) {
            return;
        }

        showLoading(true);
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);
        ApiManager.getInstance().getAuthClient().login(loginDto, new AuthCallback<ApiResponse<AuthResponseDto>>() {
            @Override
            public void onSuccess(ApiResponse<AuthResponseDto> result) {
                runOnUiThread(() -> {
                    AuthResponseDto authResponse = result.getData();
                    String token = authResponse.getToken();
                    ApiManager.getInstance().setAuthToken(token);
                    SessionManager.saveToken(LoginActivity.this, token);
                    showLoading(false);
                    if (authResponse.getUser() != null) {
                        //Save user
                        User user = authResponse.getUser().toUser();
                        SessionManager.saveUser(user);
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
            @Override
            public void onFailure(Throwable throwable) {
                // Handle network failure
                runOnUiThread(() -> {
                    Toast.makeText(LoginActivity.this, "Network error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void performRegister() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (validateInput(username, password, email)) {
            return;
        }

        showLoading(true);
        RegisterDto registerDto = new RegisterDto();

        registerDto.setUsername(username);
        registerDto.setPassword(password);
        registerDto.setEmail(email);

        ApiManager.getInstance().getAuthClient().register(registerDto, new AuthCallback<ApiResponse<AuthResponseDto>>() {
            @Override
            public void onSuccess(ApiResponse<AuthResponseDto> result) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    switchMode();

                    etUsername.setText(username);
                    etPassword.setText(""); // Clear password for security
                    etPassword.requestFocus(); // Focus vào password field
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Log.d("LoginActivity", "Register error: " + error);
                    Toast.makeText(LoginActivity.this, "Lỗi đăng ký: " + error, Toast.LENGTH_SHORT).show();
                });
            }
            @Override
            public void onFailure(Throwable throwable) {
                // Handle network failure
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(LoginActivity.this, "Network error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private boolean validateInput(String username, String password, String email) {
        if (TextUtils.isEmpty(username)) {
            etUsername.setError("Vui lòng nhập tên đăng nhập");
            etUsername.requestFocus();
            return true;
        }

        if (username.length() < 3) {
            etUsername.setError("Tên đăng nhập phải có ít nhất 3 ký tự");
            etUsername.requestFocus();
            return true;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return true;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return true;
        }

        if (!isLoginMode && TextUtils.isEmpty(email)) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return true;
        }

        if (!isLoginMode && !isValidEmail(email)) {
            etEmail.setError("Email không hợp lệ");
            etEmail.requestFocus();
            return true;
        }

        return false;
    }

    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void switchMode() {
        isLoginMode = !isLoginMode;

        if (isLoginMode) {
            // Switch to Login mode
            tvTitle.setText("Đăng Nhập");
            btnLogin.setText("ĐĂNG NHẬP");
            tvSwitchText.setText("Chưa có tài khoản?");
            btnSwitchToRegister.setText("Đăng ký ngay");
            tilEmail.setVisibility(View.GONE);
        } else {
            // Switch to Register mode
            tvTitle.setText("Đăng Ký");
            btnLogin.setText("ĐĂNG KÝ");
            tvSwitchText.setText("Đã có tài khoản?");
            btnSwitchToRegister.setText("Đăng nhập");
            tilEmail.setVisibility(View.VISIBLE);
        }

        // Clear input fields
        etUsername.setText("");
        etPassword.setText("");
        etEmail.setText("");
        etUsername.setError(null);
        etPassword.setError(null);
        etEmail.setError(null);
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnSwitchToRegister.setEnabled(!show);
        etUsername.setEnabled(!show);
        etPassword.setEnabled(!show);
        etEmail.setEnabled(!show);
    }
}