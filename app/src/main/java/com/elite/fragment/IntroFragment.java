package com.elite.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.elite.util.StatusBar;
import com.example.realestate.R;
import com.example.realestate.databinding.FragmentIntroBinding;


public class IntroFragment extends Fragment {

    FragmentIntroBinding viewIntro;
    final static String LAYOUT_ID = "layoutid";

    public static IntroFragment newInstance(int layoutId) {
        IntroFragment pane = new IntroFragment();
        Bundle args = new Bundle();
        args.putInt(LAYOUT_ID, layoutId);
        pane.setArguments(args);
        return pane;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        StatusBar.initIntroScreen(requireActivity());
        viewIntro = FragmentIntroBinding.inflate(inflater, container, false);
        int position = getArguments().getInt(LAYOUT_ID, -1);

        Integer[] Images = {R.drawable.img_intro_1, R.drawable.img_intro_2, R.drawable.img_intro_3};
        String[] Title = {getResources().getString(R.string.intro_title_1),
                getResources().getString(R.string.intro_title_2),
                getResources().getString(R.string.intro_title_3)};
        String[] Desc = {getResources().getString(R.string.intro_desc_1),
                getResources().getString(R.string.intro_desc_2),
                getResources().getString(R.string.intro_desc_3)};

        viewIntro.ivIntro.setImageResource(Images[position]);
        viewIntro.tvBooksTitle.setText(Title[position]);
        viewIntro.tvBookDesc.setText(Desc[position]);

        return viewIntro.getRoot();
    }
}
