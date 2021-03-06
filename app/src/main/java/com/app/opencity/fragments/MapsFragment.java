package com.app.opencity.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.opencity.R;
import com.app.opencity.activities.MainActivity;
import com.app.opencity.activities.PostActivity;
import com.app.opencity.activities.SettingActivity;
import com.app.opencity.models.POIs;
import com.app.opencity.models.PostIts;
import com.app.opencity.models.SessionManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class MapsFragment extends Fragment implements LocationListener,
        GooglePlayServicesClient.OnConnectionFailedListener,
        GooglePlayServicesClient.ConnectionCallbacks, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMapLongClickListener{
    public static final String ARG_MAP_NUMBER = "map_number";
    public static final String EXTRA_LONGITUDE = "extra_longitude";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    private LocationClient mLocationClient;
    private GoogleMap mMap;
    private Location mCurrentLocation;
	private PostItDetailFragment mFragmentDetail;
    private View mView = null;
    private Marker mAddMarker = null;
    private LinkedList<MarkerOptions> mMarkers = new LinkedList<MarkerOptions>();
    private LinkedList<PostIts> mPostIts;
    private LinkedList<POIs> mPOIs;
    private SessionManager mManager;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.menu_maps, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refreshMap();
                return true;
            case R.id.setting:
                createSettingActivity();
                return true;
            default:
                super.onOptionsItemSelected(item);

        }
        return true;
    }

    public static MapsFragment newInstance(int position) {
        MapsFragment fragment = new MapsFragment();
        Bundle args = new Bundle();
        args.putInt(MapsFragment.ARG_MAP_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        this.mView = inflater.inflate(R.layout.fragment_map, container, false);
        mLocationClient = new LocationClient(getActivity(), this, this);
        mManager = new SessionManager(getActivity().getApplicationContext());
		mFragmentDetail = ((PostItDetailFragment) getFragmentManager()
                .findFragmentById(R.id.fragmentDetailPostIt));
        hideDetailsFragment();
        return this.mView;
    }

    public void bindView(boolean isRefresh) {
        mMap = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.fragmentMap)).getMap();
        if (mMap != null && mLocationClient.isConnected() == true && mCurrentLocation != null) {
            LatLng place = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            if (!isRefresh)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place, 14));
            mMap.setMyLocationEnabled(true);
            mMap.setOnInfoWindowClickListener(this);
            mMap.setOnMapLongClickListener(this);
        }
    }

    public void onDestroyView() {
        super.onDestroyView();
        killFragment();
    }

    public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    public void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(getActivity(), "Connected", Toast.LENGTH_SHORT).show();
        mCurrentLocation = mLocationClient.getLastLocation();
        FetchPostItTask dataTask = new FetchPostItTask();
        dataTask.execute(String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()));
        if (ifPointOfInterest()) {
            FetchPOIsTask dataTaskPOIs = new FetchPOIsTask();
            dataTaskPOIs.execute(String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()));
        }
        if (mAddMarker != null){
            mAddMarker.remove();
            mAddMarker = null;
        }
        bindView(false);
    }

    private void refreshMap()
    {
        mCurrentLocation = mLocationClient.getLastLocation();
        FetchPostItTask dataTask = new FetchPostItTask();
        dataTask.execute(String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()));
        if (ifPointOfInterest()) {
            FetchPOIsTask dataTaskPOIs = new FetchPOIsTask();
            dataTaskPOIs.execute(String.valueOf(mCurrentLocation.getLatitude()), String.valueOf(mCurrentLocation.getLongitude()));
        }
        bindView(true);
    }

    public void putMarckersOnMap() {
        Log.v("TAG", "MarkerList Size = " + mMarkers.size());
        for (MarkerOptions element : mMarkers) {
            mMap.addMarker(element);
        }
    }

    public void postItToMarker(LinkedList<PostIts> list) {
        for (PostIts element : list) {
            LatLng place = new LatLng(element.getLocation()[1], element.getLocation()[0]);
            mMarkers.push(new MarkerOptions()
                    .title(element.getTitle())
                    .snippet(element.getDescription())
                    .position(place)
                    .icon(BitmapDescriptorFactory.defaultMarker(element.getType().equals("positive") ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED)));
        }
    }

    public void POIsToMarker(LinkedList<POIs> list) {
        for (POIs element : list) {
            LatLng place = new LatLng(element.getLatitude(), element.getLongitude());
            mMarkers.push(new MarkerOptions()
                    .title("POI")
                    .snippet(element.getName())
                    .position(place)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
        }
    }
    @Override
    public void onInfoWindowClick(final Marker marker) {
        LinearLayout layout = (LinearLayout) mView.findViewById(R.id.layout_fragment_details);
        layout.setVisibility(View.VISIBLE);
        if (!marker.getTitle().equals("POI") && !marker.getTitle().equals(getString(R.string.add_post))) {
			mFragmentDetail.setPostIt(getCurrentPostIt(marker));
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction()
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(mFragmentDetail)
                    .commit();
        }
        else if (marker.getTitle().equals(getString(R.string.add_post)))
        {
            if (!mManager.isLog()) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

                alertDialog.setTitle("ATTENTION");
                alertDialog.setMessage(getString(R.string.warning_post));
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        marker.remove();
                    }
                });
                alertDialog.show();
            }
            else {
                LatLng position = marker.getPosition();
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.putExtra(EXTRA_LATITUDE, position.latitude);
                intent.putExtra(EXTRA_LONGITUDE, position.longitude);
                startActivity(intent);
            }
        }
    }

	public void hideDetailsFragment() {
        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                .hide(mFragmentDetail)
                .commit();
        MainActivity.hideKeyboard(getActivity());
    }

    public PostIts getCurrentPostIt(Marker marker) {
        for (PostIts element : mPostIts) {
            if (element.getTitle().equals(marker.getTitle())) {
                return element;
            }
        }
        return null;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

        if (mAddMarker == null)
        {
            mAddMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .title(getString(R.string.add_post))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }
        else {
            mAddMarker.remove();
            mAddMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .draggable(true)
                    .title(getString(R.string.add_post))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
        }
    }

    public class FetchPostItTask extends AsyncTask<String, Void, LinkedList<PostIts>> {

        private final String LOG_TAG = FetchPostItTask.class.getSimpleName();

        public FetchPostItTask() {

        }


        private LinkedList<PostIts> getDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_TITLE = "title";
            final String OWM_DESCRIPTION = "description";
            final String OWM_TYPE = "type";
            final String OWM_OWNER = "owner";
            final String OWM_LOCATION = "location";
            final String OWM_ID = "_id";
            final String OWM_CREATED = "createdAt";

            JSONArray dataArray = new JSONArray(forecastJsonStr);

            mMarkers.clear();
            LinkedList<PostIts> resultStrs = new LinkedList<PostIts>();
            Log.v(LOG_TAG, dataArray.length() + "");
            for (int i = 0; i < dataArray.length(); i++) {
                // Get the JSON object representing the enterprise
                JSONObject dataPostIt = dataArray.getJSONObject(i);
                resultStrs.push(new PostIts(
                                dataPostIt.getString(OWM_TITLE),
                                dataPostIt.getString(OWM_DESCRIPTION),
                                dataPostIt.getString(OWM_TYPE),
                                dataPostIt.getString(OWM_OWNER),
                                dataPostIt.getJSONArray(OWM_LOCATION),
                                dataPostIt.getString(OWM_ID),
                                dataPostIt.getString(OWM_CREATED))
                );
            }
            Log.v(LOG_TAG, resultStrs.size() + "");
            postItToMarker(resultStrs);
            mPostIts = resultStrs;
            return resultStrs;
        }

        @Override
        protected LinkedList<PostIts> doInBackground(String... params) {

            if (params.length == 0)
                return null;
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String postItJsonStr = null;

            try {
                final String OPENCITY_BASE_URL =
                        "http://opencity-moonshot.herokuapp.com/api/postits/searchByLocation?";
                final String LAT_PARAM = "lat";
                final String LON_PARAM = "lon";

                Uri builtUri = Uri.parse(OPENCITY_BASE_URL).buildUpon()
                        .appendQueryParameter(LAT_PARAM, params[0])
                        .appendQueryParameter(LON_PARAM, params[1])
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
                    postItJsonStr = null;
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
                    postItJsonStr = null;
                    return null;
                }
                postItJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Club JSON String: " + postItJsonStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                postItJsonStr = null;
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
                return getDataFromJson(postItJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(LinkedList<PostIts> result) {
            Log.v(LOG_TAG, "LOAD ENDED");
            putMarckersOnMap();
        }
    }

    public class FetchPOIsTask extends AsyncTask<String, Void, LinkedList<POIs>> {

        private final String LOG_TAG = FetchPostItTask.class.getSimpleName();

        public FetchPOIsTask() {

        }

        private LinkedList<POIs> getDataFromJson(String forecastJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_NAME = "name";
            final String OWM_TYPE = "type";
            final String OWM_LOCATION = "loc";
            final String OWM_ID = "_id";

            JSONArray dataArray = new JSONArray(forecastJsonStr);

            LinkedList<POIs> resultStrs = new LinkedList<POIs>();
            Log.v(LOG_TAG, dataArray.length() + "");
            for (int i = 0; i < dataArray.length(); i++) {
                // Get the JSON object representing the enterprise
                JSONObject dataPostIt = dataArray.getJSONObject(i);
                resultStrs.push(new POIs(
                                dataPostIt.getString(OWM_NAME),
                                dataPostIt.getString(OWM_ID),
                                dataPostIt.getInt(OWM_TYPE),
                                dataPostIt.getJSONArray(OWM_LOCATION)));
            }
            Log.v(LOG_TAG, resultStrs.size() + "");
            POIsToMarker(resultStrs);
            mPOIs = resultStrs;
            return resultStrs;
        }

        @Override
        protected LinkedList<POIs> doInBackground(String... params) {

            if (params.length == 0)
                return null;
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String postItJsonStr = null;

            try {
                final String OPENCITY_BASE_URL =
                        "http://opencity-moonshot.herokuapp.com/api/pois/searchByLocation?";
                final String LAT_PARAM = "lat";
                final String LON_PARAM = "lon";

                Uri builtUri = Uri.parse(OPENCITY_BASE_URL).buildUpon()
                        .appendQueryParameter(LAT_PARAM, params[0])
                        .appendQueryParameter(LON_PARAM, params[1])
                        .appendQueryParameter("limit", "500")
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
                    postItJsonStr = null;
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
                    postItJsonStr = null;
                    return null;
                }
                postItJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Club JSON String: " + postItJsonStr);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                postItJsonStr = null;
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
                return getDataFromJson(postItJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(LinkedList<POIs> result) {
            Log.v(LOG_TAG, "LOAD ENDED");
            putMarckersOnMap();
        }
    }

    private void createSettingActivity()
    {
        startActivity(new Intent(getActivity().getApplicationContext(), SettingActivity.class));
    }

    private boolean ifPointOfInterest()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getBoolean(getString(R.string.pref_POI_key), false);
    }
    private void killFragment()
    {
        Fragment fragment = (getFragmentManager().findFragmentById(R.id.fragmentMap));
        FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();

        if (fragment != null) {
            ft.remove(fragment);
			ft.remove(mFragmentDetail);
            ft.commit();
        }
    }
}