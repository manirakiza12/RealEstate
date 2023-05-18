package com.elite.paymentmethod;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.elite.response.RazorPayTokenRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityPaymentBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RazorPayActivity extends AppCompatActivity implements PaymentResultListener {

    String planId, planName, planPrice, planCurrency, planGateway, razorPayKey;
    Button btnPay;
    Method method;
    String orderId = "";
    ProgressDialog pDialog;
    ActivityPaymentBinding paymentBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(RazorPayActivity.this);
        paymentBinding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(paymentBinding.getRoot());
        method = new Method(RazorPayActivity.this);

        paymentBinding.toolbarMain.tvName.setText(getString(R.string.payment_razor));
        paymentBinding.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        pDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        planPrice = intent.getStringExtra("planPrice");
        planName = intent.getStringExtra("planName");
        planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        razorPayKey = intent.getStringExtra("razorPayKey");

        btnPay = findViewById(R.id.btn_pay);

        Checkout.preload(getApplicationContext());

        getOrderId();

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (orderId.isEmpty()) {
                    getOrderId();
                } else {
                    startPayment();
                }
            }
        });

    }

    private void getOrderId() {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RazorPayActivity.this));
        double big = Double.parseDouble(planPrice);
        int amount = (int) (big) * 100;
        jsObj.addProperty("user_id", method.getUserId());
        jsObj.addProperty("amount", amount);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RazorPayTokenRP> call = apiService.getRazorPayTokenData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RazorPayTokenRP>() {
            @Override
            public void onResponse(@NotNull Call<RazorPayTokenRP> call, @NotNull Response<RazorPayTokenRP> response) {
                try {
                    RazorPayTokenRP paymentCheckOutRP = response.body();

                     if (paymentCheckOutRP !=null && paymentCheckOutRP.getSuccess().equals("1")) {
                        orderId = paymentCheckOutRP.getItemPaymentCheckOuts().getRazorpay_order_id();
                        if (method.isNetworkAvailable()) {
                            startPayment();
                        } else {
                            showError(getString(R.string.no_internet_msg));
                        }
                    } else {
                        showError(paymentCheckOutRP.getMsg());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.no_error_msg));
                }

                pDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<RazorPayTokenRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                pDialog.dismiss();
                method.alertBox(getResources().getString(R.string.no_error_msg));
            }
        });
    }

    public void startPayment() {
        final Activity activity = this;
        final Checkout co = new Checkout();
        co.setKeyID(razorPayKey);

        try {
            JSONObject options = new JSONObject();
            options.put("name", getString(R.string.app_name));
            options.put("description", getString(R.string.razor_desc));
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", planCurrency);
            double big = Double.valueOf(planPrice);
            int amount = (int) (big) * 100;
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            //preFill.put("email", method.userId());
            //     preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            showError("Error in payment: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String Title) {
        new AlertDialog.Builder(RazorPayActivity.this)
                .setTitle(getString(R.string.razor_payment_error_1))
                .setMessage(Title)
                .setIcon(R.mipmap.app_icon)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        try {
            if (method.isNetworkAvailable()) {
                new Transaction(RazorPayActivity.this)
                        .purchasedItem(planId, method.getUserId(), razorpayPaymentID, planGateway);
            } else {
                showError(getString(R.string.no_internet_msg));
            }
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentSuccess", e);
        }
    }

    @Override
    public void onPaymentError(int code, String response) {
        try {
            showError("Payment failed: " + code + " " + response);
        } catch (Exception e) {
            Log.e("TAG", "Exception in onPaymentError", e);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
