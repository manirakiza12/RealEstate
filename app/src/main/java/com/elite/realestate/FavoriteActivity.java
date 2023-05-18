package com.elite.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.elite.adapter.FavoriteAdapter;
import com.elite.item.Property;
import com.elite.response.AdvSearchRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.BannerAds;
import com.elite.util.Constant;
import com.elite.util.EndlessRecyclerViewScrollListener;
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


public class FavoriteActivity extends AppCompatActivity {

    ActivityAdvanceSearchBinding bindingAdvSearch;
    Method method;
    private int pageIndex = 1;
    private boolean isFirst = true, isLoadMore = false;
    List<Property> propertyList;
    FavoriteAdapter gridAdapter;
    int j = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(FavoriteActivity.this);

        bindingAdvSearch = ActivityAdvanceSearchBinding.inflate(getLayoutInflater());
        setContentView(bindingAdvSearch.getRoot());
        GlobalBus.getBus().register(this);

        method = new Method(FavoriteActivity.this);
        method.forceRTLIfSupported();
        propertyList = new ArrayList<>();

        bindingAdvSearch.toolbarMain.tvName.setText(getString(R.string.fav_property));

        bindingAdvSearch.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());
        bindingAdvSearch.layRecycle.rvRecycle.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(FavoriteActivity.this, 2);
        bindingAdvSearch.layRecycle.rvRecycle.setLayoutManager(layoutManager);
        bindingAdvSearch.layRecycle.rvRecycle.setFocusable(false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (gridAdapter.getItemViewType(position) != 1) {
                    return 2;
                }
                return 1;
            }
        });

        onRequest();
        EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (isLoadMore) {
                    new Handler().postDelayed(() -> {
                        pageIndex++;
                        isFirst = false;
                        onRequest();
                    }, 1000);
                } else {
                    gridAdapter.hideHeader();
                }
            }
        };
        bindingAdvSearch.layRecycle.rvRecycle.addOnScrollListener(endlessRecyclerViewScrollListener);
        BannerAds.showBannerAds(FavoriteActivity.this, bindingAdvSearch.layoutAds);
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            favData();
        } else {
            onState(1);
        }
    }

    private void favData() {
        if (isFirst)
            showProgress(true);
        Call<AdvSearchRP> call;
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(FavoriteActivity.this));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        jsObj.addProperty("user_id", method.getUserId());
        call = apiService.getFavoriteData(API.toBase64(jsObj.toString()), pageIndex);
        call.enqueue(new Callback<AdvSearchRP>() {
            @Override
            public void onResponse(@NotNull Call<AdvSearchRP> call, @NotNull Response<AdvSearchRP> response) {

                AdvSearchRP advSearchRP = response.body();
                if (advSearchRP != null) {
                    if (isFirst)
                        showProgress(false);
                    isLoadMore = advSearchRP.isLoadMore();
                    if (advSearchRP.getPropertyList().isEmpty()) {
                        if (isFirst) {
                            onState(2);//no data
                        }
                    } else {
                        if (advSearchRP.getPropertyList().size() != 0) {
                            for (int i = 0; i < advSearchRP.getPropertyList().size(); i++) {
                                if (Constant.isNative) {
                                    if (j % Constant.nativePosition == 0) {
                                        propertyList.add(null);
                                        j++;
                                    }
                                }
                                propertyList.add(advSearchRP.getPropertyList().get(i));
                                j++;
                            }
                            if (isFirst) {
                                isFirst = false;
                                gridAdapter = new FavoriteAdapter(FavoriteActivity.this, propertyList);
                                bindingAdvSearch.layRecycle.rvRecycle.setAdapter(gridAdapter);
                                gridAdapter.setOnItemClickListener(position -> {
                                    Intent intentDetail = new Intent(FavoriteActivity.this, DetailActivity.class);
                                    intentDetail.putExtra("ID", propertyList.get(position).getPropertyId());
                                    startActivity(intentDetail);
                                });
                            } else {
                                gridAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    onState(3);//error
                }
            }

            @Override
            public void onFailure(@NotNull Call<AdvSearchRP> call, @NotNull Throwable t) {
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
                desc = getString(R.string.no_fav_msg);
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
            bindingAdvSearch.layRecycle.layState.getRoot().setVisibility(View.GONE);
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
                    propertyList.remove(i);
                    gridAdapter.notifyItemRemoved(i);
                    gridAdapter.notifyItemRangeChanged(i, propertyList.size());
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gridAdapter.hideHeader();
                        }
                    }, 100);
                }
            }
        }
    }
}