package com.example.haotian.tutorial32;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;

import static android.R.attr.permission;

public class MapsActivity extends FragmentActivity {
    public static final String TAG = "MapsActivity";
    public static final int THUMBNAIL = 1;

    private PhoneApi phoneApi;
    private PhoneService phoneService;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),"Locations.csv");


    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    Button picButton; //takes user to camera


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        //phoneApi = PhoneApi.getApi();
        //phoneService = phoneApi.getService();


        picButton = (Button) findViewById(R.id.photobutton);
        verifyCameraPermissions(MapsActivity.this);

        /*picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/


    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(20, 20)).title("Happycoding"));

    }


    public void launchCamera(View v){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
//        Toast.makeText(this, "Clicked on Button", Toast.LENGTH_LONG).show();

    }

    private Location getMostAccurateLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;
        Log.d("VIMIG", Boolean.toString(gps_enabled));
        Log.d("VIMIG", Boolean.toString(network_enabled));

        try {
            if (gps_enabled)
                gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } catch(SecurityException e){
            gps_loc = null;
        }

        try {
            if (network_enabled)
                net_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } catch(SecurityException e) {
            net_loc = null;
        }

        Log.d("VIMIG", gps_loc.toString());
        Log.d("VIMIG", net_loc.toString());

        if (gps_loc != null && net_loc != null) {

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

            // I used this just to get an idea (if both avail, its upto you which you want to take as I've taken location with more accuracy)

        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }
        return finalLoc;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Log.d("VIMIG", imageBitmap.toString());
            Location currentLoc = getMostAccurateLocation();
            Log.d("VIMIG", currentLoc.toString());
            addMarker(currentLoc, imageBitmap);
        }
    }

    private void addMarker(Location loc, Bitmap img){

        mMap.addMarker(new MarkerOptions()
            .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
            .title("Test")
            .icon(BitmapDescriptorFactory.fromBitmap(img))
            .snippet("Latitude=" + Double.toString(loc.getLatitude()) + "Longitude=" + Double.toString(loc.getLongitude())));
    }

    public static void verifyCameraPermissions(Activity activity) {
        String[] CAMERA_PERMISSIONS = {Manifest.permission.CAMERA};
        int permisison = ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA);

        if (permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    activity,
                    CAMERA_PERMISSIONS,
                    REQUEST_IMAGE_CAPTURE
            );
        }

    }
}
