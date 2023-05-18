package com.elite.realestate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.elite.util.Constant;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityAboutUsBinding;


public class AboutUsActivity extends AppCompatActivity {

    ActivityAboutUsBinding bindingAbout;
    ProgressDialog progressDialog;
    Method method;
    String abData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(AboutUsActivity.this);

        bindingAbout = ActivityAboutUsBinding.inflate(getLayoutInflater());
        setContentView(bindingAbout.getRoot());

        method = new Method(AboutUsActivity.this);
        method.forceRTLIfSupported();

        Intent intent=getIntent();
        abData=intent.getStringExtra("ABOUT");

        progressDialog = new ProgressDialog(AboutUsActivity.this, R.style.MyAlertDialogStyle);
        bindingAbout.toolbarMain.tvName.setText(getString(R.string.about_title));

        bindingAbout.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        if (!Constant.appListData.getAppLogo().equals("")) {
            Glide.with(AboutUsActivity.this).load(Constant.appListData.getAppLogo())
                    .placeholder(R.drawable.home_cat_placeholder)
                    .into(bindingAbout.ivAppLogo);
        }

        bindingAbout.tvAppName1.setText(Constant.appListData.getAppName());
        bindingAbout.tvAppVersion.setText(Constant.appListData.getAppVersion());
        bindingAbout.tvCompany.setText(Constant.appListData.getAppCompany());
        bindingAbout.tvEmail.setText(Constant.appListData.getAppEmail());
        bindingAbout.tvWebsite.setText(Constant.appListData.getAppWebsite());
        bindingAbout.tvContact.setText(Constant.appListData.getAppContact());

        WebSettings webSettings = bindingAbout.webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        bindingAbout.webView.setBackgroundColor(Color.TRANSPARENT);
        bindingAbout.webView.setFocusableInTouchMode(false);
        bindingAbout.webView.setFocusable(false);
        bindingAbout.webView.getSettings().setDefaultTextEncodingName("UTF-8");
        String mimeType = "text/html";
        String encoding = "utf-8";

        String text = "<html dir=" +method.isWebViewTextRtl() + "><head>"
                + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/urbanistbold.ttf\")}body{font-family: MyFont;color: " + method.webViewAboutText() + " font-size: 15px;line-height:1.7}"
                + "a {color:" + method.webViewLink() + "text-decoration:none}"
                + "</style></head>"
                + "<body>"
                + abData
                + "</body></html>";

        bindingAbout.webView.loadDataWithBaseURL(null, text, mimeType, encoding, null);

        bindingAbout.ivFb.setOnClickListener(v -> {
            if (Constant.appListData.getFacebookLink().isEmpty()) {
                Toast.makeText(AboutUsActivity.this, getString(R.string.no_link_found), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.appListData.getFacebookLink())));
            }
        });

        bindingAbout.ivInsta.setOnClickListener(v -> {
            if (Constant.appListData.getInstagramLink().isEmpty()) {
                Toast.makeText(AboutUsActivity.this, getString(R.string.no_link_found), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.appListData.getInstagramLink())));
            }
        });

        bindingAbout.ivYt.setOnClickListener(v -> {
            if (Constant.appListData.getYoutubeLink().isEmpty()) {
                Toast.makeText(AboutUsActivity.this, getString(R.string.no_link_found), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.appListData.getYoutubeLink())));
            }
        });

        bindingAbout.ivTwitter.setOnClickListener(v -> {
            if (Constant.appListData.getTwitterLink().isEmpty()) {
                Toast.makeText(AboutUsActivity.this, getString(R.string.no_link_found), Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constant.appListData.getTwitterLink())));
            }
        });

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