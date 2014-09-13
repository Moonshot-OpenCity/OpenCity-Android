package com.app.opencity.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.opencity.R;
import com.app.opencity.models.CheckToken;

public class SplashScreenActivity extends Activity {
    // Splash screen timer
    public static String INTENT_EXTRA_PROFILE = "profile_bool";
    private static int SPLASH_TIME_OUT = 2000;
    private boolean mProfile = false;

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        StartAnimations();
        new CheckToken(this, true).execute();
        new checkData().execute();
    }

    public void setProfile()
    {
        mProfile = true;
    }

    private class checkData extends AsyncTask<Void, Integer, Boolean> {
        private boolean mConnexion;

        protected Boolean doInBackground(Void... values) {
            mConnexion = false;
            if (isConnected(getApplicationContext())) {
                mConnexion = true;
            }
            try {
                Thread.sleep(SPLASH_TIME_OUT);
            } catch (InterruptedException e) {

            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            String TAG = SplashScreenActivity.class.getSimpleName();
            Log.v(TAG, "finish current instance");
            if (!mConnexion) {
                AlertDialog alertDialog = new AlertDialog.Builder(SplashScreenActivity.this).create();

                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage("Merci de verifier votre connexion internet\nL'application va se fermer.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                alertDialog.show();
            } else {
                finish();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra(INTENT_EXTRA_PROFILE, mProfile);
                startActivity(intent);
            }
        }
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

    }

    public boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if (netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting())) return true;
            else {
                return false;
            }
        } else {
            return false;
        }
    }
}