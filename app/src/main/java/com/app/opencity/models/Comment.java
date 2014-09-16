package com.app.opencity.models;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by DAVID Flavien on 14/09/2014.
 */
public class Comment {
    private String mId;
    private String mCreation;
    private String mOwnerName;
    private String mOwnerId;
    private String mOwnerMail;
    private String mDescription;
    private String mPicture;

    public Comment(String description, String ownerName, String ownerId, String ownerMail, String id, String creation, String picture) {
        this.mDescription = description;
        this.mOwnerName = ownerName;
        this.mOwnerId = ownerId;
        this.mOwnerMail = ownerMail;
        this.mId = id;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = null;
        try {
            d = sdf.parse(creation);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String formattedTime = output.format(d);
        Log.v("CONVERTED DATE", "-> " + formattedTime);
        this.mCreation = new DateTimeUtils().calcDiff(formattedTime);
        this.mPicture = picture;
    }

    public String getmPicture(){
        return mPicture;
    }

    public String getmId() {
        return mId;
    }

    public String getCreation() {
        return mCreation;
    }

    public String getOwnerName() {
        return mOwnerName;
    }

    public String getOwnerId() {
        return mOwnerId;
    }

    public String getOwnerMail() {
        return mOwnerMail;
    }

    public String getDescription() {
        return mDescription;
    }
}
