package com.egames.drawing;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class MyGLRenderer implements GLSurfaceView.Renderer {

    public List<Triangle> mTriangles;
    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private float[] mRotationMatrix = new float[16];
    private float[] mTranslationMatrix = new float[16];
    private float dragVertex;
    public int triangleRate = 25;
    public volatile float mX;
    public volatile float mY;
    public int triangleCount = 1;
    public int tickCount = 0;

    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        // Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        // initialize a triangle
        mTriangles = new CopyOnWriteArrayList<Triangle>();
    }

    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];
        // Redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        for(Iterator<Triangle> i = mTriangles.iterator(); i.hasNext(); ) {
            Triangle currentTri = i.next();
            Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, -1.0f);
            Matrix.translateM(mRotationMatrix, 0, currentTri.x, currentTri.y, 0f);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            // Draw triangle
            currentTri.draw(scratch);
        }

    }


    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    public void addTriangle(float x, float y){
        Random speed = new Random();
        Triangle newTriangle = new Triangle();
        newTriangle.id = triangleCount;
        newTriangle.x = x;
        newTriangle.y = y;
        newTriangle.speed = speed.nextInt(8-3)+4;

        Random r = new Random();
        float r1 = (r.nextInt(100-50)+30)/100f;
        float r2 = (r.nextInt(100-50)+30)/100f;
        float r3 = (r.nextInt(100-50)+30)/100f;
        newTriangle.setColor(r1, r2, r3, 1f);

        triangleCount++;
        mTriangles.add(newTriangle);
    }

    public void removeTriangle(){
        mTriangles.clear();

    }

}
