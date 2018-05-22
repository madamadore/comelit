package it.matteoavanzini.comelit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.matteoavanzini.comelit.R;
import it.matteoavanzini.comelit.model.Post;

/**
 * Created by emme on 14/05/2018.
 */

public class SimplePostRecyclerViewAdapter
        extends RecyclerViewAdapter<Post, SimplePostRecyclerViewAdapter.ViewHolder> {

    public SimplePostRecyclerViewAdapter(OnSimpleItemRecyclerListener parent,
                                         List<Post> items) {
        super(parent, items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Post post) {
        holder.mIdView.setText(Integer.toString(post.getId()));
        holder.mContentView.setText(post.getTitle());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mIdView;
        final TextView mContentView;
        final ImageView mPreferred;

        ViewHolder(View itemLayout) {
            super(itemLayout);
            mIdView = (TextView) itemLayout.findViewById(R.id.id_text);
            mContentView = (TextView) itemLayout.findViewById(R.id.content);
            mPreferred = (ImageView) itemLayout.findViewById(R.id.preferred);
            mPreferred.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPreferred.setImageResource(R.drawable.ic_star_yellow_24dp);
                }
            });
        }
    }
}
