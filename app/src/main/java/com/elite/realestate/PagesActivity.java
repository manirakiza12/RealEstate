package com.elite.realestate;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebSettings;

import androidx.appcompat.app.AppCompatActivity;

import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.databinding.ActivityPagesBinding;


public class PagesActivity extends AppCompatActivity {

    ActivityPagesBinding viewPages;
    Method method;
    String pageTitle, pageDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(PagesActivity.this);
        viewPages = ActivityPagesBinding.inflate(getLayoutInflater());
        setContentView(viewPages.getRoot());

        method = new Method(PagesActivity.this);
        method.forceRTLIfSupported();

        Intent intent = getIntent();
        pageTitle = intent.getStringExtra("PAGE_TITLE");
        pageDesc = intent.getStringExtra("PAGE_DESC");

        viewPages.toolbarMain.tvName.setText(pageTitle);
        viewPages.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        WebSettings webSettings = viewPages.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        viewPages.webView.setBackgroundColor(Color.TRANSPARENT);
        viewPages.webView.setFocusableInTouchMode(false);
        viewPages.webView.setFocusable(false);
        viewPages.webView.getSettings().setDefaultTextEncodingName("UTF-8");
        String mimeType = "text/html";
        String encoding = "utf-8";

        String text = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/urbanistbold.ttf\")}body{font-family: MyFont;color: " + method.webViewAboutText() + " font-size: 15px;line-height:1.7}"
                + "a {color:" + method.webViewLink() + "text-decoration:none}"
                + "</style></head>"
                + "<body>"
                + pageDesc
                + "</body></html>";

        viewPages.webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);
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
