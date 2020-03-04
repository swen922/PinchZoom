package com.horovod.android.pinchzoom;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    //ScaleGestureDetector detector;
    //float scaleFactor = 1.0f;
    //GestureDetectorCompat gestureDetector;


    float scalediff;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private float oldDist = 1f;
    private float d = 0f;
    private float newRot = 0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.img);


        //detector = new ScaleGestureDetector(this, new ScaleListener());
        //gestureDetector = new GestureDetectorCompat(this, this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        //Picasso.get().load(R.drawable.shutterstock_626268023).resize(width * 2, height * 2).into(imageView);
        //Picasso.get().load(R.drawable.shutterstock_626268023).resizeDimen(width * 2, height * 2).into(imageView);
        Picasso.get().load(R.drawable.shutterstock_626268023).fit().into(imageView);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, height);
        //RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(relativeLayout.getLayoutParams());


        /*layoutParams.leftMargin = 50;
        layoutParams.topMargin = 50;
        layoutParams.bottomMargin = -250;
        layoutParams.rightMargin = -250;*/
        imageView.setLayoutParams(layoutParams);



        imageView.setOnTouchListener(new View.OnTouchListener() {

            RelativeLayout.LayoutParams parms;
            int startWidth = 0;
            int startHeight = 0;
            float dx = 0, dy = 0, x = 0, y = 0, angle = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //return gestureDetector.onTouchEvent(event);

                final ImageView im = (ImageView) v;
                ((BitmapDrawable) im.getDrawable()).setAntiAlias(true);

                parms = (RelativeLayout.LayoutParams) im.getLayoutParams();

                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN :
                        startWidth = parms.width;
                        startHeight = parms.height;
                        dx = event.getRawX() - parms.leftMargin;
                        dy = event.getRawY() - parms.topMargin;
                        mode = DRAG;
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN :
                        oldDist = spacing(event);
                        if (oldDist > 10f) {
                            mode = ZOOM;
                        }
                        d = rotation(event);
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == DRAG) {
                            x = event.getRawX();
                            y = event.getRawY();
                            parms.leftMargin = (int) (x - dx);
                            parms.topMargin = (int) (y - dy);
                            parms.rightMargin = 0;
                            parms.bottomMargin = 0;
                            parms.rightMargin = parms.leftMargin + (5 * parms.width);
                            parms.bottomMargin = parms.topMargin + (10 * parms.height);
                            im.setLayoutParams(parms);
                        }
                        else if (mode == ZOOM) {
                            if (event.getPointerCount() == 2) {
                                /*newRot = rotation(event);
                                float r = newRot - d;
                                angle = r;*/

                                x = event.getRawX();
                                y = event.getRawY();

                                float newDist = spacing(event);
                                if (newDist > 10f) {
                                    float scale = newDist / oldDist * im.getScaleX();

                                    // Тут проверка, насколько масштаб картинки меньше ее исходного размера
                                    // 1 – это ровно исходный
                                    if (scale > 0.9f) {
                                        scalediff = scale;
                                        im.setScaleX(scale);
                                        im.setScaleY(scale);
                                    }
                                }
                                //im.animate().rotationBy(angle).setDuration(0).setInterpolator(new LinearInterpolator()).start();
                                x = event.getRawX();
                                y = event.getRawY();
                                parms.leftMargin = (int) ((x - dx) + scalediff);
                                parms.topMargin = (int) ((y - dy) + scalediff);
                                parms.rightMargin = 0;
                                parms.bottomMargin = 0;
                                parms.rightMargin = parms.leftMargin + (5 * parms.width);
                                parms.bottomMargin = parms.topMargin + (10 * parms.height);
                                im.setLayoutParams(parms);
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private float rotation(MotionEvent event) {
        double deltaX = (event.getX(0) - event.getX(1));
        double deltaY = (event.getY(0) - event.getY(1));
        double radians = Math.atan2(deltaY, deltaX);
        return (float) Math.toDegrees(radians);
    }





    /*class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
            imageView.setScaleX(scaleFactor);
            imageView.setScaleY(scaleFactor);
            imageView.setRotation(detector.getCurrentSpanX());
            return true;
        }
    }*/

}
