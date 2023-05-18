package com.elite.realestate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.elite.fragment.IntroFragment;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.ActivityIntroBinding;


public class IntroActivity extends AppCompatActivity {

    ActivityIntroBinding viewIntroBinding;
    PagerAdapter pagerAdapter;
    Method method;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBar.initIntroScreen(IntroActivity.this);

        viewIntroBinding = ActivityIntroBinding.inflate(getLayoutInflater());
        View view = viewIntroBinding.getRoot();
        setContentView(view);
        method = new Method(IntroActivity.this);
        method.forceRTLIfSupported();

        pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewIntroBinding.vpIntro.setAdapter(pagerAdapter);
        viewIntroBinding.dotsIndicator.attachTo(viewIntroBinding.vpIntro);

        viewIntroBinding.btnNext.setOnClickListener(v -> viewIntroBinding.vpIntro.setCurrentItem(viewIntroBinding.vpIntro.getCurrentItem() + 1));

        viewIntroBinding.btnNext.setOnClickListener(view1 -> {
            if (viewIntroBinding.vpIntro.getCurrentItem() == pagerAdapter.getCount() - 1) {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                viewIntroBinding.vpIntro.setCurrentItem(viewIntroBinding.vpIntro.getCurrentItem() + 1);
            }

        });

        viewIntroBinding.vpIntro.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == pagerAdapter.getCount() - 1) {
                    viewIntroBinding.btnNext.setText(getString(R.string.lbl_get_started));
                } else {
                    viewIntroBinding.btnNext.setText(getString(R.string.lbl_next));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            IntroFragment tp;
            tp = IntroFragment.newInstance(position);
            return tp;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}