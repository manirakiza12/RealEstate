package com.elite.paymentmethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;

import com.elite.util.API;
import com.elite.util.Method;
import com.example.realestate.R;
import com.elite.realestate.SuccessActivity;
import com.elite.response.PaymentSuccessRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Transaction {
    ProgressDialog pDialog;
    Activity mContext;
    Method method;

    public Transaction(Activity context) {
        this.mContext = context;
        pDialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        method = new Method(mContext);
    }

    public void purchasedItem(String planId, String userId, String paymentId, String paymentGateway) {
        pDialog.show();
        pDialog.setMessage(mContext.getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(mContext));
        jsObj.addProperty("plan_id", planId);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("payment_id", paymentId);
        jsObj.addProperty("payment_gateway", paymentGateway);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaymentSuccessRP> call = apiService.getPaymentSuccessData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PaymentSuccessRP>() {
            @Override
            public void onResponse(@NotNull Call<PaymentSuccessRP> call, @NotNull Response<PaymentSuccessRP> response) {
                PaymentSuccessRP paymentSuccessRP = response.body();
                if (paymentSuccessRP != null) {
                    if (paymentSuccessRP.getSuccess().equals("1")) {
                        Intent intent = new Intent(mContext, SuccessActivity.class);
                        intent.putExtra("MSG", paymentSuccessRP.getMsg());
                        mContext.startActivity(intent);
                        mContext.finishAffinity();
                    } else {
                        method.alertBox(paymentSuccessRP.getMsg());
                    }
                }
                pDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<PaymentSuccessRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                pDialog.dismiss();
                method.alertBox(mContext.getResources().getString(R.string.no_error_msg));
            }
        });
    }
}
