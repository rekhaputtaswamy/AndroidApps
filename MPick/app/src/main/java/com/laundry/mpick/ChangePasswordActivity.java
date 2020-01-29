package com.laundry.mpick;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.laundry.utils.CommonUtils;

public class ChangePasswordActivity extends AppCompatActivity {
    private Button saveBtn;
    private boolean isOptLogin;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(getIntent().hasExtra("login_type") && "otpLogin".equals(getIntent().getExtras().getString("otpLogin"))) {
            isOptLogin = true;
        }

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initialise();

    }

    private void initialise() {
        saveBtn = (Button) findViewById(R.id.save_password);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences pref = getApplicationContext().getSharedPreferences(CommonUtils.SHARED_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("otp");
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(isOptLogin) {
            Intent intent = new Intent(ChangePasswordActivity.this, MainActivity.class);
            startActivity(intent);
        }
        finish();
    }
}
