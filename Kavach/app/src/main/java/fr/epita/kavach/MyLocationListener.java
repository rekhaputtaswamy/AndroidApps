package fr.epita.kavach;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {

        // Retrieving Latitude
        location.getLatitude();
        // Retrieving getLongitude
        location.getLongitude();

        String url = "http://maps.google.com/staticmap?center="
                + location.getLatitude() + "," + location.getLongitude()
                + "&zoom=14&size=512x512&maptype=mobile/&markers="
                + location.getLatitude() + "," + location.getLongitude();


        Log.i("Current Location:", url);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i("GPS", "GPS Disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i("GPS", "GPS Enabled");

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

}
