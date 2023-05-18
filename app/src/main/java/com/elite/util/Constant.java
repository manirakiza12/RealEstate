package com.elite.util;


import com.elite.item.AdsInfo;
import com.elite.item.Gallery;
import com.elite.response.AppDetailRP;

import java.util.List;

public class Constant {

    public static String constantCurrency;
    public static String webViewText = "#3F3D56B2;";
    public static String webViewTextDark = "#FFFFFF;";

    public static String webViewTextAuthor = "#4d506ccc;";
    public static String webViewTextDarkAuthor = "#FFFFFF;";

    public static String webViewLink = "#99414141;";
    public static String webViewLinkDark = "#FFFFFF;";

    public static String webViewTextAbout = "#65637BE5;";
    public static String webViewTextAboutDark = "#FFFFFF;";

    public static List<Gallery> listGallery;

    public static String USER_LATITUDE;
    public static String USER_LONGITUDE;

    public static int AD_COUNT = 0;

    public static AppDetailRP.RealEstate appListData;

    public static boolean isNative= false;
    public static boolean isBanner= false;
    public static boolean isInterstitial= false;

    public static AdsInfo adsInfo;

    public static int interstitialClick,nativePosition;
    public static String bannerId,interstitialId,nativeId,publisherId,
            adNetworkType,minPropertyPrice,maxPropertyPrice;

    public static boolean isAppUpdate = false, isAppUpdateCancel = false;
    public static int appUpdateVersion;
    public static String  appUpdateUrl, appUpdateDesc;
}
