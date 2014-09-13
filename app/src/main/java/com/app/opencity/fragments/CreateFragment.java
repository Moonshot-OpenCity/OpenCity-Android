package com.app.opencity.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.app.opencity.R;
import com.app.opencity.models.CheckToken;
import com.app.opencity.models.CreateAccount;

/**
 * Created by mehdichouag on 12/09/2014.
 */
public class CreateFragment extends Fragment implements View.OnClickListener{
    private View mView;
    private Button mCreate;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mName;


    public CreateFragment()
    {
    }

    public static CreateFragment newInstance() {
        return new CreateFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_create, container, false);
        mCreate = (Button) mView.findViewById(R.id.create);
        mEmail = (EditText)mView.findViewById(R.id.email);
        mName = (EditText)mView.findViewById(R.id.name);
        mPassword = (EditText)mView.findViewById(R.id.password);
        mCreate.setOnClickListener(this);
        return mView;
    }
    @Override
    public void onClick(View v)
    {
        String name;
        String email;
        String password;
        CheckToken check;
        String ret = null;

        if (!(name = mName.getText().toString()).isEmpty() && !(email = mEmail.getText().toString()).isEmpty() && !(password = mPassword.getText().toString()).isEmpty())
        {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                CreateAccount account = new CreateAccount(getActivity());
                account.execute(email, password, name);
                check = new CheckToken(getActivity());
                check.execute();
            }
            else
                Toast.makeText(getActivity(), "Email non valide", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getActivity(), "Champs Incomplete", Toast.LENGTH_SHORT).show();
    }
}
