package com.elite.realestate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.elite.response.RegisterRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Constant;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityRegisterBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding bindingRegister;
    ProgressDialog progressDialog;
    Method method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLoginScreen(RegisterActivity.this);

        bindingRegister = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(bindingRegister.getRoot());

        method = new Method(RegisterActivity.this);
        method.forceRTLIfSupported();

        progressDialog = new ProgressDialog(RegisterActivity.this, R.style.MyAlertDialogStyle);

        bindingRegister.tvPrivacy.setOnClickListener(v -> {
            Intent intentPage = new Intent(RegisterActivity.this, PagesActivity.class);
            intentPage.putExtra("PAGE_TITLE", Constant.appListData.getPageList().get(2).getPageTitle());
            intentPage.putExtra("PAGE_DESC", Constant.appListData.getPageList().get(2).getPageContent());
            startActivity(intentPage);
        });

        bindingRegister.tvLogin.setOnClickListener(v -> {
            Intent intent_register = new Intent(RegisterActivity.this, LoginActivity.class);
            intent_register.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent_register);
        });

        bindingRegister.cbPrivacy.setOnCheckedChangeListener((checkBox, isChecked) -> {

        });

        bindingRegister.mbRegister.setOnClickListener(v -> form());

    }

    public void form() {

        String name = bindingRegister.etName.getText().toString();
        String email = bindingRegister.etEmail.getText().toString();
        String password = bindingRegister.etPassword.getText().toString();
        String phoneNo = bindingRegister.etPhone.getText().toString();

        bindingRegister.etName.setError(null);
        bindingRegister.etEmail.setError(null);
        bindingRegister.etPassword.setError(null);
        bindingRegister.etPhone.setError(null);

        if (name.equals("") || name.isEmpty()) {
            bindingRegister.etName.requestFocus();
            bindingRegister.etName.setError(getResources().getString(R.string.please_enter_name));
        } else if (!isValidMail(email) || email.isEmpty()) {
            bindingRegister.etEmail.requestFocus();
            bindingRegister.etEmail.setError(getResources().getString(R.string.please_enter_email));
        } else if (password.equals("") || password.isEmpty()) {
            bindingRegister.etPassword.requestFocus();
            bindingRegister.etPassword.setError(getResources().getString(R.string.please_enter_password));
        } else {
            bindingRegister.etName.clearFocus();
            bindingRegister.etEmail.clearFocus();
            bindingRegister.etPassword.clearFocus();

            if (bindingRegister.cbPrivacy.isChecked()) {
                if (method.isNetworkAvailable()) {
                    register(name, email, password, phoneNo);
                } else {
                    method.alertBox(getResources().getString(R.string.no_internet_msg));
                }
            } else {
                Toast.makeText(RegisterActivity.this, getString(R.string.please_accept), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private boolean isValidMail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void register(String sendName, String sendEmail, String sendPassword, String sendPhone) {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(RegisterActivity.this));
        jsObj.addProperty("name", sendName);
        jsObj.addProperty("email", sendEmail);
        jsObj.addProperty("password", sendPassword);
        jsObj.addProperty("phone", sendPhone);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<RegisterRP> call = apiService.getRegisterData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<RegisterRP>() {
            @Override
            public void onResponse(@NotNull Call<RegisterRP> call, @NotNull Response<RegisterRP> response) {

                try {
                    RegisterRP registerRP = response.body();
                    if (registerRP != null) {
                        if (registerRP.getSuccess().equals("1")) {
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finishAffinity();
                            Toast.makeText(RegisterActivity.this, registerRP.getMsg(), Toast.LENGTH_SHORT).show();
                        } else {
                            method.alertBox(registerRP.getMsg());
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
}