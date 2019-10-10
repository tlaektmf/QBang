package com.example.visualmath.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTabHost;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.visualmath.HomeActivity;
import com.example.visualmath.ItemDetailFragment;
import com.example.visualmath.ItemListActivity;
import com.example.visualmath.R;
import com.example.visualmath.TabFragment;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

//        final TextView textView = root.findViewById(R.id.text_dashboard);
//        dashboardViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

//        //activity 이동
//        Intent intent;
//        intent = new Intent(getActivity(), ItemListActivity.class);
//        startActivity(intent);

        getFragmentManager().beginTransaction().add(R.id.showView, new TabFragment()).commit();

        /*
        탭 생성
         */
        TabHost tabHost1 = (TabHost) root.findViewById(R.id.tabHost) ;
        tabHost1.setup() ;

        // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.view);

        ts1.setIndicator("질문중") ;
        tabHost1.addTab(ts1)  ;



        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts2.setContent(R.id.view) ;
        ts2.setIndicator("질문 완료") ;
        tabHost1.addTab(ts2) ;



        return root;
    }
}