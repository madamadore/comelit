package it.matteoavanzini.comelit.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.matteoavanzini.comelit.PostDetailActivity;
import it.matteoavanzini.comelit.R;
import it.matteoavanzini.comelit.dummy.Post;
import it.matteoavanzini.comelit.fragment.PostDetailFragment;

/**
 * Created by emme on 14/05/2018.
 */

public class SimplePostRecyclerViewAdapter
        extends RecyclerView.Adapter<SimplePostRecyclerViewAdapter.ViewHolder> {

    public interface OnSimplePostRecyclerListener {
        void onSimplePostRecyclerSelected(Post post);
    }

    private OnSimplePostRecyclerListener mListener;
    private final FragmentActivity mParentActivity;
    private final List<Post> mValues;
    private final boolean mTwoPane;

    public SimplePostRecyclerViewAdapter(FragmentActivity parent,
                                         List<Post> items,
                                         boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    public void setOnSimplePostRecyclerListener(OnSimplePostRecyclerListener listener) {
        this.mListener = listener;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Post item = (Post) view.getTag();

            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putParcelable(PostDetailFragment.ARG_ITEM_ID, item);
                PostDetailFragment fragment = new PostDetailFragment();
                fragment.setArguments(arguments);

                mParentActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.post_detail_container, fragment)
                        .commit();

                if (mListener != null) {
                    mListener.onSimplePostRecyclerSelected(item);
                }

            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra(PostDetailFragment.ARG_ITEM_ID, item);

                context.startActivity(intent);
            }
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Post item = mValues.get(position);
        holder.mIdView.setText(Integer.toString(item.getId()));
        holder.mContentView.setText(item.getTitle());

        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
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
