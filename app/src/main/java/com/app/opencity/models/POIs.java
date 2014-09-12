package com.app.opencity.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DAVID Flavien on 12/09/2014.
 */
public class POIs {
    private String mId;
    private int mType;
    private double[] mLocation = new double[2];
    private String mName;

    public POIs(String name, String id, int type, JSONArray location) {
        this.mName = name;
        this.mId = id;
        this.mType = type;
        try {
            this.mLocation[0] = location.getDouble(0);
            this.mLocation[1] = location.getDouble(1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getmId() {
        return mId;
    }

    public int getType() {
        return mType;
    }

    public String getName() {
        return mName;
    }

    public double getLatitude() {
        return mLocation[0];
    }

    public double getLongitude() {
        return mLocation[1];
    }
}
