package com.example.dinabenayad_cherif.finalproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.Date;

public class MainActivity extends Activity implements SensorEventListener, LocationListener {

    TextView accelerometertxtView;
    TextView rotationtxtView;
    TextView gpsCoordinatesView;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_COARSE_LOCATION = 2;
    static final int REQUEST_FINE_LOCATION = 3;
    static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    Location lastLocation = null;


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;
    private Sensor mRotation;
    private Sensor mLineAcc;
    private Sensor mGravity;

    private String[] accelerometerStr = new String[3];
    private String[] magnetometerStr = new String[3];
    private String[] gyroscopeStr = new String[3];
    private String[] rotationStr = new String[3];
    private String[] linaccStr = new String[3];
    private String[] gravityStr = new String[3];

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public boolean isEmpty(String[] arr) {
        boolean empty = true;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null) {
                empty = false;
                break;
            }
        }
        return empty;
    }

    public boolean allData() {
        if (isEmpty(accelerometerStr) || isEmpty(magnetometerStr) || isEmpty(gyroscopeStr) || isEmpty(rotationStr) || isEmpty(linaccStr) || isEmpty(gravityStr)) {
            return false;
        } else {
            return true;
        }
    }


    @Override
    public final void onSensorChanged(SensorEvent event) {
        Log.d("Changing in sensor data", "sensor data changed");
        if (allData()) {
            //this.writer = new CSVWriter(new FileWriter(file, true), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            Date date = new Date();
            String[] row = {(new Timestamp(date.getTime())).toString(), accelerometerStr[0], accelerometerStr[1], accelerometerStr[2], magnetometerStr[0], magnetometerStr[1], magnetometerStr[2],
                    gyroscopeStr[0], gyroscopeStr[1], gyroscopeStr[2], rotationStr[0], rotationStr[1], rotationStr[2], linaccStr[0],
                    linaccStr[1], linaccStr[2], gravityStr[0], gravityStr[1], gravityStr[2]};
            Log.d("Printing out row values", accelerometerStr[0]);
            //this.writer.writeNext(row);
            accelerometerStr = new String[3];
            magnetometerStr = new String[3];
            gyroscopeStr = new String[3];
            rotationStr = new String[3];
            linaccStr = new String[3];
            gravityStr = new String[3];
            //writer.close();
        }

        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerStr[0] = "" + event.values[0] + "";
            accelerometerStr[1] = "" + event.values[1] + "";
            accelerometerStr[2] = "" + event.values[2] + "";
            accelerometertxtView = new TextView(this);
            accelerometertxtView =(TextView)findViewById(R.id.textView2);
            accelerometertxtView.setText(accelerometerStr[0] + " " + accelerometerStr[1] + " " + accelerometerStr[2]);

        } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            magnetometerStr[0] = "" + event.values[0] + "";
            magnetometerStr[1] = "" + event.values[1] + "";
            magnetometerStr[2] = "" + event.values[2] + "";

        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeStr[0] = "" + event.values[0] + "";
            gyroscopeStr[1] = "" + event.values[1] + "";
            gyroscopeStr[2] = "" + event.values[2] + "";

        } else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            rotationStr[0] = "" + event.values[0] + "";
            rotationStr[1] = "" + event.values[1] + "";
            rotationStr[2] = "" + event.values[2] + "";
            rotationtxtView = new TextView(this);
            rotationtxtView =(TextView)findViewById(R.id.textView5);
            rotationtxtView.setText(rotationStr[0] + " " + rotationStr[1] + " " + rotationStr[2]);

        } else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            linaccStr[0] = "" + event.values[0] + "";
            linaccStr[1] = "" + event.values[1] + "";
            linaccStr[2] = "" + event.values[2] + "";

        } else {
            gravityStr[0] = "" + event.values[0] + "";
            gravityStr[1] = "" + event.values[1] + "";
            gravityStr[2] = "" + event.values[2] + "";
        }

        Location location = getMostAccurateLocation();
        if (lastLocation != null) {
            if (location.getLongitude() != lastLocation.getLongitude() || lastLocation.getLatitude() != location.getLongitude()) {
                lastLocation = location;
                gpsCoordinatesView = new TextView(this);
                gpsCoordinatesView = (TextView) findViewById(R.id.textView7);
                gpsCoordinatesView.setText(location.getLatitude() + " " + location.getLongitude());
            }
        }
        else {
            lastLocation = location;
        }



        // The light sensor returns a single value.
        // Many sensors return 3 values, one for each axis.
        //float lux = event.values[0];
        // Do something with this sensor value.
    }

    public void startCollectingData(View v) {
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLineAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void stopCollectingData(View v) {
        mSensorManager.unregisterListener(this);
    }


    public void trainDecisionTree(View v){
        Intent intent = new Intent(this, BuildClassifier.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyLocationPermissions(MainActivity.this);
        verifyStoragePermissions(MainActivity.this);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mLineAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);


    }



    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        double latitude=location.getLatitude();
        double longitude=location.getLongitude();
        String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
    }

    public void onProviderEnabled(String provider) {}
    public void onProviderDisabled(String provider) {}
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    private Location getMostAccurateLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //LocationListener locationListener = new LocationListener();
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                double latitude=location.getLatitude();
                double longitude=location.getLongitude();
                String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        //locationManager.requestLocationUpdates();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0.0f, locationListener);

        boolean gps_enabled = false;
        boolean network_enabled = false;

        gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        Location net_loc = null, gps_loc = null, finalLoc = null;

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

        if (gps_loc != null && net_loc != null) {

            //smaller the number more accurate result will
            if (gps_loc.getAccuracy() > net_loc.getAccuracy())
                finalLoc = net_loc;
            else
                finalLoc = gps_loc;

        } else {

            if (gps_loc != null) {
                finalLoc = gps_loc;
            } else if (net_loc != null) {
                finalLoc = net_loc;
            }
        }
        return finalLoc;
    }

    public static void verifyLocationPermissions(Activity activity){
        String[] LOCATION_COARSE_PERMISSIONS = {Manifest.permission.ACCESS_COARSE_LOCATION};
        String[] LOCATION_FINE_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION};
        int coarse_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int fine_permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);


        if (fine_permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    LOCATION_FINE_PERMISSIONS,
                    REQUEST_FINE_LOCATION
            );
        }
    }

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
