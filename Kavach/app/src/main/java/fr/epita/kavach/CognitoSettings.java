package fr.epita.kavach;

import android.content.Context;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool;
import com.amazonaws.regions.Regions;

public class CognitoSettings {
    private Context context;
    private final String userPoolId = "eu-central-1_fDGoWqSS1";
    private final String clientId = "753ha8tqco133gstu3gpbovpn9";
    private final String clientSecret = "2rd7s7eem8gmefvu4s1mlh2aso6m3ijrcisceulf9n11cqovje8";
    private final Regions cognitoregion = Regions.EU_CENTRAL_1;

    public CognitoSettings(Context context) {
        this.context = context;
    }

    public String getUserPoolId() {
        return userPoolId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public Regions getCognitoregion() {
        return cognitoregion;
    }

    public CognitoUserPool getUserPool(){
        return new CognitoUserPool(context,userPoolId,clientId,clientSecret,cognitoregion);
    }
}
