package edu.cwru.sail.imagelearning;

import android.Manifest;
import android.app.ActionBar;
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
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.VelocityTracker;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.squareup.picasso.Picasso;

import java.io.File;

/**
 *
 * @author vsocrates dinabenayadcherif
 *
 * Code adapted from Fingerpaint Android SDK Samples
 *
 * https://github.com/Miserlou/Android-SDK-Samples/blob/master/ApiDemos/src/com/example/android/apis/graphics/FingerPaint.java
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

	private ImageView iv;
	private DrawingImageView overlayView;
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




		Log.d("here we go: ", imgDir);

		iv = (ImageView) findViewById(R.id.imageView);
		overlayView = (DrawingImageView) findViewById(R.id.imageViewOverlay);

		File img = new File(imgDir+File.separator+"smile1.jpg");

	if (img.exists()) {
			//Loading Image from URL
			Picasso.with(this)
					.load("https://www.simplifiedcoding.net/wp-content/uploads/2015/10/advertise.png")
					//.load(img)
					//.placeholder(R.drawable.placeholder)   // optional
					//.error(R.drawable.error)      // optional
					.resize(240, 320)                        // optional
					.into(iv);

		}
		verifyStoragePermissions(MainActivity.this);

		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv.getLayoutParams();
		overlayView.setLayoutParams(layoutParams);
		overlayView.setAlpha(0f);


		Log.d("about to get into it", "it");


	}

	@Override
	public final void onSensorChanged(SensorEvent event) {
		Sensor sensor = event.sensor();
		if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			event.values[0];
			event.values[1];
			event.values[2];

		}
		else if (sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {

		}
		else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {

		}
		else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {

		}
		else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

		}
		else (sensor.getType() == Sensor.TYPE_GRAVITY) {

		}
		// The light sensor returns a single value.
		// Many sensors return 3 values, one for each axis.
		float lux = event.values[0];
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
		Log.d("Hello", "its me");
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (requestCode == 1234) {

				//Get ImageURi and load with help of picasso
				//Uri selectedImageURI = data.getData()
				//iv.setImageURI(data.getData());
				Picasso.with(this).load(data.getData()).resize(1000, 1000)
						.into(iv);

		}

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
		// Check if we have write permission
		String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
		int REQUEST_EXTERNAL_STORAGE = 1;
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
