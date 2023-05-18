package com.elite.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.elite.realestate.LoginActivity;
import com.elite.util.Events;
import com.elite.util.GlobalBus;
import com.elite.util.Method;
import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.FragmentLatestBinding;

import org.greenrobot.eventbus.Subscribe;


public class LatestFragment extends Fragment {

    FragmentLatestBinding bindingLatest;
    Method method;
    FragmentManager fragmentManager;
    PopupWindow popupWindow;
    boolean isGridSelect = true;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        StatusBar.initLatestScreen(requireActivity());
        bindingLatest = FragmentLatestBinding.inflate(inflater, container, false);
        GlobalBus.getBus().register(this);
        method = new Method(requireActivity());
        method.forceRTLIfSupported();
        fragmentManager = getChildFragmentManager();

        if (method.getIsLogin()){
            if (!method.getUserImage().isEmpty()) {
                Glide.with(requireActivity()).load(method.getUserImage())
                        .placeholder(R.drawable.user_profile)
                        .into(bindingLatest.clUserImage.ivUserImage);
            }
            bindingLatest.tvUserName.setText(method.getUserName());
        }else {
            Glide.with(requireActivity()).load(R.drawable.user_profile)
                    .into(bindingLatest.clUserImage.ivUserImage);
            bindingLatest.tvUserName.setText(getString(R.string.default_user_name));
            bindingLatest.clUserImage.ivOnline.setImageResource(R.drawable.offline);
        }
        method.profilePopUpWindow();
        bindingLatest.clUserImage.ivUserImage.setOnClickListener(view -> {
            if (method.getIsLogin()){
                method.popupWindowProfile.showAsDropDown(view,-153,0);
            }else {
                Toast.makeText(requireActivity(), getString(R.string.login_require), Toast.LENGTH_SHORT).show();
                Intent intentLogin = new Intent(requireActivity(), LoginActivity.class);
                intentLogin.putExtra("isFromDetail", true);
                startActivity(intentLogin);
            }
        });

        bindingLatest.ibGrid.setOnClickListener(view -> {
            isGridSelect = true;
            bindingLatest.ibList.setColorFilter(getResources().getColor(R.color.img_normal), PorterDuff.Mode.SRC_IN);
            bindingLatest.ibGrid.setColorFilter(getResources().getColor(R.color.img_select), PorterDuff.Mode.SRC_IN);
            bindingLatest.ibGrid.setBackgroundResource(R.drawable.icon_purple_bg);
            bindingLatest.ibList.setBackgroundResource(R.drawable.icon_light_purple_bg);
            goGrid("Latest");
        });

        bindingLatest.ibList.setOnClickListener(view -> {
            isGridSelect = false;
            bindingLatest.ibList.setColorFilter(getResources().getColor(R.color.img_select), PorterDuff.Mode.SRC_IN);
            bindingLatest.ibGrid.setColorFilter(getResources().getColor(R.color.img_normal), PorterDuff.Mode.SRC_IN);
            bindingLatest.ibGrid.setBackgroundResource(R.drawable.icon_light_purple_bg);
            bindingLatest.ibList.setBackgroundResource(R.drawable.icon_purple_bg);
            goList("Latest");
        });
        goGrid("Latest");

        setPopUpWindow();
        bindingLatest.rlSortBy.setOnClickListener(view -> popupWindow.showAsDropDown(view,-153,0));

        bindingLatest.ibFilter.setOnClickListener(view -> {
            Bundle bundle = new Bundle();
            bundle.putString("postId", "");
            bundle.putString("userId", method.getUserId());
            AdvanceSearchFragment advanceSearchFragment = new AdvanceSearchFragment();
            advanceSearchFragment.setArguments(bundle);
            advanceSearchFragment.show(getChildFragmentManager(), advanceSearchFragment.getTag());
        });

        return bindingLatest.getRoot();
    }

    private void setPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater)
                requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_sort_by_popup, null);
        TextView tvLatest=view.findViewById(R.id.tvDaiLatest);
        TextView tvPriceHigh=view.findViewById(R.id.tvDiaPriceHigh);
        TextView tvPriceLow=view.findViewById(R.id.tvDiaPriceLow);
        TextView tvDistance=view.findViewById(R.id.tvDiaDistance);

        tvLatest.setOnClickListener(view1 -> {
            if (isGridSelect) {
                goGrid("Latest");
            } else {
                goList("Latest");
            }
            popupWindow.dismiss();
        });

        tvPriceHigh.setOnClickListener(view12 -> {
            if (isGridSelect) {
                goGrid("LatestHighPrice");
            } else {
                goList("LatestHighPrice");
            }
            popupWindow.dismiss();
        });

        tvPriceLow.setOnClickListener(view13 -> {
            if (isGridSelect) {
                goGrid("LatestLowPrice");
            } else {
                goList("LatestLowPrice");
            }
            popupWindow.dismiss();
        });

        tvDistance.setOnClickListener(view14 -> {
            if (isGridSelect) {
                goGrid("LatestDistance");
            } else {
                goList("LatestDistance");
            }
            popupWindow.dismiss();
        });

        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, true);
    }


    private void goGrid(String latestType) {
        Bundle bundle = new Bundle();
        bundle.putString("postId", "");
        bundle.putString("postName", "");
        bundle.putString("postType", latestType);
        PropertyGridFragment propertyGridFragment = new PropertyGridFragment();
        propertyGridFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flLatest, propertyGridFragment, "")
                .commitAllowingStateLoss();
    }

    private void goList(String latestType) {
        Bundle bundle = new Bundle();
        bundle.putString("postId", "");
        bundle.putString("postName", "");
        bundle.putString("postType", latestType);
        PropertyListFragment propertyListFragment = new PropertyListFragment();
        propertyListFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.flLatest, propertyListFragment, "")
                .commitAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        GlobalBus.getBus().unregister(this);
    }

    @Subscribe
    public void onEvent(Events.ProfileUpdate profileUpdate){
        if (!method.getUserImage().isEmpty()) {
            Glide.with(requireActivity()).load(method.getUserImage())
                    .placeholder(R.drawable.user_profile)
                    .into(bindingLatest.clUserImage.ivUserImage);
        }
        bindingLatest.tvUserName.setText(method.getUserName());
        bindingLatest.clUserImage.ivOnline.setImageResource(R.drawable.online);
    }
}
