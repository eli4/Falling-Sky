package com.egames.drawing;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MyGLSurfaceView extends GLSurfaceView {

    private final MyGLRenderer mRenderer;
    public Triangle mTriangle;
    TextView score;
    boolean gameOn;
    private float mPreviousX = 0f;
    private float mPreviousY = 0f;


    public MyGLSurfaceView(Context context) {
        super(context);

        mRenderer = new MyGLRenderer();
        init(context);
    }

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
        super(context,attrs);

        mRenderer = new MyGLRenderer();
        init(context);
    }


    private void init(Context context) {
        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        long initTime = System.currentTimeMillis();
        final int width = getWidth();
        final int height = getHeight();
        float pixelsPerUnit = height / 2.f;
        float x = (width / 2 - e.getX()) / pixelsPerUnit;
        float y = (height / 2 - e.getY()) / pixelsPerUnit;
        if(mPreviousX == 0f && mPreviousY == 0f){
        }

        float aura = 0.07f;
        for(Triangle tri : mRenderer.mTriangles)
        {
            float topVertexY = tri.y + Triangle.TRIHEIGHT + aura;
            float topVertexX = tri.x + aura;
            float leftVertexY = tri.y +  -Triangle.TRIHEIGHT/2 - aura;
            float leftVertexX = tri.x + -Triangle.TRIWIDTH - aura;
            float rightVertexY = tri.y +  -Triangle.TRIHEIGHT/2 -aura;
            float rightVertexX = tri.x +  Triangle.TRIWIDTH + aura;
            boolean inTri = PointInTriangle(x, y, topVertexX, topVertexY, leftVertexX, leftVertexY, rightVertexX, rightVertexY);


            switch (e.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (inTri && gameOn) {
                        //find out which triangle and remove only that one
                        mRenderer.mTriangles.remove(tri);
                        int currentScore = Integer.parseInt(score.getText().toString());
                        Integer newScore = currentScore + 10;
                        score.setText(newScore.toString());

                    }
            }
            if(inTri){
                break;
            }
        }
        return true;
    }

    float sign (float x1, float y1, float x2, float y2, float x3, float y3)
    {
        return (x1 - x3) * (y2 - y3) - (x2 - x3) * (y1 - y3);
    }

    boolean PointInTriangle (float x, float y, float x1, float y1, float x2, float y2, float x3, float y3)
    {
        boolean b1, b2, b3;

        b1 = sign(x,y,x1,y1,x2,y2) < 0.0f;
        b2 = sign(x,y,x2,y2,x3,y3) < 0.0f;
        b3 = sign(x,y,x3,y3,x1,y1) < 0.0f;
        return ((b1 == b2) && (b2 == b3));
    }

    public void onTick(final Timer fps){
        requestRender();

        queueEvent(new Runnable() {
            @Override
            public void run() {
                mRenderer.tickCount++;
                final int height = getHeight();
                float pixelsPerUnit = height / 2.f;


                if (mRenderer.tickCount % mRenderer.triangleRate == 0) {
                    Random coord = new Random();
                    final int width = getWidth();
                    int triangleWidth = (int)(Triangle.TRIWIDTH * pixelsPerUnit);
                    float x = ((width - triangleWidth) / 2 - coord.nextInt(width - triangleWidth)) / pixelsPerUnit;
                    float y = (height / 2) / pixelsPerUnit;
                    mRenderer.addTriangle(x, y);
                }
                if (mRenderer.triangleRate != 2 && mRenderer.tickCount % 40 == 0) {
                    mRenderer.triangleRate--;
                }

                for (Triangle tri : mRenderer.mTriangles) {
                    float glspeed = tri.speed / pixelsPerUnit;
                    float newY = tri.y - glspeed;
                    tri.y = newY;
                    if (tri.y < -(height+90) / 2 /pixelsPerUnit) {
                        mRenderer.mTriangles.remove(tri);
                        gameOn = false;
                        fps.cancel();
                        fps.purge();
                        return;
                    }
                }
                requestRender();
            }
        });
     }

    public void begin(TextView score) {
        this.score = score;
        gameOn = true;
        final Timer fps = new Timer();
        if(gameOn) {
            fps.schedule(new TimerTask() {
                @Override
                public void run() {
                    onTick(fps);
                }
            }, 0, 50);
        }
    }

    public void clearSurfaceView(){
        Log.d("clearSurfaceView()", "was called");
        mRenderer.removeTriangle();
        mRenderer.tickCount = 0;
        mRenderer.triangleCount = 1;
        mRenderer.triangleRate = 25;
        requestRender();
    }

}

