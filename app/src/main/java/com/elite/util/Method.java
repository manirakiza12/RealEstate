package com.elite.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.elite.realestate.EditProfileActivity;
import com.elite.realestate.FavoriteActivity;
import com.elite.realestate.LoginActivity;
import com.elite.realestate.MySubscriptionActivity;
import com.example.realestate.R;
import com.elite.response.FavoriteRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Method {

    public Activity activity;
    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    private final String myPreference = "login";
    public String IS_WELCOME = "is_welcome";
    String filename;
    public static boolean isDownload = true;
    public String themSetting = "them";
    public String notification = "notification";
    public PopupWindow popupWindowProfile;

    @SuppressLint("CommitPrefEdits")
    public Method(Activity activity) {
        this.activity = activity;
        pref = activity.getSharedPreferences(myPreference, 0); // 0 - for private mode
        editor = pref.edit();
    }

    public boolean isWelcome() {
        return pref.getBoolean(IS_WELCOME, true);
    }

    public void setFirstWelcome(boolean isFirstTime) {
        editor.putBoolean(IS_WELCOME, isFirstTime);
        editor.commit();
    }

    //network check
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // view format
    public static String Format(Integer number) {
        String[] suffix = new String[]{"k", "m", "b", "t"};
        int size = (number != 0) ? (int) Math.log10(number) : 0;
        if (size >= 3) {
            while (size % 3 != 0) {
                size = size - 1;
            }
        }
        double notation = Math.pow(10, size);
        String result = (size >= 3) ? +(Math.round((number / notation) * 100) / 100.0d) + suffix[(size / 3) - 1] : +number + "";
        return result;
    }

    public static String convertDec(double price) {
        String value;
        DecimalFormat dec = new DecimalFormat("#,##,###");
        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        dec.setDecimalFormatSymbols(decimalFormatSymbols);
        value = dec.format(price);
        return value;
    }

    //alert message box
    public void alertBox(String message) {
        try {
            if (activity != null) {
                if (!activity.isFinishing()) {
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity, R.style.DialogTitleTextStyle);
                    builder.setMessage(Html.fromHtml(message));
                    builder.setCancelable(false);
                    builder.setPositiveButton(activity.getResources().getString(R.string.ok),
                            (arg0, arg1) -> {
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        } catch (Exception e) {
            Log.d("error_message", e.toString());
        }

    }

    public void saveType(String type) {
        pref = activity.getSharedPreferences(myPreference, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("type", type);
        editor.commit();
    }

    public void setUserId(String userId) {
        pref = activity.getSharedPreferences(myPreference, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_id", userId);
        editor.apply();
    }

    public String getUserType() {
        pref = activity.getSharedPreferences(myPreference, 0);
        return pref.getString("type", "");
    }


    public boolean getFirstIsLogin() {
        pref = activity.getSharedPreferences(myPreference, 0);
        return pref.getBoolean("IsLoggedInFirst", false);
    }

    public void saveFirstIsLogin(boolean flag) {
        pref = activity.getSharedPreferences(myPreference, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("IsLoggedInFirst", flag);
        editor.apply();
    }


    public void saveIsLogin(boolean flag) {
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
        editor.putBoolean("IsLoggedIn", flag);
        editor.apply();
    }

    public boolean getIsLogin() {
        pref = activity.getSharedPreferences(myPreference, 0);
        return pref.getBoolean("IsLoggedIn", false);
    }

    public void saveLogin(String userId, String userName, String userEmail, String userType, String userAId,String userImage,String phone) {
        pref = activity.getSharedPreferences(myPreference, 0);
        editor = pref.edit();
        editor.putString("user_id", userId);
        editor.putString("user_name", userName);
        editor.putString("email", userEmail);
        editor.putString("type", userType);
        editor.putString("aid", userAId);
        editor.putString("user_image", userImage);
        editor.putString("user_phone", phone);
        editor.apply();
    }


    public String getUserPhone() {
        pref = activity.getSharedPreferences(myPreference, 0);
        return pref.getString("user_phone", "");
    }

    public String getUserId() {
        pref = activity.getSharedPreferences(myPreference, 0);
        return pref.getString("user_id", "");
    }

    public String getUserName() {
        pref = activity.getSharedPreferences(myPreference, 0);
        return pref.getString("user_name", "");
    }

    public String getUserEmail() {
        pref = activity.getSharedPreferences(myPreference, 0);
        return pref.getString("email", "");
    }

    public String getUserImage() {
        pref = activity.getSharedPreferences(myPreference, 0);
        return pref.getString("user_image", "");
    }

    //rtl
    public void forceRTLIfSupported() {
        if (activity.getResources().getString(R.string.isRTL).equals("true")) {
            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
    }

    public String webViewText() {
        String color;
        if (isDarkMode()) {
            color = Constant.webViewTextDark;
        } else {
            color = Constant.webViewText;
        }
        return color;
    }

    public String webViewTextAuthor() {
        String color;
        if (isDarkMode()) {
            color = Constant.webViewTextDarkAuthor;
        } else {
            color = Constant.webViewTextAuthor;
        }
        return color;
    }

    public String webViewAboutText() {
        String color;
        if (isDarkMode()) {
            color = Constant.webViewTextAboutDark;
        } else {
            color = Constant.webViewTextAbout;
        }
        return color;
    }


    public String webViewLink() {
        String color;
        if (isDarkMode()) {
            color = Constant.webViewLinkDark;
        } else {
            color = Constant.webViewLink;
        }
        return color;
    }

    public String isWebViewTextRtl() {
        String isRtl;
        if (isRtl()) {
            isRtl = "rtl";
        } else {
            isRtl = "ltr";
        }
        return isRtl;
    }

    //rtl
    public boolean isRtl() {
        return activity.getResources().getString(R.string.isRTL).equals("true");
    }

    public String themMode() {
        return pref.getString(themSetting, "system");
    }

    //check dark mode or not
    public boolean isDarkMode() {
        int currentNightMode = activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                return false;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                return true;
            default:
                return false;
        }
    }


    //get screen width
    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        final Point point = new Point();
        point.x = display.getWidth();
        point.y = display.getHeight();
        columnWidth = point.x;
        return columnWidth;
    }

    public void addToFav(String id, String userId, String type, FavouriteIF favouriteIF) {

        ProgressDialog progressDialog = new ProgressDialog(activity, R.style.MyAlertDialogStyle);

        progressDialog.show();
        progressDialog.setMessage(activity.getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(activity));
        jsObj.addProperty("post_id", id);
        jsObj.addProperty("user_id", userId);
        jsObj.addProperty("post_type", type);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<FavoriteRP> call = apiService.getFavUnFavData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<FavoriteRP>() {
            @Override
            public void onResponse(@NotNull Call<FavoriteRP> call, @NotNull Response<FavoriteRP> response) {

                try {
                    FavoriteRP favoriteRP = response.body();

                    if (favoriteRP.getSuccess().equals("true")) {
                        favouriteIF.isFavourite(favoriteRP.getSuccess(), favoriteRP.getMsg());
                    } else {
                        favouriteIF.isFavourite("", favoriteRP.getMsg());
                    }
                    Events.FavProperty favProperty = new Events.FavProperty();
                    favProperty.setPropertyId(id);
                    favProperty.setIsFav(favoriteRP.getSuccess());
                    GlobalBus.getBus().post(favProperty);
                    Toast.makeText(activity, favoriteRP.getMsg(), Toast.LENGTH_SHORT).show();


                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    alertBox(activity.getResources().getString(R.string.no_error_msg));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<FavoriteRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                alertBox(activity.getResources().getString(R.string.no_error_msg));
            }
        });

    }

    public void profilePopUpWindow() {
        LayoutInflater inflater = (LayoutInflater)
                activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_profile_popup, null);
        TextView tvEditProfile=view.findViewById(R.id.tvEditProfile);
        TextView tvFavProperty=view.findViewById(R.id.tvFavProperty);
        TextView tvLogout=view.findViewById(R.id.tvLogout);
        TextView tvMySubs=view.findViewById(R.id.tvMySubs);

        tvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentEdit=new Intent(activity, EditProfileActivity.class);
                activity.startActivity(intentEdit);
                popupWindowProfile.dismiss();
            }
        });

        tvFavProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentFav=new Intent(activity, FavoriteActivity.class);
                activity.startActivity(intentFav);
                popupWindowProfile.dismiss();
            }
        });

        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logoutDialog();
                popupWindowProfile.dismiss();
            }
        });

        tvMySubs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSub=new Intent(activity, MySubscriptionActivity.class);
                activity.startActivity(intentSub);
                popupWindowProfile.dismiss();
            }
        });

        popupWindowProfile = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
    }
    public void logoutDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(activity);
        dialog.setContentView(R.layout.layout_logout);
        if (Method.this.isRtl()) {
            dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }
        MaterialButton buttonCancel = dialog.findViewById(R.id.mbCancel);
        MaterialButton buttonYes = dialog.findViewById(R.id.mbYes);

        buttonCancel.setOnClickListener(v -> dialog.dismiss());

        buttonYes.setOnClickListener(v -> {
            if (Method.this.getUserType().equals("google")) {

                // Configure sign-in to request the ic_user_login's ID, email address, and basic
                // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
                GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

                // Build a GoogleSignInClient with the options specified by gso.
                //Google login
                GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

                mGoogleSignInClient.signOut()
                        .addOnCompleteListener(activity, task -> {
                            Method.this.saveIsLogin(false);
                            Method.this.setUserId("");
                            activity.startActivity(new Intent(activity, LoginActivity.class));
                            activity.finishAffinity();
                        });
            } else if (Method.this.getUserType().equals("facebook")) {
                LoginManager.getInstance().logOut();
                Method.this.saveIsLogin(false);
                Method.this.setUserId("");
                activity.startActivity(new Intent(activity, LoginActivity.class));
                activity.finishAffinity();
            } else {
                Method.this.saveIsLogin(false);
                Method.this.setUserId("");
                activity.startActivity(new Intent(activity, LoginActivity.class));
                activity.finishAffinity();
            }
            dialog.dismiss();
        });

        dialog.show();
    }

}
