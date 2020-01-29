package fr.epita.kavach;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Cognito";
    private EditText emailid,password;
    private Button login_btn;
    private TextView register_user;
    Context context = MainActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginUser();
    }

    private void loginUser() {
        final Intent signupintent = new Intent(this, SignupAcitivity.class);
        final Intent landingintent = new Intent(this,LandingActivity.class);
        emailid = (findViewById(R.id.emailid));
        password = (findViewById(R.id.password));
        login_btn = (findViewById(R.id.login_btn));
        register_user = (findViewById(R.id.registertext));

        final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
            @Override
            public void onSuccess(CognitoUserSession userSession, CognitoDevice newDevice) {

                Log.i(TAG, "Login successful, can get tokens here!");

                /*userSession contains the tokens*/

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(userSession);
                Log.i(TAG, "user session: "+json);
                startActivity(landingintent);
            }

            @Override
            public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation
                    , String userId) {

                Log.i(TAG, "in getAuthenticationDetails()....");

                /*need to get the userId & password to continue*/
                AuthenticationDetails authenticationDetails = new AuthenticationDetails(userId
                        , String.valueOf(password.getText()), null);

                // Pass the user sign-in credentials to the continuation
                authenticationContinuation.setAuthenticationDetails(authenticationDetails);

                // Allow the sign-in to continue
                authenticationContinuation.continueTask();

            }

            @Override
            public void getMFACode(MultiFactorAuthenticationContinuation continuation) {

                Log.i(TAG, "in getMFACode()....");

                // if Multi-factor authentication is required; get the verification code from user
//                multiFactorAuthenticationContinuation.setMfaCode(mfaVerificationCode);

                // Allow the sign-in process to continue
//                multiFactorAuthenticationContinuation.continueTask();
            }

            @Override
            public void authenticationChallenge(ChallengeContinuation continuation) {
                Log.i(TAG, "in authenticationChallenge()....");
            }

            @Override
            public void onFailure(Exception exception) {
                Log.i(TAG, "Login failed: " + exception.getLocalizedMessage());
                new AlertDialog.Builder(context)
                        .setTitle("Login Failed")
                        .setMessage("exception.getLocalizedMessage()")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        };
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailid.getText().toString().isEmpty() || password.getText().toString().isEmpty()){
                    new AlertDialog.Builder(context)
                            .setTitle("Login Failed")
                            .setMessage("Username or Password cannot be Empty")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                }
                            }).show();
                }
                else{
                    // Commented to run locally
                    CognitoSettings cognitoSettings = new CognitoSettings(MainActivity.this);

                    CognitoUser thisUser = cognitoSettings.getUserPool().getUser(String.valueOf(emailid.getText()));
                    Log.i(TAG, "Login button clicked....");

                    thisUser.getSessionInBackground(authenticationHandler);
                }
            }
        });

        register_user.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Navigate to Register User screen
                startActivity(signupintent);
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
