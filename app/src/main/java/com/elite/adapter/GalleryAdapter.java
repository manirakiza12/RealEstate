package com.elite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elite.item.Gallery;
import com.example.realestate.R;
import com.elite.util.AdInterstitialAds;
import com.elite.util.OnClick;
import com.example.realestate.databinding.RowGalleryBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

    Activity activity;
    List<Gallery> galleryList;
    OnClick onClick;
    int itemSize;
    public GalleryAdapter(Activity activity, List<Gallery> galleryList,int itemSize) {
        this.activity = activity;
        this.galleryList = galleryList;
        this.itemSize=itemSize;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowGalleryBinding.inflate(activity.getLayoutInflater()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (!galleryList.get(position).getGallery_image().equals("")) {
            Glide.with(activity.getApplicationContext()).load(galleryList.get(position).getGallery_image())
                    .placeholder(R.drawable.property_placeholder)
                    .into(holder.rowGalleryBinding.ivGallery);
        }

        if (position==2){
            holder.rowGalleryBinding.tvSize.setText(activity.getString(R.string.item_size,itemSize));
        }else {
            holder.rowGalleryBinding.vwGalleyShadow.setVisibility(View.GONE);
            holder.rowGalleryBinding.llSize.setVisibility(View.GONE);
        }


        holder.rowGalleryBinding.ivGallery.setOnClickListener(view -> AdInterstitialAds.ShowInterstitialAds(activity, holder.getBindingAdapterPosition(), onClick));
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RowGalleryBinding rowGalleryBinding;

        public ViewHolder(RowGalleryBinding rowGalleryBinding) {
            super(rowGalleryBinding.getRoot());
            this.rowGalleryBinding = rowGalleryBinding;
        }
    }

    public void setOnItemClickListener(OnClick clickListener) {
        this.onClick = clickListener;
    }
}
