package com.elite.util;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;

import androidx.multidex.MultiDex;

import com.elite.realestate.ActivitySplash;
import com.facebook.FacebookSdk;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OSNotificationOpenedResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;


public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        AudienceNetworkAds.initialize(this);
        MobileAds.initialize(this, initializationStatus -> {
        });


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        OneSignal.initWithContext(this);
        OneSignal.setAppId("7de6c54f-7717-4d96-aeef-3219835115bd");
        OneSignal.setNotificationOpenedHandler(new NotificationExtenderExample());

    }

    class NotificationExtenderExample implements OneSignal.OSNotificationOpenedHandler {

        @Override
        public void notificationOpened(OSNotificationOpenedResult result) {
            JSONObject data = result.getNotification().getAdditionalData();
            String postId, postTitle;
            String isExternalLink;

            postId = data.optString("post_id", null);
            isExternalLink = data.optString("external_link", null);
            postTitle = data.optString("post_title", null);
            Intent intent;
            if (postId.equals("") && !isExternalLink.equals("false") && !isExternalLink.trim().isEmpty()) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(isExternalLink));
            } else {
                intent = new Intent(MyApplication.this, ActivitySplash.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("id", postId);
                intent.putExtra("type", "Noti");
                intent.putExtra("title", postTitle);
            }
            startActivity(intent);
        }
    }
}
