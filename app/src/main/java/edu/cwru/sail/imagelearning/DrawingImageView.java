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
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * code adapt
 */

public class DrawingImageView extends ImageView {

    boolean drawEnabled = false;

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
        if (drawEnabled == true){
            canvas.drawPath(path, drawPaint);
        }

    }

    public void clearPath(){
        path.reset();
        invalidate();
    }

    public void enableCircling(){
        drawEnabled = true;
        invalidate();
    }

    public void disableCircling(){
        clearPath();
        drawEnabled = false;
        invalidate();
    }

    public void saveCSV(){
        Context context = this.getContext();
        CharSequence text = "CSV Saved!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        if (drawEnabled != false)
            toast.show();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //The new one
        super.onTouchEvent(event);

        x = event.getX();
        y = event.getY();
        if (drawEnabled == false){
            clearPath();
            return true;
        }
        try {
           this.writer = new CSVWriter(new FileWriter(file, true), ',',  CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END);
            float x = event.getX();
            float y = event.getY();

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
                if (drawEnabled == true){
                    this.writer.writeNext(row);
                }
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