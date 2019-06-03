package com.information.showinformation.adapters;

import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.information.showinformation.R;
import com.information.showinformation.models.InformationModel;

import java.util.ArrayList;
import java.util.List;

public class InformationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<InformationModel> mInfoList = new ArrayList<InformationModel>();

    public void addItem(InformationModel item){
        if (!mInfoList.contains(item))
        mInfoList.add(item);
    }

    public void clear(){
        mInfoList.clear();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = layoutInflater.inflate(R.layout.list_row, parent, false);
        Log.e("TAGGGGGGG", "ggggggggggggggggggggggggggggggggggggggg");
        return new InfoViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof InfoViewHolder)
            ((InfoViewHolder) viewHolder).bind(mInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return mInfoList.size();
    }

    class InfoViewHolder extends RecyclerView.ViewHolder {

        private TextView infoTextItem;
        private TextView startActiveItem;
        private TextView endActiveItem;

        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            infoTextItem = itemView.findViewById(R.id.info_text_item);
            startActiveItem = itemView.findViewById(R.id.date_from_item);
            endActiveItem = itemView.findViewById(R.id.date_to_item);
        }

        public void bind(InformationModel raw) {
            infoTextItem.setText(raw.getText());
            startActiveItem.setText(raw.getStartDate()==null?"":raw.getStartDate().toString());
            endActiveItem.setText(raw.getEndDate()==null?"":raw.getEndDate().toString());
        }

    }
}
