package com.elite.realestate;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.databinding.LayoutPaymentSuccessBinding;

public class SuccessActivity extends AppCompatActivity {

    LayoutPaymentSuccessBinding successBinding;
    String msg;
    Method method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initLatestScreen(SuccessActivity.this);
        successBinding = LayoutPaymentSuccessBinding.inflate(getLayoutInflater());
        setContentView(successBinding.getRoot());

        method = new Method(SuccessActivity.this);
        method.forceRTLIfSupported();

        Intent intent = getIntent();
        msg = intent.getStringExtra("MSG");

        successBinding.tvSuccessMsg.setText(msg);
        successBinding.btnHome.setOnClickListener(v -> {
            Intent intentMain = new Intent(SuccessActivity.this, MainActivity.class);
            startActivity(intentMain);
            finishAffinity();
        });


    }

     @Override
    public void onBackPressed() {
        //super.onBackPressed();
     }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}