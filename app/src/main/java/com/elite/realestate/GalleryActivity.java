package com.elite.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.elite.adapter.GalleryAllAdapter;
import com.elite.item.Detail;
import com.elite.response.DetailRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.BannerAds;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityGalleryBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class GalleryActivity extends AppCompatActivity {

    ActivityGalleryBinding bindingGallery;
    Method method;
    GalleryAllAdapter galleryAdapter;
    String postId;
    Detail detailList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(GalleryActivity.this);
        bindingGallery = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(bindingGallery.getRoot());

        method = new Method(GalleryActivity.this);
        method.forceRTLIfSupported();

        Intent intent = getIntent();
        postId = intent.getStringExtra("ID");

        bindingGallery.toolbarMain.tvName.setText(getString(R.string.property_gallery));

        bindingGallery.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        bindingGallery.rowRecycle.rvRecycle.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(GalleryActivity.this, 3);
        bindingGallery.rowRecycle.rvRecycle.setLayoutManager(layoutManager);
        bindingGallery.rowRecycle.rvRecycle.setFocusable(false);
        BannerAds.showBannerAds(GalleryActivity.this, bindingGallery.layoutAds);

        onRequest();

    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            detailData(method.getUserId());
        } else {
            onState(1);
        }
    }

    private void detailData(String userId) {
        showProgress(true);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(GalleryActivity.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("property_id", postId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DetailRP> call = apiService.getDetailData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DetailRP>() {
            @Override
            public void onResponse(@NotNull Call<DetailRP> call, @NotNull Response<DetailRP> response) {

                DetailRP detailRP = response.body();
                if (detailRP != null) {
                    showProgress(false);
                    detailList = detailRP.getDetailList();

                    if (detailList.getGalleryList().isEmpty()) {
                        onState(2);
                    } else {
                        galleryAdapter = new GalleryAllAdapter(GalleryActivity.this, detailList.getGalleryList());
                        bindingGallery.rowRecycle.rvRecycle.setAdapter(galleryAdapter);
                        galleryAdapter.setOnItemClickListener(position -> {
                            Intent intentShow=new Intent(GalleryActivity.this,FullGalleryActivity.class);
                            intentShow.putExtra("imageId", detailList.getGalleryList().get(position).getGallery_image());
                            intentShow.putExtra("imageType", "Gallery");
                            startActivity(intentShow);
                        });
                    }

                } else {
                    onState(3);//error
                }
            }

            @Override
            public void onFailure(@NotNull Call<DetailRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                onState(3);
            }
        });
    }

    private void onState(int state) {
        bindingGallery.rowRecycle.getRoot().setVisibility(View.VISIBLE);
        bindingGallery.rowRecycle.rvRecycle.setVisibility(View.GONE);
        bindingGallery.rowRecycle.progressHome.setVisibility(View.GONE);
        String title, desc;
        int image;
        switch (state) {
            case 1:
            default:
                title = getString(R.string.no_internet);
                desc = getString(R.string.no_internet_msg);
                image = R.drawable.img_no_internet;
                break;
            case 2:
                title = getString(R.string.no_data);
                desc = getString(R.string.no_data_msg);
                image = R.drawable.img_no_data;
                break;
            case 3:
                title = getString(R.string.no_error);
                desc = getString(R.string.no_error_msg);
                image = R.drawable.img_no_server;
                break;
        }
        bindingGallery.rowRecycle.layState.ivState.setImageResource(image);
        bindingGallery.rowRecycle.layState.tvState.setText(title);
        bindingGallery.rowRecycle.layState.tvStateMsg.setText(desc);

        bindingGallery.rowRecycle.layState.btnRefreshNow.setOnClickListener(view -> {
            bindingGallery.rowRecycle.layState.getRoot().setVisibility(View.GONE);
            onRequest();
        });
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingGallery.rowRecycle.progressHome.setVisibility(View.VISIBLE);
            bindingGallery.rowRecycle.rvRecycle.setVisibility(View.GONE);
        } else {
            bindingGallery.rowRecycle.progressHome.setVisibility(View.GONE);
            bindingGallery.rowRecycle.rvRecycle.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}