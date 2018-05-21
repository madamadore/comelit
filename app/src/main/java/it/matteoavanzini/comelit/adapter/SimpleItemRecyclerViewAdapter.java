package it.matteoavanzini.comelit.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.matteoavanzini.comelit.PostDetailActivity;
import it.matteoavanzini.comelit.fragment.PostDetailFragment;
import it.matteoavanzini.comelit.R;
import it.matteoavanzini.comelit.dummy.DummyContent;

/**
 * Created by emme on 14/05/2018.
 */
@Deprecated
public class SimpleItemRecyclerViewAdapter
        extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

    public interface OnSimpleItemRecyclerListener {
        void onSimpleItemRecyclerItemSelected(String itemId);
    }

    private OnSimpleItemRecyclerListener mListener;
    private final FragmentActivity mParentActivity;
    private final List<DummyContent.DummyItem> mValues;
    private final boolean mTwoPane;

    public SimpleItemRecyclerViewAdapter(FragmentActivity parent,
                                  List<DummyContent.DummyItem> items,
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
            DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();

            if (mTwoPane) {
                Bundle arguments = new Bundle();
                arguments.putString(PostDetailFragment.ARG_ITEM_ID, item.id);
                PostDetailFragment fragment = new PostDetailFragment();
                fragment.setArguments(arguments);

                mParentActivity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.post_detail_container, fragment)
                        .commit();

                if (mListener != null) {
                    mListener.onSimpleItemRecyclerItemSelected(item.id);
                }

            } else {
                Context context = view.getContext();
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra(PostDetailFragment.ARG_ITEM_ID, item.id);

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
        DummyContent.DummyItem item = mValues.get(position);
        holder.mIdView.setText(item.id);
        holder.mContentView.setText(item.content);

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

        ViewHolder(View itemLayout) {
            super(itemLayout);
            mIdView = (TextView) itemLayout.findViewById(R.id.id_text);
            mContentView = (TextView) itemLayout.findViewById(R.id.content);
        }
    }
}
