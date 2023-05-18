package com.elite.realestate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elite.adapter.SpinCatAdapter;
import com.elite.adapter.UploadGalleryAdapter;
import com.elite.item.EditProperty;
import com.elite.item.UploadImage;
import com.elite.response.CategoryRP;
import com.elite.response.EditPropertyRP;
import com.elite.response.UploadPropertyRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Events;
import com.elite.util.GlobalBus;
import com.elite.util.Method;
import com.elite.util.NothingSelectedSpinnerAdapter;
import com.elite.util.Picker;
import com.elite.util.PickerListener;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityUploadPropertyBinding;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UploadActivity extends AppCompatActivity {

    ActivityUploadPropertyBinding bindingUpload;
    Method method;
    SpinCatAdapter spinCatAdapter;
    ArrayAdapter<String> adapterBuySell, adapterFurnished, adapterVerified;
    CategoryRP categoryRP;
    String[] strBuySell, strFurnished, strVerified;
    File fileFeature, fileFloor, fileGallery;
    Picker picker = new Picker(this);
    boolean isFeature, isFloor, isGallery = false;
    UploadGalleryAdapter uploadGalleryAdapter;
    ArrayList<UploadImage> list;
    String strTitle, strDesc, strPhone, strAddress, strLat, strLong, strBed, strBath, strArea, strPrice, strAmenity;
    ProgressDialog progressDialog;
    boolean isEdit = false;
    EditProperty editProperty;
    String strEditPostId, str_TypeId, str_Purpose, str_Verified, str_Furnished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(UploadActivity.this);
        bindingUpload = ActivityUploadPropertyBinding.inflate(getLayoutInflater());
        setContentView(bindingUpload.getRoot());

        method = new Method(UploadActivity.this);
        method.forceRTLIfSupported();
        list = new ArrayList<>();
        uploadGalleryAdapter = new UploadGalleryAdapter(UploadActivity.this, list);
        bindingUpload.rvUploadGallery.setAdapter(uploadGalleryAdapter);

        progressDialog = new ProgressDialog(UploadActivity.this, R.style.MyAlertDialogStyle);

        Intent intent = getIntent();
        strEditPostId = intent.getStringExtra("ID");
        isEdit = intent.getBooleanExtra("isEdit", false);

        strBuySell = getResources().getStringArray(R.array.buy_sell_array);
        strFurnished = getResources().getStringArray(R.array.furnished_array);
        strVerified = getResources().getStringArray(R.array.verified_array);

        if (isEdit){
            bindingUpload.toolbarMain.tvName.setText(getString(R.string.update_property_title));
        }else {
            bindingUpload.toolbarMain.tvName.setText(getString(R.string.add_property_title));
        }
        bindingUpload.toolbarMain.fabBack.setOnClickListener(v -> onBackPressed());

        bindingUpload.tvUploadFeatureImage.setOnClickListener(view -> picker.pickGallery(new PickerListener() {
            @Override
            public void onPicked(Uri uri, File file, Bitmap bitmap) {
                isFeature = true;
                fileFeature = file;
                Glide.with(UploadActivity.this.getApplicationContext()).load(fileFeature)
                        .into(bindingUpload.ivShowFeature);
                bindingUpload.ivShowFeature.setVisibility(View.VISIBLE);
            }
        }));

        bindingUpload.tvUploadFloorImage.setOnClickListener(view -> picker.pickGallery(new PickerListener() {
            @Override
            public void onPicked(Uri uri, File file, Bitmap bitmap) {
                isFloor = true;
                fileFloor = file;
                Glide.with(UploadActivity.this.getApplicationContext()).load(fileFloor)
                        .into(bindingUpload.ivShowFloor);
                bindingUpload.ivShowFloor.setVisibility(View.VISIBLE);
            }
        }));

        bindingUpload.rvUploadGallery.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(UploadActivity.this, LinearLayoutManager.HORIZONTAL, false);
        bindingUpload.rvUploadGallery.setLayoutManager(layoutManager);
        bindingUpload.rvUploadGallery.setFocusable(false);

        bindingUpload.tvUploadGalleryImage.setOnClickListener(view -> picker.pickGallery(new PickerListener() {
            @Override
            public void onPicked(Uri uri, File file, Bitmap bitmap) {
                isGallery = true;
                fileGallery = file;
                list.add(new UploadImage(file));
                uploadGalleryAdapter.notifyDataSetChanged();
            }
        }));


        bindingUpload.mbSubmit.setOnClickListener(view -> {
            strTitle = bindingUpload.etTitle.getText().toString();
            strDesc = bindingUpload.etDesc.getText().toString();
            strPhone = bindingUpload.etPhone.getText().toString();
            strAddress = bindingUpload.etAddress.getText().toString();
            strLat = bindingUpload.etLat.getText().toString();
            strLong = bindingUpload.etLong.getText().toString();
            strBed = bindingUpload.etBed.getText().toString();
            strBath = bindingUpload.etBath.getText().toString();
            strArea = bindingUpload.etArea.getText().toString();
            strPrice = bindingUpload.etPrice.getText().toString();
            strAmenity = bindingUpload.etAmenity.getText().toString();


            if (bindingUpload.spAddCat.getSelectedItemPosition() == 0) {
                Toast.makeText(UploadActivity.this, getString(R.string.select_home_type), Toast.LENGTH_SHORT).show();
            } else if (bindingUpload.spAddPurpose.getSelectedItemPosition() == 0) {
                Toast.makeText(UploadActivity.this, getString(R.string.select_home), Toast.LENGTH_SHORT).show();
            } else if (strTitle.isEmpty()) {
                bindingUpload.etTitle.requestFocus();
                bindingUpload.etTitle.setError(getString(R.string.add_property_enter, getString(R.string.add_property_name)));
            } else if (strDesc.isEmpty()) {
                bindingUpload.etDesc.requestFocus();
                bindingUpload.etDesc.setError(getString(R.string.add_property_enter, getString(R.string.add_property_desc)));
            } else if (strPhone.isEmpty()) {
                bindingUpload.etPhone.requestFocus();
                bindingUpload.etPhone.setError(getString(R.string.add_property_enter, getString(R.string.add_property_phone)));
            } else if (strAddress.isEmpty()) {
                bindingUpload.etAddress.requestFocus();
                bindingUpload.etAddress.setError(getString(R.string.add_property_enter, getString(R.string.add_property_address)));
            } else if (strLat.isEmpty()) {
                bindingUpload.etLat.requestFocus();
                bindingUpload.etLat.setError(getString(R.string.add_property_enter, getString(R.string.add_property_lat)));
            } else if (strLong.isEmpty()) {
                bindingUpload.etLong.requestFocus();
                bindingUpload.etLong.setError(getString(R.string.add_property_enter, getString(R.string.add_property_long)));
            } else if (strBed.isEmpty()) {
                bindingUpload.etBed.requestFocus();
                bindingUpload.etBed.setError(getString(R.string.add_property_enter, getString(R.string.add_property_bed)));
            } else if (strBath.isEmpty()) {
                bindingUpload.etBath.requestFocus();
                bindingUpload.etBath.setError(getString(R.string.add_property_enter, getString(R.string.add_property_bath)));
            } else if (strArea.isEmpty()) {
                bindingUpload.etArea.requestFocus();
                bindingUpload.etArea.setError(getString(R.string.add_property_enter, getString(R.string.add_property_area)));
            } else if (strPrice.isEmpty()) {
                bindingUpload.etPrice.requestFocus();
                bindingUpload.etPrice.setError(getString(R.string.add_property_enter, getString(R.string.add_property_price)));
            } else if (strAmenity.isEmpty()) {
                bindingUpload.etAmenity.requestFocus();
                bindingUpload.etAmenity.setError(getString(R.string.add_property_enter, getString(R.string.add_property_amenity)));
            } else if (bindingUpload.spAddFurnished.getSelectedItemPosition() == 0) {
                Toast.makeText(UploadActivity.this, getString(R.string.add_property_furnished), Toast.LENGTH_SHORT).show();
            } else if (!isFeature && !isEdit) {
                Toast.makeText(UploadActivity.this, getString(R.string.add_property_select, getString(R.string.add_property_fea_image)), Toast.LENGTH_SHORT).show();
            } else if (!isFloor && !isEdit) {
                Toast.makeText(UploadActivity.this, getString(R.string.add_property_select, getString(R.string.add_property_plan_image)), Toast.LENGTH_SHORT).show();
            } else if (!isGallery && !isEdit) {
                Toast.makeText(UploadActivity.this, getString(R.string.add_property_select, getString(R.string.add_property_gallery_image)), Toast.LENGTH_SHORT).show();
            } else {
                propertyUpload();
            }
        });

        spinData();
        onRequest();
    }

    public void propertyUpload() {
        progressDialog.show();
        progressDialog.setMessage(getResources().getString(R.string.loading));
        progressDialog.setCancelable(false);

        MultipartBody.Part featureBody = null;
        MultipartBody.Part floorPart = null;
        List<MultipartBody.Part> listGallery = new ArrayList<>();

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(UploadActivity.this));
        if (isEdit) {
            jsObj.addProperty("post_id", strEditPostId);
            jsObj.addProperty("verified", String.valueOf(bindingUpload.spAddVerified.getSelectedItem()));
        }else {
            jsObj.addProperty("verified", "NO");
        }
        jsObj.addProperty("user_id", method.getUserId());
        jsObj.addProperty("type_id", categoryRP.getCategoryList().get(bindingUpload.spAddCat.getSelectedItemPosition() - 1).getCategoryId());
        jsObj.addProperty("title", strTitle);
        jsObj.addProperty("description", strDesc);
        jsObj.addProperty("phone", strPhone);
        jsObj.addProperty("address", strAddress);
        jsObj.addProperty("latitude", strLat);
        jsObj.addProperty("longitude", strLong);
        jsObj.addProperty("purpose", String.valueOf(bindingUpload.spAddPurpose.getSelectedItem()));
        jsObj.addProperty("bedrooms", strBed);
        jsObj.addProperty("bathrooms", strBath);
        jsObj.addProperty("area", strArea);
        jsObj.addProperty("furnishing", String.valueOf(bindingUpload.spAddFurnished.getSelectedItem()));
        jsObj.addProperty("amenities", strAmenity);
        jsObj.addProperty("price", strPrice);
        MediaType mediaType = MediaType.parse("multipart/form-data");
        if (isFeature) {
            featureBody = MultipartBody.Part.createFormData("featured_image", fileFeature.getName(), RequestBody.create(fileFeature, mediaType));
        }
        if (isFloor) {
            floorPart = MultipartBody.Part.createFormData("floor_plan_image", fileFloor.getName(), RequestBody.create(fileFloor, mediaType));
        }
        if (isGallery) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getImageUrl().isEmpty()) {
                    MultipartBody.Part galleryBody = MultipartBody.Part.createFormData("image_gallery[]", list.get(i).getImageFile().getName(), RequestBody.create(list.get(i).getImageFile(), mediaType));
                    listGallery.add(galleryBody);
                }
            }
        }
        RequestBody requestBody_data = RequestBody.create(API.toBase64(jsObj.toString()), mediaType);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<UploadPropertyRP> call;
        if (isEdit) {
            call = apiService.getEditPropertyData(requestBody_data, featureBody, floorPart, listGallery);
        } else {
            call = apiService.getUploadPropertyData(requestBody_data, featureBody, floorPart, listGallery);
        }
        call.enqueue(new Callback<UploadPropertyRP>() {
            @Override
            public void onResponse(@NotNull Call<UploadPropertyRP> call, @NotNull Response<UploadPropertyRP> response) {

                try {
                    UploadPropertyRP uploadPropertyRP = response.body();
                    if (uploadPropertyRP != null) {
                        if (uploadPropertyRP.getSuccess().equals("1")) {
                            if(isEdit){
                                Events.myPropertyUpdate myPropertyUpdate = new Events.myPropertyUpdate();
                                GlobalBus.getBus().post(myPropertyUpdate);
                                onBackPressed();
                            }else {
                                Toast.makeText(UploadActivity.this, uploadPropertyRP.getMsg(), Toast.LENGTH_SHORT).show();
                                Intent intentMain = new Intent(UploadActivity.this, MainActivity.class);
                                startActivity(intentMain);
                                finish();
                            }
                        } else {
                            method.alertBox(uploadPropertyRP.getMsg());
                        }
                    }
                } catch (Exception e) {
                    Log.d("exception_error", e.toString());
                    method.alertBox(getResources().getString(R.string.no_error_msg));
                }


                progressDialog.dismiss();
            }

            @Override
            public void onFailure(@NotNull Call<UploadPropertyRP> call, @NotNull Throwable t) {
                // Log error here since request failed
                Log.e("fail", t.toString());
                progressDialog.dismiss();
                method.alertBox(getResources().getString(R.string.no_error_msg));
            }
        });
    }

    private void categoryAllData() {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(UploadActivity.this));
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<CategoryRP> call = apiService.getAllCategoryData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<CategoryRP>() {
            @Override
            public void onResponse(@NotNull Call<CategoryRP> call, @NotNull Response<CategoryRP> response) {

                categoryRP = response.body();
                if (categoryRP != null) {

                    spinCatAdapter = new SpinCatAdapter(UploadActivity.this, R.layout.spinner_item_home, categoryRP.getCategoryList());
                    spinCatAdapter.setDropDownViewResource(R.layout.spinner_item_home);
                    bindingUpload.spAddCat.setAdapter(
                            new NothingSelectedSpinnerAdapter(spinCatAdapter,
                                    R.layout.contact_spinner_row_select_type, UploadActivity.this));
                    bindingUpload.ivDownArrowAdd.setOnClickListener(view -> bindingUpload.spAddCat.performClick());

                    if (isEdit) {
                        for (int i = 0; i < categoryRP.getCategoryList().size(); i++) {
                            if (str_TypeId.equals(categoryRP.getCategoryList().get(i).getCategoryId())) {
                                bindingUpload.spAddCat.setSelection(i + 1);
                                break;
                            }
                        }
                    }

                    bindingUpload.spAddCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int position, long id) {

                            ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.spinner_text_add_property));
                            ((TextView) parent.getChildAt(0)).setTextSize(16);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });


                } else {
                    method.alertBox(getString(R.string.no_error_msg));
                }
            }

            @Override
            public void onFailure(@NotNull Call<CategoryRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                method.alertBox(getString(R.string.no_error_msg));
            }
        });
    }

    private void spinData() {
        adapterBuySell = new ArrayAdapter<>(UploadActivity.this, R.layout.spinner_item_home, strBuySell);
        adapterBuySell.setDropDownViewResource(R.layout.spinner_item_home);
        bindingUpload.spAddPurpose.setAdapter(
                new NothingSelectedSpinnerAdapter(adapterBuySell,
                        R.layout.contact_spinner_row_selected_purpose, UploadActivity.this));
        bindingUpload.ivDownArrowAddPurpose.setOnClickListener(view -> bindingUpload.spAddPurpose.performClick());

        bindingUpload.spAddPurpose.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.spinner_text_add_property));
                ((TextView) parent.getChildAt(0)).setTextSize(16);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapterFurnished = new ArrayAdapter<>(UploadActivity.this, R.layout.spinner_item_home, strFurnished);
        adapterFurnished.setDropDownViewResource(R.layout.spinner_item_home);
        bindingUpload.spAddFurnished.setAdapter(
                new NothingSelectedSpinnerAdapter(adapterFurnished,
                        R.layout.contact_spinner_row_selected_furnished, UploadActivity.this));
        bindingUpload.ivDownArrowAddFur.setOnClickListener(view -> bindingUpload.spAddFurnished.performClick());

        bindingUpload.spAddFurnished.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.spinner_text_add_property));
                ((TextView) parent.getChildAt(0)).setTextSize(16);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isEdit) {
            bindingUpload.mcVerified.setVisibility(View.VISIBLE);
            adapterVerified = new ArrayAdapter<>(UploadActivity.this, R.layout.spinner_item_home, strVerified);
            adapterVerified.setDropDownViewResource(R.layout.spinner_item_home);
            bindingUpload.spAddVerified.setAdapter(
                    new NothingSelectedSpinnerAdapter(adapterVerified,
                            R.layout.contact_spinner_row_selected_furnished, UploadActivity.this));
            bindingUpload.ivDownArrowAddVerified.setOnClickListener(view -> bindingUpload.spAddVerified.performClick());

            bindingUpload.spAddVerified.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view,
                                           int position, long id) {

                    ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.spinner_text_add_property));
                    ((TextView) parent.getChildAt(0)).setTextSize(16);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void onRequest() {
        if (method.isNetworkAvailable()) {
            if (isEdit) {
                editPropertyData();
            } else {
                categoryAllData();
            }

        } else {
            onState(1);
        }
    }

    private void editPropertyData() {
        bindingUpload.progressHome.setVisibility(View.VISIBLE);
        bindingUpload.mcVerified.setVisibility(View.VISIBLE);
        showProgress(true);
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(UploadActivity.this));
        jsObj.addProperty("post_id", strEditPostId);
        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<EditPropertyRP> call = apiService.getEditPropertyData(API.toBase64(jsObj.toString()));
        call.enqueue(new Callback<EditPropertyRP>() {
            @Override
            public void onResponse(@NotNull Call<EditPropertyRP> call, @NotNull Response<EditPropertyRP> response) {

                EditPropertyRP editPropertyRP = response.body();
                if (editPropertyRP != null) {
                    showProgress(false);
                    categoryAllData();
                    editProperty = editPropertyRP.getEditProperty();
                    str_TypeId = editProperty.getType_id();
                    str_Furnished = editProperty.getFurnishing();
                    str_Purpose = editProperty.getPurpose();
                    str_Verified = editProperty.getVerified();

                    bindingUpload.etTitle.setText(editProperty.getTitle());
                    bindingUpload.etDesc.setText(Html.fromHtml(editProperty.getDescription()));
                    bindingUpload.etPhone.setText(editProperty.getPhone());
                    bindingUpload.etAddress.setText(editProperty.getAddress());
                    bindingUpload.etLat.setText(editProperty.getLatitude());
                    bindingUpload.etLong.setText(editProperty.getLongitude());
                    bindingUpload.etBed.setText(editProperty.getBedrooms());
                    bindingUpload.etBath.setText(editProperty.getBathrooms());
                    bindingUpload.etArea.setText(editProperty.getArea());
                    bindingUpload.etPrice.setText(editProperty.getPrice());
                    bindingUpload.etAmenity.setText(editProperty.getAmenities());

                    bindingUpload.ivShowFeature.setVisibility(View.VISIBLE);
                    bindingUpload.ivShowFloor.setVisibility(View.VISIBLE);
                    if (!editProperty.getImage().equals("")) {
                        Glide.with(UploadActivity.this).load(editProperty.getImage())
                                .placeholder(R.drawable.property_placeholder)
                                .into(bindingUpload.ivShowFeature);
                    }

                    if (!editProperty.getFloor_plan_image().equals("")) {
                        Glide.with(UploadActivity.this).load(editProperty.getFloor_plan_image())
                                .placeholder(R.drawable.property_placeholder)
                                .into(bindingUpload.ivShowFloor);
                    }
                    list.addAll(editProperty.getGalleryEditProperties());
                    uploadGalleryAdapter.notifyDataSetChanged();

                    if (str_Purpose.equals("Sale")) {
                        bindingUpload.spAddPurpose.setSelection(1);
                    } else {
                        bindingUpload.spAddPurpose.setSelection(2);
                    }

                    switch (str_Furnished) {
                        case "Furnished":
                            bindingUpload.spAddFurnished.setSelection(1);
                            break;
                        case "Semi-Furnished":
                            bindingUpload.spAddFurnished.setSelection(2);
                            break;
                        case "Unfurnished":
                            bindingUpload.spAddFurnished.setSelection(3);
                            break;
                    }

                    if (str_Verified.equals("YES")) {
                        bindingUpload.spAddVerified.setSelection(1);
                    } else {
                        bindingUpload.spAddVerified.setSelection(2);
                    }


                } else {
                    onState(3);//error
                }
            }

            @Override
            public void onFailure(@NotNull Call<EditPropertyRP> call, @NotNull Throwable t) {
                Log.e("fail", t.toString());
                onState(3);
            }
        });
    }

    private void onState(int state) {
        bindingUpload.layState.getRoot().setVisibility(View.VISIBLE);
        bindingUpload.nsUploadScroll.setVisibility(View.GONE);
        bindingUpload.progressHome.setVisibility(View.GONE);
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
        bindingUpload.layState.ivState.setImageResource(image);
        bindingUpload.layState.tvState.setText(title);
        bindingUpload.layState.tvStateMsg.setText(desc);

        bindingUpload.layState.btnRefreshNow.setOnClickListener(view -> {
            bindingUpload.layState.getRoot().setVisibility(View.GONE);
            onRequest();
        });
    }

    private void showProgress(boolean isProgress) {
        if (isProgress) {
            bindingUpload.progressHome.setVisibility(View.VISIBLE);
            bindingUpload.nsUploadScroll.setVisibility(View.GONE);
        } else {
            bindingUpload.progressHome.setVisibility(View.GONE);
            bindingUpload.nsUploadScroll.setVisibility(View.VISIBLE);
        }
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
