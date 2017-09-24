package edu.cwru.sail.imagelearning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class DrawingImageView extends ImageView {

    private Path myPath;

    private float myX;
    private float myY;

    private static final double TOUCH_TOLERANCE = 5;

    private Paint paint = new Paint();

    public DrawingImageView(Context context) {
        super(context);
        myPath = new Path();
        Bitmap b = Bitmap.createBitmap(1000, 1000, Bitmap.Config.ARGB_8888);
    }

    public DrawingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        float x = event.getX();
        float y = event.getY();


        Log.d("we are  class: ", Double.toString(x) + "  " + Double.toString(y));
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x,y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                touch_stop(x,y);
                invalidate();
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
}
