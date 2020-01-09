package com.example.visualmath.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.example.visualmath.R;
import com.example.visualmath.VM_RegisterProblemActivity;

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