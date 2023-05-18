package com.elite.realestate;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.elite.response.EditProfileRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Events;
import com.elite.util.GlobalBus;
import com.elite.util.Method;
import com.elite.util.Picker;
import com.elite.util.PickerListener;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityEditProfileBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding viewEditProfile;
    Method method;
    ProgressDialog progressDialog;
    Picker picker = new Picker(this);
    File fileProfile;
    boolean isProfile = false;
    String pass, name, phoneNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initHomeScreen(EditProfileActivity.this);
        viewEditProfile = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(viewEditProfile.getRoot());

        method = new Method(EditProfileActivity.this);
        method.forceRTLIfSupported();
        progressDialog = new ProgressDialog(EditProfileActivity.this, R.style.MyAlertDialogStyle);

        viewEditProfile.tvName.setText(getString(R.string.edit_profile));
        viewEditProfile.fabBack.setOnClickListener(v -> onBackPressed());

        Glide.with(EditProfileActivity.this.getApplicationContext()).load(method.getUserImage())
                .placeholder(R.drawable.user_profile)
                .into(viewEditProfile.ivUser);
        viewEditProfile.tvProfileUserName.setText(method.getUserName());
        viewEditProfile.tvProfileEmail.setText(method.getUserEmail());
        viewEditProfile.etName.setText(method.getUserName());
        viewEditProfile.etEmail.setText(method.getUserEmail());
        viewEditProfile.etPhone.setText(method.getUserPhone());

        viewEditProfile.ivEditIcon.setOnClickListener(view -> picker.pickGallery(new PickerListener() {
            @Override
            public void onPicked(Uri uri, File file, Bitmap bitmap) {
                isProfile = true;
                fileProfile = file;
                Glide.with(EditProfileActivity.this.getApplicationContext()).load(fileProfile)
                        .into(viewEditProfile.ivUser);
            }
        }));

        viewEditProfile.mbUpdate.setOnClickListener(view -> {
            name = viewEditProfile.etName.getText().toString();
            phoneNo = viewEditProfile.etPhone.getText().toString();

            viewEditProfile.etName.setError(null);
            viewEditProfile.etPhone.setError(null);

            if (name.equals("") || name.isEmpty()) {
                viewEditProfile.etName.requestFocus();
                viewEditProfile.etName.setError(getResources().getString(R.string.please_enter_name));
            } else if (phoneNo.equals("") || phoneNo.isEmpty()) {
                viewEditProfile.etPhone.requestFocus();
                viewEditProfile.etPhone.setError(getResources().getString(R.string.please_enter_phone));
            } else {
                if (method.isNetworkAvailable()) {

                    viewEditProfile.etName.clearFocus();
                    viewEditProfile.etPhone.clearFocus();
                    pass = viewEditProfile.etPassword.getText().toString();
                    if (pass.isEmpty()) {
                        pass = "";
                    }
                    profileUpdate();
                } else {
                    method.alertBox(getResources().getString(R.string.no_internet_msg));
                }
            }
        });

    }

    public void profileUpdate() {

        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        MultipartBody.Part body = null;

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(EditProfileActivity.this));
        jsObj.addProperty("user_id", method.getUserId());
        jsObj.addProperty("name", name);
        jsObj.addProperty("email", viewEditProfile.etEmail.getText().toString());
        jsObj.addProperty("password", pass);
        jsObj.addProperty("phone", phoneNo);
        MediaType mediaType=MediaType.parse("multipart/form-data");
        if (isProfile) {
            body = MultipartBody.Part.createFormData("user_image", fileProfile.getName(), RequestBody.create(fileProfile,mediaType));
        }
        RequestBody requestBody_data =
                RequestBody.create(API.toBase64(jsObj.toString()),mediaType);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<EditProfileRP> call = apiService.getEditProfileData(requestBody_data, body);
        call.enqueue(new Callback<EditProfileRP>() {
            @Override
            public void onResponse(@NotNull Call<EditProfileRP> call, @NotNull Response<EditProfileRP> response) {

                try {
                    EditProfileRP editProfileRP = response.body();
                    if (editProfileRP != null) {
                        if (editProfileRP.getSuccess().equals("1")) {
                            method.saveLogin(method.getUserId(), name, viewEditProfile.etEmail.getText().toString(), "normal", "", editProfileRP.getItemUserEdits().getUser_image(), phoneNo);
                            Events.ProfileUpdate profileUpdate = new Events.ProfileUpdate();
                            GlobalBus.getBus().post(profileUpdate);
                            Toast.makeText(EditProfileActivity.this, editProfileRP.getMsg(), Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        } else {
                            method.alertBox(editProfileRP.getMsg());
                        }
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.no_error_msg));
                }


                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<EditProfileRP> call, @NotNull Throwable t) {
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
