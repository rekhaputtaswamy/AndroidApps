package com.shieldapp.shield;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LandingPageActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener,
        NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{

    private static final String TAG = "LANDING_PAGE_ACTIVITY";
    private PopupWindow mPopupWindow, dialogAlert, modeOfTransport, confirmationAlert;
    private DrawerLayout drawer;
    private Context mContext;

    private EditText planned_date_time, from, to, via;
    private ImageView calendar;
    private Button nextBtn;
    private CheckBox chBox;
    private String selectedMode = "";

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    GoogleMap mGoogleMap;
    SupportMapFragment mapFrag;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    FusedLocationProviderClient mFusedLocationClient;
    MarkerOptions markerOptions = new MarkerOptions();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        mContext = getApplicationContext();

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

//        ImageView nav_menu = (ImageView) findViewById(R.id.hamburger_menu);
//        nav_menu.setOnClickListener(this);

        ImageView trip_track = (ImageView) findViewById(R.id.trip_track_btn);
        trip_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showTransportPopUp();
            }
        });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);
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

    @Override
    protected void onPause() {
        super.onPause();
        //stop location updates when Activity is no longer active
        if (mFusedLocationClient != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000); // two minute interval
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mGoogleMap.setMyLocationEnabled(false);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        }
        else {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mGoogleMap.setMyLocationEnabled(false);

        }

    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.i("MapsActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker.remove();
                }

                //Place current location marker
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                markerOptions.position(latLng);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pink_dot));
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions);
                CameraPosition newCamPos = new CameraPosition(latLng,
                        15.5f,
                        mGoogleMap.getCameraPosition().tilt, //use old tilt
                        mGoogleMap.getCameraPosition().bearing);
                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null);
                mCurrLocationMarker.setPosition(latLng);

            }
        }
    };

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(LandingPageActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mGoogleMap.setMyLocationEnabled(true);
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, 25, 25);

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    @Override
    public void onLocationChanged(Location location) {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pink_dot));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraPosition newCamPos = new CameraPosition(latLng,
                15.5f,
                mGoogleMap.getCameraPosition().tilt, //use old tilt
                mGoogleMap.getCameraPosition().bearing);
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(newCamPos), 4000, null);
        mCurrLocationMarker.setPosition(latLng);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void showLogoutAlert() {
        final android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(LandingPageActivity.this);
        alertDialogBuilder.setTitle("Logout");

        alertDialogBuilder
                .setMessage("Do You want to Logout from app?")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        Intent intent = new Intent(LandingPageActivity.this, SignInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                });

        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_logout) {
            showLogoutAlert();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializePopupWindow(View customView) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels * 2 / 3;

        mPopupWindow = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
        );

        mPopupWindow.setFocusable(true);
        mPopupWindow.update();

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            mPopupWindow.setElevation(5.0f);
        }

        mPopupWindow.showAtLocation(drawer, Gravity.BOTTOM,0,0);

        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                if(dialogAlert != null && dialogAlert.isShowing()) {
                    dialogAlert.dismiss();
                }
            }
        });
    }

    private void setViewGroupActions(View customView) {
        calendar = (ImageView) customView.findViewById(R.id.calendar);
        nextBtn = (Button) customView.findViewById(R.id.next_btn);
        from = (EditText) customView.findViewById(R.id.from);
        to = (EditText) customView.findViewById(R.id.to);
        planned_date_time = (EditText) customView.findViewById(R.id.date_time);
        via = (EditText) customView.findViewById(R.id.via);
        chBox = (CheckBox) customView.findViewById(R.id.chkIos);

        //Checkbox action
        chBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if(chBox.isChecked() && dialogAlert != null && dialogAlert.isShowing()) {
                    dialogAlert.dismiss();
                }
            }
        });

        //planned date time text change listener
        from.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!from.getText().toString().isEmpty()) {
                    nextBtn.setEnabled(true);
                    nextBtn.setBackgroundResource(R.drawable.gradient_button);
                } else {
                    nextBtn.setEnabled(false);
                    nextBtn.setBackgroundResource(R.drawable.nxtbtn_custom_layout);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        nextBtn.setOnClickListener(this);
        calendar.setOnClickListener(this);

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

    private void showPopupDialog(String title_txt, String text_txt) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_alert_layout,null);

        TextView title = (TextView) customView.findViewById(R.id.alert_title);
        TextView messageText = (TextView) customView.findViewById(R.id.alert_text);

        title.setText(title_txt);
        messageText.setText(text_txt);

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels - 60;

        dialogAlert = new PopupWindow(
                customView,
                width,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            dialogAlert.setElevation(5.0f);
        }

        dialogAlert.showAtLocation(drawer, Gravity.TOP,0,0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trip_track:
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
                // Inflate the custom layout/view
                View customView = inflater.inflate(R.layout.trip_track_custom_layout,null);
                setViewGroupActions(customView);
                initializePopupWindow(customView);
                break;

            case R.id.next_btn:
                nextAction();
                break;

            case R.id.hamburger_menu:
                menuAction();
                break;

            case R.id.calendar:
                setTime();
                break;

            case R.id.nextbtn:
                setModesNextAction();
                break;

            case R.id.modes_own_vehicle:
                selectedMode = "Own Vehicle";
                break;

            case R.id.modes_taxi:
                selectedMode = "Cab";
                break;

            case R.id.modes_office_cab:
                selectedMode = "Office Cab";
                break;

            case R.id.modes_bus:
                selectedMode = "Bus";
                break;

            case R.id.modes_auto:
                selectedMode = "Others";
                break;

            case R.id.confirm_btn:
                confirmationAlert.dismiss();
                break;
        }
    }

    private void setModesNextAction() {

        if(modeOfTransport != null && modeOfTransport.isShowing()) {
            modeOfTransport.dismiss();
        }

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.custom_confirmation_layout,null);

        Button confirm_btn = (Button) customView.findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(this);

        confirmationAlert = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        confirmationAlert.setFocusable(true);
        confirmationAlert.update();

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            confirmationAlert.setElevation(5.0f);
        }

        confirmationAlert.showAtLocation(drawer, Gravity.BOTTOM,0,0);
    }

    private void setTime() {
        // Get Current time
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);


        TimePickerDialog timePickerDialog = new TimePickerDialog(LandingPageActivity.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        planned_date_time.setText(hourOfDay + ":" + minute);
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void nextAction() {
        if(dialogAlert != null && dialogAlert.isShowing()) {
            dialogAlert.dismiss();
        }

        if(from.getText().toString().isEmpty() || to.getText().toString().isEmpty() ||
                planned_date_time.getText().toString().isEmpty()) {
            showPopupDialog("Error!!", "All Fields are mandatory");

        } else if(!chBox.isChecked() || via.getText().toString().isEmpty()) {
            showPopupDialog("I WANT TO TRAVEL VIA", "Please select your best route " +
                    "or set your route by setting the waypoint");
            via.setText("");
        } else {
            showModeOfTransportPopUp();
            mPopupWindow.dismiss();
        }
    }

    private void menuAction() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(Gravity.START);
        }
    }

    private void initializeModesView(View customView) {
        RelativeLayout own_layout, cab_layout, office_cab_layout, bus_layout, others_layout;
        own_layout = (RelativeLayout) customView.findViewById(R.id.modes_own_vehicle);
        own_layout.setOnClickListener(this);

        cab_layout = (RelativeLayout) customView.findViewById(R.id.modes_taxi);
        cab_layout.setOnClickListener(this);

        office_cab_layout = (RelativeLayout) customView.findViewById(R.id.modes_office_cab);
        office_cab_layout.setOnClickListener(this);

        bus_layout = (RelativeLayout) customView.findViewById(R.id.modes_bus);
        bus_layout.setOnClickListener(this);

        others_layout = (RelativeLayout) customView.findViewById(R.id.modes_auto);
        others_layout.setOnClickListener(this);

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        showTransportPopUp();
    }

    private void showTransportPopUp(){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.pop_call_me_layout, null);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.activity_landing_page);
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
        popupWindow.showAtLocation(constraintLayout, Gravity.BOTTOM, 0, 0);
        final TextView date_text = (TextView) popupView.findViewById(R.id.call_me_date_text);
        final TextView time_text = (TextView) popupView.findViewById(R.id.call_me_time_text);

        final Calendar myCalendar = Calendar.getInstance();
        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);
                String timeFormat = "hh : mm a";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(timeFormat, Locale.US);
                time_text.setText(simpleDateFormat.format(myCalendar.getTime()).toLowerCase());
            }
        };


        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String dateFormat = "d MMMM, y";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                date_text.setText(simpleDateFormat.format(myCalendar.getTime()));
            }

        };

        time_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(LandingPageActivity.this,time,myCalendar.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE),false).show();
            }
        });

        date_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(LandingPageActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }

    private void showModeOfTransportPopUp() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.transport_mode_layout,null);

        initializeModesView(customView);

        Button modes_next_btn = (Button) customView.findViewById(R.id.nextbtn);
        modes_next_btn.setOnClickListener(this);

        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int height = displayMetrics.heightPixels / 2;

        modeOfTransport = new PopupWindow(
                customView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                height
        );

        modeOfTransport.setFocusable(true);
        modeOfTransport.update();

        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            modeOfTransport.setElevation(5.0f);
        }

        modeOfTransport.showAtLocation(drawer, Gravity.BOTTOM,0,0);

    }
}
