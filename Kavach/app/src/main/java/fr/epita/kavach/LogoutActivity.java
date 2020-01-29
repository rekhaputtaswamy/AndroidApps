package fr.epita.kavach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;

public class LogoutActivity extends AppCompatActivity {

    private static final String TAG = "Cognito";
    private ProgressBar logoutspinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        logoutspinner = (ProgressBar)findViewById(R.id.logoutspinner);
        logoutspinner.setVisibility(View.VISIBLE);
        final Intent loginintent = new Intent(this,MainActivity.class);

        CognitoSettings cognitoSettings = new CognitoSettings(this);
        CognitoUser currentUser = cognitoSettings.getUserPool().getCurrentUser();

        Log.i(TAG, "calling signout....");

        currentUser.signOut();
        startActivity(loginintent);

    }
}
