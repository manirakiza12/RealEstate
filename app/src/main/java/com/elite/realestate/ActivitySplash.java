package com.elite.realestate;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.elite.response.AppDetailRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Constant;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivitySplashBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.wortise.ads.WortiseSdk;
import com.wortise.ads.consent.ConsentManager;

import org.jetbrains.annotations.NotNull;

import java.util.Currency;

import kotlin.Unit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ActivitySplash extends AppCompatActivity {

    ActivitySplashBinding bindingSplash;
    Method method;
    static int SPLASH_TIME_OUT = 1000;
    String str_package;
    Boolean isCancelled = false;
    int WAIT = 3000;
    private String id = "0", type = "", title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initSplashScreen(ActivitySplash.this);

        bindingSplash = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(bindingSplash.getRoot());

        method = new Method(ActivitySplash.this);
        method.forceRTLIfSupported();

        new Handler(Looper.getMainLooper()).postDelayed(this::onRequest, SPLASH_TIME_OUT);
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("id")) {
                id = intent.getStringExtra("id");
                type = intent.getStringExtra("type");
                title = intent.getStringExtra("title");
            }
        }
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            if (method.getIsLogin()) {
                appDetailData(method.getUserId());
            } else {
                appDetailData("0");
            }
        } else {
            onState(1);
        }
    }

    private void appDetailData(String userId) {

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ActivitySplash.this));
        jsObj.addProperty("user_id", userId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<AppDetailRP> call = apiService.getAppDetailData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<AppDetailRP>() {
            @Override
            public void onResponse(@NotNull Call<AppDetailRP> call, @NotNull Response<AppDetailRP> response) {

                AppDetailRP appDetailRP = response.body();
                if (appDetailRP != null) {

                    Currency currency = Currency.getInstance(appDetailRP.getRealEstate().getCurrencyCode());
                    Constant.appListData = appDetailRP.getRealEstate();
                    if (!Constant.appListData.getAdsList().isEmpty()) {
                        Constant.adsInfo = Constant.appListData.getAdsList().get(0).getAdsInfo();
                        if (Constant.appListData.getAdsList().get(0).getAdsInfo().getNativeOnOff() != null) {
                            Constant.isNative = Constant.appListData.getAdsList().get(0).getAdsInfo().getNativeOnOff().equals("1");
                            Constant.nativeId = Constant.adsInfo.getNativeId();
                            Constant.nativePosition = Constant.adsInfo.getNativePosition();
                        }
                        Constant.isBanner = Constant.appListData.getAdsList().get(0).getAdsInfo().getBannerOnOff().equals("1");
                        Constant.isInterstitial = Constant.appListData.getAdsList().get(0).getAdsInfo().getInterstitialOnOff().equals("1");
                        Constant.publisherId = Constant.adsInfo.getPublisherId();
                        Constant.bannerId = Constant.adsInfo.getBannerId();
                        Constant.interstitialId = Constant.adsInfo.getInterstitialId();
                        Constant.adNetworkType = Constant.appListData.getAdsList().get(0).getAdsName();
                        Constant.interstitialClick = Constant.adsInfo.getInterstitialClicks();
                        initializeAds();
                    }
                    Constant.minPropertyPrice = Constant.appListData.getMinPrice();
                    Constant.maxPropertyPrice = Constant.appListData.getMaxPrice();


                    Constant.appUpdateVersion = Constant.appListData.getAppUpdateVersionCode();
                    Constant.appUpdateUrl = Constant.appListData.getAppUpdateLink();
                    Constant.appUpdateDesc = Constant.appListData.getAppUpdateDesc();
                    Constant.isAppUpdate = Constant.appListData.getAppUpdateHideShow().equals("true");
                    Constant.isAppUpdateCancel = Constant.appListData.getAppUpdateCancelOption().equals("true");

                    String checkLogin = appDetailRP.getLoginStatus();
                    String symbol = currency.getSymbol();
                    str_package = appDetailRP.getRealEstate().getAppPackageName();
                    Constant.constantCurrency = symbol;
                    if (str_package.equals(getPackageName())) {
                        new Handler().postDelayed(() -> {
                            if (!isCancelled) {
                                if (method.isWelcome()) {
                                    startActivity(new Intent(ActivitySplash.this, IntroActivity.class));
                                    finish();
                                    method.setFirstWelcome(false);
                                } else {
                                    if (method.getIsLogin()) {
                                        if (checkLogin.equals("true")) {
                                            callActivity();
                                        } else {
                                            method.saveIsLogin(false);
                                            method.setUserId("");
                                            Intent intent = new Intent(ActivitySplash.this, LoginActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                            finish();
                                            Toast.makeText(ActivitySplash.this, getString(R.string.logout_disable), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        callActivity();
                                    }
                                }
                            }

                        }, WAIT);
                    } else {
                        method.alertBox(getResources().getString(R.string.license_msg));
                    }
                } else {
                    onState(3);//error
                }
            }

            @Override
            public void onFailure(@NotNull Call<AppDetailRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                onState(3);
            }
        });
    }

    private void onState(int state) {
        bindingSplash.layState.getRoot().setVisibility(View.VISIBLE);
        bindingSplash.splashScreen.setVisibility(View.GONE);
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
        bindingSplash.layState.ivState.setImageResource(image);
        bindingSplash.layState.tvState.setText(title);
        bindingSplash.layState.tvStateMsg.setText(desc);

        bindingSplash.layState.btnRefreshNow.setOnClickListener(view -> {
            bindingSplash.layState.getRoot().setVisibility(View.GONE);
            bindingSplash.splashScreen.setVisibility(View.VISIBLE);
            onRequest();
        });
    }

    private void callActivity() {
        if (type.equals("Noti")) {
            if (!id.isEmpty()) {
                Intent intentCatList = new Intent(ActivitySplash.this, CategoryListActivity.class);
                intentCatList.putExtra("CatId", id);
                intentCatList.putExtra("CatName", title);
                startActivity(intentCatList);
                finishAffinity();
            } else {
                Intent intent = new Intent(ActivitySplash.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finishAffinity();
            }
        } else {
            Intent intent = new Intent(ActivitySplash.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAffinity();
        }

    }

    private void initializeAds() {
        switch (Constant.adNetworkType) {
            case "Unity Ads":
                UnityAds.initialize(ActivitySplash.this, Constant.adsInfo.getPublisherId(), false, new IUnityAdsInitializationListener() {
                    @Override
                    public void onInitializationComplete() {
                        Log.d(TAG, "Unity Ads Initialization Complete");
                    }

                    @Override
                    public void onInitializationFailed(UnityAds.UnityAdsInitializationError error, String message) {
                        Log.d(TAG, "Unity Ads Initialization Failed: [" + error + "] " + message);
                    }
                });
                break;
            case "AppLovins MAX":
                AppLovinSdk.getInstance(ActivitySplash.this).setMediationProvider(AppLovinMediationProvider.MAX);
                AppLovinSdk.getInstance(ActivitySplash.this).initializeSdk(config -> {

                });
                break;
            case "StartApp":
                StartAppSDK.init(this, Constant.adsInfo.getPublisherId(), false);
                StartAppAd.disableSplash();

                break;
            case "Wortise":
                WortiseSdk.initialize(this, Constant.adsInfo.getPublisherId(), true, () -> {
                    ConsentManager.requestOnce(ActivitySplash.this);
                    return Unit.INSTANCE;
                });
                break;
        }

    }

    @Override
    protected void onDestroy() {
        isCancelled = true;
        super.onDestroy();
    }
}