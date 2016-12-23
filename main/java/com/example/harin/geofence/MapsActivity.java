package com.example.harin.geofence;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.view.View.OnClickListener;



public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GeoFence gf=null;
    private Button click;
    private boolean status=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        click=(Button) findViewById(R.id.button2);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // onclick event for the button to start or stop tracking
        click.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) { // on click on button home which changes the home locaiton

                if(status==true)
                {
                    click.setText("START TRACKING");
                    status=false;
                    gf.stop();
                    makeToast("Locaiton tracking stopped ..... ");
                }
                else
                {
                    click.setText("STOP TRACKING");
                    status=true;
                    gf.start();
                    makeToast("Locaiton Tracking started ..... ");
                }

            }
        });
        gf=new GeoFence(mMap,this,this); // calling the geoFence with the constructor.
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true); // location gps marking
        mMap.getUiSettings().setCompassEnabled(true); // enapling compass
        mMap.getUiSettings().setZoomControlsEnabled(true);// enabling the zoom in and out button on map.

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }
    // creating the permissions for Marshmellow and above.
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    // call the location permission request
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }
// over written method.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "Need Location permission to run this APP", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }
    //creating a maketoast method to display toast.
    public void makeToast(String out)
    {
        Toast.makeText(MapsActivity.this,out,
                Toast.LENGTH_LONG).show();
    }
    // when app is paused
    @Override
    protected void onPause() {
        try {
            if(status==true)
            super.onPause();
            gf.pause();
        }
        catch(Exception e)
        {
            makeToast(e.toString());
        }
    }
    // when app is resumed
    @Override
    public void onResume() {
        try{
            super.onResume();
            if(status==true)
            gf.resume();
        }
        catch(Exception e)
        {
            makeToast(e.toString());
        }
    }
    //when app is started
    @Override
    public void onStart() {
        try{
            super.onStart();
            if(status==true)
            gf.start();
        }
        catch(Exception e)
        {
            makeToast(e.toString());
        }
    }
// when app is stopped
    @Override
    public void onStop() {
        try{
            super.onStop();
            if(status==true)
            gf.stop();
        }
        catch(Exception e)
        {
            makeToast(e.toString());
        }
    }
    // marking on map for a particular map.
    public void addMarker(LatLng loc)
    {
        mMap.addMarker(new MarkerOptions()
                .position(loc)
                .title("Marker").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
    }
    //draws the polylines between two locations.
    public void drawPolyline(LatLng src,LatLng dest)
    {
        mMap.addPolyline(new PolylineOptions()
                .add(src, dest)
                .width(5)
                .color(Color.BLUE));
    }
}
