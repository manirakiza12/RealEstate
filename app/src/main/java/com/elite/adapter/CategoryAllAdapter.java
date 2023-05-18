package com.elite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.elite.item.Category;
import com.example.realestate.R;
import com.elite.util.OnClick;
import com.example.realestate.databinding.RowAllCatBinding;

import org.jetbrains.annotations.NotNull;

import java.util.List;


public class CategoryAllAdapter extends RecyclerView.Adapter<CategoryAllAdapter.ViewHolder> {

    Activity activity;
    List<Category> categoryList;
    OnClick onClick;
    private int row_index = -1;

    public CategoryAllAdapter(Activity activity, List<Category> categoryList) {
        this.activity = activity;
        this.categoryList = categoryList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowAllCatBinding.inflate(activity.getLayoutInflater()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {


        holder.rowAllCatBinding.tvAllCat.setText(categoryList.get(position).getCategoryName());

        if (row_index == position) {
            holder.rowAllCatBinding.mcHomeCat.setCardBackgroundColor(activity.getResources().getColor(R.color.card_select_bg));
            holder.rowAllCatBinding.tvAllCat.setTextColor(activity.getResources().getColor(R.color.card_select_text));
        } else {
            holder.rowAllCatBinding.mcHomeCat.setCardBackgroundColor(activity.getResources().getColor(R.color.card_normal_bg));
            holder.rowAllCatBinding.tvAllCat.setTextColor(activity.getResources().getColor(R.color.card_normal_text));
        }
        holder.rowAllCatBinding.mcHomeCat.setOnClickListener(view -> onClick.position(holder.getBindingAdapterPosition()));
    }

    public void select(int position) {
        row_index = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RowAllCatBinding rowAllCatBinding;

        public ViewHolder(RowAllCatBinding rowAllCatBinding) {
            super(rowAllCatBinding.getRoot());
            this.rowAllCatBinding = rowAllCatBinding;
        }
    }

    public void setOnItemClickListener(OnClick clickListener) {
        this.onClick = clickListener;
    }
}
