package com.elite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elite.database.DatabaseHelperRecent;
import com.elite.item.Property;
import com.elite.util.AdInterstitialAds;
import com.elite.util.Constant;
import com.elite.util.Method;
import com.elite.util.OnClick;
import com.example.realestate.R;
import com.example.realestate.databinding.RowHomeRecentBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class RecentHomeAdapter extends RecyclerView.Adapter<RecentHomeAdapter.ViewHolder> {

    Activity activity;
    List<Property> propertyList;
    OnClick onClick;
    Method method;
    DatabaseHelperRecent databaseHelperRecent;

    public RecentHomeAdapter(Activity activity, List<Property> propertyList) {
        this.activity = activity;
        this.propertyList = propertyList;
        method=new Method(activity);
        databaseHelperRecent=new DatabaseHelperRecent(activity);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowHomeRecentBinding.inflate(activity.getLayoutInflater()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Property propertyItem=propertyList.get(position);
        if (!propertyItem.getPropertyImage().equals("")) {
            Glide.with(activity.getApplicationContext()).load(propertyItem.getPropertyImage())
                    .placeholder(R.drawable.property_placeholder)
                    .into(holder.rowHomeItemBinding.ivRecent);
        }

         holder.rowHomeItemBinding.tvPrice.setText(Constant.constantCurrency+method.convertDec(propertyItem.getPropertyPrice()));
        holder.rowHomeItemBinding.tvTitle.setText(propertyItem.getPropertyTitle());
        holder.rowHomeItemBinding.tvLocation.setText(propertyItem.getPropertyAddress());

        holder.rowHomeItemBinding.mcRecent.setOnClickListener(view -> AdInterstitialAds.ShowInterstitialAds(activity,holder.getBindingAdapterPosition(),onClick));
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RowHomeRecentBinding rowHomeItemBinding;

        public ViewHolder(RowHomeRecentBinding rowHomeItemBinding) {
            super(rowHomeItemBinding.getRoot());
            this.rowHomeItemBinding = rowHomeItemBinding;
        }
    }

    public void setOnItemClickListener(OnClick clickListener) {
        this.onClick = clickListener;
    }
}
