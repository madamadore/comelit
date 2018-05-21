package it.matteoavanzini.comelit.adapter;

import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by emme on 14/05/2018.
 */

public abstract
        class RecyclerViewAdapter<T extends Parcelable, H extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<H> {

    public interface OnSimpleItemRecyclerListener {
        void onSimpleRecyclerItemSelected(Parcelable item);
    }

    protected OnSimpleItemRecyclerListener mListener;
    protected final List<T> mValues;

    public RecyclerViewAdapter(OnSimpleItemRecyclerListener listener,
                               List<T> items) {
        mValues = items;
        mListener = listener;
    }

    public abstract void onBindViewHolder(H holder, T item);

    @Override
    public void onBindViewHolder(H holder, int position) {
        final T item = mValues.get(position);
        onBindViewHolder(holder, item);

        holder.itemView.setTag(item);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onSimpleRecyclerItemSelected(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

}
