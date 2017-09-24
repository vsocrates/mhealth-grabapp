package edu.cwru.sail.imagelearning;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

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

public class MainActivity extends Activity {

	private DrawingImageView iv;
	private final String imgDir = Environment.getExternalStorageDirectory().toString() + "/DCIM/";

	//Make sure that this part is dynamically defined by the Browse Folder and
	// your CSV file name is "THE_SAME_FOLDER_NAME.csv"

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d("here we go: ", imgDir);

		iv = (DrawingImageView) findViewById(R.id.imageView);

		File img = new File(imgDir+File.separator+"smile1.jpg");

	if (img.exists()) {
			//Loading Image from URL
			Picasso.with(this)
					.load("https://www.simplifiedcoding.net/wp-content/uploads/2015/10/advertise.png")
					//.load(img)
					//.placeholder(R.drawable.placeholder)   // optional
					//.error(R.drawable.error)      // optional
					.resize(1000, 1000)                        // optional
					.into(iv);

		}

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
//		//String imgDir = Environment.getExternalStorageDirectory().toString() + "/Downloads/carrying_p25_001_19.png";
//		String imgDir = Environment.getRootDirectory().toString() + "/Downloads/carrying_p25_001_19.png";
//		File img = new File(imgDir);
//		if (img.exists()){
//			Log.d("ImageExists","Yes Image exists");
//		}
//		else {
//			Log.d("ImageExists", "No Image does not exist");
//		}
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
				//Uri selectedImageURI = data.getData();

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

}
