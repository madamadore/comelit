package it.matteoavanzini.comelit.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.matteoavanzini.comelit.R;
import it.matteoavanzini.comelit.model.Comment;

/**
 * Created by emme on 14/05/2018.
 */

public class CommentRecyclerViewAdapter
        extends RecyclerViewAdapter<Comment, CommentRecyclerViewAdapter.ViewHolder> {

    public CommentRecyclerViewAdapter(OnSimpleItemRecyclerListener parent,
                                      List<Comment> items) {
        super(parent, items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, Comment comment) {
        holder.mNameView.setText(comment.getName());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView mNameView;

        ViewHolder(View itemLayout) {
            super(itemLayout);
            mNameView = (TextView) itemLayout.findViewById(R.id.name);
        }
    }
}
