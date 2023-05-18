package com.elite.paymentmethod;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.elite.realestate.EditProfileActivity;
import com.elite.response.PayUMoneyHashRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.BuildConfig;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityPaymentBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.payu.base.models.ErrorResponse;
import com.payu.base.models.PayUPaymentParams;
import com.payu.checkoutpro.PayUCheckoutPro;
import com.payu.checkoutpro.models.PayUCheckoutProConfig;
import com.payu.checkoutpro.utils.PayUCheckoutProConstants;
import com.payu.ui.model.listeners.PayUCheckoutProListener;
import com.payu.ui.model.listeners.PayUHashGenerationListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PayUMoneyActivity extends AppCompatActivity {

    String planId, planPrice, planCurrency, planGateway, planGateWayText, payUMoneyMerchantId, payUMoneyMerchantKey, planName;
    Method method;
    boolean isSandbox = false;
    public static final String SURL = BuildConfig.My_api + "app_payu_success";
    public static final String FURL = BuildConfig.My_api + "app_payu_failed";
    ProgressDialog pDialog;
    ActivityPaymentBinding payUMoneyBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(PayUMoneyActivity.this);
        payUMoneyBinding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(payUMoneyBinding.getRoot());

        method = new Method(PayUMoneyActivity.this);
        payUMoneyBinding.toolbarMain.tvName.setText(getString(R.string.payment_payumoney));
        payUMoneyBinding.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        planId = intent.getStringExtra("planId");
        planName = intent.getStringExtra("planName");
        planPrice = intent.getStringExtra("planPrice");
        planCurrency = intent.getStringExtra("planCurrency");
        planGateway = intent.getStringExtra("planGateway");
        planGateWayText = intent.getStringExtra("planGatewayText");
        payUMoneyMerchantId = intent.getStringExtra("payUMoneyMerchantId");
        payUMoneyMerchantKey = intent.getStringExtra("payUMoneyMerchantKey");
        isSandbox = intent.getBooleanExtra("isSandbox", false);

        pDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);

        if (method.getUserPhone().isEmpty()) {
            showErrorPhone();
        } else {
            startPayment();
        }

        payUMoneyBinding.btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (method.getUserPhone().isEmpty()) {
                    showErrorPhone();
                } else {
                    startPayment();
                }

            }
        });

    }

    public void startPayment() {
        PayUPaymentParams.Builder builder = new PayUPaymentParams.Builder();
        builder.setAmount(planPrice)
                .setIsProduction(!isSandbox)
                .setProductInfo(planName)
                .setKey(payUMoneyMerchantKey)
                .setTransactionId(System.currentTimeMillis() + "")
                .setFirstName(method.getUserName())
                .setEmail(method.getUserEmail())
                .setPhone(method.getUserPhone())
                .setUserCredential(method.getUserEmail())
                .setSurl(SURL)
                .setFurl(FURL);
        PayUPaymentParams payUPaymentParams = builder.build();
        PayUCheckoutProConfig payUCheckoutProConfig = new PayUCheckoutProConfig();
        payUCheckoutProConfig.setMerchantName(getString(R.string.app_name));
        payUCheckoutProConfig.setMerchantLogo(R.mipmap.app_icon);

        PayUCheckoutPro.open(PayUMoneyActivity.this, payUPaymentParams, payUCheckoutProConfig, new PayUCheckoutProListener() {
            @Override
            public void onPaymentSuccess(@NonNull Object response) {
                HashMap<String, Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                //  String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                try {
                    assert payuResponse != null;
                    JSONObject mainJson = new JSONObject(payuResponse);
                    String paymentId = mainJson.getString("txnid");
                    if (method.isNetworkAvailable()) {
                        new Transaction(PayUMoneyActivity.this)
                                .purchasedItem(planId, method.getUserId(), paymentId, planGateway);
                    } else {
                        showError(getString(R.string.no_internet_msg));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPaymentFailure(@NonNull Object response) {
                HashMap<String, Object> result = (HashMap<String, Object>) response;
                String payuResponse = (String) result.get(PayUCheckoutProConstants.CP_PAYU_RESPONSE);
                //    String merchantResponse = (String) result.get(PayUCheckoutProConstants.CP_MERCHANT_RESPONSE);
                try {
                    assert payuResponse != null;
                    JSONObject mainJson = new JSONObject(payuResponse);
                    String errorMessage = mainJson.getString("Error_Message");
                    showError(errorMessage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onPaymentCancel(boolean b) {

            }

            @Override
            public void onError(@NonNull ErrorResponse errorResponse) {
                String errorMessage = errorResponse.getErrorMessage();
                Log.e("onError", "Yes");
                showError(errorMessage);
            }

            @Override
            public void generateHash(@NonNull HashMap<String, String> hashMap, @NonNull PayUHashGenerationListener payUHashGenerationListener) {
                String hashName = hashMap.get(PayUCheckoutProConstants.CP_HASH_NAME);
                String hashData = hashMap.get(PayUCheckoutProConstants.CP_HASH_STRING);
                if (!TextUtils.isEmpty(hashName) && !TextUtils.isEmpty(hashData)) {
                    //Do not generate hash from local, it needs to be calculated from server side only. Here, hashString contains hash created from your server side.
                    getHash(hashName, hashData, payUHashGenerationListener);
                }
            }

            @Override
            public void setWebViewProperties(@Nullable WebView webView, @Nullable Object o) {

            }
        });
    }

    private void showError(String Title) {
        new AlertDialog.Builder(PayUMoneyActivity.this)
                .setTitle(getString(R.string.payment_payumoney))
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

    private void showErrorPhone() {
        new AlertDialog.Builder(PayUMoneyActivity.this)
                .setTitle(getString(R.string.payment_payumoney))
                .setMessage(getString(R.string.payment_need_phone))
                .setIcon(R.mipmap.app_icon)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(PayUMoneyActivity.this, EditProfileActivity.class);
                        intent.putExtra("isFromPayU", true);
                        startActivityForResult(intent, 1118);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1118 && resultCode == 1187) {
            if (method.getUserPhone().isEmpty()) {
                showErrorPhone();
            } else {
                startPayment();
            }
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

    private void getHash(String hashName, String hashData, PayUHashGenerationListener payUHashGenerationListener) {
        pDialog.show();
        pDialog.setMessage(getResources().getString(R.string.loading));
        pDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(PayUMoneyActivity.this));
        jsObj.addProperty("hashdata", hashData);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PayUMoneyHashRP> call = apiService.getPayUMoneyHashData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<PayUMoneyHashRP>() {
            @Override
            public void onResponse(@NotNull Call<PayUMoneyHashRP> call, @NotNull Response<PayUMoneyHashRP> response) {
                try {
                    PayUMoneyHashRP paypalTokenRP = response.body();

                    if (paypalTokenRP != null && paypalTokenRP.getSuccess().equals("1")) {
                        HashMap<String, String> dataMap = new HashMap<>();
                        dataMap.put(hashName, paypalTokenRP.getItemPaypalTokens().getPayu_hash());
                        payUHashGenerationListener.onHashGenerated(dataMap);
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
            public void onFailure(@NotNull Call<PayUMoneyHashRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                pDialog.dismiss();
                method.alertBox(getResources().getString(R.string.no_error_msg));
            }
        });
    }
}
