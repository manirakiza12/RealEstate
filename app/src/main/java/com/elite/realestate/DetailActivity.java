package com.elite.realestate;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elite.adapter.GalleryAdapter;
import com.elite.adapter.LatestHomeAdapter;
import com.elite.database.DatabaseHelperRecent;
import com.elite.fragment.AmenitiesFragment;
import com.elite.fragment.ReportBookFragment;
import com.elite.item.Detail;
import com.elite.item.Gallery;
import com.elite.item.Property;
import com.elite.response.DetailRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.BannerAds;
import com.elite.util.Constant;
import com.elite.util.Events;
import com.elite.util.FavouriteIF;
import com.elite.util.GlobalBus;
import com.elite.util.Method;
import com.elite.util.OnClick;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityDetailBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity {

    ActivityDetailBinding bindingDetail;
    Method method;
    String postId, postName;
    Detail detailList;
    ArrayList<String> mAmenities;
    FragmentManager fragmentManager;
    LatestHomeAdapter latestHomeAdapter;
    GalleryAdapter galleryAdapter;
    DatabaseHelperRecent databaseHelperRecent;
    List<Property> propertyListRelated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initHomeScreen(DetailActivity.this);
        bindingDetail = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(bindingDetail.getRoot());
        GlobalBus.getBus().register(this);

        method = new Method(DetailActivity.this);
        method.forceRTLIfSupported();
        mAmenities = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();
        databaseHelperRecent = new DatabaseHelperRecent(DetailActivity.this);
        propertyListRelated = new ArrayList<>();

        Intent intent = getIntent();
        postId = intent.getStringExtra("ID");
        postName = intent.getStringExtra("CatName");

        bindingDetail.fabBack.setOnClickListener(view -> onBackPressed());

        bindingDetail.rvRelated.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        bindingDetail.rvRelated.setLayoutManager(layoutManager);
        bindingDetail.rvRelated.setFocusable(false);

        bindingDetail.rvGallery.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManagerGallery = new LinearLayoutManager(DetailActivity.this, LinearLayoutManager.HORIZONTAL, false);
        bindingDetail.rvGallery.setLayoutManager(layoutManagerGallery);
        bindingDetail.rvGallery.setFocusable(false);
        BannerAds.showBannerAds(DetailActivity.this, bindingDetail.layoutAds);

        onRequest();

        bindingDetail.ibReport.setOnClickListener(view -> {
            if (method.getIsLogin()) {
                Bundle bundle = new Bundle();
                bundle.putString("postId", postId);
                bundle.putString("userId", method.getUserId());
                ReportBookFragment reportBookFragment = new ReportBookFragment();
                reportBookFragment.setArguments(bundle);
                reportBookFragment.show(getSupportFragmentManager(), reportBookFragment.getTag());

            } else {
                Toast.makeText(DetailActivity.this, getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(DetailActivity.this, LoginActivity.class);
                intentLogin.putExtra("isFromDetail", true);
                startActivity(intentLogin);
            }
        });
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            detailData(method.getUserId());
        } else {
            onState(1);
        }
    }

    private void detailData(String userId) {
        showProgress(true);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(DetailActivity.this));
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("property_id", postId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<DetailRP> call = apiService.getDetailData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<DetailRP>() {
            @Override
            public void onResponse(@NotNull Call<DetailRP> call, @NotNull Response<DetailRP> response) {

                DetailRP detailRP = response.body();
                if (detailRP != null) {
                    showProgress(false);
                    detailList = detailRP.getDetailList();

                    if (!detailList.getPost_image().equals("")) {
                        Glide.with(DetailActivity.this).load(detailList.getPost_image())
                                .placeholder(R.drawable.property_placeholder)
                                .into(bindingDetail.ivProperty);
                    }

                    bindingDetail.tvPrice.setText(Constant.constantCurrency + method.convertDec(detailList.getPrice()));
                    bindingDetail.tvView.setText(method.Format(detailList.getTotal_views()));
                    bindingDetail.tvTitle.setText(detailList.getPost_title());
                    bindingDetail.tvLocation.setText(detailList.getAddress());
                    bindingDetail.ibShare.setOnClickListener(view -> {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, detailList.getPost_title() + "\n" + detailList.getAddress() + "\n" + getResources().getString(R.string.share_msg) + "\n" + "https://play.google.com/store/apps/details?id=" + getPackageName());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    });

                    bindingDetail.ibLocation.setOnClickListener(view -> {
                        String geoUri = "http://maps.google.com/maps?q=loc:" + detailList.getLatitude() + "," + detailList.getLongitude() + " (" + detailList.getPost_title() + ")";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        startActivity(intent);
                    });

                    if (detailList.getFavourite()) {
                        bindingDetail.ibFav.setImageResource(R.drawable.ic_fav_hover);
                    } else {
                        bindingDetail.ibFav.setImageResource(R.drawable.ic_fav);
                    }
                    bindingDetail.ibFav.setOnClickListener(view -> {
                        if (method.getIsLogin()) {
                            FavouriteIF favouriteIF = (isFavourite, message) -> {
                                if (isFavourite.equals("true")) {
                                    bindingDetail.ibFav.setImageResource(R.drawable.ic_fav_hover);
                                } else {
                                    bindingDetail.ibFav.setImageResource(R.drawable.ic_fav);
                                }
                            };
                            method.addToFav(detailList.getPost_id(), method.getUserId(), "Property", favouriteIF);
                        } else {
                            Toast.makeText(DetailActivity.this, getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                            Intent intentLogin = new Intent(DetailActivity.this, LoginActivity.class);
                            intentLogin.putExtra("isFromDetail", true);
                            startActivity(intentLogin);
                        }
                    });

                    ContentValues contentValuesRecent = new ContentValues();
                    if (databaseHelperRecent.getRecentById(postId)) {
                        databaseHelperRecent.removeRecentById(postId);
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_ID, postId);
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_TITLE, detailList.getPost_title());
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_IMAGE, detailList.getPost_image());
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_PRICE, detailList.getPrice());
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_ADDRESS, detailList.getAddress());
                        databaseHelperRecent.addRecent(DatabaseHelperRecent.TABLE_FAVOURITE_NAME, contentValuesRecent, null);
                    } else {
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_ID, postId);
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_TITLE, detailList.getPost_title());
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_IMAGE, detailList.getPost_image());
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_PRICE, detailList.getPrice());
                        contentValuesRecent.put(DatabaseHelperRecent.KEY_ADDRESS, detailList.getAddress());
                        databaseHelperRecent.addRecent(DatabaseHelperRecent.TABLE_FAVOURITE_NAME, contentValuesRecent, null);
                    }

                    bindingDetail.tvBeds.setText(detailList.getBedrooms() + getString(R.string.detail_bed));
                    bindingDetail.tvBath.setText(detailList.getBathrooms() + getString(R.string.detail_bath));
                    bindingDetail.tvSF.setText(detailList.getArea());
                    bindingDetail.tvFurn.setText(detailList.getFurnishing());
                    if (detailList.getVerified().equals("YES")) {
                        bindingDetail.tvVery.setText(getString(R.string.detail_very));
                    } else {
                        bindingDetail.tvVery.setText(getString(R.string.detail_non_very));
                    }
                    if (!detailList.getUser_image().equals("")) {
                        Glide.with(DetailActivity.this).load(detailList.getUser_image())
                                .placeholder(R.drawable.img_owner)
                                .into(bindingDetail.ivOwner);
                    }
                    bindingDetail.tvOwnerName.setText(detailList.getUser_name());
                    bindingDetail.ibWhatApp.setOnClickListener(view -> {
                        String url = "https://api.whatsapp.com/send?phone=" + detailList.getPhone();
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    });

                    bindingDetail.ibCall.setOnClickListener(view -> {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + detailList.getPhone()));
                        startActivity(callIntent);
                    });

                    WebSettings webSettings = bindingDetail.wvDesc.getSettings();
                    webSettings.setJavaScriptEnabled(true);
                    webSettings.setPluginState(WebSettings.PluginState.ON);
                    bindingDetail.wvDesc.setBackgroundColor(Color.TRANSPARENT);
                    bindingDetail.wvDesc.setFocusableInTouchMode(false);
                    bindingDetail.wvDesc.setFocusable(false);

                    bindingDetail.wvDesc.getSettings().setDefaultTextEncodingName("UTF-8");
                    String mimeType = "text/html";
                    String encoding = "utf-8";

                    String text = "<html dir=" + method.isWebViewTextRtl() + "><head>"
                            + "<style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/urbanistbold.ttf\")}body{font-family: MyFont;color: " + method.webViewText() + "font-size: 15px;line-height:1.7;margin-left: 0px;margin-right: 0px;margin-top: 0px;margin-bottom: 0px;padding: 0px;}"
                            + "a {color:" + method.webViewLink() + "text-decoration:none}"
                            + "</style></head>"
                            + "<body>"
                            + detailList.getPost_description()
                            + "</body></html>";

                    bindingDetail.wvDesc.loadDataWithBaseURL(null, text, mimeType, encoding, null);
                    if (!detailList.getAmenities().isEmpty())
                        mAmenities = new ArrayList<>(Arrays.asList(detailList.getAmenities().split(",")));
                    if (!detailList.getAmenities().isEmpty()) {
                        AmenitiesFragment amenitiesFragment = AmenitiesFragment.newInstance(mAmenities);
                        fragmentManager.beginTransaction().replace(R.id.ContainerAmenities, amenitiesFragment).commit();
                    } else {
                        bindingDetail.tvAme.setVisibility(View.GONE);
                        bindingDetail.ContainerAmenities.setVisibility(View.GONE);
                    }

                    if (!detailList.getFloor_plan_image().equals("")) {
                        Glide.with(DetailActivity.this).load(detailList.getFloor_plan_image())
                                .placeholder(R.drawable.property_placeholder)
                                .into(bindingDetail.ivFloor);
                    }
                    bindingDetail.ivFloor.setOnClickListener(view -> {
                        Intent intentShow = new Intent(DetailActivity.this, FullGalleryActivity.class);
                        intentShow.putExtra("imageId", detailList.getFloor_plan_image());
                        intentShow.putExtra("imageType", "Floor");
                        startActivity(intentShow);
                    });

                    if (detailList.getRelatedProperty().isEmpty()) {
                        bindingDetail.rvRelated.setVisibility(View.GONE);
                        bindingDetail.rlRelated.setVisibility(View.GONE);
                    } else {
                        int k = Math.min(detailList.getRelatedProperty().size(), 3);
                        List<Property> propertyListRelated = new ArrayList<>();
                        for (int j = 0; j < k; j++) {
                            propertyListRelated.add(detailList.getRelatedProperty().get(j));
                        }
                        latestHomeAdapter = new LatestHomeAdapter(DetailActivity.this, propertyListRelated);
                        bindingDetail.rvRelated.setAdapter(latestHomeAdapter);
                        latestHomeAdapter.setOnItemClickListener(position -> {
                            Intent intentDetail = new Intent(DetailActivity.this, DetailActivity.class);
                            intentDetail.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intentDetail.putExtra("ID", propertyListRelated.get(position).getPropertyId());
                            startActivity(intentDetail);
                        });

                        bindingDetail.tvSeeAllRelated.setOnClickListener(view -> {
                            Intent intentRelated = new Intent(DetailActivity.this, RelatedPropertyActivity.class);
                            intentRelated.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intentRelated.putExtra("postId", postId);
                            startActivity(intentRelated);
                        });
                    }

                    bindingDetail.tvSeeAllGallery.setOnClickListener(view -> {
                        Intent intentGallery = new Intent(DetailActivity.this, GalleryDetailActivity.class);
                        intentGallery.putExtra("imagePos", 0);
                        startActivity(intentGallery);
                    });
                    if (detailList.getGalleryList().isEmpty()) {
                        bindingDetail.rvGallery.setVisibility(View.GONE);
                        bindingDetail.rlGallery.setVisibility(View.GONE);
                    } else {
                        int k = Math.min(detailList.getGalleryList().size(), 3);
                        List<Gallery> gallery = new ArrayList<>();
                        for (int j = 0; j < k; j++) {
                            gallery.add(detailList.getGalleryList().get(j));
                        }
                        Constant.listGallery=detailList.getGalleryList();
                        galleryAdapter = new GalleryAdapter(DetailActivity.this, gallery, detailList.getGalleryList().size() - k);
                        bindingDetail.rvGallery.setAdapter(galleryAdapter);
                        galleryAdapter.setOnItemClickListener(new OnClick() {
                            @Override
                            public void position(int position) {
                                Intent intentShow = new Intent(DetailActivity.this, GalleryDetailActivity.class);
                                intentShow.putExtra("imagePos", position);
                                startActivity(intentShow);
                            }
                        });
                    }
                    postViewData(postId);

                } else {
                    onState(3);//error
                }
            }

            @Override
            public void onFailure(@NotNull Call<DetailRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                onState(3);
            }
        });
    }

    private void onState(int state) {
        bindingDetail.layState.getRoot().setVisibility(View.VISIBLE);
        bindingDetail.clMain.setVisibility(View.GONE);
        bindingDetail.progressHome.setVisibility(View.GONE);
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
        bindingDetail.layState.ivState.setImageResource(image);
        bindingDetail.layState.tvState.setText(title);
        bindingDetail.layState.tvStateMsg.setText(desc);

        bindingDetail.layState.btnRefreshNow.setOnClickListener(view -> {
            bindingDetail.layState.getRoot().setVisibility(View.GONE);
            onRequest();
        });
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingDetail.progressHome.setVisibility(View.VISIBLE);
            bindingDetail.clMain.setVisibility(View.GONE);
        } else {
            bindingDetail.progressHome.setVisibility(View.GONE);
            bindingDetail.clMain.setVisibility(View.VISIBLE);
        }
    }

    private void postViewData(String propertyId) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(DetailActivity.this));
        jsObj.addProperty("post_id", propertyId);
        jsObj.addProperty("post_type", "Property");
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<JsonObject> call = apiService.getPostViewData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NotNull Call<JsonObject> call, @NotNull Response<JsonObject> response) {
            }

            @Override
            public void onFailure(@NotNull Call<JsonObject> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void getFav(Events.FavProperty favProperty) {
        for (int i = 0; i < propertyListRelated.size(); i++) {
            if (propertyListRelated.get(i) != null) {
                if (propertyListRelated.get(i).getPropertyId().equals(favProperty.getPropertyId())) {
                    propertyListRelated.get(i).setPropertyFavorite(Boolean.parseBoolean(favProperty.getIsFav()));
                    latestHomeAdapter.notifyItemChanged(i);
                }
            }
        }
    }
}