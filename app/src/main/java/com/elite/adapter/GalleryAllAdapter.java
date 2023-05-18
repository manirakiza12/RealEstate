package com.elite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elite.item.Gallery;
import com.example.realestate.R;
import com.example.realestate.databinding.RowGalleryAllBinding;
import com.elite.util.OnClick;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class GalleryAllAdapter extends RecyclerView.Adapter<GalleryAllAdapter.ViewHolder> {

    Activity activity;
    List<Gallery> galleryList;
    OnClick onClick;

    public GalleryAllAdapter(Activity activity, List<Gallery> galleryList) {
        this.activity = activity;
        this.galleryList = galleryList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowGalleryAllBinding.inflate(activity.getLayoutInflater()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (!galleryList.get(position).getGallery_image().equals("")) {
            Glide.with(activity.getApplicationContext()).load(galleryList.get(position).getGallery_image())
                    .placeholder(R.drawable.property_placeholder)
                    .into(holder.rowGalleryBinding.ivGallery);
        }

        holder.rowGalleryBinding.ivGallery.setOnClickListener(view -> onClick.position(holder.getBindingAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RowGalleryAllBinding rowGalleryBinding;

        public ViewHolder(RowGalleryAllBinding rowGalleryBinding) {
            super(rowGalleryBinding.getRoot());
            this.rowGalleryBinding = rowGalleryBinding;
        }
    }

    public void setOnItemClickListener(OnClick clickListener) {
        this.onClick = clickListener;
    }
}
