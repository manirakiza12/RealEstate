package com.elite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elite.item.Property;
import com.elite.realestate.LoginActivity;
import com.example.realestate.R;
import com.elite.util.AdInterstitialAds;
import com.elite.util.Constant;
import com.elite.util.FavouriteIF;
import com.elite.util.Method;
import com.elite.util.OnClick;
import com.example.realestate.databinding.RowHomeItemBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class LatestHomeAdapter extends RecyclerView.Adapter<LatestHomeAdapter.ViewHolder> {

    Activity activity;
    List<Property> propertyList;
    OnClick onClick;
    Method method;

    public LatestHomeAdapter(Activity activity, List<Property> propertyList) {
        this.activity = activity;
        this.propertyList = propertyList;
        method=new Method(activity);
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowHomeItemBinding.inflate(activity.getLayoutInflater()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        Property propertyItem=propertyList.get(position);
        if (!propertyItem.getPropertyImage().equals("")) {
            Glide.with(activity.getApplicationContext()).load(propertyItem.getPropertyImage())
                    .placeholder(R.drawable.property_placeholder)
                    .into(holder.rowHomeItemBinding.ivHomeItem);
        }
        if (propertyItem.getPropertyPurpose().equals("Rent")){
            holder.rowHomeItemBinding.tvRentSell.setText(activity.getString(R.string.rent_text));
            holder.rowHomeItemBinding.mcRentSellBG.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.rent_bg));
        }else {
            holder.rowHomeItemBinding.tvRentSell.setText(activity.getString(R.string.sell_text));
            holder.rowHomeItemBinding.mcRentSellBG.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.sell_bg));
        }

        holder.rowHomeItemBinding.tvView.setText(method.Format(propertyItem.getPropertyViews()));
        holder.rowHomeItemBinding.tvPrice.setText(Constant.constantCurrency+method.convertDec(propertyItem.getPropertyPrice()));
        holder.rowHomeItemBinding.tvTitle.setText(propertyItem.getPropertyTitle());
        holder.rowHomeItemBinding.tvLocation.setText(propertyItem.getPropertyAddress());

        holder.rowHomeItemBinding.mcHomeItem.setOnClickListener(view -> AdInterstitialAds.ShowInterstitialAds(activity,holder.getBindingAdapterPosition(),onClick));

        if (propertyItem.getPropertyFavorite()) {
            holder.rowHomeItemBinding.ibFav.setImageResource(R.drawable.ic_fav_hover);
        } else {
            holder.rowHomeItemBinding.ibFav.setImageResource(R.drawable.ic_fav);
        }
        holder.rowHomeItemBinding.ibFav.setOnClickListener(view -> {
            if (method.getIsLogin()) {
                FavouriteIF favouriteIF = (isFavourite, message) -> {
                    if (isFavourite.equals("true")) {
                        holder.rowHomeItemBinding.ibFav.setImageResource(R.drawable.ic_fav_hover);
                    } else {
                        holder.rowHomeItemBinding.ibFav.setImageResource(R.drawable.ic_fav);
                    }
                };
                method.addToFav(propertyItem.getPropertyId(), method.getUserId(), "Property", favouriteIF);
            } else {
                Toast.makeText(activity, activity.getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(activity, LoginActivity.class);
                intentLogin.putExtra("isFromDetail", true);
                activity.startActivity(intentLogin);
            }
        });
    }

    @Override
    public int getItemCount() {
        return propertyList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RowHomeItemBinding rowHomeItemBinding;

        public ViewHolder(RowHomeItemBinding rowHomeItemBinding) {
            super(rowHomeItemBinding.getRoot());
            this.rowHomeItemBinding = rowHomeItemBinding;
        }
    }

    public void setOnItemClickListener(OnClick clickListener) {
        this.onClick = clickListener;
    }
}
