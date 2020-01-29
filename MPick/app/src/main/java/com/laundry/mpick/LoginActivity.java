package com.laundry.mpick;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.laundry.utils.CommonUtils;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int PERMISSION_REQUEST_TO_SEND_SMS = 0;
    private EditText userEditText, passwordEditText;
    private Button loginBtn;
    private TextView forgot_pwd, register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initialise();

    }

    private void initialise() {

        SharedPreferences pref = getApplicationContext().getSharedPreferences(CommonUtils.SHARED_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        if (pref.contains("otp")) {
            editor.remove("otp");
        }

        userEditText = (EditText) findViewById(R.id.username);
        userEditText.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.user),
                null);
        userEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(userEditText.getText())) {
                        userEditText.setError(null);

                    }
                }
            }
        });

        passwordEditText = (EditText) findViewById(R.id.password);
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.password),
                null);

        loginBtn = (Button) findViewById(R.id.login);
        loginBtn.setOnClickListener(this);

        forgot_pwd = (TextView) findViewById(R.id.forgot_pwd);
        forgot_pwd.setOnClickListener(this);

        register = (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);
    }

    private void validate() {

        if (TextUtils.isEmpty(userEditText.getText())) {
            userEditText.setError("Username cannot be Empty!");

        } else if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordEditText.setError("Password cannot be Empty!");

        } else {

            SharedPreferences sharedpreferences = getSharedPreferences(CommonUtils.SHARED_PREF,
                    Context.MODE_PRIVATE);
            String otp = "";
            if (sharedpreferences.contains("otp")) {
                otp = sharedpreferences.getString("otp", "");

                if (otp != null && !otp.equals("") && otp.equals(passwordEditText.getText().toString())) {

                    Intent intent = new Intent(LoginActivity.this, ChangePasswordActivity.class);
                    intent.putExtra("login_type", "otpLogin");
                    startActivity(intent);
                } else {
                    CommonUtils.showDialog(LoginActivity.this, "Warning", "Invalid OTP!");
                }
            } else {

                Intent intent = new Intent(LoginActivity.this, AdminOrdersActivity.class);
                startActivity(intent);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:
                validate();
                break;

            case R.id.forgot_pwd:
                sendOTPMessage();
                break;

            case R.id.register:
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
        }
    }

    protected void sendOTPMessage() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.SEND_SMS)) {
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.SEND_SMS},
                            PERMISSION_REQUEST_TO_SEND_SMS);
                }
            }
        } else {
            sendSMS();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_TO_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMS();

                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    private void sendSMS() {
        SmsManager smsManager = SmsManager.getDefault();
        String mobileNo = getMobNo();
        String otp = String.valueOf(CommonUtils.generateRandomNumber());
        if (mobileNo != null && !mobileNo.equals("")) {
            smsManager.sendTextMessage(mobileNo, null, otp, null, null);
            Toast.makeText(getApplicationContext(), "OTP sent.",
                    Toast.LENGTH_LONG).show();

            SharedPreferences pref = getApplicationContext().getSharedPreferences(CommonUtils.SHARED_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("otp", otp);
            editor.commit();

        } else {
            Toast.makeText(getApplicationContext(),
                    "Mobile No. is unavailable", Toast.LENGTH_LONG).show();
        }
    }

    private String getMobNo() {
        String mobNo = "";
        SharedPreferences sharedpreferences = getSharedPreferences(CommonUtils.SHARED_PREF,
                Context.MODE_PRIVATE);

        if (sharedpreferences.contains("mob_no")) {
            mobNo = sharedpreferences.getString("mob_no", "");
        }

        return mobNo;
    }
}
