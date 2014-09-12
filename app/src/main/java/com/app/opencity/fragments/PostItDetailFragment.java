package com.app.opencity.fragments;



import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.opencity.R;
import com.app.opencity.models.PostIts;

import org.w3c.dom.Text;

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
    private PostIts mPostIt;
    private FrameLayout mFrame;

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
        mFrame = (FrameLayout) v.findViewById(R.id.layoutDetails);
    }

    public void setData() {
        Log.v("SET DATA", "In");
        if (mPostIt != null) {
            mTitle.setText(mPostIt.getTitle());
            mDescription.setText(mPostIt.getDescription());
            mOwner.setText(Html.fromHtml(String.format(getString(R.string.ownerDetail), mPostIt.getOwmer(), "1 jour")));
            if (mPostIt.getType().equals("positive")) {
                mFrame.setBackgroundColor(getResources().getColor(R.color.green));
            } else {
                mFrame.setBackgroundColor(getResources().getColor(R.color.red));
            }
        }
    }

    public void setPostIt(PostIts postIt) {
        this.mPostIt = postIt;
        setData();
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
    }
}
