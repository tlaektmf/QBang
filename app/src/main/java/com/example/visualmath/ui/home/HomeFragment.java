package com.example.visualmath.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.visualmath.DashboardListFragment;
import com.example.visualmath.HomeActivity;
import com.example.visualmath.MainActivity;
import com.example.visualmath.R;
import com.example.visualmath.VM_RegisterProblemActivity;
import com.example.visualmath.ui.dashboard.DashboardFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);


//        final TextView textView = root.findViewById(R.id.);
//        homeViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

        //ds.shim
        final ImageButton buttonQVideo=root.findViewById(R.id.ib_qText);
        final ImageButton buttonQText=root.findViewById(R.id.ib_qVideo);


        buttonQText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), VM_RegisterProblemActivity.class);
                startActivity(intent);
            }
        });


        buttonQVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), VM_RegisterProblemActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

}