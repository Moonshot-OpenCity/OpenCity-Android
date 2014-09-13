package com.app.opencity.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.app.opencity.R;
import com.app.opencity.fragments.MapsFragment;
import com.app.opencity.models.SessionManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class PostActivity extends Activity implements View.OnClickListener{

    ImageButton mUp;
    Intent intent;
    ImageButton mDown;
    EditText mTitle;
    EditText mContent;
    SessionManager mManager;

    private class PostInformation{
        String title;
        String content;
        double[] positon;
        String type;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mUp = (ImageButton)findViewById(R.id.positive);
        mDown = (ImageButton)findViewById(R.id.negative);
        mTitle = (EditText)findViewById(R.id.title_post);
        mContent = (EditText)findViewById(R.id.content_post);
        mDown.setTag(Boolean.valueOf(false));
        mUp.setTag(Boolean.valueOf(false));
        mDown.setOnClickListener(this);
        mUp.setOnClickListener(this);
        intent = getIntent();
        mManager = new SessionManager(this);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu)
    {
        getMenuInflater().inflate(R.menu.post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menu)
    {
        switch (menu.getItemId())
        {
            case R.id.valid:
                checkForm();
                return true;
            case R.id.cancel:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(menu);
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v == mDown){
            if ((Boolean)mDown.getTag() == false && (Boolean)mUp.getTag() == true) {
                mUp.setImageResource(R.drawable.grey_thumb_up);
                mUp.setTag(Boolean.valueOf(false));
                mDown.setImageResource(R.drawable.thumb_down);
                mDown.setTag(new Boolean(true));
            }
            else if ((Boolean)mUp.getTag() == false){
                mDown.setImageResource(R.drawable.thumb_down);
                mDown.setTag(Boolean.valueOf(true));
            }
        }
        if (v == mUp)
        {
            if ((Boolean)mUp.getTag() == false && (Boolean)mDown.getTag() == true) {
                mDown.setImageResource(R.drawable.grey_thumb_down);
                mDown.setTag(Boolean.valueOf(false));
                mUp.setImageResource(R.drawable.thumb_up);
                mUp.setTag(Boolean.valueOf(true));
            }
            else if ((Boolean)mDown.getTag() == false){
                mUp.setImageResource(R.drawable.thumb_up);
                mUp.setTag(Boolean.valueOf(true));
            }

        }
    }
    private void checkForm()
    {
        String title = mTitle.getText().toString();
        String content = mContent.getText().toString();
        Boolean up = (Boolean)mUp.getTag();
        Boolean down = (Boolean)mDown.getTag();

        if (content.isEmpty() || title.isEmpty() || (!up && !down)){
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();

            alertDialog.setTitle("ATTENTION");
            alertDialog.setMessage(getString(R.string.warning_create_post));
            alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();
        }
        else{
            PostInformation info = new PostInformation();

            info.title = title;
            info.content = content;
            info.type = up ? "positive" : "negative";
            info.positon = new double[2];
            info.positon[0] = intent.getDoubleExtra(MapsFragment.EXTRA_LONGITUDE, 0);
            info.positon[1] = intent.getDoubleExtra(MapsFragment.EXTRA_LATITUDE, 0);
            new PostData().execute(info);
        }

    }
    private class PostData extends AsyncTask<PostInformation, Void, Integer>{
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://opencity-moonshot.herokuapp.com/api/postits");
        StringBuilder builder = new StringBuilder();
        String token = mManager.getToken();
        int statueCode = 0;

        @Override
        protected Integer doInBackground(PostInformation... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("title", params[0].title));
                nameValuePairs.add(new BasicNameValuePair("description", params[0].content));
                nameValuePairs.add(new BasicNameValuePair("type", params[0].type));
                nameValuePairs.add(new BasicNameValuePair("lon", String.valueOf(params[0].positon[0])));
                nameValuePairs.add(new BasicNameValuePair("lat", String.valueOf(params[0].positon[1])));
                httppost.addHeader("Authorization", "Bearer " + ((token  == null) ? "" : token));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                statueCode = response.getStatusLine().getStatusCode();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return statueCode;
        }
        public void onPostExecute(Integer result)
        {
            if (result == 201) {
                Toast.makeText(PostActivity.this, "Post-it envoyer", Toast.LENGTH_LONG).show();
                finish();
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(PostActivity.this).create();

                alertDialog.setTitle("Erreur");
                alertDialog.setMessage(getString(R.string.post_failed));
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }

        }
    }
}
