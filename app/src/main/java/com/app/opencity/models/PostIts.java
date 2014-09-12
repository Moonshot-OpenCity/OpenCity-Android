package com.app.opencity.models;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

/**
 * Created by DAVID Flavien on 11/09/2014.
 */
public class PostIts implements Serializable {
    private String mTitle;
    private String mDescription;
    private String mType;
    private String mOwmer;
    private double[] mLocation = new double[2];
    private String mId;
    private String mCreation;

    public PostIts(String title, String description, String type, String owner, JSONArray location, String id, String creation) {
        this.mTitle = title;
        this.mDescription = description;
        this.mType = type;
        this.mOwmer = owner;
        try {
            this.mLocation[0] = location.getDouble(1);
            this.mLocation[1] = location.getDouble(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.mId = id;
        this.mCreation = creation;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getType() {
        return mType;
    }

    public String getOwmer() {
        return mOwmer;
    }

    public double[] getLocation() {
        return mLocation;
    }

    public String getmId() {
        return mId;
    }

    public String getCreation() {
        return mCreation;
    }
}
