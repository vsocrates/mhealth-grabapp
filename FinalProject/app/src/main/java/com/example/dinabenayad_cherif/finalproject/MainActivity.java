package com.example.dinabenayad_cherif.finalproject;

import android.Manifest;

import java.io.InputStream;
import java.util.Set;
import android.Manifest.permission;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import com.google.android.gms.common.api.GoogleApiClient;
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
import android.os.AsyncTask;
import com.google.android.gms.awareness.*;
import com.google.android.gms.awareness.state.*;
//import android.os.Build;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader.ArffReader;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

import static android.os.SystemClock.elapsedRealtime;

//import android.os.Build;
//import com.meapsoft.FFT;
//import weka.core.DenseInstance;
//import weka.core.;

public class MainActivity extends Activity implements SensorEventListener, LocationListener {

    TextView rotationtxtView;
    TextView gpsCoordinatesView;

    private Set<BluetoothDevice>pairedDevices;

    private BluetoothAdapter mBluetoothAdapter = null;

    private static final int mFeatLen = Globals.ACCELEROMETER_BLOCK_CAPACITY + 2;

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_COARSE_LOCATION = 2;
    static final int REQUEST_FINE_LOCATION = 3;
    static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    Location lastLocation = null;


    private OnSensorChangedTask mAsyncTask;

    private int mServiceTaskType;
    private File mFeatureFile;

    private int REQUEST_ENABLE_BT = 1;


    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private Sensor mGyroscope;
    private Sensor mRotation;
    private Sensor mLineAcc;
    private Sensor mGravity;

    private GoogleApiClient mGoogleApiClient;

    private Instances mDataset;
    private Attribute mClassAttribute;


    //private String mLabel;

    private static ArrayBlockingQueue<Double> mAccBuffer;

    private String[] accelerometerStr = new String[3];
    private String[] magnetometerStr = new String[3];
    private String[] gyroscopeStr = new String[3];
    private String[] rotationStr = new String[3];
    private String[] linaccStr = new String[3];
    private String[] gravityStr = new String[3];

    Classifier mClassifier = null;
    final List<String> classes = new ArrayList<String>() {
        {
            add("Standing");
            add("Walking");
            add("Running");
            add("Others");
        }
    };

    File path = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS);

    long startCollectingTime;
    long stopCollectingTime;

    private TextView walkingSummaryText;
    private TextView runningSummaryText;
    private TextView bikingSummaryText;
    private TextView drivingSummaryText;

    String summaryFileName = "SummaryStats.csv";
    String unlabeledDataFileName = "UnlabeledData.arff";
    String dtFName = "DecisionTreeModel.model";
    String naiveBayesFName = "NaiveBayesModel.model";


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






//    @Override
//    public final void onSensorChanged(SensorEvent event) {
//        Log.d("Changing in sensor data", "sensor data changed");
//        if (allData()) {
//            //this.writer = new CSVWriter(new FileWriter(file, true), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
//            Date date = new Date();
//            String[] row = {(new Timestamp(date.getTime())).toString(), accelerometerStr[0], accelerometerStr[1], accelerometerStr[2], magnetometerStr[0], magnetometerStr[1], magnetometerStr[2],
//                    gyroscopeStr[0], gyroscopeStr[1], gyroscopeStr[2], rotationStr[0], rotationStr[1], rotationStr[2], linaccStr[0],
//                    linaccStr[1], linaccStr[2], gravityStr[0], gravityStr[1], gravityStr[2]};
//            Log.d("Printing out row values", accelerometerStr[0]);
//            //this.writer.writeNext(row);
//            accelerometerStr = new String[3];
//            magnetometerStr = new String[3];
//            gyroscopeStr = new String[3];
//            rotationStr = new String[3];
//            linaccStr = new String[3];
//            gravityStr = new String[3];
//            //writer.close();
//        }
//
//        Sensor sensor = event.sensor;
//        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
//            accelerometerStr[0] = "" + event.values[0] + "";
//            accelerometerStr[1] = "" + event.values[1] + "";
//            accelerometerStr[2] = "" + event.values[2] + "";
//            accelerometertxtView = new TextView(this);
//            accelerometertxtView = (TextView) findViewById(R.id.textView2);
//            accelerometertxtView.setText(accelerometerStr[0] + " " + accelerometerStr[1] + " " + accelerometerStr[2]);
//
//        } else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
//            magnetometerStr[0] = "" + event.values[0] + "";
//            magnetometerStr[1] = "" + event.values[1] + "";
//            magnetometerStr[2] = "" + event.values[2] + "";
//
//        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
//            gyroscopeStr[0] = "" + event.values[0] + "";
//            gyroscopeStr[1] = "" + event.values[1] + "";
//            gyroscopeStr[2] = "" + event.values[2] + "";
//
//        } else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
//            rotationStr[0] = "" + event.values[0] + "";
//            rotationStr[1] = "" + event.values[1] + "";
//            rotationStr[2] = "" + event.values[2] + "";
//            rotationtxtView = new TextView(this);
//            rotationtxtView = (TextView) findViewById(R.id.textView5);
//            rotationtxtView.setText(rotationStr[0] + " " + rotationStr[1] + " " + rotationStr[2]);
//
//        } else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
//            linaccStr[0] = "" + event.values[0] + "";
//            linaccStr[1] = "" + event.values[1] + "";
//            linaccStr[2] = "" + event.values[2] + "";
//
//        } else {
//            gravityStr[0] = "" + event.values[0] + "";
//            gravityStr[1] = "" + event.values[1] + "";
//            gravityStr[2] = "" + event.values[2] + "";
//        }
//
//        Location location = getMostAccurateLocation();
//        if (lastLocation != null) {
//            if (location.getLongitude() != lastLocation.getLongitude() || lastLocation.getLatitude() != location.getLongitude()) {
//                lastLocation = location;
//                gpsCoordinatesView = new TextView(this);
//                gpsCoordinatesView = (TextView) findViewById(R.id.textView7);
//                gpsCoordinatesView.setText(location.getLatitude() + " " + location.getLongitude());
//            }
//        } else {
//            lastLocation = location;
//        }
//
//
//        // The light sensor returns a single value.
//        // Many sensors return 3 values, one for each axis.
//        //float lux = event.values[0];
//        // Do something with this sensor value.
//    }

    public void startCollectingData(View v) {
        Toast.makeText(this, "Started Collecting Data!", Toast.LENGTH_SHORT).show();

        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mLineAcc, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);

        mAsyncTask = new OnSensorChangedTask();
        mAsyncTask.execute();


        startCollectingTime = elapsedRealtime();

    }

    public void stopCollectingData(View v) {
        Toast.makeText(this, "Stopped Collecting Data!", Toast.LENGTH_SHORT).show();
        stopCollectingTime = elapsedRealtime();
        mAsyncTask.cancel(true);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mSensorManager.unregisterListener(this);
        //stopCollectingTime = elapsedRealtime();

    }


    public void trainDecisionTree(View v) {
        Intent intent = new Intent(this, BuildClassifier.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyLocationPermissions(MainActivity.this);
        verifyStoragePermissions(MainActivity.this);

        walkingSummaryText = (TextView) findViewById(R.id.walkingsummarytextview);
        runningSummaryText = (TextView) findViewById(R.id.runningsummaryview);
        bikingSummaryText = (TextView) findViewById(R.id.bikingsummaryview);
        drivingSummaryText = (TextView) findViewById(R.id.drivingsummaryview);

//
//        Intent intent = new Intent(this, RecommendationsActivity.class);
//        startActivity(intent);

        double walkingSeconds =0;
        double bikingSeconds =0;
        double runningSeconds =0;
        double drivingSeconds =0;



        try {
            path.mkdirs();
            File summaryfile = new File(path, summaryFileName);
            CSVReader reader = new CSVReader(new FileReader(summaryfile));
            HashMap<String, Double> classCounts = new HashMap<>();
            String [] nextLine;


            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                //we want the classifications and then add up all the time, placed into the hashmap
                double currentVal = 0d;

                if(classCounts.containsKey(nextLine[1])){
                    currentVal = classCounts.get(nextLine[1]);
                    currentVal += Double.parseDouble(nextLine[0]);
                    classCounts.put(nextLine[1], currentVal);
                } else {
                    classCounts.put(nextLine[1], Double.parseDouble(nextLine[0]));
                }
            }

            Log.d("VIMIG", classCounts.toString());
            if(classCounts.containsKey("Walking")) {
                walkingSeconds = classCounts.get("Walking");
                walkingSummaryText.setText(convertSecondsToDisplay(classCounts.get("Walking")));
            }
            else {
                walkingSeconds = 0;
                walkingSummaryText.setText(String.format("%d days", 0));
            }

            if(classCounts.containsKey("Running")) {
                runningSeconds = classCounts.get("Running");
                runningSummaryText.setText(convertSecondsToDisplay(classCounts.get("Running")));
            }
            else {
                runningSeconds = 0;
                runningSummaryText.setText(String.format("%d days", 0));
            }

            if(classCounts.containsKey("Biking")) {
                bikingSeconds = classCounts.get("Biking");
                bikingSummaryText.setText(convertSecondsToDisplay(classCounts.get("Biking")));
            }
            else {
                bikingSeconds = 0;
                bikingSummaryText.setText(String.format("%d days", 0));
            }

            if(classCounts.containsKey("Driving")) {
                drivingSeconds = classCounts.get("Driving");
                drivingSummaryText.setText(convertSecondsToDisplay(classCounts.get("Driving")));
            }
            else {
                drivingSeconds = 0;
                drivingSummaryText.setText(String.format("%d days", 0));
            }



        } catch(IOException e){
            Log.d("VIMIG", "io");
            walkingSummaryText.setText("No Data Available!");
            runningSummaryText.setText("No Data Available!");
            bikingSummaryText.setText("No Data Available!");
            drivingSummaryText.setText("No Data Available!");
            e.printStackTrace();
        }

        Intent intent = new Intent(this, RecommendationsActivity.class);
        intent.putExtra("walking", walkingSeconds);
        intent.putExtra("biking", bikingSeconds);
        intent.putExtra("driving", drivingSeconds);
        intent.putExtra("running", runningSeconds);
        startActivity(intent);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        mLineAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mAccBuffer = new ArrayBlockingQueue<Double>(
                Globals.ACCELEROMETER_BUFFER_CAPACITY);

        path.mkdirs();
        mFeatureFile = new File(path, Globals.FEATURE_FILE_NAME);

        //ArrayList<Attribute> allAttr = new ArrayList<Attribute>();
        FastVector allAttr = new FastVector();

        // Adding FFT coefficient attributes
        DecimalFormat df = new DecimalFormat("0000");

        for (int i = 0; i < Globals.ACCELEROMETER_BLOCK_CAPACITY; i++) {
            allAttr.addElement(new Attribute(Globals.FEAT_FFT_COEF_LABEL + df.format(i)));
            //allAttr.add(new Attribute(Globals.FEAT_FFT_COEF_LABEL + df.format(i)));
        }
        // Adding the max feature
        allAttr.addElement(new Attribute(Globals.FEAT_MAX_LABEL));

        // Declare a nominal attribute along with its candidate values
        FastVector labelItems = new FastVector();
        labelItems.addElement(Globals.CLASS_LABEL_STANDING);
        labelItems.addElement(Globals.CLASS_LABEL_WALKING);
        labelItems.addElement(Globals.CLASS_LABEL_RUNNING);
//        labelItems.addElement("Biking");
//        labelItems.addElement("Driving");
        labelItems.addElement(Globals.CLASS_LABEL_OTHER);
        mClassAttribute = new Attribute(Globals.CLASS_LABEL_KEY, labelItems);
        allAttr.addElement(mClassAttribute);
        mDataset = new Instances(Globals.FEAT_SET_NAME, allAttr, Globals.FEATURE_SET_CAPACITY);

//        if (!mBluetoothAdapter.isEnabled()) {
//            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
//        }
//        String deviceName = "edison";
//        BluetoothDevice result = null;
//        Set<BluetoothDevice> devices = mBluetoothAdapter.getBondedDevices();
//        if (devices != null) {
//            for (BluetoothDevice device : devices) {
//                if (deviceName.equals(device.getName())) {
//                    result = device;
//                    break;
//                }
//            }
//        }
//
//        BluetoothSocket socket;
//        Log.d("device is", result.getAddress());
//        try {
//            // Use the UUID of the device that discovered // TODO Maybe need extra device object
//            if (result != null) {
//                Log.i("checking bluetooth", "Device Name: " + result.getName());
//                Log.i("checking bluetooth", "Device UUID: " + result.getUuids()[0].getUuid());
//                socket = result.createRfcommSocketToServiceRecord(result.getUuids()[0].getUuid());
//                socket.connect();
//                InputStream in = socket.getInputStream();
//                Log.d("getting input stream", in.toString());
//
//
//
//            }
//            else Log.d("checking bluetooth", "Device is null.");
//        }
//
//        catch (NullPointerException e) {
//            Log.d ("null pointer", "cannot open socket");
//        }
//        catch (IOException e) { }
//



    }

    public int[] getConditions() {
        int[] i = new int[1];
        i[0] = 5;
        return i;

    }

    private String convertSecondsToDisplay(double seconds) {

        long secondsRounded = Math.round(seconds);
        Log.d("VIMIG", Long.toString(secondsRounded));
        long days = TimeUnit.SECONDS
                .toDays(secondsRounded);
        Log.d("VIMIG", Long.toString(days));
        secondsRounded -= TimeUnit.DAYS.toSeconds(days);

        long hours = TimeUnit.SECONDS
                .toHours(secondsRounded);
        secondsRounded -= TimeUnit.HOURS.toSeconds(hours);

        long minutes = TimeUnit.SECONDS
                .toMinutes(secondsRounded);
        secondsRounded -= TimeUnit.MINUTES.toSeconds(minutes);

        String displayTime;
        if(days == 0)
            displayTime = String.format("%d hrs, %d min.", hours, minutes);
        else if (hours == 0)
            displayTime = String.format("%d min.", minutes);
        else
            displayTime = String.format("%d days, %d hrs, %d min.", days, hours, minutes);

        return displayTime;
    }

    public void onLocationChanged(Location location) {
        // Called when a new location is found by the network location provider.
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
    }

    public void onProviderEnabled(String provider) {
    }

    public void onProviderDisabled(String provider) {
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

    private Location getMostAccurateLocation() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //LocationListener locationListener = new LocationListener();
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String msg = "New Latitude: " + latitude + "New Longitude: " + longitude;
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        //locationManager.requestLocationUpdates();
        if (ActivityCompat.checkSelfPermission(this, permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        }
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

    public void classifyCurrentSessionClick(View v) {
        try {
            path.mkdirs();
            //we want to pick one of these
            //start with naive Bayes for now
            File modelFile = new File(path,naiveBayesFName);
            FileInputStream inStream = new FileInputStream(modelFile);
            mClassifier = (Classifier) weka.core.SerializationHelper.read(inStream);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // Weka "catch'em all!"
            e.printStackTrace();
        }
        Toast.makeText(this, "Model loaded.", Toast.LENGTH_SHORT).show();


        try {
            path.mkdirs();
            ArffReader arff;
            File unlabeledData = new File(path, unlabeledDataFileName);
            BufferedReader reader = new BufferedReader(new FileReader(unlabeledData));
            arff = new ArffReader(reader, 1000);

            Instances data = arff.getStructure();
            data.setClassIndex(data.numAttributes() - 1);

            Instance inst;
            ArrayList<Integer> classifiedVals = new ArrayList<Integer>();
//            inst = arff.readInstance(data);
//            Log.d("VIMIG", "instance" + inst.toString());
            while((inst = arff.readInstance(data)) != null) {
                //convert from double to int, double values are classifications
                classifiedVals.add(new Double(mClassifier.classifyInstance(inst)).intValue());

                //don't need data right now
                //data.add(inst);
            }

            if(classifiedVals.size() > 0){
                int summaryClassification = mode(classifiedVals);
                String classificationLabel = classes.get(summaryClassification);

                path.mkdirs();
                File summaryData = new File(path, summaryFileName);
                if (!summaryData.exists()) {
                    summaryData.createNewFile();
                }
                CSVWriter writer = new CSVWriter(new FileWriter(summaryData, true));
                long timeElapsed = stopCollectingTime - startCollectingTime;
                double timeDouble = timeElapsed / 1000.0;
                startCollectingTime = stopCollectingTime = 0;
                String records = Double.toString(timeDouble)+","+classificationLabel;
                String[] record = records.split(",");

                //write to file
                writer.writeNext(record);
                writer.close();

                //delete unlabeled data file after classification
                unlabeledData.delete();
                Toast.makeText(this, "Classified Data!", Toast.LENGTH_SHORT).show();
            }


        } catch (IOException e) {
            Log.d("VIMIG", "caught2");
            Toast.makeText(this, "Couldn't find Unlabeled Data File :(", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("VIMIG", "caught1");
            Toast.makeText(this, "Couldn't classify for some reason :(", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    public static int mode(ArrayList<Integer> arr)
    {
        HashMap arrayVals = new HashMap();
        int maxOccurences = 1;
        int mode = arr.get(0);

        for(int i = 0; i<arr.size(); i++)
        {
            int currentIndexVal = arr.get(i);
            if(arrayVals.containsKey(currentIndexVal)){
                int currentOccurencesNum = (Integer) arrayVals.get(currentIndexVal);
                currentOccurencesNum++;
                arrayVals.put(currentIndexVal, currentOccurencesNum );
                if(currentOccurencesNum >= maxOccurences)
                {
                    mode = currentIndexVal;
                    maxOccurences = currentOccurencesNum;
                }
            }
            else{
                arrayVals.put(arr.get(i), 1);
            }
        }


        return mode;
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

    private class OnSensorChangedTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... arg0) {

            Instance inst = new Instance(mFeatLen);
            inst.setDataset(mDataset);
            int blockSize = 0;
            FFT fft = new FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY);
            double[] accBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
            double[] re = accBlock;
            double[] im = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];

            double max = Double.MIN_VALUE;

            while (true) {
                try {
                    // need to check if the AsyncTask is cancelled or not in the while loop
                    if (isCancelled () == true)
                    {
                        return null;
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().doubleValue();

                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        Log.d("DINA", "reached 64 in size");
                        blockSize = 0;

                        // time = System.currentTimeMillis();
                        max = .0;
                        for (double val : accBlock) {
                            if (max < val) {
                                max = val;
                            }
                        }

                        fft.fft(re, im);

                        for (int i = 0; i < re.length; i++) {
                            double mag = Math.sqrt(re[i] * re[i] + im[i]
                                    * im[i]);
                            inst.setValue(i, mag);
                            im[i] = .0; // Clear the field
                        }

                        // Append max after frequency component
                        inst.setValue(Globals.ACCELEROMETER_BLOCK_CAPACITY, max);
                        inst.setValue(mClassAttribute, Instance.missingValue());
                        mDataset.add(inst);
                        Log.i("new instance", mDataset.numInstances() + "");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {

            Log.e("123", mDataset.numInstances()+"");

            if (mServiceTaskType == Globals.SERVICE_TASK_TYPE_CLASSIFY) {
                super.onCancelled();
                return;
            }
            Log.i("in the loop","still in the loop cancelled");
            String toastDisp;

            if (mFeatureFile.exists()) {

                // merge existing and delete the old dataset
                DataSource source;
                try {
                    // Create a datasource from mFeatureFile where
                    // mFeatureFile = new File(getExternalFilesDir(null),
                    // "features.arff");
                    source = new DataSource(new FileInputStream(mFeatureFile));
                    // Read the dataset set out of this datasource
                    Instances oldDataset = source.getDataSet();
                    oldDataset.setClassIndex(mDataset.numAttributes() - 1);
                    // Sanity checking if the dataset format matches.
                    if (!oldDataset.equalHeaders(mDataset)) {
                        // Log.d(Globals.TAG,
                        // oldDataset.equalHeadersMsg(mDataset));
                        throw new Exception(
                                "The two datasets have different headers:\n");
                    }

                    // Move all items over manually
                    for (int i = 0; i < mDataset.numInstances(); i++) {
                        oldDataset.add(mDataset.instance(i));
                    }

                    mDataset = oldDataset;
                    // Delete the existing old file.
                    mFeatureFile.delete();
                    Log.i("delete","delete the file");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //toastDisp = getString(R.string.ui_sensor_service_toast_success_file_updated);

            } else {
               // toastDisp = getString(R.string.ui_sensor_service_toast_success_file_created)   ;
            }
            Log.i("save","create saver here");
            // create new Arff file
            ArffSaver saver = new ArffSaver();
            // Set the data source of the file content
            saver.setInstances(mDataset);
            Log.e("1234", mDataset.numInstances()+"");
            try {
                // Set the destination of the file.
                // mFeatureFile = new File(getExternalFilesDir(null),
                // "features.arff");
                saver.setFile(mFeatureFile);
                // Write into the file
                saver.writeBatch();
                Log.i("batch","write batch here");
                //Toast.makeText(getApplicationContext(), toastDisp,
                    //    Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                //toastDisp = getString(R.string.ui_sensor_service_toast_error_file_saving_failed);
                e.printStackTrace();
            }

            Log.i("toast","toast here");
            super.onCancelled();
        }

    }

    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            accelerometerStr[0] = "" + event.values[0] + "";
            accelerometerStr[1] = "" + event.values[1] + "";
            accelerometerStr[2] = "" + event.values[2] + "";

            //accelerometertxtView.setText(accelerometerStr[0] + " " + accelerometerStr[1] + " " + accelerometerStr[2]);


            double m = Math.sqrt(event.values[0] * event.values[0]
                    + event.values[1] * event.values[1] + event.values[2]
                    * event.values[2]);

            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.

            try {
                mAccBuffer.add(new Double(m));
            } catch (IllegalStateException e) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                ArrayBlockingQueue<Double> newBuf = new ArrayBlockingQueue<Double>(
                        mAccBuffer.size() * 2);

                mAccBuffer.drainTo(newBuf);
                mAccBuffer = newBuf;
                mAccBuffer.add(new Double(m));
            }
        }
    }
}
