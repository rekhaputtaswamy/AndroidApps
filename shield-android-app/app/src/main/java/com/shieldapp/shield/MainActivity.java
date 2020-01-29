package com.shieldapp.shield;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rey.material.widget.Slider;
import com.shieldapp.library.AbstractRouting;
import com.shieldapp.library.PolyUtils;
import com.shieldapp.library.Route;
import com.shieldapp.library.RouteException;
import com.shieldapp.library.Routing;
import com.shieldapp.library.RoutingListener;


import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;

    private LatLng Source = null;
    private LatLng Destination = null;
    private RoutingListener routeListen;
    private List<Route> routes;
    private List<LatLng> polyline;
    private FusedLocationProviderClient mFusedLocationClient;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private final int REQUEST_LOCATION_PERMISSION = 1;
    private String[] perms = {android.Manifest.permission.ACCESS_FINE_LOCATION};
    private ArrayList<String> listItems=new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    private ListView routesListView;
    private float tolerance;
    private String TAG="Sheild App";
    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialising
        PlaceAutocompleteFragment sourceAutoComplete = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.source_autocomplete_fragment);
        mAuth = FirebaseAuth.getInstance();
        PlaceAutocompleteFragment destinationAutoComplete = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.destination_autocomplete_fragment);
        final TextView toleranceLabel = (TextView) findViewById(R.id.toleranceLabel);
        final Slider toleranceInput = (Slider) findViewById(R.id.toleranceInput);
        listItems = new ArrayList<String>();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.listview_item, listItems);
        routesListView = (ListView) findViewById(R.id.routeslistview);
        routesListView.setAdapter(adapter);
        sourceAutoComplete.setHint("Pickup");
        destinationAutoComplete.setHint("Drop off");
        requestLocationPermission();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mAuth = FirebaseAuth.getInstance();


        //Listeners
        toleranceInput.setOnPositionChangeListener(new Slider.OnPositionChangeListener() {
            @Override
            public void onPositionChanged(Slider view, boolean fromUser, float oldPos, float newPos, int oldValue, int newValue) {
                toleranceLabel.setText(newValue+ " Meters Tolerance");
                tolerance=(float)newValue;
            }
        });
        sourceAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
               Source = place.getLatLng();
               if(Destination!=null){
                   findRoutes();
               }
            }

            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        destinationAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Destination = place.getLatLng();
                if(Source!=null){
                    findRoutes();
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });



        routesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),"Route set via "+routes.get((int)id).getName(),Toast.LENGTH_SHORT).show();
                polyline = routes.get((int)id).getPoints();
                getLocation();
                getSupportActionBar().setTitle("Route Set Via "+routes.get((int)id).getName());
            }
        });

        routeListen = new RoutingListener() {
            @Override
            public void onRoutingFailure(RouteException e) {
                Log.e(TAG,e.getMessage());
            }

            @Override
            public void onRoutingStart() {

            }

            @Override
            public void onRoutingSuccess(List<Route> route, int shortestRouteIndex) {
                routes = route;
                listItems.clear();
                for(int i=0;i<route.size();i++){
                    listItems.add(route.get(i).getName());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onRoutingCancelled() {

            }
        };

        logEvent("HomeScreenActivated");

    }

    public void logEvent(String eventName){
        Bundle params = new Bundle();
        params.putString("userId", mAuth.getUid());
        mFirebaseAnalytics.logEvent(eventName, params);
    }

    public void findRoutes(){
        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(routeListen)
                .alternativeRoutes(true)
                .waypoints(Source, Destination)
                .build();
        routing.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        if(EasyPermissions.hasPermissions(this, perms)) {
        return;
        }
        else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    @SuppressLint("MissingPermission")
    public void getLocation(){
        Criteria criteria = new Criteria();
        LocationManager locationManager;
        String provider;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        provider = locationManager.getBestProvider(criteria, false);
        Location location = locationManager.getLastKnownLocation(provider);
        locationManager.requestLocationUpdates(provider, 400, 1, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.e(TAG,"ENTERED");
                        if(location!=null){
                            LatLng point = new LatLng(location.getLatitude(),location.getLongitude());
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference locationRef = database.getReference("/users/"+mAuth.getUid()+"/location");
                            locationRef.setValue(point);
                            if(polyline!=null)
                           if(PolyUtils.isLocationOnPath(point,polyline,true,tolerance)){
                               getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1e88e5")));
                           } else {
                               getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#FF0000")));
                               Toast.makeText(getApplicationContext(),"You have deviated from the route",Toast.LENGTH_SHORT).show();
                           }
                        }
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
                });

                // Initialize the location fields
        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
        }
    }

}

