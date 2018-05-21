package it.matteoavanzini.comelit.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import it.matteoavanzini.comelit.PostDetailActivity;
import it.matteoavanzini.comelit.R;
import it.matteoavanzini.comelit.fragment.PostDetailFragment;

/**
 * Created by emme on 14/05/2018.
 */

public abstract
        class SimpleRecyclerViewAdapter<T extends Parcelable, H extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<H> {

    public interface OnSimpleItemRecyclerListener {
        void onSimpleRecyclerItemSelected(Parcelable item);
    }

    private OnSimpleItemRecyclerListener mListener;
    private final FragmentActivity mParentActivity;
    private final List<T> mValues;
    private final boolean mTwoPane;

    public SimpleRecyclerViewAdapter(FragmentActivity parent,
                                     List<T> items,
                                     boolean twoPane) {
        mValues = items;
        mParentActivity = parent;
        mTwoPane = twoPane;
    }

    public void setOnSimpleItemRecyclerListener(OnSimpleItemRecyclerListener listener) {
        this.mListener = listener;
    }

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            T item = (T) view.getTag();

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
                    mListener.onSimpleRecyclerItemSelected(item);
                }

            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra(PostDetailFragment.ARG_ITEM_ID, item);

                context.startActivity(intent);
            }
        }
    };

    public abstract void onBindViewHolder(H holder, T item);

    @Override
    public void onBindViewHolder(H holder, int position) {
        T item = mValues.get(position);
        onBindViewHolder(holder, item);

        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(mOnClickListener);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
