package edu.cwru.sail.imagelearning;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class MainActivity extends Activity {

	private ImageView iv;
	private final String imgDir = Environment.getExternalStorageDirectory().toString() + "/DCIM/";
	//Make sure that this part is dynamically defined by the Browse Folder and
	// your CSV file name is "THE_SAME_FOLDER_NAME.csv"

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.d("here we go: ", imgDir);

		iv = (ImageView) findViewById(R.id.imageView);

		File img = new File(imgDir+File.separator+"smile1.jpg");

//		if (img.exists()) {
			//Loading Image from URL
			Picasso.with(this)
					.load("https://www.simplifiedcoding.net/wp-content/uploads/2015/10/advertise.png")
					//.load(img)
					//.placeholder(R.drawable.placeholder)   // optional
					//.error(R.drawable.error)      // optional
					.resize(1000, 1000)                        // optional
					.into(iv);
			Bitmap b = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b);
			c.drawBitmap(b, new Matrix(), null);

//		}

		Log.d("about to get into it", "it");
		iv.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				Log.d("oh boy@!!!!: ",  Double.toString(motionEvent.getX()) + " " + Double.toString(motionEvent.getY()));

				return true;
			}
		});



	}

	public boolean browseFolder(MenuItem item) {
		Log.d("here we go:", "Clicked on button");
		Intent i = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
		final int ACTIVITY_SELECT_IMAGE = 1234;
		startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
		//String imgDir = Environment.getExternalStorageDirectory().toString() + "/Downloads/carrying_p25_001_19.png";
		String imgDir = Environment.getRootDirectory().toString() + "/Downloads/carrying_p25_001_19.png";
		File img = new File(imgDir);
		if (img.exists()){
			Log.d("ImageExists","Yes Image exists");
		}
		else {
			Log.d("ImageExists", "No Image does not exist");
		}
		Log.d("CheckFile", "imgDir");
		return true;
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
