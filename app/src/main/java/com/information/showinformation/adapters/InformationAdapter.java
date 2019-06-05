package com.information.showinformation.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.information.showinformation.ItemFragment.OnListFragmentInteractionListener;
import com.information.showinformation.R;
import com.information.showinformation.models.InformationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link InformationModel} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class InformationAdapter extends RecyclerView.Adapter<InformationAdapter.ViewHolder> {

    private final List<InformationModel> mValues;
    private final OnListFragmentInteractionListener mListener;

    public InformationAdapter(OnListFragmentInteractionListener listener) {
        mValues = new ArrayList<>();
        mListener = listener;
    }

    public void addItem(String info){
        mValues.add((new InformationModel(info)));
    }

    public void clearAll(){
        mValues.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.infoTextItem.setText(mValues.get(position).getText());
        holder.startActiveItem.setText(mValues.get(position).getStartString());
        holder.endActiveItem.setText(mValues.get(position).getEndString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView infoTextItem;
        public TextView startActiveItem;
        public TextView endActiveItem;
        public InformationModel mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            infoTextItem = mView.findViewById(R.id.info_text_item);
            startActiveItem = mView.findViewById(R.id.date_from_item);
            endActiveItem = mView.findViewById(R.id.date_to_item);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + infoTextItem.getText() + "'";
        }
    }
}
