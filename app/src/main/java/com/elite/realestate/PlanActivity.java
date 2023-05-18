package com.elite.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.elite.adapter.PlanAdapter;
import com.elite.paymentmethod.Transaction;
import com.elite.response.PlanRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityPlanBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PlanActivity extends AppCompatActivity {

    ActivityPlanBinding bindingPlan;
    Method method;
    PlanAdapter planAdapter;
    int selectedPlan = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(PlanActivity.this);

        bindingPlan = ActivityPlanBinding.inflate(getLayoutInflater());
        setContentView(bindingPlan.getRoot());

        method = new Method(PlanActivity.this);
        method.forceRTLIfSupported();

        bindingPlan.ivClose.setOnClickListener(v -> onBackPressed());

        bindingPlan.rvPlanList.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(PlanActivity.this, 1);
        bindingPlan.rvPlanList.setLayoutManager(layoutManager);
        bindingPlan.rvPlanList.setFocusable(false);

        onRequest();
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            planListData();
        } else {
            onState(1);
        }
    }

    private void planListData() {
        showProgress(true);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PlanActivity.this));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PlanRP> call = apiService.getPlanData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PlanRP>() {
            @Override
            public void onResponse(@NotNull Call<PlanRP> call, @NotNull Response<PlanRP> response) {

                PlanRP planRP = response.body();
                if (planRP != null) {
                    showProgress(false);
                    if (planRP.getItemPlanLists().isEmpty()) {
                        onState(2);//no data
                    } else {
                        planAdapter = new PlanAdapter(PlanActivity.this, planRP.getItemPlanLists());
                        bindingPlan.rvPlanList.setAdapter(planAdapter);
                        planAdapter.select(0);
                        planAdapter.setOnItemClickListener(position -> {
                            selectedPlan = position;
                            planAdapter.select(position);
                        });
                        bindingPlan.mbSelectPlan.setOnClickListener(v -> {
                            PlanRP.ItemPlanList itemPlan = planRP.getItemPlanLists().get(selectedPlan);
                            String isFreePlan = itemPlan.getPlan_price();
                            if (isFreePlan.equals("0.00")) {
                                if (method.isNetworkAvailable()) {
                                    new Transaction(PlanActivity.this).purchasedItem(itemPlan.getPlan_id(), method.getUserId(), "N/A","Free");
                                } else {
                                    Toast.makeText(PlanActivity.this, getString(R.string.no_internet_msg), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Intent intent = new Intent(PlanActivity.this, PaymentMethodActivity.class);
                                intent.putExtra("planId", itemPlan.getPlan_id());
                                intent.putExtra("planName", itemPlan.getPlan_name());
                                intent.putExtra("planPrice", itemPlan.getPlan_price());
                                intent.putExtra("planPriceCurrency", itemPlan.getCurrency_code());
                                intent.putExtra("planUserId", method.getUserId());
                                intent.putExtra("planLimit", itemPlan.getPlan_property_limit());
                                intent.putExtra("planDuration", itemPlan.getPlan_duration());
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    onState(3);//error
                }
            }

            @Override
            public void onFailure(@NotNull Call<PlanRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                onState(3);
            }
        });
    }

    private void onState(int state) {
        bindingPlan.layState.getRoot().setVisibility(View.VISIBLE);
        bindingPlan.rlMain.setVisibility(View.GONE);
        bindingPlan.progressHome.setVisibility(View.GONE);
        bindingPlan.mbSelectPlan.setVisibility(View.GONE);
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
        bindingPlan.layState.ivState.setImageResource(image);
        bindingPlan.layState.tvState.setText(title);
        bindingPlan.layState.tvStateMsg.setText(desc);

        bindingPlan.layState.btnRefreshNow.setOnClickListener(view -> {
            bindingPlan.layState.getRoot().setVisibility(View.GONE);
            onRequest();
        });
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingPlan.progressHome.setVisibility(View.VISIBLE);
            bindingPlan.rlMain.setVisibility(View.GONE);
        } else {
            bindingPlan.progressHome.setVisibility(View.GONE);
            bindingPlan.rlMain.setVisibility(View.VISIBLE);
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