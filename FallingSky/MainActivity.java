package com.egames.drawing;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends FragmentActivity implements GameOverDialogFragment.NoticeDialogListener {
    private MyGLSurfaceView mGLView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        setContentView(R.layout.activity_main);
        mGLView = (MyGLSurfaceView) findViewById(R.id.glSurfaceViewID);
    }

    public void gameStart(View view){
        Button myButton = (Button) findViewById(R.id.buttonID);
        myButton.setEnabled(false);
        TextView score = (TextView) findViewById(R.id.score);
        score.setText("0");
        mGLView.clearSurfaceView();
        final Timer fps = new Timer();
        mGLView.begin(score);
        fps.schedule(new TimerTask() {
            @Override
            public void run() {
                isGameOver(fps);
            }
        }, 0, 50);
    }

    public void isGameOver(Timer fps){
        if(mGLView.gameOn == false) {
            DialogFragment GO = new GameOverDialogFragment();
            GO.setCancelable(false);
            GO.show(getSupportFragmentManager(), "GameOver");
            fps.cancel();
            fps.purge();
        }
    }

    public void onDialogPositiveClick(DialogFragment dialog) {
        // User touched the dialog's positive button
        gameStart(mGLView);
    }

    public void onDialogNegativeClick(DialogFragment dialog){
        Button myButton = (Button) findViewById(R.id.buttonID);
        myButton.setEnabled(true);
    }

}

