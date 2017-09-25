package edu.cwru.sail.imagelearning;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.VelocityTracker;
import android.view.View;

import com.opencsv.CSVWriter;
import com.squareup.picasso.Picasso;

import static android.R.attr.permission;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author vsocrates dinabenayadcherif
 *
 * Code adapted from ANDROID TOUCH SCREEN Tutorial
 *
 * https://inducesmile.com/android/android-touch-screen-example-tutorial/
 *
 *
 */

public class MainActivity extends Activity implements SensorEventListener {

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

//	private ImageView iv;
	private DrawingImageView overlayView;

	private File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"SensorData.csv");
	private CSVWriter writer;

	private final String imgDir = Environment.getExternalStorageDirectory().toString() + "/DCIM/";

	//Make sure that this part is dynamically defined by the Browse Folder and
	// your CSV file name is "THE_SAME_FOLDER_NAME.csv"
	private VelocityTracker mVelocityTracker = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
		mLineAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		mGravity = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mLineAcc, SensorManager.SENSOR_DELAY_NORMAL);
		mSensorManager.registerListener(this, mGravity, SensorManager.SENSOR_DELAY_NORMAL);




		Log.d("here we go: ", imgDir);

//		iv = (ImageView) findViewById(R.id.imageView);
		overlayView = (DrawingImageView) findViewById(R.id.imageViewOverlay);

//		File img = new File(imgDir+File.separator+"smile1.jpg");
//
//	if (img.exists()) {
//			//Loading Image from URL
//			Picasso.with(this)
//					.load("https://www.simplifiedcoding.net/wp-content/uploads/2015/10/advertise.png")
//					//.load(img)
//					//.placeholder(R.drawable.placeholder)   // optional
//					//.error(R.drawable.error)      // optional
//					.resize(240, 320)                        // optional
//					.into(overlayView);
//
//		}
		verifyStoragePermissions(MainActivity.this);
		verifyCameraPermissions(MainActivity.this);
//		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv.getLayoutParams();
//		overlayView.setLayoutParams(layoutParams);
//		overlayView.setAlpha(0f);


		Log.d("about to get into it", "it");


	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	public boolean isEmpty(String[] arr) {
		boolean empty = true;
		for (int i=0; i<arr.length; i++) {
			if (arr[i] != null) {
				empty = false;
				break;
			}
		}
		return empty;
	}

	public boolean allData(){
		if (isEmpty(accelerometerStr) || isEmpty(magnetometerStr) || isEmpty(gyroscopeStr) || isEmpty(rotationStr) || isEmpty(linaccStr) ||isEmpty(gravityStr)) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public final void onSensorChanged(SensorEvent event) {
		Log.d("Changing in sensor data", "sensor data changed");
		if (allData()){
			try {
				this.writer = new CSVWriter(new FileWriter(file, true), ',', CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
				String[] row = {accelerometerStr[0], accelerometerStr[1], accelerometerStr[2], magnetometerStr[0], magnetometerStr[1], magnetometerStr[2],
						gyroscopeStr[0], gyroscopeStr[1], gyroscopeStr[2], rotationStr[0], rotationStr[1], rotationStr[2], linaccStr[0],
						linaccStr[1], linaccStr[2], gravityStr[0], gravityStr[1], gravityStr[2]};
				Log.d("Printing out row values", accelerometerStr[0]);
				if (writer == null) {
					Log.d("Writer is null", "Writer is null");
				}
				this.writer.writeNext(row);
				accelerometerStr = new String[3];
				magnetometerStr = new String[3];
				gyroscopeStr = new String[3];
				rotationStr = new String[3];
				linaccStr = new String[3];
				gravityStr = new String[3];
				String[] arr = {"hello!"};
				writer.writeNext(arr);
				writer.close();
			}
			catch (IOException ioe) {
				Log.e("Catching exception", "I got an error", ioe);

			}
		}

		Sensor sensor = event.sensor;
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			accelerometerStr[0] = "" + event.values[0] + "";
			accelerometerStr[1] = "" + event.values[1] + "";
			accelerometerStr[2] = "" + event.values[2] + "";

		}
		else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			magnetometerStr[0] = "" + event.values[0] + "";
			magnetometerStr[1] = "" + event.values[1] + "";
			magnetometerStr[2] = "" + event.values[2] + "";

		}
		else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
			gyroscopeStr[0] = "" + event.values[0] + "";
			gyroscopeStr[1] = "" + event.values[1] + "";
			gyroscopeStr[2] = "" + event.values[2] + "";

		}
		else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
			rotationStr[0] = "" + event.values[0] + "";
			rotationStr[1] = "" + event.values[1] + "";
			rotationStr[2] = "" + event.values[2] + "";

		}
		else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
			linaccStr[0] = "" + event.values[0] + "";
			linaccStr[1] = "" + event.values[1] + "";
			linaccStr[2] = "" + event.values[2] + "";

		}
		else {
			gravityStr[0] = "" + event.values[0] + "";
			gravityStr[1] = "" + event.values[1] + "";
			gravityStr[2] = "" + event.values[2] + "";
		}
		// The light sensor returns a single value.
		// Many sensors return 3 values, one for each axis.
		//float lux = event.values[0];
		// Do something with this sensor value.
	}


	public boolean browseFolder(MenuItem item) {
		Log.d("here we go:", "Clicked on button");
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		final int ACTIVITY_SELECT_IMAGE = 1234;
		//startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
		i.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
		Log.d("CheckFile", "imgDir");
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("Hello its me", Integer.toString(requestCode));
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1234) {

				//Get ImageURi and load with help of picasso
				//Uri selectedImageURI = data.getData()
				//iv.setImageURI(data.getData());
				Picasso.with(this).load(data.getData()).resize(1000, 1000)
						.into(overlayView);

		}	else if (requestCode == REQUEST_IMAGE_CAPTURE){
				Bundle extras = data.getExtras();
				Bitmap imageBitmap = (Bitmap) extras.get("data");
//				Picasso.with(this).load(imageBitmap).resize(1000,1000).into(overlayView);
				Bitmap resized = Bitmap.createScaledBitmap(imageBitmap, 1000, 1000, true);
				overlayView.setImageBitmap(resized);
			}

		}
	}



	public void takePicture(View view) {
		dispatchTakePictureIntent();
	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
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

	public static void verifyStoragePermissions(Activity activity) {
		String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
		int REQUEST_EXTERNAL_STORAGE = 1;
		int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
					activity,
					PERMISSIONS_STORAGE,
					REQUEST_EXTERNAL_STORAGE
			);
		}
	}

	static final int REQUEST_IMAGE_CAPTURE = 1;

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
