package fr.epita.kavach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;


public class SignupAcitivity extends AppCompatActivity {
    private static final String TAG = "Cognito";
    private EditText email,password,phonenumber,fullname;
    private RadioGroup radioGenderGroup;
    private RadioButton radioGenderButton;
    private Button registerbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        registerUser();
    }

    private void registerUser(){
        final Intent intent = new Intent(this, VerifyCodeActivity.class);
        email = findViewById(R.id.emailid);
        password = findViewById(R.id.password);
        phonenumber = (findViewById(R.id.phone_number));
        fullname = (findViewById(R.id.fullname));
        radioGenderGroup = (findViewById(R.id.radioGender));

        registerbtn = findViewById(R.id.registerbutton);

        final CognitoUserAttributes userAttributes = new CognitoUserAttributes();
        final SignUpHandler signupCallback = new SignUpHandler() {
            @Override
            public void onSuccess(CognitoUser user, boolean signUpConfirmationState
                    , CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
                // Sign-up was successful
                Log.i(TAG, "sign up success...is confirmed: " + signUpConfirmationState);
                // Check if this user (cognitoUser) needs to be confirmed
                if (!signUpConfirmationState) {
                    Log.i(TAG, "sign up success...not confirmed, verification code sent to: "
                            + cognitoUserCodeDeliveryDetails.getDestination());
                    startActivity(intent);
                } else {
                    // The user has already been confirmed
                    Log.i(TAG, "sign up success...confirmed");
                }
            }

            @Override
            public void onFailure(Exception exception) {
                // Sign-up failed, check exception for the cause
                Log.i(TAG, "sign up failure: " + exception.getLocalizedMessage());
            }
        };
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get selected radio button from radioGroup
                int selectedId = radioGenderGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                radioGenderButton = (RadioButton)findViewById(selectedId);
                userAttributes.addAttribute("name", String.valueOf(fullname.getText()));
                userAttributes.addAttribute("phone_number", String.valueOf(phonenumber.getText()));
                userAttributes.addAttribute("gender", String.valueOf(radioGenderButton.getText()));
                userAttributes.addAttribute("custom:status", "1");
                userAttributes.addAttribute("email", String.valueOf(email.getText()));

                CognitoSettings cognitoSettings = new CognitoSettings(SignupAcitivity.this);

                cognitoSettings.getUserPool().signUpInBackground(String.valueOf(email.getText())
                        , String.valueOf(password.getText()), userAttributes
                        , null, signupCallback);
            }
        });
    }
}
