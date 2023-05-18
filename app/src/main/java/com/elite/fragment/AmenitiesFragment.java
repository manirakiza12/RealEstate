package com.elite.fragment;


import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elite.adapter.AmenitiesAdapter;
import com.elite.util.ItemOffsetDecoration;
import com.example.realestate.R;

import java.util.ArrayList;

public class AmenitiesFragment extends Fragment {

    public RecyclerView recyclerView;
    static ArrayList<String> mList;
    AmenitiesAdapter mAdapter;

    public static AmenitiesFragment newInstance(ArrayList<String> categoryId) {
        AmenitiesFragment f = new AmenitiesFragment();
        mList = categoryId;
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_amenities, container, false);
        recyclerView = rootView.findViewById(R.id.vertical_courses_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        recyclerView.addItemDecoration(itemDecoration);

        mAdapter = new AmenitiesAdapter(getActivity(), mList);
        recyclerView.setAdapter(mAdapter);

        return rootView;
    }

}
