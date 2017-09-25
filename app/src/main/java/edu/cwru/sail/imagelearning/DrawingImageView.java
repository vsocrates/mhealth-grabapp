package edu.cwru.sail.imagelearning;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.ImageView;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * code adapt
 */

public class DrawingImageView extends ImageView {

    private float x;
    private float y;

    Paint drawPaint;
    private Path path = new Path();

    private VelocityTracker mVelocityTracker = null;
    private File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"MatrixValues.csv");
    private CSVWriter writer;
//
//
//    public DrawingImageView(Context context) {
//        super(context);
//    }

    public DrawingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawPaint = new Paint(Paint.DITHER_FLAG);
        drawPaint.setAntiAlias(true);
        drawPaint.setColor(Color.RED);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeWidth(5);
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int width, int height) {
        super.onSizeChanged(w, h, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        Log.d("itsa me, mario:", "on Draw");
        canvas.drawPath(path, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //The new one
        super.onTouchEvent(event);

        x = event.getX();
        y = event.getY();

        try {
            //            bwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.csv")));
            //            CSVWriter this.writer = new CSVWriter(bwriter, ",");
            // this.writer = new CSVWriter(new FileWriter("test.csv"), ',');
            //this.writer = new CSVWriter()
            this.writer = new CSVWriter(new FileWriter(file, true), ',',  CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            //
            //this.writer = new CSVWriter(new FileWriter(file));
            // file.mkdirs();
            //            file.createNewFile();
            float x = event.getX();
            float y = event.getY();

            // Log.d("Directory path", )


            Log.d("we are  class: ", Double.toString(x) + "  " + Double.toString(y));


            switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the
                    // velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                path.moveTo(x, y);
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                Log.d("", "X velocity: " + mVelocityTracker.getXVelocity());
                Log.d("", "Y velocity: " + mVelocityTracker.getYVelocity());
                Log.d("Pressure", "" + event.getPressure() + "");
                String[] row  = {"" + x + "", "" + y + "", "" + mVelocityTracker.getXVelocity() + "", "" + mVelocityTracker.getYVelocity() + "", "" + event.getPressure() + ""};
                Log.d("Printing out row values", row[0]);
                if (writer == null) {
                    Log.d("Writer is null", "Writer is null");
                }
                this.writer.writeNext(row);
                path.lineTo(x, y);

                invalidate();
                return true;
            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
            default:
                return true;
        }
            writer.close();

        }
        catch (IOException ioe){
            Log.e("Catching exception", "I got an error", ioe);
        }

        invalidate();
        return true;
    }
}

//public class DrawingImageView extends ImageView {
//
//    private Path myPath;
//
//    private float myX;
//    private float myY;
//
//    private static final double TOUCH_TOLERANCE = 5;
//
//    private Paint paint = new Paint();
//
//    private Canvas mCanvas;
//
//    private File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"MatrixValues.csv");
//
//    private CSVWriter writer;
//    private BufferedWriter bwriter;
//
//    private VelocityTracker mVelocityTracker = null;
//
//    public DrawingImageView(Context context) {
//        super(context);
//        init();
//        myPath = new Path();
//        Bitmap b = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
//
//    }
//
//    public DrawingImageView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//        myPath = new Path();
//
//    }
//
//    public DrawingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init();
//        myPath = new Path();
//
//
//    }
//
//    private void init(){
//
//    }
//
//    private void touch_start(float x, float y) {
//        myPath.reset();
//
//        myX = x;
//        myY = y;
//        myPath.moveTo(x, y);
//    }
//
//    private void touch_move(float x, float y) {
//        float changeX = Math.abs(myX - x);
//        float changeY = Math.abs(myY - y);
//
//        if (changeX >= TOUCH_TOLERANCE || changeY >= TOUCH_TOLERANCE) {
//            myPath.lineTo(x, y);
//            myX = x;
//            myY = y;
//        }
//    }
//
//    private void touch_stop(float x, float y) {
//        myPath.lineTo(myX, myY);
//
//
//
//    }
//
//
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        super.onTouchEvent(event);
//        try {
////            bwriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("test.csv")));
////            CSVWriter this.writer = new CSVWriter(bwriter, ",");
//            // this.writer = new CSVWriter(new FileWriter("test.csv"), ',');
//            //this.writer = new CSVWriter()
//            this.writer = new CSVWriter(new FileWriter(file, true), ',',  CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
////
//            //this.writer = new CSVWriter(new FileWriter(file));
//// file.mkdirs();
////            file.createNewFile();
//            float x = event.getX();
//            float y = event.getY();
//
//           // Log.d("Directory path", )
//
//
//            Log.d("we are  class: ", Double.toString(x) + "  " + Double.toString(y));
//            switch (event.getActionMasked()) {
//                case MotionEvent.ACTION_DOWN:
//                    if(mVelocityTracker == null) {
//                        // Retrieve a new VelocityTracker object to watch the
//                        // velocity of a motion.
//                        mVelocityTracker = VelocityTracker.obtain();
//                    }
//                    else {
//                        // Reset the velocity tracker back to its initial state.
//                        mVelocityTracker.clear();
//                    }
//                    // Add a user's movement to the tracker.
//                    mVelocityTracker.addMovement(event);
//                    touch_start(x, y);
//                    invalidate();
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    mVelocityTracker.addMovement(event);
//                    // When you want to determine the velocity, call
//                    // computeCurrentVelocity(). Then call getXVelocity()
//                    // and getYVelocity() to retrieve the velocity for each pointer ID.
//                    mVelocityTracker.computeCurrentVelocity(1000);
//                    // Log velocity of pixels per second
//                    // Best practice to use VelocityTrackerCompat where possible.
//                    Log.d("", "X velocity: " + mVelocityTracker.getXVelocity());
//                    Log.d("", "Y velocity: " + mVelocityTracker.getYVelocity());
//                    Log.d("Pressure", "" + event.getPressure() + "");
//                    String[] row  = {"" + x + "", "" + y + "", "" + mVelocityTracker.getXVelocity() + "", "" + mVelocityTracker.getYVelocity() + "", "" + event.getPressure() + ""};
//                    Log.d("Printing out row values", row[0]);
//                    if (writer == null) {
//                        Log.d("Writer is null", "Writer is null");
//                    }
//                    this.writer.writeNext(row);
//
//                    touch_move(x,y);
//                    invalidate();
//                    break;
//                case MotionEvent.ACTION_UP:
//                    touch_stop(x,y);
//                    invalidate();
//
//                case MotionEvent.ACTION_CANCEL:
//                    mVelocityTracker.recycle();
//                    mVelocityTracker = null;
//                    break;
//            }
//            writer.close();
//
//        }
//        catch (IOException ioe){
//            Log.e("Catching exception", "I got an error", ioe);
//        }
//
//        Log.d("Right before return", "Right before return");
//        return true;
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        canvas.drawColor(0xFFAAAAAA);
//
////        canvas.drawBitmap();
//    }
//
////    @Override
////    public boolean onTouchEvent(MotionEvent event) {
////        int index = event.getActionIndex();
////        int action = event.getActionMasked();
////        int pointerId = event.getPointerId(index);
////        Log.d("tracking motion", "tracking the motion");
////
////        switch(action) {
////            case MotionEvent.ACTION_DOWN:
////                if(mVelocityTracker == null) {
////                    // Retrieve a new VelocityTracker object to watch the
////                    // velocity of a motion.
////                    mVelocityTracker = VelocityTracker.obtain();
////                }
////                else {
////                    // Reset the velocity tracker back to its initial state.
////                    mVelocityTracker.clear();
////                }
////                // Add a user's movement to the tracker.
////                mVelocityTracker.addMovement(event);
////                break;
////            case MotionEvent.ACTION_MOVE:
////                mVelocityTracker.addMovement(event);
////                // When you want to determine the velocity, call
////                // computeCurrentVelocity(). Then call getXVelocity()
////                // and getYVelocity() to retrieve the velocity for each pointer ID.
////                mVelocityTracker.computeCurrentVelocity(1000);
////                // Log velocity of pixels per second
////                // Best practice to use VelocityTrackerCompat where possible.
////                Log.d("", "X velocity: " + mVelocityTracker.getXVelocity());
////                Log.d("", "Y velocity: " + mVelocityTracker.getYVelocity());
////                break;
////            case MotionEvent.ACTION_UP:
////            case MotionEvent.ACTION_CANCEL:
////                // Return a VelocityTracker object back to be re-used by others.
////                mVelocityTracker.recycle();
////                break;
////        }
////        return true;
////    }
//}
