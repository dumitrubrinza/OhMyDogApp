package ie.wit.ohmydog.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ie.wit.ohmydog.R;

public class Splash extends Activity {

    private boolean mIsBackButtonPressed;
    private static final int SPLASH_DURATION = 2000;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                finish();

                if (!mIsBackButtonPressed){
                    Intent intent = new Intent(Splash.this , MainActivity.class);
                    Splash.this.startActivity(intent);
                }
            }
        },SPLASH_DURATION);

    }

    @Override
    public void onBackPressed() {
        mIsBackButtonPressed = true;
        super.onBackPressed();
    }

}
