package com.elite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elite.item.Category;
import com.example.realestate.R;
import com.elite.util.AdInterstitialAds;
import com.elite.util.OnClick;
import com.example.realestate.databinding.RowHomeCatBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class CategoryHomeAdapter extends RecyclerView.Adapter<CategoryHomeAdapter.ViewHolder> {

    Activity activity;
    List<Category> categoryList;
    OnClick onClick;
    private final int[] catText;

    public CategoryHomeAdapter(Activity activity, List<Category> categoryList, int[] catText) {
        this.activity = activity;
        this.categoryList = categoryList;
        this.catText = catText;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowHomeCatBinding.inflate(activity.getLayoutInflater()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (!categoryList.get(position).getCategoryImage().equals("")) {
            Glide.with(activity.getApplicationContext()).load(categoryList.get(position).getCategoryImage())
                    .placeholder(R.drawable.home_cat_placeholder)
                    .into(holder.rowHomeCatBinding.ivHomeCat);
        }
        holder.rowHomeCatBinding.tvHomeCatTitle.setTextColor(catText[position % catText.length]);
        holder.rowHomeCatBinding.tvHomeCatTitle.setText(categoryList.get(position).getCategoryName());

        holder.rowHomeCatBinding.mcHomeCat.setOnClickListener(view -> AdInterstitialAds.ShowInterstitialAds(activity, holder.getBindingAdapterPosition(), onClick));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RowHomeCatBinding rowHomeCatBinding;

        public ViewHolder(RowHomeCatBinding rowHomeCatBinding) {
            super(rowHomeCatBinding.getRoot());
            this.rowHomeCatBinding = rowHomeCatBinding;
        }
    }

    public void setOnItemClickListener(OnClick clickListener) {
        this.onClick = clickListener;
    }
}
