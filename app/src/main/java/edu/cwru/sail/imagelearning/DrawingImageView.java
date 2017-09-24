package edu.cwru.sail.imagelearning;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

public class DrawingImageView extends ImageView {

    private PointF point;
    private Paint paint = new Paint();

    public DrawingImageView(Context context) {
        super(context);
    }

    public DrawingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                point = new PointF(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                point.set(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                point = null;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (point != null) {
            canvas.drawCircle(point.x, point.y, 100, paint);
        }
    }
}
