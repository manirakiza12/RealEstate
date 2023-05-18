package com.elite.realestate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.elite.response.LoginRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Constant;
import com.elite.util.Events;
import com.elite.util.GlobalBus;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityLoginBinding;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding bindingLogin;
    ProgressDialog progressDialog;
    Method method;
    public static final String mypreference = "mypref";
    public static final String pref_email = "pref_email";
    public static final String pref_password = "pref_password";
    public static final String pref_check = "pref_check";
    private static SharedPreferences pref;
    private static SharedPreferences.Editor editor;
    GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 007;

    //Facebook login
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    boolean isWhichScreen;
    LoginRP.ItemUser itemUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLoginScreen(LoginActivity.this);

        bindingLogin = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(bindingLogin.getRoot());

        method = new Method(LoginActivity.this);
        method.forceRTLIfSupported();

        pref = getSharedPreferences(mypreference, 0); // 0 - for private mode
        editor = pref.edit();
        method = new Method(LoginActivity.this);
        method.saveFirstIsLogin(true);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.MyAlertDialogStyle);

        Intent intent = getIntent();
        isWhichScreen = intent.getBooleanExtra("isFromDetail", false);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //facebook button
        callbackManager = CallbackManager.Factory.create();

        bindingLogin.cbRememberMe.setChecked(false);
        if (pref.getBoolean(pref_check, false)) {
            bindingLogin.etEmail.setText(pref.getString(pref_email, null));
            bindingLogin.etPassword.setText(pref.getString(pref_password, null));
            bindingLogin.cbRememberMe.setChecked(true);
        } else {
            bindingLogin.etEmail.setText("");
            bindingLogin.etPassword.setText("");
            bindingLogin.cbRememberMe.setChecked(false);
        }

        bindingLogin.tvPrivacy.setOnClickListener(v -> {
            Intent intentPage = new Intent(LoginActivity.this, PagesActivity.class);
            intentPage.putExtra("PAGE_TITLE", Constant.appListData.getPageList().get(2).getPageTitle());
            intentPage.putExtra("PAGE_DESC", Constant.appListData.getPageList().get(2).getPageContent());
            startActivity(intentPage);
        });

        bindingLogin.btnSkip.setOnClickListener(view -> {
            Intent intent_skip = new Intent(LoginActivity.this, MainActivity.class);
            intent_skip.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_skip);
            finishAffinity();
        });

        bindingLogin.tvForgotPassword.setOnClickListener(v -> {
            Intent intent_forgot = new Intent(LoginActivity.this, ForgotPassActivity.class);
            intent_forgot.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_forgot);
        });

        bindingLogin.tvSignUp.setOnClickListener(v -> {
            Intent intent_register = new Intent(LoginActivity.this, RegisterActivity.class);
            intent_register.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_register);
        });

        bindingLogin.cbRememberMe.setOnCheckedChangeListener((checkBox, isChecked) -> {

        });

        bindingLogin.mbLogin.setOnClickListener(v -> {
            String strEmail = bindingLogin.etEmail.getText().toString();
            String strPassword = bindingLogin.etPassword.getText().toString();

            bindingLogin.etEmail.setError(null);
            bindingLogin.etPassword.setError(null);

            if (!isValidMail(strEmail) || strEmail.isEmpty()) {
                bindingLogin.etEmail.requestFocus();
                bindingLogin.etEmail.setError(getResources().getString(R.string.please_enter_email));
            } else if (strPassword.isEmpty()) {
                bindingLogin.etPassword.requestFocus();
                bindingLogin.etPassword.setError(getResources().getString(R.string.please_enter_password));
            } else {
                if (bindingLogin.cbPrivacy.isChecked()) {

                    if (bindingLogin.cbRememberMe.isChecked()) {
                        editor.putString(pref_email, bindingLogin.etEmail.getText().toString());
                        editor.putString(pref_password, bindingLogin.etPassword.getText().toString());
                        editor.putBoolean(pref_check, true);
                        editor.commit();
                    } else {
                        editor.putBoolean(pref_check, false);
                        editor.commit();
                    }

                    if (method.isNetworkAvailable()) {
                        login(strEmail, strPassword);
                    } else {
                        method.alertBox(getResources().getString(R.string.no_internet_msg));
                    }
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.please_accept), Toast.LENGTH_SHORT).show();
                }
            }
        });

        bindingLogin.cbPrivacy.setOnCheckedChangeListener((checkBox, isChecked) -> {

        });

        bindingLogin.llFacebook.setOnClickListener(v -> {
            if (bindingLogin.cbPrivacy.isChecked()) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList(EMAIL, "public_profile"));
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.please_accept), Toast.LENGTH_SHORT).show();
            }
        });

        bindingLogin.llGoogle.setOnClickListener(v -> {
            if (bindingLogin.cbPrivacy.isChecked()) {
                signIn();
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.please_accept), Toast.LENGTH_SHORT).show();
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                fbUser(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void login(final String sendEmail, final String sendPassword) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(LoginActivity.this));
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginRP> call = apiService.getLoginData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<LoginRP>() {
            @Override
            public void onResponse(@NotNull Call<LoginRP> call, @NotNull Response<LoginRP> response) {

                try {
                    LoginRP loginRP = response.body();
                    if (loginRP != null) {
                        if (loginRP.getSuccess().equals("1")) {
                            itemUser = loginRP.getItemUserList();
                            method.saveIsLogin(true);
                            method.saveLogin(itemUser.getUser_id(), itemUser.getName(), itemUser.getEmail(), "normal", "", itemUser.getUser_image(),itemUser.getPhone());
                            Events.ProfileUpdate profileUpdate = new Events.ProfileUpdate();
                            GlobalBus.getBus().post(profileUpdate);
                            if (isWhichScreen) {
                                finish();
                            } else {
                                ActivityCompat.finishAffinity(LoginActivity.this);
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            Toast.makeText(LoginActivity.this, loginRP.getMsg(), Toast.LENGTH_SHORT).show();

                        } else {
                            method.alertBox(loginRP.getMsg());
                        }
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.no_error_msg));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<LoginRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.no_error_msg));
            }
        });
    }

    public void registerSocialNetwork(String aid, String sendName, String sendEmail, String type) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(LoginActivity.this));
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("social_id", aid);
        jsObj.addProperty("login_type", type);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoginRP> call = apiService.getSocialLoginData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<LoginRP>() {
            @Override
            public void onResponse(@NotNull Call<LoginRP> call, @NotNull Response<LoginRP> response) {

                try {
                    LoginRP loginRPSocial = response.body();
                    if (loginRPSocial != null) {
                        if (loginRPSocial.getSuccess().equals("1")) {
                            itemUser = loginRPSocial.getItemUserList();
                            method.saveIsLogin(true);
                            method.saveLogin(itemUser.getUser_id(), itemUser.getName(), itemUser.getEmail(), type, aid, itemUser.getUser_image(),itemUser.getPhone());
                            Events.ProfileUpdate profileUpdate = new Events.ProfileUpdate();
                            GlobalBus.getBus().post(profileUpdate);
                            if (isWhichScreen) {
                                finish();
                            } else {
                                ActivityCompat.finishAffinity(LoginActivity.this);
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            Toast.makeText(LoginActivity.this, loginRPSocial.getMsg(), Toast.LENGTH_SHORT).show();
                        } else {
                            method.alertBox(loginRPSocial.getMsg());
                        }
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.no_error_msg));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<LoginRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.no_error_msg));
            }
        });
    }


    //Google login
    private void signIn() {
        if (method.isNetworkAvailable()) {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else {
            method.alertBox(getResources().getString(R.string.no_internet_msg));
        }

    }

    //Google login get callback
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    //Google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.

            assert account != null;
            String id = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();

            registerSocialNetwork(id, name, email, "google");

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
        }
    }

    //facebook login get email and name
    private void fbUser(LoginResult loginResult) {
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String id = object.getString("id");
                    String name = object.getString("name");
                    String email = object.getString("email");
                    registerSocialNetwork(id, name, email, "facebook");
                } catch (JSONException e) {
                    try {
                        String id = object.getString("id");
                        String name = object.getString("name");
                        registerSocialNetwork(id, name, "", "facebook");
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email"); // Parameters that we ask for facebook
        graphRequest.setParameters(parameters);
        graphRequest.executeAsync();
    }
}