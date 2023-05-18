package com.elite.realestate;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.PorterDuff;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.elite.fragment.CategoryFragment;
import com.elite.fragment.HomeFragment;
import com.elite.fragment.LatestFragment;
import com.elite.fragment.MyPropertyFragment;
import com.elite.fragment.SettingFragment;
import com.elite.response.SubscriptionRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.BannerAds;
import com.elite.util.Constant;
import com.elite.util.Method;
import com.elite.util.OnClick;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ixidev.gdpr.BuildConfig;
import com.ixidev.gdpr.GDPRChecker;

import org.jetbrains.annotations.NotNull;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    ActivityMainBinding bindingMain;
    Method method;
    FragmentManager fragmentManager;
    FirebaseAnalytics firebaseAnalytics;
    FrameLayout[] frameLayout;
    ImageView[] imageViews;
    TextView[] textViews;
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private GoogleApiClient googleApiClient;
    private Location myLocation;
    ProgressDialog progressDialog;
    int versionCode;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initHomeScreen(MainActivity.this);
        bindingMain = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bindingMain.getRoot());

        method = new Method(MainActivity.this);
        method.forceRTLIfSupported();
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        versionCode = BuildConfig.VERSION_CODE;

        try {
            PackageInfo info;
            info = getPackageManager().getPackageInfo(
                    getPackageName(), //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        fragmentManager = getSupportFragmentManager();
        frameLayout = new FrameLayout[]{bindingMain.bottomNav.fmHome, bindingMain.bottomNav.fmLatest, bindingMain.bottomNav.fmCat, bindingMain.bottomNav.fmProperty, bindingMain.bottomNav.fmSetting};
        imageViews = new ImageView[]{bindingMain.bottomNav.ivHome, bindingMain.bottomNav.ivLatest, bindingMain.bottomNav.ivCat, bindingMain.bottomNav.ivProperty, bindingMain.bottomNav.ivSetting};
        textViews = new TextView[]{bindingMain.bottomNav.tvHome, bindingMain.bottomNav.tvLatest, bindingMain.bottomNav.tvCat, bindingMain.bottomNav.tvSetting, bindingMain.bottomNav.tvSetting};
        progressDialog = new ProgressDialog(MainActivity.this, R.style.MyAlertDialogStyle);

        if (Constant.isBanner){
            if (Constant.adNetworkType.equals("Admob")) {
                checkForConsent();
            } else {
                BannerAds.showBannerAds(MainActivity.this, bindingMain.layoutAds);
            }
        }

        if (Constant.appUpdateVersion > versionCode && Constant.isAppUpdate) {
            newUpdateDialog();
        }
        selectBottomNav(0);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setOnItemClickListener(position -> {
            if (position == 1) {
                selectBottomNav(1);
                LatestFragment latestFragment = new LatestFragment();
                loadFrag(latestFragment, "", fragmentManager);
            } else if (position == 2) {
                selectBottomNav(2);
                CategoryFragment categoryFragment = new CategoryFragment();
                loadFrag(categoryFragment, "", fragmentManager);
            }

        });
        loadFrag(homeFragment, "", fragmentManager);
        setUpGClient();

        bindingMain.ivAddProperty.setOnClickListener(view -> {
            if (method.getIsLogin()) {
                subscriptionCheckData();
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                intentLogin.putExtra("isFromDetail", true);
                startActivity(intentLogin);
            }
        });


        bindingMain.bottomNav.llHome.setOnClickListener(view -> {
            selectBottomNav(0);
            HomeFragment homeFragment1 = new HomeFragment();
            homeFragment1.setOnItemClickListener(new OnClick() {
                @Override
                public void position(int position) {
                    if (position == 1) {
                        selectBottomNav(1);
                        LatestFragment latestFragment = new LatestFragment();
                        loadFrag(latestFragment, "", fragmentManager);
                    } else if (position == 2) {
                        selectBottomNav(2);
                        CategoryFragment categoryFragment = new CategoryFragment();
                        loadFrag(categoryFragment, "", fragmentManager);
                    }

                }
            });
            loadFrag(homeFragment1, "", fragmentManager);
        });

        bindingMain.bottomNav.llLatest.setOnClickListener(view -> {
            selectBottomNav(1);
            LatestFragment latestFragment = new LatestFragment();
            loadFrag(latestFragment, "", fragmentManager);
        });

        bindingMain.bottomNav.llCat.setOnClickListener(view -> {
            selectBottomNav(2);
            CategoryFragment categoryFragment = new CategoryFragment();
            loadFrag(categoryFragment, "", fragmentManager);
        });

        bindingMain.bottomNav.llProperty.setOnClickListener(view -> {
            selectBottomNav(3);
            if (method.getIsLogin()) {
                MyPropertyFragment myPropertyFragment = new MyPropertyFragment();
                loadFrag(myPropertyFragment, "", fragmentManager);
            } else {
                Toast.makeText(MainActivity.this, getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intentLogin);
            }
        });

        bindingMain.bottomNav.llSetting.setOnClickListener(view -> {
            selectBottomNav(4);
            SettingFragment settingFragment = new SettingFragment();
            loadFrag(settingFragment, "", fragmentManager);
        });


    }

    private void selectBottomNav(int pos) {
        for (int i = 0; i < frameLayout.length; i++) {
            if (i == pos) {
                frameLayout[i].setBackgroundResource(R.drawable.bottom_bar_select_bg);
                imageViews[i].setColorFilter(getResources().getColor(R.color.bottom_bar_selected_title), PorterDuff.Mode.SRC_IN);
                textViews[i].setTextColor(getResources().getColor(R.color.bottom_bar_selected_title));
            } else {
                frameLayout[i].setBackgroundResource(R.drawable.bottom_bar_normal_bg);
                imageViews[i].setColorFilter(getResources().getColor(R.color.bottom_bar_normal_title), PorterDuff.Mode.SRC_IN);
                textViews[i].setTextColor(getResources().getColor(R.color.bottom_bar_normal_title));
            }
        }
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameMain, f1, name);
        ft.commitAllowingStateLoss();
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;
        if (myLocation != null) {
            Double latitude = myLocation.getLatitude();
            Double longitude = myLocation.getLongitude();
            Constant.USER_LATITUDE = String.valueOf(latitude);
            Constant.USER_LONGITUDE = String.valueOf(longitude);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS_GPS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    getMyLocation();
                    break;
                case Activity.RESULT_CANCELED:
                    permissionReject();
                    break;
            }
        }
    }

    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this,
                        listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            }
        } else {
            getMyLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {
            permissionReject();
        }
    }

    private void permissionReject() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(getString(R.string.app_name))
                .setMessage(getString(R.string.location_permission))
                .setIcon(R.mipmap.app_icon)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(1000 * 1000);
                    locationRequest.setFastestInterval(1000 * 1000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi
                            .requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result =
                            LocationServices.SettingsApi
                                    .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(result1 -> {
                        final Status status = result1.getStatus();
                        switch (status.getStatusCode()) {
                            case LocationSettingsStatusCodes.SUCCESS:
                                // All location settings are satisfied.
                                // You can initialize location requests here.
                                int permissionLocation1 = ContextCompat
                                        .checkSelfPermission(MainActivity.this,
                                                Manifest.permission.ACCESS_FINE_LOCATION);
                                if (permissionLocation1 == PackageManager.PERMISSION_GRANTED) {
                                    myLocation = LocationServices.FusedLocationApi
                                            .getLastLocation(googleApiClient);
                                }
                                break;
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                // Location settings are not satisfied.
                                // But could be fixed by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    // Ask to turn on GPS automatically
                                    status.startResolutionForResult(MainActivity.this,
                                            REQUEST_CHECK_SETTINGS_GPS);
                                } catch (IntentSender.SendIntentException e) {
                                    // Ignore the error.
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                // Location settings are not satisfied.
                                // However, we have no way
                                // to fix the
                                // settings so we won't show the dialog.
                                // finish();
                                break;
                        }
                    });
                }
            }
        }
    }

    private void subscriptionCheckData() {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(MainActivity.this));
        jsObj.addProperty("user_id", method.getUserId());
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<SubscriptionRP> call = apiService.getSubscriptionData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<SubscriptionRP>() {
            @Override
            public void onResponse(@NotNull Call<SubscriptionRP> call, @NotNull Response<SubscriptionRP> response) {

                SubscriptionRP subscriptionRP = response.body();
                progressDialog.dismiss();
                if (subscriptionRP != null) {
                    if (subscriptionRP.getSuccess().equals("0")) {
                        Intent intentPlan = new Intent(MainActivity.this, PlanActivity.class);
                        startActivity(intentPlan);
                        Toast.makeText(MainActivity.this, subscriptionRP.getMsg(), Toast.LENGTH_SHORT).show();
                    } else {
                        if (subscriptionRP.getItemSubscription().getProperty_limit_reached().equals("true")) {
                            Intent intentPlan = new Intent(MainActivity.this, PlanActivity.class);
                            startActivity(intentPlan);
                            Toast.makeText(MainActivity.this, getString(R.string.plan_msg), Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intentUpload = new Intent(MainActivity.this, UploadActivity.class);
                            startActivity(intentUpload);
                        }
                    }

                } else {
                    method.alertBox(getString(R.string.no_error_msg));
                }
            }

            @Override
            public void onFailure(@NotNull Call<SubscriptionRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                method.alertBox(getString(R.string.no_error_msg));
            }
        });
    }

    public void checkForConsent() {
        new GDPRChecker()
                .withContext(MainActivity.this)
                .withPrivacyUrl(getString(R.string.privacy_link))
                .withPublisherIds(Constant.publisherId)
                .check();
        BannerAds.showBannerAds(MainActivity.this, bindingMain.layoutAds);
    }

    private void newUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getString(R.string.app_update_title));
        builder.setCancelable(false);
        builder.setMessage(Constant.appUpdateDesc);
        builder.setPositiveButton(getString(R.string.app_update_btn), (dialog, which) -> startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(Constant.appUpdateUrl))));
        if (Constant.isAppUpdateCancel) {
            builder.setNegativeButton(getString(R.string.app_cancel_btn), (dialog, which) -> {

            });
        }
        builder.setIcon(R.mipmap.app_icon);
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() != 0) {
            super.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_key), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
}