package com.elite.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.elite.response.ReportRP;
import com.elite.rest.ApiClient;
import com.elite.rest.ApiInterface;
import com.elite.util.API;
import com.elite.util.Method;
import com.example.realestate.R;
import com.example.realestate.databinding.LayoutReportBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReportBookFragment extends BottomSheetDialogFragment {

    LayoutReportBinding bindingReport;
    String postId, userId;
    ProgressDialog progressDialog;
    Method method;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        bindingReport = LayoutReportBinding.inflate(inflater, container, false);
        method = new Method(requireActivity());
        method.forceRTLIfSupported();

        if (getArguments() != null) {
            postId = getArguments().getString("postId");
            userId = getArguments().getString("userId");
        }
        progressDialog = new ProgressDialog(requireActivity(), R.style.MyAlertDialogStyle);
        bindingReport.mbLater.setOnClickListener(v -> dismiss());

        bindingReport.mbSubmit.setOnClickListener(v -> {
            if (bindingReport.etReport.getText().toString().isEmpty()) {
                Toast.makeText(requireActivity(), getString(R.string.report_write_title), Toast.LENGTH_SHORT).show();
            } else {
                dismiss();
                sendRateReviewData();
            }
        });
        return bindingReport.getRoot();
    }


    private void sendRateReviewData() {
        Activity activity = getActivity();
        if (isAdded() && activity != null) {
            progressDialog.show();
            progressDialog.setMessage(getResources().getString(R.string.loading));
            progressDialog.setCancelable(false);

            JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API(requireActivity()));
            jsObj.addProperty("post_id", postId);
            jsObj.addProperty("user_id", userId);
            jsObj.addProperty("post_type", "Property");
            jsObj.addProperty("message", bindingReport.etReport.getText().toString());
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<ReportRP> call = apiService.getReportPropertyData(API.toBase64(jsObj.toString()));
            call.enqueue(new Callback<ReportRP>() {
                @Override
                public void onResponse(@NotNull Call<ReportRP> call, @NotNull Response<ReportRP> response) {

                    ReportRP reportRP = response.body();
                    if (reportRP != null) {
                        method.alertBox(reportRP.getMsg());
                    }
                    progressDialog.dismiss();
                }

                @Override
                public void onFailure(@NotNull Call<ReportRP> call, @NotNull Throwable t) {
                    // Log error here since request failed
                    Log.e("fail", t.toString());
                    progressDialog.dismiss();
                    method.alertBox(getResources().getString(R.string.no_error_msg));
                }
            });
        }
    }
}