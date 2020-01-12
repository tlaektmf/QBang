package com.example.visualmath.ui.home;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.visualmath.R;
import com.example.visualmath.VM_ProblemListActivity;
import com.example.visualmath.VM_RegisterProblemActivity;
import com.example.visualmath.VM_TeacherSolveProblemActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeacherHomeFragment extends Fragment {
    private HomeViewModel homeViewModel;

//    public TeacherHomeFragment() {
//        // Required empty public constructor
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_teacher_home, container, false);

        final ImageButton buttonPickProblem=root.findViewById(R.id.ib_pickProblem);
        final ImageButton buttonSolveProblem=root.findViewById(R.id.ib_solveProblem);



        buttonPickProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), VM_ProblemListActivity.class);
                startActivity(intent);
            }
        });


        buttonSolveProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getActivity(), VM_TeacherSolveProblemActivity.class);
                startActivity(intent);
            }
        });


        return root;
    }

}
