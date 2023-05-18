package com.elite.realestate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.elite.response.SubscriptionRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivitySubscriptionBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MySubscriptionActivity extends AppCompatActivity {

    ActivitySubscriptionBinding bindingSubscription;
    ProgressDialog progressDialog;
    Method method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(MySubscriptionActivity.this);

        bindingSubscription = ActivitySubscriptionBinding.inflate(getLayoutInflater());
        setContentView(bindingSubscription.getRoot());

        method = new Method(MySubscriptionActivity.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(MySubscriptionActivity.this, R.style.MyAlertDialogStyle);
        bindingSubscription.toolbarMain.tvName.setText(getString(R.string.subscription_title));

        bindingSubscription.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        bindingSubscription.btnRenew.setOnClickListener(view -> {
            Intent intentPlan=new Intent(MySubscriptionActivity.this,PlanActivity.class);
            startActivity(intentPlan);
        });

        onRequest();
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            subscriptionData();
        } else {
            onState(1);
        }
    }

    private void subscriptionData() {
        showProgress(true);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MySubscriptionActivity.this));
        jsObj.addProperty("user_id", method.getUserId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SubscriptionRP> call = apiService.getSubscriptionData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SubscriptionRP>() {
            @Override
            public void onResponse(@NotNull Call<SubscriptionRP> call, @NotNull Response<SubscriptionRP> response) {

                SubscriptionRP subscriptionRP = response.body();
                if (subscriptionRP != null) {
                    showProgress(false);
                    if (subscriptionRP.getSuccess().equals("0")) {
                        bindingSubscription.llPlan.setVisibility(View.GONE);
                        bindingSubscription.tvNoPlan.setVisibility(View.VISIBLE);
                        bindingSubscription.btnRenew.setText(getString(R.string.plan_buy));
                        bindingSubscription.btnRenew.setVisibility(View.VISIBLE);
                        bindingSubscription.tvNoPlan.setText(subscriptionRP.getMsg());
                    } else {
                        bindingSubscription.llPlan.setVisibility(View.VISIBLE);
                        bindingSubscription.tvNoPlan.setVisibility(View.GONE);
                        bindingSubscription.btnRenew.setVisibility(View.GONE);
                        bindingSubscription.tvPlan.setText(subscriptionRP.getItemSubscription().getCurrent_plan());
                        bindingSubscription.tvExpirePlan.setText(subscriptionRP.getItemSubscription().getExpired_on());
                        bindingSubscription.tvTotalProperty.setText(subscriptionRP.getItemSubscription().getUser_total_property());
                        bindingSubscription.tvPropertyLimit.setText(subscriptionRP.getItemSubscription().getPlan_property_limit());
                    }

                } else {
                    onState(3);//error
                }
            }

            @Override
            public void onFailure(@NotNull Call<SubscriptionRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                onState(3);
            }
        });
    }

    private void onState(int state) {

        bindingSubscription.layState.getRoot().setVisibility(View.VISIBLE);
        bindingSubscription.secCenter.setVisibility(View.GONE);
        bindingSubscription.progressHome.setVisibility(View.GONE);
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
        bindingSubscription.layState.ivState.setImageResource(image);
        bindingSubscription.layState.tvState.setText(title);
        bindingSubscription.layState.tvStateMsg.setText(desc);

        bindingSubscription.layState.btnRefreshNow.setOnClickListener(view -> {
            bindingSubscription.layState.getRoot().setVisibility(View.GONE);
            onRequest();
        });
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingSubscription.progressHome.setVisibility(View.VISIBLE);
            bindingSubscription.secCenter.setVisibility(View.GONE);
        } else {
            bindingSubscription.progressHome.setVisibility(View.GONE);
            bindingSubscription.secCenter.setVisibility(View.VISIBLE);
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