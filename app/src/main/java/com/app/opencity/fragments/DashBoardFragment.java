package com.app.opencity.fragments;



import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.opencity.R;

/**
 * A simple {@link Fragment} subclass.
 *
 */
public class DashBoardFragment extends Fragment {
    public static final String ARG_DASHBOARD_NUMBER = "dashboard_number";

    public DashBoardFragment() {
        // Required empty public constructor
    }

    public static DashBoardFragment newInstance(int position) {
        DashBoardFragment fragment = new DashBoardFragment();
        Bundle args = new Bundle();
        args.putInt(DashBoardFragment.ARG_DASHBOARD_NUMBER, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dash_board, container, false);
    }


}
