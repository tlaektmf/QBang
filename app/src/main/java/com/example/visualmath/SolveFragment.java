package com.example.visualmath;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SolveFragment extends Fragment {

    ViewGroup rootView;
    TextView textViewTitle;
    TextView textViewGrade;
    ImageView imageViewProblem;
    TextView textViewDetail;
    ImageView imageViewOther1;
    ImageView imageViewOther2;
    ImageView imageViewOther3;

    public SolveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView=(ViewGroup)inflater.inflate(R.layout.fragment_solve, container, false);
        init(rootView);
        //return inflater.inflate(R.layout.fragment_solve, container, false);
        return rootView;
    }

    public void init(ViewGroup _rootView){
         textViewTitle=_rootView.findViewById(R.id.tv_title);
         textViewGrade=_rootView.findViewById(R.id.tv_grade);
         imageViewProblem=_rootView.findViewById(R.id.iv_file_problem);
         textViewDetail=_rootView.findViewById(R.id.tv_detail);
         imageViewOther1=_rootView.findViewById(R.id.iv_picture1);
         imageViewOther2=_rootView.findViewById(R.id.iv_picture2);
         imageViewOther3=_rootView.findViewById(R.id.iv_picture3);

         setWidget();
    }
    public void setWidget(){
        textViewTitle.setText("2019 10월 교육청 모의고사");
        textViewGrade.setText("고등");
        textViewDetail.setText("답지를 봐도 잘 모르겠다. 답지 첨부 합니다");
        imageViewProblem.setImageResource(R.drawable.img_math1);
        imageViewOther1.setImageResource(R.drawable.img_math2);
        imageViewOther2.setImageResource(R.drawable.img_math3);

    }
}
