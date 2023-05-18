package com.elite.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.elite.adapter.MyPropertyAdapter;
import com.elite.item.Property;
import com.elite.realestate.DetailActivity;
import com.elite.realestate.LoginActivity;
import com.elite.response.LatestRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Constant;
import com.elite.util.Events;
import com.elite.util.GlobalBus;
import com.elite.util.Method;
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

public class MyPropertyFragment extends Fragment {

    FragmentCategoryBinding bindingMyProperty;
    Method method;
    MyPropertyAdapter myPropertyAdapter;
    List<Property> propertyList;
    int j = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        StatusBar.initLatestScreen(requireActivity());
        bindingMyProperty = FragmentCategoryBinding.inflate(inflater, container, false);
        GlobalBus.getBus().register(this);
        method = new Method(requireActivity());
        method.forceRTLIfSupported();
        propertyList = new ArrayList<>();

        if (method.getIsLogin()) {
            if (!method.getUserImage().isEmpty()) {
                Glide.with(requireActivity()).load(method.getUserImage())
                        .placeholder(R.drawable.user_profile)
                        .into(bindingMyProperty.clUserImage.ivUserImage);
            }
            bindingMyProperty.tvUserName.setText(method.getUserName());
        } else {
            Glide.with(requireActivity()).load(R.drawable.user_profile)
                    .into(bindingMyProperty.clUserImage.ivUserImage);
            bindingMyProperty.tvUserName.setText(getString(R.string.default_user_name));
            bindingMyProperty.clUserImage.ivOnline.setImageResource(R.drawable.offline);
        }

        method.profilePopUpWindow();
        bindingMyProperty.clUserImage.ivUserImage.setOnClickListener(view -> {
            if (method.getIsLogin()) {
                method.popupWindowProfile.showAsDropDown(view, -153, 0);
            } else {
                Toast.makeText(requireActivity(), getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(requireActivity(), LoginActivity.class);
                intentLogin.putExtra("isFromDetail", true);
                startActivity(intentLogin);
            }
        });

        bindingMyProperty.rvCat.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 2);
        bindingMyProperty.rvCat.setLayoutManager(layoutManager);
        bindingMyProperty.rvCat.setFocusable(false);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (myPropertyAdapter.getItemViewType(position) ==1) {
                    return 2;
                }
                return 1;
            }
        });

        onRequest();
        return bindingMyProperty.getRoot();
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            myPropertyData(method.getUserId());
        } else {
            onState(1);
        }
    }

    private void myPropertyData(String userId) {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            showProgress(true);
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<LatestRP> call = apiService.getUserPropertyData(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<LatestRP>() {
                @Override
                public void onResponse(@NotNull Call<LatestRP> call, @NotNull Response<LatestRP> response) {

                    LatestRP latestRP = response.body();
                    if (latestRP != null) {
                        showProgress(false);
                        if (latestRP.getLatestPropertyList().isEmpty()) {
                            onState(2);//no data
                        } else {
                            if (latestRP.getLatestPropertyList().size() != 0) {
                                for (int i = 0; i < latestRP.getLatestPropertyList().size(); i++) {
                                    if (Constant.isNative) {
                                        if (j % Constant.nativePosition == 0) {
                                            propertyList.add(null);
                                            j++;
                                        }
                                    }
                                    propertyList.add(latestRP.getLatestPropertyList().get(i));
                                    j++;
                                }
                                myPropertyAdapter = new MyPropertyAdapter(activity, propertyList);
                                bindingMyProperty.rvCat.setAdapter(myPropertyAdapter);
                                myPropertyAdapter.setOnItemClickListener(position -> {
                                    Intent intentCatList = new Intent(activity, DetailActivity.class);
                                    intentCatList.putExtra("ID", propertyList.get(position).getPropertyId());
                                    intentCatList.putExtra("CatName", propertyList.get(position).getPropertyTitle());
                                    startActivity(intentCatList);
                                });
                            }
                        }

                    } else {
                        onState(3);//error
                    }
                }

                @Override
                public void onFailure(@NotNull Call<LatestRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    onState(3);
                }
            });
        }
    }

    private void onState(int state) {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            bindingMyProperty.layState.getRoot().setVisibility(View.VISIBLE);
            bindingMyProperty.rvCat.setVisibility(View.GONE);
            bindingMyProperty.progressHome.setVisibility(View.GONE);
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
                    desc = activity.getString(R.string.no_property_msg);
                    image = R.drawable.img_no_data;
                    break;
                case 3:
                    title = activity.getString(R.string.no_error);
                    desc = activity.getString(R.string.no_error_msg);
                    image = R.drawable.img_no_server;
                    break;
            }
            bindingMyProperty.layState.ivState.setImageResource(image);
            bindingMyProperty.layState.tvState.setText(title);
            bindingMyProperty.layState.tvStateMsg.setText(desc);

            bindingMyProperty.layState.btnRefreshNow.setOnClickListener(view -> {
                bindingMyProperty.layState.getRoot().setVisibility(View.GONE);
                onRequest();
            });
        }
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingMyProperty.progressHome.setVisibility(View.VISIBLE);
            bindingMyProperty.rvCat.setVisibility(View.GONE);
        } else {
            bindingMyProperty.progressHome.setVisibility(View.GONE);
            bindingMyProperty.rvCat.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void onEvent(Events.myPropertyUpdate myPropertyUpdate){
        propertyList.clear();
        onRequest();
    }
}
