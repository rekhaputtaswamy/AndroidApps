package com.shieldapp.shield;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SignInActivity extends AppCompatActivity {
    private EditText phone_no;
    private FloatingActionButton fab_button;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);


        mainLayout = (LinearLayout) findViewById(R.id.mainLayout);
        phone_no = (EditText) findViewById(R.id.phone_no);

        fab_button = findViewById(R.id.fab);
        fab_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(phone_no.getText().toString().equals("") || phone_no.getText().toString().length() < 10) {
                Context context = getApplicationContext();
                CharSequence text = "Please Enter Phone Number";

                Snackbar snackbar = Snackbar
                        .make(mainLayout, text, Snackbar.LENGTH_LONG);
                snackbar.show();

            } else {
                Intent intent = new Intent(SignInActivity.this, SignupActivity.class);
                intent.putExtra("phone_no", phone_no.getText().toString());
                startActivity(intent);

            }
            }
        });

    }

}
