package com.app.opencity.models;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mehdichouag on 11/09/2014.
 */
public class LoginToken extends AsyncTask<String, Void, String> {

    Activity mActivity;
    SessionManager mManager;
    public LoginToken(Activity activity)
    {
        super();
        this.mActivity = activity;
        mManager = new SessionManager(mActivity.getApplicationContext());
    }

    @Override
    protected String doInBackground(String... params) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://opencity-moonshot.herokuapp.com/auth/local");
        StringBuilder builder = new StringBuilder();

        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("email", params[0]));
            nameValuePairs.add(new BasicNameValuePair("password", params[1]));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream content = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                    builder.append(line);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }
    protected void onPostExecute(String result)
    {
        JSONObject obj = null;
        try {
            obj = new JSONObject(result);
            try
            {
                result = obj.getString("token");
                mManager.connectUser(result);
                Toast.makeText(mActivity, "Connexion Réussie", Toast.LENGTH_SHORT).show();
            }
            catch (JSONException e)
            {
                Toast.makeText(mActivity, "Echec de la connection", Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}