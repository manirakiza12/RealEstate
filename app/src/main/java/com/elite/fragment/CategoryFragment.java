package com.elite.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.elite.adapter.CategoryAdapter;
import com.elite.item.Category;
import com.elite.realestate.CategoryListActivity;
import com.elite.realestate.LoginActivity;
import com.elite.response.CategoryRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Constant;
import com.elite.util.EndlessRecyclerViewScrollListener;
import com.elite.util.Events;
import com.elite.util.GlobalBus;
import com.elite.util.Method;
import com.elite.util.OnClick;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.FragmentCategoryBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CategoryFragment extends Fragment {

    FragmentCategoryBinding bindingCat;
    Method method;
    private int pageIndex = 1;
    private boolean isFirst = true, isLoadMore = false;
    CategoryAdapter categoryAdapter;
    List<Category> listCategory;
    int j = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        StatusBar.initLatestScreen(requireActivity());
        bindingCat = FragmentCategoryBinding.inflate(inflater, container, false);
        GlobalBus.getBus().register(this);
        method = new Method(requireActivity());
        method.forceRTLIfSupported();
        listCategory = new ArrayList<>();

        if (method.getIsLogin()) {
            if (!method.getUserImage().isEmpty()) {
                Glide.with(requireActivity()).load(method.getUserImage())
                        .placeholder(R.drawable.user_profile)
                        .into(bindingCat.clUserImage.ivUserImage);
            }
            bindingCat.tvUserName.setText(method.getUserName());
        } else {
            Glide.with(requireActivity()).load(R.drawable.user_profile)
                    .into(bindingCat.clUserImage.ivUserImage);
            bindingCat.tvUserName.setText(getString(R.string.default_user_name));
            bindingCat.clUserImage.ivOnline.setImageResource(R.drawable.offline);
        }
        method.profilePopUpWindow();
        bindingCat.clUserImage.ivUserImage.setOnClickListener(view -> {
            if (method.getIsLogin()) {
                method.popupWindowProfile.showAsDropDown(view, -153, 0);
            } else {
                Toast.makeText(requireActivity(), getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(requireActivity(), LoginActivity.class);
                intentLogin.putExtra("isFromDetail", true);
                startActivity(intentLogin);
            }
        });

        bindingCat.rvCat.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2);
        bindingCat.rvCat.setLayoutManager(layoutManager);
        bindingCat.rvCat.setFocusable(false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (categoryAdapter.getItemViewType(position) != 1) {
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
                    categoryAdapter.hideHeader();
                }
            }
        };
        bindingCat.rvCat.addOnScrollListener(endlessRecyclerViewScrollListener);
        return bindingCat.getRoot();
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            categoryData();
        } else {
            onState(1);
        }
    }

    private void categoryData() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            if (isFirst)
                showProgress(true);
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CategoryRP> call = apiService.getCategoryData(API.toBase64(jsObj.toString()), pageIndex);
            call.enqueue(new Callback<CategoryRP>() {
                @Override
                public void onResponse(@NotNull Call<CategoryRP> call, @NotNull Response<CategoryRP> response) {

                    CategoryRP categoryRP = response.body();
                    if (categoryRP != null) {
                        if (isFirst)
                            showProgress(false);
                        isLoadMore = categoryRP.isLoadMore();
                        if (categoryRP.getCategoryList().isEmpty()) {
                            if (isFirst) {
                                onState(2);//no data
                            }
                        } else {
                            if (categoryRP.getCategoryList().size() != 0) {
                                for (int i = 0; i < categoryRP.getCategoryList().size(); i++) {
                                    if (Constant.isNative) {
                                        if (j % Constant.nativePosition == 0) {
                                            listCategory.add(null);
                                            j++;
                                        }
                                    }
                                    listCategory.add(categoryRP.getCategoryList().get(i));
                                    j++;
                                }
                                if (isFirst) {
                                    isFirst = false;
                                    categoryAdapter = new CategoryAdapter(activity, listCategory, activity.getResources().getIntArray(R.array.cat_bg), activity.getResources().getIntArray(R.array.cat_title_text));
                                    bindingCat.rvCat.setAdapter(categoryAdapter);
                                    categoryAdapter.setOnItemClickListener(new OnClick() {
                                        @Override
                                        public void position(int position) {
                                            Intent intentCatList = new Intent(activity, CategoryListActivity.class);
                                            intentCatList.putExtra("CatId", listCategory.get(position).getCategoryId());
                                            intentCatList.putExtra("CatName", listCategory.get(position).getCategoryName());
                                            startActivity(intentCatList);
                                        }
                                    });
                                } else {
                                    categoryAdapter.notifyDataSetChanged();
                                }
                            }
                        }

                    } else {
                        onState(3);//error
                    }
                }

                @Override
                public void onFailure(@NotNull Call<CategoryRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    onState(3);
                }
            });
        }
    }

    private void onState(int state) {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            bindingCat.layState.getRoot().setVisibility(View.VISIBLE);
            bindingCat.rvCat.setVisibility(View.GONE);
            bindingCat.progressHome.setVisibility(View.GONE);
            String title, desc;
            int image;
            switch (state) {
                case 1:
                default:
                    title = activity.getString(R.string.no_internet);
                    desc = activity.getString(R.string.no_internet_msg);
                    image = R.drawable.img_no_internet;
                    break;
                case 2:
                    title = activity.getString(R.string.no_data);
                    desc = activity.getString(R.string.no_data_msg);
                    image = R.drawable.img_no_data;
                    break;
                case 3:
                    title = activity.getString(R.string.no_error);
                    desc = activity.getString(R.string.no_error_msg);
                    image = R.drawable.img_no_server;
                    break;
            }
            bindingCat.layState.ivState.setImageResource(image);
            bindingCat.layState.tvState.setText(title);
            bindingCat.layState.tvStateMsg.setText(desc);

            bindingCat.layState.btnRefreshNow.setOnClickListener(view -> {
                bindingCat.layState.getRoot().setVisibility(View.GONE);
                onRequest();
            });
        }
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingCat.progressHome.setVisibility(View.VISIBLE);
            bindingCat.rvCat.setVisibility(View.GONE);
        } else {
            bindingCat.progressHome.setVisibility(View.GONE);
            bindingCat.rvCat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void onEvent(Events.ProfileUpdate profileUpdate) {
        if (!method.getUserImage().isEmpty()) {
            Glide.with(requireActivity()).load(method.getUserImage())
                    .placeholder(R.drawable.user_profile)
                    .into(bindingCat.clUserImage.ivUserImage);
        }
        bindingCat.tvUserName.setText(method.getUserName());
        bindingCat.clUserImage.ivOnline.setImageResource(R.drawable.online);
    }
}
