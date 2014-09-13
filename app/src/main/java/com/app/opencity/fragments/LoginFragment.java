package com.app.opencity.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.app.opencity.R;
import com.app.opencity.activities.MainActivity;
import com.app.opencity.models.CheckToken;
import com.app.opencity.models.LoginToken;
import com.app.opencity.models.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    public static final String ARG_LOGIN_NUMBER = "login_number";

    private MainActivity mActivity;
    private SessionManager mManager;
    private Button mSend;
    private Button mCreate;
    private View    mView;
    private EditText mEmail;
    private EditText mPassword;


    public LoginFragment()
    {
    }

    public static LoginFragment newInstance(int position) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putInt(LoginFragment.ARG_LOGIN_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mActivity = (MainActivity)getActivity();
        mManager = new SessionManager(getActivity().getApplicationContext());
        mView = inflater.inflate(R.layout.fragment_login, container, false);
        mSend = (Button) mView.findViewById(R.id.connect);
        mCreate = (Button) mView.findViewById(R.id.create);
        mEmail = (EditText)mView.findViewById(R.id.email);
        mPassword = (EditText)mView.findViewById(R.id.password);
        mSend.setOnClickListener(this);
        mCreate.setOnClickListener(this);
        return mView;
    }



    @Override
    public void onClick(View v)
    {
        LoginToken token;
        CheckToken check;

        if (v == mSend)
        {
            mManager.logout();
            token = new LoginToken(getActivity());
            token.execute(mEmail.getText().toString(), mPassword.getText().toString());
            check = new CheckToken(getActivity());
            check.execute();
        }
        else if (v == mCreate)
            createAccountFragment();

    }

    public void createAccountFragment()
    {
        Fragment fragment = CreateFragment.newInstance();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction= fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.content_frame, fragment).commit();
    }
}
