package com.elite.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.elite.adapter.RelatedAdapter;
import com.elite.item.Detail;
import com.elite.item.Property;
import com.elite.response.DetailRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.BannerAds;
import com.elite.util.Constant;
import com.elite.util.Events;
import com.elite.util.GlobalBus;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityAdvanceSearchBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RelatedPropertyActivity extends AppCompatActivity {

    ActivityAdvanceSearchBinding bindingAdvSearch;
    Method method;
    List<Property> propertyList;
    String postId;
    int j = 1;
    Detail detailList;
    RelatedAdapter relatedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(RelatedPropertyActivity.this);

        bindingAdvSearch = ActivityAdvanceSearchBinding.inflate(getLayoutInflater());
        setContentView(bindingAdvSearch.getRoot());
        GlobalBus.getBus().register(this);

        method = new Method(RelatedPropertyActivity.this);
        method.forceRTLIfSupported();
        propertyList = new ArrayList<>();

        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        bindingAdvSearch.toolbarMain.tvName.setText(getString(R.string.related_property));


        bindingAdvSearch.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());
        bindingAdvSearch.layRecycle.rvRecycle.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(RelatedPropertyActivity.this, 2);
        bindingAdvSearch.layRecycle.rvRecycle.setLayoutManager(layoutManager);
        bindingAdvSearch.layRecycle.rvRecycle.setFocusable(false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (relatedAdapter.getItemViewType(position) == 1) {
                    return 2;
                }
                return 1;
            }
        });

        onRequest();

        BannerAds.showBannerAds(RelatedPropertyActivity.this, bindingAdvSearch.layoutAds);
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            relatedDetailData(method.getUserId());
        } else {
            onState(1);
        }
    }

    private void relatedDetailData(String userId) {
        showProgress(true);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RelatedPropertyActivity.this));
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
                    if (detailList.getRelatedProperty().isEmpty()) {
                        onState(2);//no data
                    } else {
                        for (int i = 0; i < detailList.getRelatedProperty().size(); i++) {
                            if (Constant.isNative) {
                                if (j % Constant.nativePosition == 0) {
                                    propertyList.add(null);
                                    j++;
                                }
                            }
                            propertyList.add(detailList.getRelatedProperty().get(i));
                            j++;
                        }
                        relatedAdapter = new RelatedAdapter(RelatedPropertyActivity.this, propertyList);
                        bindingAdvSearch.layRecycle.rvRecycle.setAdapter(relatedAdapter);
                        relatedAdapter.setOnItemClickListener(position -> {
                            Intent intentDetail = new Intent(RelatedPropertyActivity.this, DetailActivity.class);
                            intentDetail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intentDetail.putExtra("ID", propertyList.get(position).getPropertyId());
                            startActivity(intentDetail);
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
        bindingAdvSearch.layRecycle.layState.getRoot().setVisibility(View.VISIBLE);
        bindingAdvSearch.layRecycle.rvRecycle.setVisibility(View.GONE);
        bindingAdvSearch.layRecycle.progressHome.setVisibility(View.GONE);
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
        bindingAdvSearch.layRecycle.layState.ivState.setImageResource(image);
        bindingAdvSearch.layRecycle.layState.tvState.setText(title);
        bindingAdvSearch.layRecycle.layState.tvStateMsg.setText(desc);

        bindingAdvSearch.layRecycle.layState.btnRefreshNow.setOnClickListener(view -> {
            bindingAdvSearch.layRecycle.getRoot().setVisibility(View.GONE);
            onRequest();
        });
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingAdvSearch.layRecycle.progressHome.setVisibility(View.VISIBLE);
            bindingAdvSearch.layRecycle.rvRecycle.setVisibility(View.GONE);
        } else {
            bindingAdvSearch.layRecycle.progressHome.setVisibility(View.GONE);
            bindingAdvSearch.layRecycle.rvRecycle.setVisibility(View.VISIBLE);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getFav(Events.FavProperty favProperty) {
        for (int i = 0; i < propertyList.size(); i++) {
            if (propertyList.get(i) != null) {
                if (propertyList.get(i).getPropertyId().equals(favProperty.getPropertyId())) {
                    propertyList.get(i).setPropertyFavorite(Boolean.parseBoolean(favProperty.getIsFav()));
                    relatedAdapter.notifyItemChanged(i);
                }
            }
        }
    }
}