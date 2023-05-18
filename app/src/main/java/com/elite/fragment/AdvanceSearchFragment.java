package com.elite.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.elite.adapter.CategoryAllAdapter;
import com.elite.item.Category;
import com.elite.realestate.AdvanceSearchActivity;
import com.elite.response.CategoryRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Constant;
import com.elite.util.Method;
import com.example.realestate.R;
import com.example.realestate.databinding.LayoutAdvanceSearchBinding;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AdvanceSearchFragment extends BottomSheetDialogFragment {

    LayoutAdvanceSearchBinding bindingAdvance;
    String postId, userId, finalMin, finalMax;
    CategoryAllAdapter categoryAllAdapter;
    Method method;
    String stringVerified, stringFurnishing, stringBed, stringBath;
    int selectedType = -1;
    ArrayList<Category> categoryArrayList;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        bindingAdvance = LayoutAdvanceSearchBinding.inflate(inflater, container, false);
        method = new Method(requireActivity());
        method.forceRTLIfSupported();

        if (getArguments() != null) {
            postId = getArguments().getString("postId");
            userId = getArguments().getString("userId");
        }
        categoryArrayList = new ArrayList<>();
        bindingAdvance.rvAllType.setHasFixedSize(true);
        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(requireActivity());
        bindingAdvance.rvAllType.setLayoutManager(flexboxLayoutManager);
        bindingAdvance.rvAllType.setFocusable(false);

        bindingAdvance.tvRangeMin.setText(Constant.constantCurrency + Constant.minPropertyPrice);
        bindingAdvance.tvRangeMax.setText(Constant.constantCurrency + Constant.maxPropertyPrice);
        bindingAdvance.crSeekBar.setMaxValue(Float.parseFloat(Constant.maxPropertyPrice));
        bindingAdvance.crSeekBar.setMinValue(Float.parseFloat(Constant.minPropertyPrice));

        bindingAdvance.crSeekBar.setMinStartValue(Float.parseFloat(Constant.minPropertyPrice));
        bindingAdvance.crSeekBar.setMaxStartValue(Float.parseFloat(Constant.maxPropertyPrice));

        bindingAdvance.crSeekBar.apply();
        bindingAdvance.crSeekBar.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
            bindingAdvance.tvRangeMin.setText(Constant.constantCurrency + String.valueOf(minValue));
            bindingAdvance.tvRangeMax.setText(Constant.constantCurrency + String.valueOf(maxValue));
            finalMax = String.valueOf(maxValue);
            finalMin = String.valueOf(minValue);
        });

        stringVerified = "";
        bindingAdvance.rgVery.setOnCheckedChangeListener((group, checkedId) -> {
            // find which radio button is selected
            if (checkedId == R.id.rbVery) {
                stringVerified = "YES";
            } else if (checkedId == R.id.rbNonVery) {
                stringVerified = "NO";
            }
        });

        stringBed = "";
        bindingAdvance.rgBed.setOnCheckedChangeListener((group, checkedId) -> {
            // find which radio button is selected
            if (checkedId == R.id.rbBed1) {
                stringBed = "1";
            } else if (checkedId == R.id.rbBed2) {
                stringBed = "2";
            } else if (checkedId == R.id.rbBed3) {
                stringBed = "3";
            } else if (checkedId == R.id.rbBed4) {
                stringBed = "4";
            }
        });

        stringBath = "";
        bindingAdvance.rgBath.setOnCheckedChangeListener((group, checkedId) -> {
            // find which radio button is selected
            if (checkedId == R.id.rbBathAny) {
                stringBath = "Any";
            } else if (checkedId == R.id.rbBath1) {
                stringBath = "1";
            } else if (checkedId == R.id.rbBath2) {
                stringBath = "2";
            } else if (checkedId == R.id.rbBath3) {
                stringBath = "3";
            }
        });

        stringFurnishing = "";
        bindingAdvance.rgFur.setOnCheckedChangeListener((group, checkedId) -> {
            // find which radio button is selected
            if (checkedId == R.id.rbFurn) {
                stringFurnishing = "Furnished";
            } else if (checkedId == R.id.rbSemi) {
                stringFurnishing = "Semi-Furnished";
            } else if (checkedId == R.id.rbUnSemi) {
                stringFurnishing = "Unfurnished";
            }
        });

        bindingAdvance.mbClear.setOnClickListener(view -> {
            bindingAdvance.rgFur.clearCheck();
            bindingAdvance.rgVery.clearCheck();
            bindingAdvance.rgBath.clearCheck();
            bindingAdvance.rgBed.clearCheck();

            bindingAdvance.tvRangeMin.setText(Constant.constantCurrency + Constant.minPropertyPrice);
            bindingAdvance.tvRangeMax.setText(Constant.constantCurrency + Constant.maxPropertyPrice);
            bindingAdvance.crSeekBar.setMaxValue(Float.parseFloat(Constant.maxPropertyPrice));
            bindingAdvance.crSeekBar.setMinValue(Float.parseFloat(Constant.minPropertyPrice));
            bindingAdvance.crSeekBar.setMinStartValue(Float.parseFloat(Constant.minPropertyPrice));
            bindingAdvance.crSeekBar.setMaxStartValue(Float.parseFloat(Constant.maxPropertyPrice));
            bindingAdvance.crSeekBar.apply();

            selectedType = -1;
            categoryAllAdapter.select(selectedType);
        });

        categoryAllData();
        return bindingAdvance.getRoot();
    }

    private void categoryAllData() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            showProgress(true);
            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(getActivity()));
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CategoryRP> call = apiService.getAllCategoryData(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<CategoryRP>() {
                @Override
                public void onResponse(@NotNull Call<CategoryRP> call, @NotNull Response<CategoryRP> response) {

                    CategoryRP categoryRP = response.body();
                    if (categoryRP != null) {
                        showProgress(false);
                        categoryAllAdapter = new CategoryAllAdapter(activity, categoryRP.getCategoryList());
                        bindingAdvance.rvAllType.setAdapter(categoryAllAdapter);
                        categoryAllAdapter.setOnItemClickListener(position -> {
                            selectedType = position;
                            categoryAllAdapter.select(position);
                        });

                        bindingAdvance.mbApply.setOnClickListener(view -> {
                            Intent intent = new Intent(requireActivity(), AdvanceSearchActivity.class);
                            intent.putExtra("Verify", stringVerified);
                            intent.putExtra("PriceMin", finalMin);
                            if (stringBath.equals("Any")) {
                                intent.putExtra("Bath", "");
                            } else {
                                intent.putExtra("Bath", stringBath);
                            }
                            intent.putExtra("Bed", stringBed);
                            intent.putExtra("PriceMax", finalMax);
                            intent.putExtra("Furnishing", stringFurnishing);
                            intent.putExtra("TypeId", selectedType == -1 ? "" : categoryRP.getCategoryList().get(selectedType).getCategoryId());
                            intent.putExtra("Type", "Adv");
                            startActivity(intent);
                            dismiss();
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

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingAdvance.progressHome.setVisibility(View.VISIBLE);
            bindingAdvance.rlMain.setVisibility(View.GONE);
        } else {
            bindingAdvance.progressHome.setVisibility(View.GONE);
            bindingAdvance.rlMain.setVisibility(View.VISIBLE);
        }
    }
}