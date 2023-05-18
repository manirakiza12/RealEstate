package com.elite.paymentmethod;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.elite.response.BraintreeCheckOutRP;
import com.elite.response.PaypalTokenRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityPaymentBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayPalActivity extends AppCompatActivity {

    String planId, planPrice, planCurrency, planGateway, isSandbox;
    BraintreeFragment mBraintreeFragment;
    String authToken = "";
    ProgressDialog pDialog;
    Method method;
    ActivityPaymentBinding paypalBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(PayPalActivity.this);
        paypalBinding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(paypalBinding.getRoot());

        method = new Method(PayPalActivity.this);
        paypalBinding.toolbarMain.tvName.setText(getString(R.string.payment_paypal));
        paypalBinding.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        planPrice = intent.getStringExtra("planPrice");
        planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        isSandbox = intent.getStringExtra("isSandbox");

        pDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        paypalBinding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!authToken.isEmpty()) {
                    makePaymentFromBraintree();
                } else {
                    showError(getString(R.string.paypal_payment_error_3));
                }
            }
        });

        generateToken();
    }

    private void generateToken() {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PayPalActivity.this));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PaypalTokenRP> call = apiService.getPaypalTokenData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PaypalTokenRP>() {
            @Override
            public void onResponse(@NotNull Call<PaypalTokenRP> call, @NotNull Response<PaypalTokenRP> response) {
                try {
                    PaypalTokenRP paypalTokenRP = response.body();

                    if (paypalTokenRP != null && paypalTokenRP.getSuccess().equals("1")) {
                        authToken = paypalTokenRP.getItemPaypalTokens().getAuthToken();
                        initBraintree(authToken);
                    } else {
                        method.alertBox(paypalTokenRP.getMsg());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.no_error_msg));
                }

                pDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<PaypalTokenRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                pDialog.dismiss();
                method.alertBox(getResources().getString(R.string.no_error_msg));
            }
        });
    }

    private void initBraintree(String authToken) {
        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this, authToken);
            mBraintreeFragment.addListener(new PaymentMethodNonceCreatedListener() {
                @Override
                public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
                    String nNonce = paymentMethodNonce.getNonce();
                    checkoutNonce(nNonce);
                }
            });
            mBraintreeFragment.addListener(new BraintreeCancelListener() {
                @Override
                public void onCancel(int requestCode) {
                    showError(getString(R.string.paypal_payment_error_2));
                }
            });

            mBraintreeFragment.addListener(new BraintreeErrorListener() {
                @Override
                public void onError(Exception error) {
                    showError(error.getMessage());
                }
            });
            Toast.makeText(PayPalActivity.this, getString(R.string.proceed_with_payment), Toast.LENGTH_SHORT).show();
        } catch (InvalidArgumentException e) {
            // There was an issue with your authorization string.
            showError(getString(R.string.paypal_payment_error_1));
        }
    }

    private void makePaymentFromBraintree() {
        PayPal.requestOneTimePayment(mBraintreeFragment, getPaypalRequest(planPrice));
    }

    private PayPalRequest getPaypalRequest(@Nullable String amount) {
        PayPalRequest request = new PayPalRequest(amount);
        request.currencyCode(planCurrency);
        request.intent(PayPalRequest.INTENT_SALE);
        return request;
    }

    private void checkoutNonce(String paymentNonce) {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PayPalActivity.this));
        jsObj.addProperty("payment_nonce", paymentNonce);
        jsObj.addProperty("payment_amount", planPrice);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<BraintreeCheckOutRP> call = apiService.getPaypalCheckOutData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<BraintreeCheckOutRP>() {
            @Override
            public void onResponse(@NotNull Call<BraintreeCheckOutRP> call, @NotNull Response<BraintreeCheckOutRP> response) {
                try {
                    BraintreeCheckOutRP paypalCheckOutRP = response.body();

                    if (paypalCheckOutRP != null && paypalCheckOutRP.getSuccess().equals("1")) {
                        String paymentId = paypalCheckOutRP.getItemBrainTreeCheckOuts().getPaypal_payment_id(); //objJson.getString("transaction_id")
                        if (method.isNetworkAvailable()) {
                            new Transaction(PayPalActivity.this)
                                    .purchasedItem(planId, method.getUserId(), paymentId, planGateway);
                        } else {
                            showError(getString(R.string.no_internet_msg));
                        }
                    } else {
                        showError(paypalCheckOutRP.getMsg());
                    }

                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.no_error_msg));
                }

                pDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<BraintreeCheckOutRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                pDialog.dismiss();
                method.alertBox(getResources().getString(R.string.no_error_msg));
            }
        });
    }

    private void showError(String Title) {
        new AlertDialog.Builder(PayPalActivity.this)
                .setTitle(getString(R.string.paypal_payment_error_4))
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
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
