package com.app.opencity.models;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.app.opencity.activities.MainActivity;
import com.app.opencity.activities.SplashScreenActivity;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * Created by mehdichouag on 12/09/2014.
 */
public class CheckToken extends AsyncTask<Void, Void, Integer>
{
    private Activity mActivity;
    private SessionManager mManager;
    private boolean mSplash = false;

    public CheckToken(Activity activity)
    {
        super();
        mActivity = activity;
        mManager = new SessionManager(mActivity.getApplicationContext());
    }

    public CheckToken(Activity activity, boolean splash)
    {
        super();
        mSplash = splash;
        mActivity = activity;
        mManager = new SessionManager(mActivity.getApplicationContext());
    }

    @Override
    protected Integer doInBackground(Void... params)
    {
        int result = 0;
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet("https://opencity-moonshot.herokuapp.com/users/me");
        String token;

        try {
            if ((token = mManager.getToken()) != null)
            {
                httpget.addHeader("Authorization", "Bearer " + token);
                HttpResponse response;
                response = httpclient.execute(httpget);
                result = response.getStatusLine().getStatusCode();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == 200) {
            if (!mSplash)
                ((MainActivity) mActivity).setProfile();
            else
                ((SplashScreenActivity) mActivity).setProfile();
        } else if (!mSplash)
            Toast.makeText(mActivity, "Echec de la connexion", Toast.LENGTH_SHORT).show();
    }
}
