package com.app.opencity.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.opencity.R;
import com.app.opencity.activities.MainActivity;
import com.app.opencity.models.SessionManager;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PROFILE_NUMBER = "profile_number";

    private SessionManager mManager;
    private View mView;
    private Button mDisconnect;
    private Activity mActivity;

    public static ProfileFragment newInstance(int position) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PROFILE_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        mActivity = getActivity();
        mManager = new SessionManager(mActivity.getApplicationContext());
        mDisconnect = (Button)mView.findViewById(R.id.disconnect);
        mDisconnect.setOnClickListener(this);
        return mView;
    }

    @Override
    public void onClick(View v) {
        if (v == mDisconnect)
        {
            mManager.logout();
            ((MainActivity)mActivity).disconnectUser();
        }
    }
}
