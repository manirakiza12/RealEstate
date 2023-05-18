package com.elite.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elite.adapter.CategoryHomeAdapter;
import com.elite.adapter.LatestHomeAdapter;
import com.elite.adapter.PopularHomeAdapter;
import com.elite.adapter.RecentHomeAdapter;
import com.elite.adapter.SpinCatAdapter;
import com.elite.database.DatabaseHelperRecent;
import com.elite.item.Property;
import com.elite.realestate.AdvanceSearchActivity;
import com.elite.realestate.CategoryListActivity;
import com.elite.realestate.DetailActivity;
import com.elite.realestate.LoginActivity;
import com.elite.realestate.RecentActivity;
import com.elite.response.CategoryRP;
import com.elite.response.HomeRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Events;
import com.elite.util.GlobalBus;
import com.elite.util.Method;
import com.elite.util.NothingSelectedSpinnerAdapter;
import com.elite.util.OnClick;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.FragmentHomeBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {

    FragmentHomeBinding bindingHome;
    Method method;
    String[] strBuySell;
    CategoryHomeAdapter categoryHomeAdapter;
    LatestHomeAdapter latestHomeAdapter;
    PopularHomeAdapter popularHomeAdapter;
    OnClick onClickPos;
    SpinCatAdapter spinCatAdapter;
    ArrayAdapter<String> adapterBuySell;
    CategoryRP categoryRP;
    DatabaseHelperRecent databaseHelperRecent;
    List<Property> propertyListRecent;
    RecentHomeAdapter recentHomeAdapter;
    List<Property> propertyListLatest,propertyListPopular;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        StatusBar.initHomeScreen(requireActivity());
        bindingHome = FragmentHomeBinding.inflate(inflater, container, false);
        GlobalBus.getBus().register(this);
        method = new Method(requireActivity());
        method.forceRTLIfSupported();
        databaseHelperRecent = new DatabaseHelperRecent(requireActivity());
        propertyListRecent = new ArrayList<>();
        propertyListLatest=new ArrayList<>();
        propertyListPopular=new ArrayList<>();

        strBuySell = getResources().getStringArray(R.array.buy_sell_array);

        if (method.getIsLogin()){
            if (!method.getUserImage().isEmpty()) {
                Glide.with(requireActivity()).load(method.getUserImage())
                        .placeholder(R.drawable.user_profile)
                        .into(bindingHome.topHome.clUserImage.ivUserImage);
            }
            bindingHome.topHome.tvUserName.setText(method.getUserName());
        }else {
                Glide.with(requireActivity()).load(R.drawable.user_profile)
                        .into(bindingHome.topHome.clUserImage.ivUserImage);
            bindingHome.topHome.tvUserName.setText(getString(R.string.default_user_name));
            bindingHome.topHome.clUserImage.ivOnline.setImageResource(R.drawable.offline);
        }

        method.profilePopUpWindow();
        bindingHome.topHome.clUserImage.ivUserImage.setOnClickListener(view -> {
            if (method.getIsLogin()){
                method.popupWindowProfile.showAsDropDown(view,-153,0);
            }else {
                Toast.makeText(requireActivity(), getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(requireActivity(), LoginActivity.class);
                intentLogin.putExtra("isFromDetail", true);
                startActivity(intentLogin);
            }
        });


        bindingHome.rvCat.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        bindingHome.rvCat.setLayoutManager(layoutManager);
        bindingHome.rvCat.setFocusable(false);

        bindingHome.rvLatest.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerCat = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        bindingHome.rvLatest.setLayoutManager(layoutManagerCat);
        bindingHome.rvLatest.setFocusable(false);

        bindingHome.rvPopular.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerPopular = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        bindingHome.rvPopular.setLayoutManager(layoutManagerPopular);
        bindingHome.rvPopular.setFocusable(false);

        bindingHome.rvRecent.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerRecent = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        bindingHome.rvRecent.setLayoutManager(layoutManagerRecent);
        bindingHome.rvRecent.setFocusable(false);

        onRequest();

        bindingHome.topHome.mbHomeSearch.setOnClickListener(view -> {
            String search = bindingHome.topHome.etSearch.getText().toString();
            if (bindingHome.topHome.spPropertyPurpose.getSelectedItemPosition() == 0) {
                Toast.makeText(requireActivity(), getString(R.string.select_home), Toast.LENGTH_SHORT).show();
            } else if (bindingHome.topHome.spPropertyType.getSelectedItemPosition() == 0) {
                Toast.makeText(requireActivity(), getString(R.string.select_home_type), Toast.LENGTH_SHORT).show();
            } else if (search.isEmpty()) {
                Toast.makeText(requireActivity(), getString(R.string.search_home), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(getActivity(), AdvanceSearchActivity.class);
                intent.putExtra("HomePurpose", String.valueOf(bindingHome.topHome.spPropertyPurpose.getSelectedItem()));
                intent.putExtra("HomeTypeId", categoryRP.getCategoryList().get(bindingHome.topHome.spPropertyType.getSelectedItemPosition() - 1).getCategoryId());
                intent.putExtra("HomeSearchText", search);
                intent.putExtra("Type", search);
                startActivity(intent);
                bindingHome.topHome.etSearch.getText().clear();
            }
        });

        return bindingHome.getRoot();
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            homeData(method.getUserId());
        } else {
            onState(1);
        }
    }

    private void homeData(String userId) {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            showProgress(true);
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            jsObj.addProperty("user_id", userId);
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<HomeRP> call = apiService.getHomeData(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<HomeRP>() {
                @Override
                public void onResponse(@NotNull Call<HomeRP> call, @NotNull Response<HomeRP> response) {

                    HomeRP homeRP = response.body();
                    if (homeRP != null) {
                        showProgress(false);
                        categoryAllData();
                        if (homeRP.getRealEstate().getHomeCatList().isEmpty()) {
                            bindingHome.rlHomeCat.setVisibility(View.GONE);
                            bindingHome.rvCat.setVisibility(View.GONE);
                        } else {
                            categoryHomeAdapter = new CategoryHomeAdapter(activity, homeRP.getRealEstate().getHomeCatList(),activity.getResources().getIntArray(R.array.cat_title_text));
                            bindingHome.rvCat.setAdapter(categoryHomeAdapter);
                            categoryHomeAdapter.setOnItemClickListener(position -> {
                                Intent intentCatList = new Intent(activity, CategoryListActivity.class);
                                intentCatList.putExtra("CatId", homeRP.getRealEstate().getHomeCatList().get(position).getCategoryId());
                                intentCatList.putExtra("CatName", homeRP.getRealEstate().getHomeCatList().get(position).getCategoryName());
                                startActivity(intentCatList);
                            });
                            bindingHome.tvHomeSeeAllCat.setOnClickListener(view -> onClickPos.position(2));
                        }

                        if (homeRP.getRealEstate().getHomeLatestList().isEmpty()) {
                            bindingHome.rlHomeLatest.setVisibility(View.GONE);
                            bindingHome.rvLatest.setVisibility(View.GONE);
                        } else {
                            propertyListLatest=homeRP.getRealEstate().getHomeLatestList();
                            latestHomeAdapter = new LatestHomeAdapter(activity, propertyListLatest);
                            bindingHome.rvLatest.setAdapter(latestHomeAdapter);
                            latestHomeAdapter.setOnItemClickListener(position -> {
                                Intent intentDetail = new Intent(activity, DetailActivity.class);
                                intentDetail.putExtra("ID", propertyListLatest.get(position).getPropertyId());
                                startActivity(intentDetail);
                            });
                            bindingHome.tvHomeSeeAllLatest.setOnClickListener(view -> onClickPos.position(1));
                        }

                        if (homeRP.getRealEstate().getHomePopularList().isEmpty()) {
                            bindingHome.rlHomePopular.setVisibility(View.GONE);
                            bindingHome.rvPopular.setVisibility(View.GONE);
                        } else {
                            propertyListPopular=homeRP.getRealEstate().getHomePopularList();
                            popularHomeAdapter = new PopularHomeAdapter(activity, propertyListPopular);
                            bindingHome.rvPopular.setAdapter(popularHomeAdapter);
                            popularHomeAdapter.setOnItemClickListener(position -> {
                                Intent intentDetail = new Intent(activity, DetailActivity.class);
                                intentDetail.putExtra("ID", propertyListPopular.get(position).getPropertyId());
                                startActivity(intentDetail);
                            });
                            bindingHome.tvHomeSeeAllPopular.setOnClickListener(view -> {
                                Intent intent = new Intent(getActivity(), AdvanceSearchActivity.class);
                                intent.putExtra("Type", "Popular");
                                startActivity(intent);
                            });
                        }
                    } else {
                        onState(3);//error
                    }
                }

                @Override
                public void onFailure(@NotNull Call<HomeRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    onState(3);
                }
            });
        }
    }

    private void onState(int state) {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            bindingHome.layState.getRoot().setVisibility(View.VISIBLE);
            bindingHome.nsSV.setVisibility(View.GONE);
            bindingHome.progressHome.setVisibility(View.GONE);
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
            bindingHome.layState.ivState.setImageResource(image);
            bindingHome.layState.tvState.setText(title);
            bindingHome.layState.tvStateMsg.setText(desc);

            bindingHome.layState.btnRefreshNow.setOnClickListener(view -> {
                bindingHome.layState.getRoot().setVisibility(View.GONE);
                onRequest();
            });
        }
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingHome.progressHome.setVisibility(View.VISIBLE);
            bindingHome.nsSV.setVisibility(View.GONE);
        } else {
            bindingHome.progressHome.setVisibility(View.GONE);
            bindingHome.nsSV.setVisibility(View.VISIBLE);
        }
    }

    public void setOnItemClickListener(OnClick clickListener) {
        onClickPos = clickListener;
    }

    private void categoryAllData() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            //showProgress(true);
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CategoryRP> call = apiService.getAllCategoryData(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<CategoryRP>() {
                @Override
                public void onResponse(@NotNull Call<CategoryRP> call, @NotNull Response<CategoryRP> response) {

                    categoryRP = response.body();
                    if (categoryRP != null) {

                        spinCatAdapter = new SpinCatAdapter(activity, R.layout.spinner_item_home, categoryRP.getCategoryList());
                        spinCatAdapter.setDropDownViewResource(R.layout.spinner_item_home);
                        bindingHome.topHome.spPropertyType.setAdapter(
                                new NothingSelectedSpinnerAdapter(spinCatAdapter,
                                        R.layout.contact_spinner_row_select_type, activity));
                        bindingHome.topHome.ivDownArrowType.setOnClickListener(view -> bindingHome.topHome.spPropertyType.performClick());

                        bindingHome.topHome.spPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {

                                ((TextView) parent.getChildAt(0)).setTextColor(activity.getResources().getColor(R.color.spinner_text));
                                ((TextView) parent.getChildAt(0)).setTextSize(16);

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });


                        adapterBuySell = new ArrayAdapter<>(activity, R.layout.spinner_item_home, strBuySell);
                        adapterBuySell.setDropDownViewResource(R.layout.spinner_item_home);
                        bindingHome.topHome.spPropertyPurpose.setAdapter(
                                new NothingSelectedSpinnerAdapter(adapterBuySell,
                                        R.layout.contact_spinner_row_selected_purpose, activity));
                        bindingHome.topHome.ivDownArrowPurpose.setOnClickListener(view -> bindingHome.topHome.spPropertyPurpose.performClick());

                        bindingHome.topHome.spPropertyPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view,
                                                       int position, long id) {

                                ((TextView) parent.getChildAt(0)).setTextColor(activity.getResources().getColor(R.color.spinner_text));
                                ((TextView) parent.getChildAt(0)).setTextSize(16);

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });

                    } else {
                        method.alertBox(activity.getString(R.string.no_error_msg));
                    }
                }

                @Override
                public void onFailure(@NotNull Call<CategoryRP> call, @NotNull Throwable t) {
                    Log.e("fail", t.toString());
                    method.alertBox(activity.getString(R.string.no_error_msg));
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        propertyListRecent = databaseHelperRecent.getRecent();
        displayDataRecent();
    }

    @SuppressLint("SetTextI18n")
    private void displayDataRecent() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            if (propertyListRecent.size() >= 2) {
                bindingHome.rlHomeRecent.setVisibility(View.VISIBLE);
                bindingHome.rvRecent.setVisibility(View.VISIBLE);
            } else {
                bindingHome.rlHomeRecent.setVisibility(View.GONE);
                bindingHome.rvRecent.setVisibility(View.GONE);
            }

            bindingHome.tvHomeSeeAllRecent.setOnClickListener(view -> {
                Intent intentRecent=new Intent(activity, RecentActivity.class);
                startActivity(intentRecent);
            });

            recentHomeAdapter = new RecentHomeAdapter(activity, propertyListRecent);
            bindingHome.rvRecent.setAdapter(recentHomeAdapter);
            recentHomeAdapter.setOnItemClickListener(position -> {
                Intent intentDetail = new Intent(activity, DetailActivity.class);
                intentDetail.putExtra("ID", propertyListRecent.get(position).getPropertyId());
                startActivity(intentDetail);
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }
    @Subscribe
    public void onEvent(Events.ProfileUpdate profileUpdate){
        if (!method.getUserImage().isEmpty()) {
            Glide.with(requireActivity()).load(method.getUserImage())
                    .placeholder(R.drawable.user_profile)
                    .into(bindingHome.topHome.clUserImage.ivUserImage);
        }
        bindingHome.topHome.tvUserName.setText(method.getUserName());
        bindingHome.topHome.clUserImage.ivOnline.setImageResource(R.drawable.online);
    }

    @Subscribe
    public void getFav(Events.FavProperty favProperty) {
        for (int i = 0; i < propertyListLatest.size(); i++) {
            if (propertyListLatest.get(i) != null) {
                if (propertyListLatest.get(i).getPropertyId().equals(favProperty.getPropertyId())) {
                    propertyListLatest.get(i).setPropertyFavorite(Boolean.parseBoolean(favProperty.getIsFav()));
                    latestHomeAdapter.notifyItemChanged(i);
                }
            }
        }

        for (int i = 0; i < propertyListPopular.size(); i++) {
            if (propertyListPopular.get(i) != null) {
                if (propertyListPopular.get(i).getPropertyId().equals(favProperty.getPropertyId())) {
                    propertyListPopular.get(i).setPropertyFavorite(Boolean.parseBoolean(favProperty.getIsFav()));
                    popularHomeAdapter.notifyItemChanged(i);
                }
            }
        }
    }
}
