package com.laundry.mpick;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;

public class SummaryActivity extends AppCompatActivity {

    private Button confirm_btn;
    private CheckBox accept_pay;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        initialise();
    }

    private void initialise() {
        accept_pay = (CheckBox) findViewById(R.id.accept);

        confirm_btn = (Button) findViewById(R.id.confirm_btn);
        if(getIntent().hasExtra("type") && "ordersList".equals(getIntent().getExtras().getString("type"))) {
            confirm_btn.setText("PAY");
            accept_pay.setVisibility(View.VISIBLE);
        } else {
            confirm_btn.setText("CONFIRM");
            accept_pay.setVisibility(View.GONE);
        }
        confirm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if("PAY".equals(confirm_btn.getText())) {

                } else if("CONFIRM".equals(confirm_btn.getText())) {

                }

                finish();
            }
        });

    }
}
