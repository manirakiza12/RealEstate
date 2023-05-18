package com.elite.realestate;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elite.response.RegisterRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityForgotPasswordBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForgotPassActivity extends AppCompatActivity {

    ActivityForgotPasswordBinding bindingForgotPass;
    ProgressDialog progressDialog;
    Method method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLoginScreen(ForgotPassActivity.this);

        bindingForgotPass = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(bindingForgotPass.getRoot());

        method = new Method(ForgotPassActivity.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(ForgotPassActivity.this, R.style.MyAlertDialogStyle);
        bindingForgotPass.toolbarMain.tvName.setText(getString(R.string.lbl_forgot_password));

        bindingForgotPass.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        bindingForgotPass.btnSendEmail.setOnClickListener(v -> {
            String string = bindingForgotPass.etEmail.getText().toString();
            bindingForgotPass.etEmail.setError(null);

            if (!isValidMail(string) || string.isEmpty()) {
                bindingForgotPass.etEmail.requestFocus();
                bindingForgotPass.etEmail.setError(getResources().getString(R.string.please_enter_email));
            } else {

                bindingForgotPass.etEmail.clearFocus();

                if (method.isNetworkAvailable()) {
                    forgetPassword(string);
                } else {
                    method.alertBox(getResources().getString(R.string.no_internet_msg));
                }
            }
        });

    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void forgetPassword(String email) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(ForgotPassActivity.this));
        jsObj.addProperty("email", email);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterRP> call = apiService.getForgotData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RegisterRP>() {
            @Override
            public void onResponse(@NotNull Call<RegisterRP> call, @NotNull Response<RegisterRP> response) {

                try {
                    RegisterRP dataRP = response.body();
                    if (dataRP != null) {
                        if (dataRP.getSuccess().equals("1")) {
                            bindingForgotPass.etEmail.setText("");
                            Toast.makeText(ForgotPassActivity.this, dataRP.getMsg(), Toast.LENGTH_SHORT).show();

                        } else {
                            method.alertBox(dataRP.getMsg());
                        }
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.no_error_msg));
                }

                progressDialog.dismiss();

            }

            @Override
            public void onFailure(@NotNull Call<RegisterRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.no_error_msg));
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