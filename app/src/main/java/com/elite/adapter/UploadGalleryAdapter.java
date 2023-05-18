package com.elite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elite.item.UploadImage;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.OnClick;
import com.example.realestate.R;
import com.example.realestate.databinding.RowUploadGalleryBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UploadGalleryAdapter extends RecyclerView.Adapter<UploadGalleryAdapter.ViewHolder> {

    Activity activity;
    ArrayList<UploadImage> galleryList;
    OnClick onClick;

    public UploadGalleryAdapter(Activity activity, ArrayList<UploadImage> galleryList) {
        this.activity = activity;
        this.galleryList = galleryList;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowUploadGalleryBinding.inflate(activity.getLayoutInflater()));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        UploadImage uploadImage = galleryList.get(position);

        Glide.with(activity.getApplicationContext()).load(uploadImage.getImageUrl().isEmpty() ? uploadImage.getImageFile().getAbsoluteFile() : uploadImage.getImageUrl())
                .placeholder(R.drawable.property_placeholder)
                .into(holder.rowGalleryBinding.ivGallery);

        holder.rowGalleryBinding.ivUploadClose.setOnClickListener(view -> {
            if (!uploadImage.getImageUrl().isEmpty()){
                galleryImageDeleteData(uploadImage.gallery_img_id);
            }
            galleryList.remove(position);
            notifyDataSetChanged();
        });

    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        RowUploadGalleryBinding rowGalleryBinding;

        public ViewHolder(RowUploadGalleryBinding rowGalleryBinding) {
            super(rowGalleryBinding.getRoot());
            this.rowGalleryBinding = rowGalleryBinding;
        }
    }

    public void setOnItemClickListener(OnClick clickListener) {
        this.onClick = clickListener;
    }

    private void galleryImageDeleteData(String galleryImageId) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("post_id", galleryImageId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.getDeleteImageData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
                JsonObject jsonObject = response.body();
                assert jsonObject != null;
                Toast.makeText(activity, jsonObject.get("msg").getAsString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
            }
        });
    }
}
