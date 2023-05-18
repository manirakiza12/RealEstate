package com.elite.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.realestate.R;
import java.util.ArrayList;


public class AmenitiesAdapter extends RecyclerView.Adapter<AmenitiesAdapter.ItemRowHolder> {

    ArrayList<String> dataList;
    Context mContext;

    public AmenitiesAdapter(Context context, ArrayList<String> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ItemRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_amenities, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemRowHolder holder, final int position) {
        holder.text.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public static class ItemRowHolder extends RecyclerView.ViewHolder {
        public TextView text;
        private ItemRowHolder(View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tvAmenities);
        }
    }
}
