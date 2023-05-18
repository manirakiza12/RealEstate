package com.elite.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.elite.adapter.GalleryAllAdapter;
import com.elite.util.BannerAds;
import com.elite.util.Constant;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityDetailGalleryBinding;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


public class GalleryDetailActivity extends AppCompatActivity {

    ActivityDetailGalleryBinding bindingGalleryDetail;
    Method method;
    int imageId;
    GalleryAllAdapter galleryAllAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(GalleryDetailActivity.this);

        bindingGalleryDetail = ActivityDetailGalleryBinding.inflate(getLayoutInflater());
        setContentView(bindingGalleryDetail.getRoot());

        method = new Method(GalleryDetailActivity.this);
        method.forceRTLIfSupported();

        Intent intent = getIntent();
        imageId = intent.getIntExtra("imagePos", 0);

        bindingGalleryDetail.toolbarMain.tvName.setText(getString(R.string.property_gallery));
        bindingGalleryDetail.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        bindingGalleryDetail.rvRecycle.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerCat = new LinearLayoutManager(GalleryDetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        bindingGalleryDetail.rvRecycle.setLayoutManager(layoutManagerCat);
        bindingGalleryDetail.rvRecycle.setFocusable(false);

        galleryAllAdapter = new GalleryAllAdapter(GalleryDetailActivity.this, Constant.listGallery);
        bindingGalleryDetail.rvRecycle.setAdapter(galleryAllAdapter);

        Picasso.get().load(Constant.listGallery.get(imageId).getGallery_image())
                .error(R.drawable.detail_placeholder)
                .into(bindingGalleryDetail.ivFullGallery, new Callback() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        bindingGalleryDetail.progressHome.setVisibility(View.GONE);
                        bindingGalleryDetail.ivFullGallery.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        bindingGalleryDetail.progressHome.setVisibility(View.GONE);
                    }

                });


        galleryAllAdapter.setOnItemClickListener(position ->
                Picasso.get().load(Constant.listGallery.get(position).getGallery_image())
                .error(R.drawable.detail_placeholder)
                .into(bindingGalleryDetail.ivFullGallery, new Callback() {
                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated method stub
                        bindingGalleryDetail.progressHome.setVisibility(View.GONE);
                        bindingGalleryDetail.ivFullGallery.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        bindingGalleryDetail.progressHome.setVisibility(View.GONE);
                    }

                }));

        BannerAds.showBannerAds(GalleryDetailActivity.this, bindingGalleryDetail.layoutAds);
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