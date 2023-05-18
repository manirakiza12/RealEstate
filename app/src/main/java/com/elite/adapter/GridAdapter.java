package com.elite.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.bumptech.glide.Glide;
import com.elite.item.Property;
import com.elite.realestate.LoginActivity;
import com.elite.util.AdInterstitialAds;
import com.elite.util.Constant;
import com.elite.util.FavouriteIF;
import com.elite.util.Method;
import com.elite.util.OnClick;
import com.example.realestate.BuildConfig;
import com.example.realestate.R;
import com.example.realestate.databinding.RowPropertyGridBinding;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.startapp.sdk.ads.nativead.NativeAdDetails;
import com.startapp.sdk.ads.nativead.NativeAdPreferences;
import com.startapp.sdk.ads.nativead.StartAppNativeAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.wortise.ads.natives.GoogleNativeAd;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class GridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    List<Property> propertyList;
    OnClick onClick;
    Method method;
    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;
    private final int VIEW_TYPE_Ad = 2;
    private Boolean isAdLoaded = false;
    AdLoader adLoader = null;
    List<com.google.android.gms.ads.nativead.NativeAd> mNativeAdsAdmob = new ArrayList<>();

    public GridAdapter(Activity activity, List<Property> propertyList) {
        this.activity = activity;
        this.propertyList = propertyList;
        method = new Method(activity);
        loadNativeAds();
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            return new ViewHolder(RowPropertyGridBinding.inflate(activity.getLayoutInflater()));
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        } else if (viewType == VIEW_TYPE_Ad) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admob_adapter, parent, false);
            return new AdOption(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {
            final ViewHolder viewHolder = (ViewHolder) holder;
            Property propertyItem = propertyList.get(position);
            if (!propertyItem.getPropertyImage().equals("")) {
                Glide.with(activity.getApplicationContext()).load(propertyItem.getPropertyImage())
                        .placeholder(R.drawable.property_placeholder)
                        .into(viewHolder.gridBinding.ivHomeItem);
            }
            if (propertyItem.getPropertyPurpose().equals("Rent")) {
                viewHolder.gridBinding.tvRentSell.setText(activity.getString(R.string.rent_text));
                viewHolder.gridBinding.mcRentSellBG.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.rent_bg));
            } else {
                viewHolder.gridBinding.tvRentSell.setText(activity.getString(R.string.sell_text));
                viewHolder.gridBinding.mcRentSellBG.setCardBackgroundColor(ContextCompat.getColor(activity, R.color.sell_bg));
            }

            viewHolder.gridBinding.tvView.setText(method.Format(propertyItem.getPropertyViews()));
            viewHolder.gridBinding.tvPrice.setText(Constant.constantCurrency + method.convertDec(propertyItem.getPropertyPrice()));
            viewHolder.gridBinding.tvTitle.setText(propertyItem.getPropertyTitle());
            viewHolder.gridBinding.tvLocation.setText(propertyItem.getPropertyAddress());

            viewHolder.gridBinding.mcPropertyGrid.setOnClickListener(view -> AdInterstitialAds.ShowInterstitialAds(activity, holder.getBindingAdapterPosition(), onClick));

            if (propertyItem.getPropertyFavorite()) {
                viewHolder.gridBinding.ibFav.setImageResource(R.drawable.ic_fav_hover);
            } else {
                viewHolder.gridBinding.ibFav.setImageResource(R.drawable.ic_fav);
            }
            viewHolder.gridBinding.ibFav.setOnClickListener(view -> {
                if (method.getIsLogin()) {
                    FavouriteIF favouriteIF = (isFavourite, message) -> {
                        if (isFavourite.equals("true")) {
                            viewHolder.gridBinding.ibFav.setImageResource(R.drawable.ic_fav_hover);
                        } else {
                            viewHolder.gridBinding.ibFav.setImageResource(R.drawable.ic_fav);
                        }
                    };
                    method.addToFav(propertyItem.getPropertyId(), method.getUserId(), "Property", favouriteIF);
                } else {
                    Toast.makeText(activity, activity.getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                    Intent intentLogin = new Intent(activity, LoginActivity.class);
                    intentLogin.putExtra("isFromDetail", true);
                    activity.startActivity(intentLogin);
                }
            });
        } else if (holder.getItemViewType() == VIEW_TYPE_Ad) {
            final AdOption adOption = (AdOption) holder;
            if (Constant.isNative) {
                switch (Constant.adNetworkType) {
                    case "Admob":
                        if (isAdLoaded) {
                            if (adOption.linearLayout.getChildCount() == 0) {
                                if (mNativeAdsAdmob.size() >= 5) {
                                    int i = new Random().nextInt(mNativeAdsAdmob.size() - 1);
                                    NativeAdView adView = (NativeAdView) activity.getLayoutInflater().inflate(R.layout.layout_native_ad_admob, null);
                                    populateUnifiedNativeAdView(mNativeAdsAdmob.get(i), adView);
                                    adOption.linearLayout.removeAllViews();
                                    adOption.linearLayout.addView(adView);
                                    adOption.linearLayout.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                        break;
                    case "AppLovins MAX":
                        if (adOption.linearLayout.getChildCount() == 0) {
                            LayoutInflater inflater = LayoutInflater.from(activity);
                            FrameLayout nativeAdLayout = (FrameLayout) inflater.inflate(R.layout.activity_native_max_template, adOption.linearLayout, false);
                            MaxNativeAdLoader nativeAdLoader = new MaxNativeAdLoader(Constant.nativeId, activity);
                            nativeAdLoader.loadAd();
                            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                                @Override
                                public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, MaxAd maxAd) {
                                    super.onNativeAdLoaded(maxNativeAdView, maxAd);
                                    // Add ad view to view.
                                    nativeAdLayout.removeAllViews();
                                    nativeAdLayout.addView(maxNativeAdView);
                                    adOption.linearLayout.addView(nativeAdLayout);
                                }

                                @Override
                                public void onNativeAdLoadFailed(String s, MaxError maxError) {
                                    super.onNativeAdLoadFailed(s, maxError);
                                }

                                @Override
                                public void onNativeAdClicked(MaxAd maxAd) {
                                    super.onNativeAdClicked(maxAd);
                                }
                            });
                        }
                        break;
                    case "StartApp":
                        if (adOption.linearLayout.getChildCount() == 0) {
                            LayoutInflater inflater = LayoutInflater.from(activity);
                            CardView adView = (CardView) inflater.inflate(R.layout.native_start_item, adOption.linearLayout, false);
                            adOption.linearLayout.addView(adView);
                            ImageView icon = adView.findViewById(R.id.icon);
                            TextView title = adView.findViewById(R.id.title);
                            TextView description = adView.findViewById(R.id.description);
                            Button button = adView.findViewById(R.id.button);
                            final StartAppNativeAd nativeAd = new StartAppNativeAd(activity);

                            nativeAd.loadAd(new NativeAdPreferences()
                                    .setAdsNumber(1)
                                    .setAutoBitmapDownload(true)
                                    .setPrimaryImageSize(1), new AdEventListener() {
                                @Override
                                public void onReceiveAd(@NonNull com.startapp.sdk.adsbase.Ad ad) {
                                    ArrayList<NativeAdDetails> ads = nativeAd.getNativeAds();    // get NativeAds list
                                    NativeAdDetails nativeAdDetails = ads.get(0);
                                    if (nativeAdDetails != null) {
                                        icon.setImageBitmap(nativeAdDetails.getImageBitmap());
                                        title.setText(nativeAdDetails.getTitle());
                                        description.setText(nativeAdDetails.getDescription());
                                        button.setText(nativeAdDetails.isApp() ? "Install" : "Open");
                                        nativeAdDetails.registerViewForInteraction(adView);
                                    }
                                }

                                @Override
                                public void onFailedToReceiveAd(@Nullable com.startapp.sdk.adsbase.Ad ad) {
                                    if (BuildConfig.DEBUG) {
                                        Log.e("onFailedToReceiveAd: ", "" + ad.getErrorMessage());
                                    }
                                }
                            });
                        }
                        break;
                    case "Facebook":
                        if (adOption.linearLayout.getChildCount() == 0) {
                            LayoutInflater inflater = LayoutInflater.from(activity);
                            LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, adOption.linearLayout, false);
                            adOption.linearLayout.addView(adView);
                            final LinearLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
                            final TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                            final MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                            final TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                            final TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                            final TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                            final Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);
                            final NativeAd nativeAd = new NativeAd(activity, Constant.nativeId);
                            NativeAdListener nativeAdListener = new NativeAdListener() {
                                @Override
                                public void onMediaDownloaded(Ad ad) {
                                    Log.d("status_data", "MediaDownloaded" + " " + ad.toString());
                                }

                                @Override
                                public void onError(Ad ad, AdError adError) {
                                    Toast.makeText(activity, adError.toString(), Toast.LENGTH_SHORT).show();
                                    Log.d("status_data", "error" + " " + adError.toString());
                                }

                                @Override
                                public void onAdLoaded(Ad ad) {
                                    // Race condition, load() called again before last ad was displayed
                                    if (nativeAd == null || nativeAd != ad) {
                                        return;
                                    }
                                    Log.d("status_data", "on load" + " " + ad.toString());
                                    NativeAdLayout nativeAdLayout = new NativeAdLayout(activity);
                                    AdOptionsView adOptionsView = new AdOptionsView(activity, nativeAd, nativeAdLayout);
                                    adChoicesContainer.removeAllViews();
                                    adChoicesContainer.addView(adOptionsView, 0);
                                    nativeAdTitle.setText(nativeAd.getAdvertiserName());
                                    nativeAdBody.setText(nativeAd.getAdBodyText());
                                    nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                                    nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                                    nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                                    sponsoredLabel.setText(nativeAd.getSponsoredTranslation());
                                    List<View> clickableViews = new ArrayList<>();
                                    clickableViews.add(nativeAdTitle);
                                    clickableViews.add(nativeAdCallToAction);
                                    nativeAd.registerViewForInteraction(
                                            adOption.linearLayout,
                                            nativeAdMedia,
                                            clickableViews);

                                }

                                @Override
                                public void onAdClicked(Ad ad) {
                                    Log.d("status_data", "AdClicked" + " " + ad.toString());
                                }

                                @Override
                                public void onLoggingImpression(Ad ad) {
                                    Log.d("status_data", "Impression" + " " + ad.toString());
                                }
                            };
                            nativeAd.loadAd(nativeAd.buildLoadAdConfig().withAdListener(nativeAdListener).build());
                        }
                        break;
                    case "Wortise":
                        if (adOption.linearLayout.getChildCount() == 0) {
                            GoogleNativeAd googleNativeAd = new GoogleNativeAd(
                                    activity, Constant.nativeId, new GoogleNativeAd.Listener() {
                                @Override
                                public void onNativeLoaded(@NonNull GoogleNativeAd googleNativeAd, @NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                                    NativeAdView adView = (NativeAdView) ((Activity) activity).getLayoutInflater().inflate(R.layout.layout_native_ad_wortise, null);
                                    populateUnifiedNativeAdView(nativeAd, adView);
                                    adOption.linearLayout.removeAllViews();
                                    adOption.linearLayout.addView(adView);
                                    adOption.linearLayout.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onNativeFailed(@NonNull GoogleNativeAd googleNativeAd, @NonNull com.wortise.ads.AdError adError) {
                                }

                                @Override
                                public void onNativeClicked(@NonNull GoogleNativeAd googleNativeAd) {
                                }

                                @Override
                                public void onNativeImpression(@NonNull GoogleNativeAd googleNativeAd) {

                                }
                            });
                            googleNativeAd.load();
                        }
                        break;
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return (null != propertyList ? propertyList.size() + 1 : 0);
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == propertyList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return VIEW_TYPE_LOADING;
        } else if (propertyList.get(position) == null) {
            return VIEW_TYPE_Ad;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RowPropertyGridBinding gridBinding;

        public ViewHolder(RowPropertyGridBinding gridBinding) {
            super(gridBinding.getRoot());
            this.gridBinding = gridBinding;
        }
    }

    public void setOnItemClickListener(OnClick clickListener) {
        this.onClick = clickListener;
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        static ProgressBar progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }

    public static class AdOption extends RecyclerView.ViewHolder {

        RelativeLayout linearLayout;

        public AdOption(View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.rl_native_ad);
        }
    }

    private void loadNativeAds() {
        if (Constant.isNative) {
            if (Constant.adNetworkType.equals("Admob")) {

                AdLoader.Builder builder = new AdLoader.Builder(activity, Constant.nativeId);
                adLoader = builder.forNativeAd(new com.google.android.gms.ads.nativead.NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(@NonNull com.google.android.gms.ads.nativead.NativeAd nativeAd) {
                        mNativeAdsAdmob.add(nativeAd);
                        isAdLoaded = true;
                        notifyDataSetChanged();
                    }
                }).build();

                // Load the Native Express ad.
                adLoader.loadAds(new AdRequest.Builder().build(), 5);

            }
        }
    }

    private void populateUnifiedNativeAdView(com.google.android.gms.ads.nativead.NativeAd nativeAd, NativeAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) Objects.requireNonNull(adView.getHeadlineView())).setText(nativeAd.getHeadline());
        if (nativeAd.getBody() == null) {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getBodyView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getCallToActionView()).setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            Objects.requireNonNull(adView.getIconView()).setVisibility(View.GONE);
        } else {
            ((ImageView) Objects.requireNonNull(adView.getIconView())).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getPriceView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.INVISIBLE);
        } else {
            Objects.requireNonNull(adView.getStoreView()).setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            Objects.requireNonNull(adView.getStarRatingView()).setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) Objects.requireNonNull(adView.getStarRatingView()))
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            Objects.requireNonNull(adView.getAdvertiserView()).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) Objects.requireNonNull(adView.getAdvertiserView())).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }
        adView.setNativeAd(nativeAd);
    }
}
