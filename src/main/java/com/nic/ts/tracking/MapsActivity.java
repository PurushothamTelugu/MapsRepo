package com.nic.ts.tracking;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nic.ts.tracking.localdbmanager.DbManager;
import com.nic.ts.tracking.localdbmanager.SavngDataLocally;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap mMap;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    LocationManager locationManager;
    LocationListener locationListener;

    LatLng latLngCurrentLocation,latLng;
    Marker mCurrLocationMarker;
    Polyline polyline;

    String address,updatedAddress,formattedDate,lat_str,lng_str;
    Button mstop_btn;
    ImageView fetchData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maplocation);

        mstop_btn = findViewById(R.id.stop_btn);
        fetchData = findViewById(R.id.fetchdataimg);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.start_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MapsActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    getCurrentLocation();
                }
            }

        });

        mstop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAndRemoveTask();
            }
        });

        fetchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MapsActivity.this, SavngDataLocally.class));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Permission is denied!", Toast.LENGTH_SHORT).show();
            }
        }
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

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latLngCurrentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLngCurrentLocation);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                mCurrLocationMarker = mMap.addMarker(markerOptions);

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngCurrentLocation));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngCurrentLocation, 15));

                polyline = mMap.addPolyline(new PolylineOptions()
                        .add(latLngCurrentLocation,latLng)
                        .width(7)
                        .color(R.color.blue));

                try{
                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
                    updatedAddress = addresses.get(0).getAddressLine(0);

                    double updated_latitude = location.getLatitude();
                    double updated_longitude = location.getLongitude();
                    double time = location.getTime();
                    lat_str = Double.toString(updated_latitude);
                    lng_str = Double.toString(updated_longitude);
                    latLngCurrentLocation = new LatLng(updated_latitude, updated_longitude);
                    mMap.addMarker(new MarkerOptions().position(latLngCurrentLocation).title("It's Current Location"));
                    mMap.addMarker(new MarkerOptions().position(latLngCurrentLocation).title("Current Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));

                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => "+c.getTime());

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    formattedDate = df.format(c.getTime());
                    // formattedDate have current date/time
                    Toast.makeText(MapsActivity.this, formattedDate, Toast.LENGTH_SHORT).show();

                    if (latLngCurrentLocation.toString().length() > 0 ){
                        String result = new DbManager(MapsActivity.this).addRecord(lat_str,lng_str,updatedAddress,formattedDate);
                        Toast.makeText(MapsActivity.this,result,Toast.LENGTH_LONG).show();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

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

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,120000,0,locationListener);

        LocationServices.getFusedLocationProviderClient(MapsActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getApplicationContext())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocIndex = locationResult.getLocations().size() - 1;
                            double lati = locationResult.getLocations().get(latestlocIndex).getLatitude();
                            double longi = locationResult.getLocations().get(latestlocIndex).getLongitude();

                            String lat_str = Double.toString(lati);
                            String loni_str = Double.toString(longi);

                            Toast.makeText(MapsActivity.this,lat_str+" ,"+loni_str,Toast.LENGTH_SHORT).show();

                            latLng = new LatLng(lati, longi);
                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                            mCurrLocationMarker = mMap.addMarker(markerOptions);

                            try{
                                Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                                List<Address> addresses = geocoder.getFromLocation(lati,longi,1);
                                address = addresses.get(0).getAddressLine(0);

                                Calendar c = Calendar.getInstance();

                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String formattedDate = df.format(c.getTime());
                                // formattedDate have current date/time
                                Toast.makeText(MapsActivity.this, formattedDate, Toast.LENGTH_SHORT).show();

                                if (latLng.toString().length() > 0 ){
                                    String result = new DbManager(MapsActivity.this).addRecord(lat_str,loni_str,address,formattedDate);
                                    Log.d("result",result);
                                    Toast.makeText(MapsActivity.this,result,Toast.LENGTH_LONG).show();
                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        } else {

                        }
                    }
                }, Looper.getMainLooper());
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
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
}