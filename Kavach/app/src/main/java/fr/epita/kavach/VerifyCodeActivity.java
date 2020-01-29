package fr.epita.kavach;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class VerifyCodeActivity extends AppCompatActivity {

    private static final String TAG = "Cognito";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_code);

        final EditText editTextCode = findViewById(R.id.secretcode);
        final EditText editTextUsername = findViewById(R.id.emailid);

        Button buttonVerify = findViewById(R.id.verify);
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmTask().execute(String.valueOf(editTextCode.getText())
                        , String.valueOf(editTextUsername.getText()));
            }
        });
    }

    private class ConfirmTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            final String[] result = new String[1];

            // Callback handler for confirmSignUp API
            final GenericHandler confirmationCallback = new GenericHandler() {

                @Override
                public void onSuccess() {
                    // User was successfully confirmed
                    result[0] = "Succeeded!";
                    Toast.makeText(getApplicationContext(),"User successfully registered",Toast.LENGTH_LONG );
                }

                @Override
                public void onFailure(Exception exception) {
                    // User confirmation failed. Check exception for the cause.
                    result[0] = "Failed: "+exception.getMessage();
                    Toast.makeText(getApplicationContext(),"User registration failed",Toast.LENGTH_LONG );
                }
            };

            CognitoSettings cognitoSettings = new CognitoSettings(VerifyCodeActivity.this);

            CognitoUser thisUser = cognitoSettings.getUserPool().getUser(params[1]);
            // This will cause confirmation to fail if the user attribute (alias) has been verified
            // for another user in the same pool
            thisUser.confirmSignUp(params[0], false, confirmationCallback);

            return result[0];
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.i(TAG, "Confirmation result: " + result);

        }
    }
}
