package com.app.opencity.models;

import android.content.Context;
import android.content.SharedPreferences;

import com.app.opencity.R;

/**
 * Created by mehdichouag on 11/09/2014.
 */
public class SessionManager
{
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Context mContext;

    private final String PACKAGE_NAME = "com.app.opencity";

    public SessionManager(Context context)
    {
        this.mContext = context;
        prefs = mContext.getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void connectUser(String token)
    {
        editor.putBoolean(mContext.getString(R.string.is_login), true);
        editor.putString(mContext.getString(R.string.pref_token_key), token);
        editor.commit();
    }

    public String getToken()
    {
        return prefs.getString(mContext.getString(R.string.pref_token_key), null);
    }

    public void logout()
    {
        editor.clear();
        editor.commit();
    }
}
