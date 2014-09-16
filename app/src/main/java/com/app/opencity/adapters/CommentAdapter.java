package com.app.opencity.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.opencity.R;
import com.app.opencity.models.CircleTransform;
import com.app.opencity.models.Comment;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;

/**
 * Created by DAVID Flavien on 14/09/2014.
 */
public class CommentAdapter extends ArrayAdapter<Comment> {

    private final LinkedList<Comment> mComments;
    private Context mContext;

    public CommentAdapter(Context context, int resource, LinkedList<Comment> objects) {
        super(context, resource, objects);
        this.mComments = objects;
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.rows_comment_layout, null);
        }

        final Comment comment = mComments.get(position);

        if (comment != null) {
            TextView description = (TextView) v.findViewById(R.id.desctiption_comment);
            final TextView details = (TextView) v.findViewById(R.id.details_comment);
            ImageView icon = (ImageView) v.findViewById(R.id.ic_profile);
            description.setText(comment.getDescription());
            Picasso.with(mContext).load(comment.getmPicture()).placeholder(R.drawable.ic_placeholder).transform(new CircleTransform()).into(icon);
            details.setText(Html.fromHtml(String.format(getContext().getResources().getString(R.string.ownerDetail), comment.getOwnerName(), comment.getCreation())));
        }

        return v;
    }

}
