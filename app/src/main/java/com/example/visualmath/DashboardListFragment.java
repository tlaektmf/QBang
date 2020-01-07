package com.example.visualmath;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardListFragment extends Fragment {

    ViewGroup rootView;
    public DashboardListFragment() {
        // Required empty public constructor
    }

    // 각각의 Fragment마다 Instance를 반환해 줄 메소드
    public static DashboardListFragment newInstance() {
        return new DashboardListFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        init(rootView);

        return rootView;

    }
    public void init(ViewGroup _rootView){

    }

}
