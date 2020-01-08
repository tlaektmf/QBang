package com.example.visualmath;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

        FragmentManager fm = ((HomeActivity)getActivity()).getSupportFragmentManager();


        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //datecheck.setVisibility(datecheck.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                //recyclerView.setVisibility(recyclerView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                ///((HomeActivity)getActivity()).replaceFragment(new DashboardFragment());
                FragmentManager fm = ((HomeActivity)getActivity()).getSupportFragmentManager();           //프래그먼트 매니저 생성
                FragmentTransaction tran = fm.beginTransaction();               //트랜잭션 가져오기


                if(fm.findFragmentById(R.id.nav_host_fragment) instanceof DashboardListFragment){
                    Log.i("dashlist","현재 frag는 dashlist");
                }

                Log.i("dashlist",fm.getBackStackEntryCount()+"");

                //tran.addToBackStack(null).commit();

                tran.replace(R.id.nav_host_fragment,new DashboardFragment()).commit();

            }
        });

        return rootView;

    }

    static View v; // 프래그먼트의 뷰 인스턴스
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("dashlist","dashlist 없어짐");
//        if(v!=null){
//            ViewGroup parent=(ViewGroup)v.getParent();
//            if(parent!=null){
//                parent.removeView(v);
//            }
//        }
    }
}
