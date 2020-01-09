package com.example.visualmath.ui.dashboard;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.visualmath.HomeActivity;
import com.example.visualmath.R;
import com.example.visualmath.ui.dashboard.DashboardFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardListFragment extends Fragment {


    ViewGroup rootView;
    public DashboardListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_dashboard_list, container, false);
        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_dashboard_list, container, false);


        //캘린더 모드 변경
        Button btn = rootView.findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //datecheck.setVisibility(datecheck.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                //recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                FragmentManager fm = ((HomeActivity)getActivity()).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기
                tran.replace(R.id.nav_host_fragment,new DashboardFragment()).commit();

            }
        });

        return rootView;

    }

}
