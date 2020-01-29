package com.laundry.mpick;

import android.content.Intent;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private DrawerLayout drawer;
    private ListView drawerList;
    private LinearLayout mainLayout, fabricLayout1, fabricLayout2, fabricLayout3, fabricLayout4, fabricLayout5, fabricLayout6;
    private ImageButton addBtn1, addBtn2, addBtn3, addBtn4, addBtn5;
    private Spinner spinner1, spinner2, spinner3, spinner4, spinner5, spinner6;
    private EditText amount1, amount2, amount3, amount4, amount5, amount6, discount, total_amt;
    private ArrayList<String> fabricsList;
    private Button reviewBtn;
    private ImageView navigation;
    private int amt1, amt2, amt3, amt4, amt5, amt6, total, discount_value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        if (getIntent().hasExtra("login_type") && "otpLogin".equals(getIntent().getExtras().getString("login_type"))) {
            Intent intent = new Intent(MainActivity.this, ChangePasswordActivity.class);
            startActivity(intent);

        }

        initialise();
    }

    private void setSpinnerList() {
        fabricsList = new ArrayList<String>();
        fabricsList.add("Pant");
        fabricsList.add("Shirt");
        fabricsList.add("Saree");
        fabricsList.add("Salwar");
        fabricsList.add("Blouse");
        fabricsList.add("Others");
    }

    private void initialise() {
        setSpinnerList();

        int [] listImages = { 0, R.drawable.user, R.drawable.orders, R.drawable.password, R.drawable.logout };
        String [] listNames = { "", "Customer Info", "Orders", "Change Password", "Logout" };

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        drawerList.setAdapter(new DrawerAdapter(this, listNames, listImages));

        mainLayout = (LinearLayout) findViewById(R.id.main);

        fabricLayout1 = (LinearLayout) findViewById(R.id.fabric1);
        fabricLayout2 = (LinearLayout) findViewById(R.id.fabric2);
        fabricLayout3 = (LinearLayout) findViewById(R.id.fabric3);
        fabricLayout4 = (LinearLayout) findViewById(R.id.fabric4);
        fabricLayout5 = (LinearLayout) findViewById(R.id.fabric5);
        fabricLayout6 = (LinearLayout) findViewById(R.id.fabric6);

        discount = (EditText) findViewById(R.id.discount);
        discount.setText("0");
        discount.setEnabled(false);
        discount_value = Integer.parseInt(discount.getText().toString());

        total_amt = (EditText) findViewById(R.id.total_amt);
        total_amt.setText("0");

        addBtn1 = (ImageButton) findViewById(R.id.add);
        addBtn1.setOnClickListener(this);
        addBtn2 = (ImageButton) findViewById(R.id.add2);
        addBtn2.setOnClickListener(this);
        addBtn3 = (ImageButton) findViewById(R.id.add3);
        addBtn3.setOnClickListener(this);
        addBtn4 = (ImageButton) findViewById(R.id.add4);
        addBtn4.setOnClickListener(this);
        addBtn5 = (ImageButton) findViewById(R.id.add5);
        addBtn5.setOnClickListener(this);

        CustomSpinnerAdapter customSpinnerAdapter = new CustomSpinnerAdapter(MainActivity.this, fabricsList);
        spinner1 = (Spinner) findViewById(R.id.spinner);
        spinner1.setAdapter(customSpinnerAdapter);
        spinner1.setOnItemSelectedListener(this);

        spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(customSpinnerAdapter);
        spinner2.setOnItemSelectedListener(this);

        spinner3 = (Spinner) findViewById(R.id.spinner3);
        spinner3.setAdapter(customSpinnerAdapter);
        spinner3.setOnItemSelectedListener(this);

        spinner4 = (Spinner) findViewById(R.id.spinner4);
        spinner4.setAdapter(customSpinnerAdapter);
        spinner4.setOnItemSelectedListener(this);

        spinner5 = (Spinner) findViewById(R.id.spinner5);
        spinner5.setAdapter(customSpinnerAdapter);
        spinner5.setOnItemSelectedListener(this);

        spinner6 = (Spinner) findViewById(R.id.spinner6);
        spinner6.setAdapter(customSpinnerAdapter);
        spinner6.setOnItemSelectedListener(this);

        reviewBtn = (Button) findViewById(R.id.review);
        reviewBtn.setOnClickListener(this);

        navigation = (ImageView) findViewById(R.id.navigation);
        navigation.setOnClickListener(this);

        amount1 = (EditText) findViewById(R.id.amount1);
        amount1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(amount1.getText())) {
                    amt1 = 0;
                } else {
                    amt1 = Integer.parseInt(amount1.getText().toString());
                }
                calculateTotal();

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        amount2 = (EditText) findViewById(R.id.amount2);
        amount2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(amount2.getText())) {
                    amt2 = 0;
                } else {
                    amt2 = Integer.parseInt(amount2.getText().toString());
                }
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        amount3 = (EditText) findViewById(R.id.amount3);
        amount3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(amount3.getText())) {
                    amt3 = 0;
                } else {
                    amt3 = Integer.parseInt(amount3.getText().toString());
                }
                calculateTotal();
            }
        });

        amount4 = (EditText) findViewById(R.id.amount4);
        amount4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(amount4.getText())) {
                    amt4 = 0;
                } else {
                    amt4 = Integer.parseInt(amount4.getText().toString());
                }
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        amount5 = (EditText) findViewById(R.id.amount5);
        amount5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(amount5.getText())) {
                    amt5 = 0;
                } else {
                    amt5 = Integer.parseInt(amount5.getText().toString());
                }
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        amount6 = (EditText) findViewById(R.id.amount6);
        amount6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(amount6.getText())) {
                    amt6 = 0;
                } else {
                    amt6 = Integer.parseInt(amount6.getText().toString());
                }
                calculateTotal();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void calculateTotal() {

        total = amt1 + amt2 + amt3 + amt4 + amt5 + amt6 - discount_value;

        total_amt.setText(String.valueOf(total));
        total_amt.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                fabricLayout2.setVisibility(View.VISIBLE);
                addBtn1.setVisibility(View.INVISIBLE);
                break;

            case R.id.add2:
                fabricLayout3.setVisibility(View.VISIBLE);
                addBtn2.setVisibility(View.INVISIBLE);
                break;

            case R.id.add3:
                fabricLayout4.setVisibility(View.VISIBLE);
                addBtn3.setVisibility(View.INVISIBLE);
                break;

            case R.id.add4:
                fabricLayout5.setVisibility(View.VISIBLE);
                addBtn4.setVisibility(View.INVISIBLE);
                break;

            case R.id.add5:
                fabricLayout6.setVisibility(View.VISIBLE);
                addBtn5.setVisibility(View.INVISIBLE);
                break;

            case R.id.review:
                Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
                startActivity(intent);
                break;

            case R.id.navigation:
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
