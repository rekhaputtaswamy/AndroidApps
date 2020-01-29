package fr.epita.kavach;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GetDetailsHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LandingActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "Cognito";

    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 10;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;

    private LocationTrack locationTrack;
    static LocationManager mLocationManager;
    private LocationListener locListener;
    private Messenger mServiceMessenger;
    private SpeechRecognizer sr;
    private SwitchCompat switcher;

    private static AppCompatActivity activity;
    private Button speak, stop;
    ProgressDialog dialog;

    TextView display_text;
    static EditText textView;
    static double latitude, longitude;

    private static final Integer LOCATION = 0x1;
    private boolean isEndOfSpeech = false;
    boolean serviceconneted;
    static int messageCounter = 0;
    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        activity = LandingActivity.this;

        display_text = (TextView) findViewById(R.id.display_name);

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locListener = new MyLocationListener();

        speak = findViewById(R.id.speak);
        stop = findViewById(R.id.stop);

        textView = findViewById(R.id.write);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSpeechService();
            }
        });
        sr = SpeechRecognizer.createSpeechRecognizer(LandingActivity.this);
        sr.setRecognitionListener(new Listner());

        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSpeechService();
            }
        });

        startSpeechService();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        CognitoSettings cognitoSettingsOut = new CognitoSettings(this);
        CognitoUser currentUser = cognitoSettingsOut.getUserPool().getCurrentUser();

        // Implement callback handler for getting details
        GetDetailsHandler getDetailsHandler = new GetDetailsHandler() {
            @Override
            public void onSuccess(CognitoUserDetails cognitoUserDetails) {

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String userdetails = gson.toJson(cognitoUserDetails);
                Log.i(TAG, "cognitoUserDetails: " + userdetails);
                try {
                    JSONObject reader = new JSONObject(userdetails);
                    JSONObject cognitouserattributes = reader.getJSONObject("cognitoUserAttributes").getJSONObject("userAttributes");
                    String userfullname = cognitouserattributes.getString("name");
                    display_text.setText("Welcome, "+userfullname);

                    Log.i(TAG, "cognitoUserDetails: " + userfullname);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception exception) {
                // Fetch user details failed, check exception for the cause
                Log.i(TAG, "failed getting cognitoUserDetails: " + exception.getLocalizedMessage());
            }
        };

        // Fetch the user details
        currentUser.getDetailsInBackground(getDetailsHandler);

        Menu menu = navigationView.getMenu();
        MenuItem menuItem = menu.findItem(R.id.SetAlert);
        View actionView = MenuItemCompat.getActionView(menuItem);

        switcher = (SwitchCompat) actionView.findViewById(R.id.alert_switch);
        switcher.setChecked(true);
        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, (switcher.isChecked()) ? "Service on" : "Service off", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

        /*logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startActivity(logoutintent);
            }
        }); */
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main2, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.MyAccount) {
            // Handle the camera action
        } else if (id == R.id.AddContacts) {


        } else if (id == R.id.SetAlert) {
            switcher.setChecked(!switcher.isChecked());
            if(switcher.isChecked()) {
                startSpeechService();
            } else {
                stopSpeechService();
            }
            Snackbar.make(item.getActionView(), (switcher.isChecked()) ? "is checked" : "not checked", Snackbar.LENGTH_SHORT).setAction("Action", null).show();

        } else if (id == R.id.LogOut) {
            Intent logoutintent = new Intent(this,LogoutActivity.class);
            logoutintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logoutintent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void stopSpeechService() {
        Intent i = new Intent(LandingActivity.this, MyVoiceService.class);
        stopService(i);
        Toast.makeText(LandingActivity.this, "stop speaking", Toast.LENGTH_SHORT).show();

        messageCounter = 0;
    }

    public void checkLocationPermission() {
        try {
            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // Asking user if explanation is needed
                if (ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {

                    //Prompt the user once explanation has been shown
                    android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(LandingActivity.this).create();
                    alertDialog.setTitle("Permission needed");
                    alertDialog.setMessage("Please allow permission to access your location to continue service");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //Prompt the user once explanation has been shown
                                    ActivityCompat.requestPermissions(LandingActivity.this,
                                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                            MY_PERMISSIONS_REQUEST_LOCATION);
                                }
                            });
                    alertDialog.show();

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(LandingActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }

            } else {

                sendSMS(getCurrentLocation());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startSpeechService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission(Manifest.permission.RECORD_AUDIO, LOCATION);
        }

        Intent i = new Intent(LandingActivity.this, MyVoiceService.class);
        bindService(i, connection, code);
        startService(i);
        Toast.makeText(LandingActivity.this, "Start Speaking", Toast.LENGTH_SHORT).show();
    }

    class Listner implements RecognitionListener {

        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.d("Speech", "ReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.d("Speech", "beginSpeech");

        }

        @Override
        public void onRmsChanged(float rmsdB) {
            Log.d("Speech", "onrms");

        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.d("Speech", "onbuffer");

        }

        @Override
        public void onEndOfSpeech() {
            isEndOfSpeech = true;

        }

        @Override
        public void onError(int error) {
            Log.d(TAG, "error " + error);
            if (!isEndOfSpeech) {
                return;
            }
            Toast.makeText(LandingActivity.this, "Try agine", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onResults(Bundle results) {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String word = (String) data.get(data.size() - 1);
            textView.setText(word);

            if (textView.getText().toString().contains("help")) {
                Toast.makeText(LandingActivity.this, "Help needed!!", Toast.LENGTH_LONG).show();
            }

            dialog.dismiss();


        }

        @Override
        public void onPartialResults(Bundle partialResults) {

            ArrayList data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String word = (String) data.get(data.size() - 1);
            textView.setText(word);

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    }

    static void sendSMS(String mapurl) {

        try {

            String phoneNo = "0758815065";
            String message = "Please Help Me!! Use the location link to find me ";

            SmsManager smsManager = SmsManager.getDefault();
            StringBuffer smsBody = new StringBuffer();
            smsBody.append(message);
            smsBody.append("\n");
            smsBody.append(Uri.parse(mapurl));

            smsManager.sendTextMessage(phoneNo, null, smsBody.toString(), null, null);
            Log.i("SMS status", "SMS Sent!!");

            messageCounter++;

        } catch (Exception e) {
            Toast.makeText(activity,
                    "SMS faild, please try again later!",
                    Toast.LENGTH_LONG).show();

            e.printStackTrace();
        }

    }

    static void smsPermissions() {
        try {
            LandingActivity object = new LandingActivity();
            //stopSpeechService();

            if (ContextCompat.checkSelfPermission(activity,
                    Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.SEND_SMS)) {
                    object.checkLocationPermission();
                } else {
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.SEND_SMS},
                            MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
            } else {
                object.checkLocationPermission();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(LandingActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LandingActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(LandingActivity.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(LandingActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                checkLocationPermission();
            } else {
                Toast.makeText(getApplicationContext(),
                        "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                return;
            }
        } else if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }

                //mLocationManager.requestLocationUpdates("gps", 0000, 000, locListener);
                String map_url = getCurrentLocation();
                sendSMS(map_url);
            }

        }

        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            }

        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentLocation() {

        locationTrack = new LocationTrack(LandingActivity.this);
        String url = "";

        /*if (!locationTrack.canGetLocation()) {

            //locationTrack.showSettingsAlert();
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            activity.startActivity(intent);
        }*/

        latitude = 48.8157;
        longitude = 2.3628;

        if(locationTrack.getLatitude() != 0.0 && locationTrack.getLongitude() != 0.0){
            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();
        }
        url = "http://maps.google.com/maps?saddr=" + latitude + "," + longitude;
        Log.i("Maps URL: ", url);

        return url;
    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.d("service", "connected");

            mServiceMessenger = new Messenger(service);
            Message msg = new Message();
            msg.what = MyVoiceService.MSG_RECOGNIZER_START_LISTENING;
            try {
                mServiceMessenger.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            serviceconneted = false;
            Log.d("service", "disconnetd");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        messageCounter = 0;
    }
}
