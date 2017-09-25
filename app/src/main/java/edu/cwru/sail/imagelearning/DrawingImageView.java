package edu.cwru.sail.imagelearning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.ImageView;

public class DrawingImageView extends ImageView {

    private Path myPath;

    private float myX;
    private float myY;

    private static final double TOUCH_TOLERANCE = 5;

    private Paint paint = new Paint();

    private Canvas mCanvas;

    private VelocityTracker mVelocityTracker = null;

    public DrawingImageView(Context context) {
        super(context);
        myPath = new Path();
        Bitmap b = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
    }

    public DrawingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        myPath = new Path();

    }

    public DrawingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        myPath = new Path();

    }

    private void touch_start(float x, float y) {
        myPath.reset();

        myX = x;
        myY = y;
        myPath.moveTo(x, y);
    }

    private void touch_move(float x, float y) {
        float changeX = Math.abs(myX - x);
        float changeY = Math.abs(myY - y);

        if (changeX >= TOUCH_TOLERANCE || changeY >= TOUCH_TOLERANCE) {
            myPath.lineTo(x, y);
            myX = x;
            myY = y;
        }
    }

    private void touch_stop(float x, float y) {
        myPath.lineTo(myX, myY);



    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        float x = event.getX();
        float y = event.getY();


        Log.d("we are  class: ", Double.toString(x) + "  " + Double.toString(y));
        switch (event.getActionMasked()) {
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
                touch_start(x, y);
                invalidate();
                break;
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
                touch_move(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_stop(x,y);
                invalidate();

            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0xFFAAAAAA);

//        canvas.drawBitmap();
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        int index = event.getActionIndex();
//        int action = event.getActionMasked();
//        int pointerId = event.getPointerId(index);
//        Log.d("tracking motion", "tracking the motion");
//
//        switch(action) {
//            case MotionEvent.ACTION_DOWN:
//                if(mVelocityTracker == null) {
//                    // Retrieve a new VelocityTracker object to watch the
//                    // velocity of a motion.
//                    mVelocityTracker = VelocityTracker.obtain();
//                }
//                else {
//                    // Reset the velocity tracker back to its initial state.
//                    mVelocityTracker.clear();
//                }
//                // Add a user's movement to the tracker.
//                mVelocityTracker.addMovement(event);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                mVelocityTracker.addMovement(event);
//                // When you want to determine the velocity, call
//                // computeCurrentVelocity(). Then call getXVelocity()
//                // and getYVelocity() to retrieve the velocity for each pointer ID.
//                mVelocityTracker.computeCurrentVelocity(1000);
//                // Log velocity of pixels per second
//                // Best practice to use VelocityTrackerCompat where possible.
//                Log.d("", "X velocity: " + mVelocityTracker.getXVelocity());
//                Log.d("", "Y velocity: " + mVelocityTracker.getYVelocity());
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                // Return a VelocityTracker object back to be re-used by others.
//                mVelocityTracker.recycle();
//                break;
//        }
//        return true;
//    }
}
