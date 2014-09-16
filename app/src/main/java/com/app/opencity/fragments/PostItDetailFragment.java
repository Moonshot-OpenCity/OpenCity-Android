package com.app.opencity.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.opencity.R;
import com.app.opencity.adapters.CommentAdapter;
import com.app.opencity.models.CircleTransform;
import com.app.opencity.models.Comment;
import com.app.opencity.models.PostIts;
import com.app.opencity.models.SessionManager;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class PostItDetailFragment extends Fragment implements View.OnClickListener {
    private Button mQuit;
    private TextView mTitle;
    private TextView mDescription;
    private ImageView mIcon;
    private TextView mOwner;
    private Button mSend;
    private PostIts mPostIt;
    private FrameLayout mFrame;
    private ListView mListOfComments;
    private LinkedList<Comment> mComments;
    private SessionManager mManager;
    private EditText mContent;

    public PostItDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_post_it_detail, container, false);
        bindView(v);
        return v;
    }

    public void bindView(View v) {
        mQuit = (Button) v.findViewById(R.id.buttonQuitDetailPostIt);
        mQuit.setOnClickListener(this);
        mTitle = (TextView) v.findViewById(R.id.nameDetailPostIt);
        mDescription = (TextView) v.findViewById(R.id.descriptionDetailPostIt);
        mIcon = (ImageView) v.findViewById(R.id.iconDetailPostIt);
        mOwner = (TextView) v.findViewById(R.id.ownerDetailPostIt);
        mSend = (Button) v.findViewById(R.id.send);
        mContent = (EditText) v.findViewById(R.id.comment_post);
        mSend.setOnClickListener(this);
        mManager = new SessionManager(getActivity());
        mFrame = (FrameLayout) v.findViewById(R.id.layoutDetails);
        mListOfComments = (ListView) v.findViewById(R.id.listViewComment);
    }

    public void setData() {
        Log.v("SET DATA", "In");
        if (mPostIt != null) {
            mTitle.setText(mPostIt.getTitle());
            mDescription.setText(mPostIt.getDescription());
            mOwner.setText(Html.fromHtml(String.format(getResources().getString(R.string.ownerDetail), mPostIt.getOwmer(), mPostIt.getCreation())));
            if (mPostIt.getType().equals("positive")) {
                mFrame.setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_border_green));
            } else {
                mFrame.setBackgroundDrawable(getResources().getDrawable(R.drawable.layout_border_red));
            }
            Picasso.with(getActivity()).load(mPostIt.getmUrlPic()).placeholder(R.drawable.ic_placeholder).transform(new CircleTransform()).into(mIcon);
            new FetchGetCommentTask().execute(mPostIt.getmId());
        }
    }

    public void setPostIt(PostIts postIt) {
        this.mPostIt = postIt;
        new FetchGetAllDataPostItTask().execute(mPostIt.getmId());
        Log.v("SET POSTIT", "Done");
    }

    @Override
    public void onClick(View view) {
        if (view == mQuit) {
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .hide(this)
                    .commit();
        }
        else if (view == mSend)
        {
            if (!mContent.getText().toString().isEmpty())
                new PostComment().execute(mContent.getText().toString(), mPostIt.getmId());
            else
            {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                alertDialog.setTitle("Erreur");
                alertDialog.setMessage(getString(R.string.send_comment_fail));
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
            mContent.setText("");
        }
    }

    public class FetchGetCommentTask extends AsyncTask<String, Void, LinkedList<Comment>> {

        private final String LOG_TAG = FetchGetCommentTask.class.getSimpleName();

        public FetchGetCommentTask() {

        }

        /**
         * Take the String representing the complete enterprises in JSON Format and
         * pull out the data we need to save the data to display in the frame bellow.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private LinkedList<Comment> getDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_DESCRIPTION = "description";
            final String OWM_OWNER = "owner";
            final String OWM_ID = "_id";
            final String OWM_CREATED = "createdAt";
            final String OWM_URL = "imageURL";

            final String OWM_OWNER_NAME = "name";
            final String OWM_OWNER_ID = "_id";
            final String OWM_OWNER_EMAIL = "email";

            JSONArray dataArray = new JSONArray(forecastJsonStr);

            LinkedList<Comment> resultStrs = new LinkedList<Comment>();
            Log.v(LOG_TAG, dataArray.length() + "");
            for (int i = 0; i < dataArray.length(); i++) {
                // Get the JSON object representing the enterprise
                JSONObject dataPostIt = dataArray.getJSONObject(i);
                JSONObject owner = dataPostIt.getJSONObject(OWM_OWNER);
                resultStrs.push(new Comment(
                                dataPostIt.getString(OWM_DESCRIPTION),
                                owner.getString(OWM_OWNER_NAME),
                                owner.getString(OWM_OWNER_ID),
                                owner.getString(OWM_OWNER_EMAIL),
                                dataPostIt.getString(OWM_ID),
                                dataPostIt.getString(OWM_CREATED),
                                owner.getString(OWM_URL))
                );
            }
            Log.v(LOG_TAG, resultStrs.size() + "");
            return resultStrs;
        }

        @Override
        protected LinkedList<Comment> doInBackground(String... params) {

            if (params.length == 0)
                return null;
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String commentJsonStr = null;

            try {
                final String OPENCITY_BASE_URL =
                        "http://opencity-moonshot.herokuapp.com/api/comments/getByPostit?";
                final String ID_POSTIT_PARAM = "postit";

                Uri builtUri = Uri.parse(OPENCITY_BASE_URL).buildUpon()
                        .appendQueryParameter(ID_POSTIT_PARAM, params[0])
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI = " + builtUri.toString());

                // Create the request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    commentJsonStr = null;
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    commentJsonStr = null;
                    return null;
                }
                commentJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Club JSON String: " + commentJsonStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                commentJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getDataFromJson(commentJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(LinkedList<Comment> result) {
            Log.v(LOG_TAG, "LOAD ENDED");
            Collections.reverse(result);
            mComments = result;
            mListOfComments.setAdapter(new CommentAdapter(getActivity(), 0, mComments));
        }
    }

    public class FetchGetAllDataPostItTask extends AsyncTask<String, Void, LinkedList<Comment>> {

        private final String LOG_TAG = FetchGetAllDataPostItTask.class.getSimpleName();

        public FetchGetAllDataPostItTask() {

        }

        /**
         * Take the String representing the complete enterprises in JSON Format and
         * pull out the data we need to save the data to display in the frame bellow.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private LinkedList<Comment> getDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_SCORE = "score";
            final String OWM_OWNER = "owner";
            final String OWM_OWNER_IMAGE = "imageURL";

            final String OWM_OWNER_NAME = "name";

            JSONObject dataArray = new JSONObject(forecastJsonStr);

            LinkedList<Comment> resultStrs = new LinkedList<Comment>();
            // Get the JSON object representing the enterprise
            JSONObject owner = dataArray.getJSONObject(OWM_OWNER);
            mPostIt.setData(owner.getString(OWM_OWNER_NAME),
                    dataArray.getString(OWM_SCORE),
                    owner.getString(OWM_OWNER_IMAGE));
            return resultStrs;
        }

        @Override
        protected LinkedList<Comment> doInBackground(String... params) {

            if (params.length == 0)
                return null;
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String commentJsonStr = null;

            try {
                final String OPENCITY_BASE_URL =
                        "http://opencity-moonshot.herokuapp.com/api/postits/" + params[0];

                Uri builtUri = Uri.parse(OPENCITY_BASE_URL).buildUpon()
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI = " + builtUri.toString());

                // Create the request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    commentJsonStr = null;
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    commentJsonStr = null;
                    return null;
                }
                commentJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Club JSON String: " + commentJsonStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                commentJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getDataFromJson(commentJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(LinkedList<Comment> result) {
            Log.v(LOG_TAG, "LOAD ENDED");
            setData();
        }
    }
    private class PostComment extends AsyncTask<String, Void, Integer>{
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("https://opencity-moonshot.herokuapp.com/api/comments");
        StringBuilder builder = new StringBuilder();
        String token = mManager.getToken();
        int statueCode = 0;

        @Override
        protected Integer doInBackground(String... params) {
            try {

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("description", params[0]));
                nameValuePairs.add(new BasicNameValuePair("postit", params[1]));
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
                Toast.makeText(getActivity(), "Commentaire envoyé", Toast.LENGTH_LONG).show();
                setData();
            }
            else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                alertDialog.setTitle("Erreur");
                alertDialog.setMessage(getString(R.string.comment_failed));
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }

        }
    }
}
