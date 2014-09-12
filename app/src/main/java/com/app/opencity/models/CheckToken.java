package com.app.opencity.models;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.app.opencity.models.SessionManager;

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

    public CheckToken(Activity activity)
    {
        super();
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
            /*FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .hide(this)
                    .commit();*/
            Toast.makeText(mActivity, "OK", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(mActivity, "KO", Toast.LENGTH_SHORT).show();
    }
}
