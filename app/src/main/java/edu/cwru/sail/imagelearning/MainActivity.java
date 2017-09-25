package edu.cwru.sail.imagelearning;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.VelocityTracker;
import android.view.View;

import com.squareup.picasso.Picasso;

import static android.R.attr.permission;

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

public class MainActivity extends Activity {

//	private ImageView iv;
	private DrawingImageView overlayView;

	private final String imgDir = Environment.getExternalStorageDirectory().toString() + "/DCIM/";

	//Make sure that this part is dynamically defined by the Browse Folder and
	// your CSV file name is "THE_SAME_FOLDER_NAME.csv"
	private VelocityTracker mVelocityTracker = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

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
