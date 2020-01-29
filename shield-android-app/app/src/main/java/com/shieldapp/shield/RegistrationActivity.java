package com.shieldapp.shield;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity {
    private Spinner year_spinner, profession_spinner;
    private TextView nameText;
    private EditText emailInput, contactInput, pwdInput, pwdReInput;
    private FloatingActionButton fabNext;
    private int screen_counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(getString(R.string.sign_up));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.title_color));
        mToolbar.setTitleTextAppearance(getApplicationContext(), R.style.SansBoldTextAppearance);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(screen_counter >= 1) {
                    --screen_counter;
                    setContentScreen();

                }
            }
        });

        nameText = (TextView) findViewById(R.id.nameInput);

        initialize(null);

        fabNext = findViewById(R.id.fab);
        fabNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ++screen_counter;
                setContentScreen();

            }
        });

    }

    private void initialize(View view) {

        setYearSpinnerData(view);
        setprofessionSpinnerData(view);
    }

    private void setPasswordChange() {
        pwdInput = (EditText) findViewById(R.id.pwdInput);
        pwdReInput = (EditText) findViewById(R.id.pwdReInput);

        pwdInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!pwdInput.getText().toString().isEmpty()) {
                    enableFab(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        pwdReInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!pwdReInput.getText().toString().isEmpty()) {
                    enableFab(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setContactChange() {
        contactInput = (EditText) findViewById(R.id.contactInput);

        contactInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!contactInput.getText().toString().isEmpty()) {
                    enableFab(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setEmailInputChange() {
        emailInput = (EditText) findViewById(R.id.emailInput);
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!emailInput.getText().toString().isEmpty()) {
                    enableFab(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setContentScreen() {
        ConstraintLayout mainLayout = (ConstraintLayout) findViewById(R.id. reg_contents);

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = null;
        enableFab(false);

        switch (screen_counter) {
            case 0:
                layout = inflater.inflate(R.layout.activity_registration, null);
                initialize(layout);
                break;

            case 1:
                if(!nameText.getText().toString().isEmpty() && year_spinner.getSelectedItemId() != 0 &&
                        profession_spinner.getSelectedItemId() != 0) {
                    layout = inflater.inflate(R.layout.registration_email, null);

                }
                break;

            case 2:
                layout = inflater.inflate(R.layout.registration_contact, null);
                break;

            case 3:
                layout = inflater.inflate(R.layout.registration_password, null);
                break;

            case 4:
                enableFab(true);
                layout = inflater.inflate(R.layout.registration_identity, null);
                break;

            case 5:
                Intent intent = new Intent(RegistrationActivity.this, TestActivity.class);
                startActivity(intent);
                finish();
                break;
        }

        if(layout != null) {
            mainLayout.removeAllViews();
            mainLayout.addView(layout);

            if(screen_counter == 1) {
                setEmailInputChange();

            } else if(screen_counter == 2) {
                setContactChange();

            } else if(screen_counter == 3) {
                setPasswordChange();

            } else if(screen_counter == 4) {
                 // TODO: functionality of Profile screen

            }
        }
    }

    private void setYearSpinnerData(View view) {
        if(view != null) {
            year_spinner = (Spinner) view.findViewById(R.id.birth_year);

        } else {
            year_spinner = (Spinner) findViewById(R.id.birth_year);

        }

        ArrayList<String> years = new ArrayList<String>();
        years.add("Birth Year");
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 1940; i <= thisYear; i++) {
            years.add(Integer.toString(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, years);

        year_spinner.setAdapter(adapter);
        year_spinner.setSelection(0, true);
        View v = year_spinner.getSelectedView();
        ((TextView) v).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) v).setTextSize(16);

        year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Change the selected item's text color
                ((TextView) view).setTextColor(getResources().getColor(R.color.gray_text));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setprofessionSpinnerData(View view) {

        if(view != null) {
            profession_spinner = (Spinner) view.findViewById(R.id.profession);

        } else {
            profession_spinner = (Spinner) findViewById(R.id.profession);

        }

        ArrayList<String> professions = new ArrayList<String>();
        professions.add("Profession");
        professions.add("Doctor");
        professions.add("Engineer");
        professions.add("Lawyer");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, professions);

        profession_spinner.setAdapter(adapter);
        profession_spinner.setSelection(0, true);
        View v = profession_spinner.getSelectedView();
        ((TextView) v).setTextColor(getResources().getColor(R.color.light_gray));
        ((TextView) v).setTextSize(16);

        profession_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Change the selected item's text color
                ((TextView) view).setTextColor(getResources().getColor(R.color.gray_text));
                enableFab(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void enableFab(boolean value) {
        if (value) {
            fabNext.setBackgroundTintList(getResources().getColorStateList(R.color.dark_gray));
        } else {
            fabNext.setBackgroundTintList(getResources().getColorStateList(R.color.light_gray));
        }
    }

}
