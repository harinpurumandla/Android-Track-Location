package com.example.harin.geofence;

/**
 * Created by harin on 10/2/2016.
 */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;


public class GeoFence implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    GoogleMap map;
    LocationRequest locrequest;
    GoogleApiClient api;
    Location currentlocation;
    Context mapcontext;
    MapsActivity gf;
    Double distance =0d;
    //Creating a constructor.
    GeoFence(GoogleMap gmap, Context context,MapsActivity geo) {
        startLocationRequest();
        map = gmap;
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return ;
        }
        mapcontext=context;
        gf=geo;
        api = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        requestLocationupdate();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
// When locaiton is changed this method is triggered.
    @Override
    public void onLocationChanged(Location location) {
      if(location !=null) {
            gf.addMarker(new LatLng(location.getLatitude(),location.getLongitude()));
        }
        if(location !=null && currentlocation !=null) {
            gf.drawPolyline(new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude()), new LatLng(location.getLatitude(), location.getLongitude()));
            distance +=getDistance(location.getLatitude(),location.getLongitude(),currentlocation.getLatitude(),currentlocation.getLongitude());
        }

        currentlocation=location;

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
// This method creates the fused api connection to request location updates.
    protected void requestLocationupdate() {
        if (ActivityCompat.checkSelfPermission(mapcontext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mapcontext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        api.connect();
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                api, locrequest, this);
    }
    // connecting a api
    public void start()
    {
        api.connect();

    }
    // disconnect the api
    public void stop()
    {
        api.disconnect();
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        gf.makeToast("You Have walked "+numberFormat.format(distance)+" miles");
    }
    // remove location updates
    public void pause() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                api, this);
    }
    // restart the location updates
    public void resume()
    {
        if(api.isConnected()==true)
            requestLocationupdate();
        else
            api.connect();
    }
    //creates a 10 sec interval location updates with a fasted interval is 5 sec.
    protected void startLocationRequest() {
        locrequest = new LocationRequest();
        locrequest.setInterval(10000);
        locrequest.setFastestInterval(5000);
        locrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    //calculates the distance between two loctions.
    public double getDistance(double hlatitude, double hlongitude, double latitude, double longitude) {
        double earthRadius = 3958.75; // in miles, change to 6371 for kilometer output
        double dLat = Math.toRadians(latitude-hlatitude);
        double dLng = Math.toRadians(longitude - hlongitude);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(hlatitude)) * Math.cos(Math.toRadians(latitude));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return  earthRadius * c; // output distance, in MILES
    }
}
