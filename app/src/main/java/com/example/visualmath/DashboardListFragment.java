package com.example.visualmath;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.visualmath.ui.dashboard.DashboardFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardListFragment extends Fragment {

    View rootView;
    public DashboardListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        rootView=inflater.inflate(R.layout.fragment_dashboard_list, container, false);


        //캘린더 모드 변경
        Button btn = rootView.findViewById(R.id.button);


        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //datecheck.setVisibility(datecheck.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                //recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                ((HomeActivity)getActivity()).replaceFragment(new DashboardFragment());


            }
        });

        return rootView;

    }


}
