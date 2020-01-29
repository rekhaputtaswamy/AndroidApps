package com.laundry.mpick;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.laundry.utils.CommonUtils;

public class RegistrationActivity extends AppCompatActivity {
    private EditText username, password, confirm_password, mobile_no;
    private CheckBox terms_check;
    private Button registration_btn;
    private boolean isCustomer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initialise();

    }

    private void initialise() {
        username = (EditText) findViewById(R.id.username);
        username.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.user),
                null);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    if(!TextUtils.isEmpty(username.getText())) {
                        username.setError(null);

                    }
                }
            }
        });

        password = (EditText) findViewById(R.id.password);
        password.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.password),
                null);

        confirm_password = (EditText) findViewById(R.id.confirm_password);
        confirm_password.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.password),
                null);

        mobile_no = (EditText) findViewById(R.id.mobile_no);
        mobile_no.setCompoundDrawablesWithIntrinsicBounds(null, null,
                ContextCompat.getDrawable(getApplicationContext(), R.drawable.mobile),
                null);

        terms_check = (CheckBox) findViewById(R.id.terms);
        terms_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    isCustomer = true;
                } else {
                    isCustomer = false;
                }
            }

        });

        registration_btn = (Button) findViewById(R.id.register);
        registration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(username.getText())) {
                    username.setError("Username is Mandatory!");

                } else if (TextUtils.isEmpty(password.getText())) {
                    password.setError("Password is Mandatory!");

                } else if (TextUtils.isEmpty(confirm_password.getText())) {
                    confirm_password.setError("Confirm Password is Mandatory!");

                } else if (!password.getText().toString().trim().equals(confirm_password.getText().toString().trim())) {
                    confirm_password.setError("Passwords should match!");

                }  else if (TextUtils.isEmpty(mobile_no.getText()) || mobile_no.getText().length() < 10 || mobile_no.getText().length() > 10) {
                    mobile_no.setError("Mobile Number is Invalid!");

                } else if (!terms_check.isChecked()) {
                    CommonUtils.showDialog(RegistrationActivity.this, "Registration Failed!", "You must accept the Terms and Conditions.");

                } else {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(CommonUtils.SHARED_PREF, 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("mob_no", mobile_no.getText().toString());
                    editor.commit();

                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);

                }
            }
        });
    }

}
