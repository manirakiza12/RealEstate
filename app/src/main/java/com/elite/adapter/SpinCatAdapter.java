package com.elite.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.elite.item.Category;

import java.util.List;

public class SpinCatAdapter extends ArrayAdapter<Category> {

    private final List<Category> mList;

    public SpinCatAdapter(@NonNull Context context, int resource, @NonNull List<Category> objects) {
        super(context, resource, objects);
        this.mList = objects;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Category getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextColor(Color.BLACK);
        label.setText(mList.get(position).getCategoryName());
        return label;
    }
}
