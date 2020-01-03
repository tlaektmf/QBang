package com.example.visualmath;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.dummy.DummyContent;
import com.example.visualmath.dummy.TestContent;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemProblemDetailFragment extends Fragment {


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID="item_id";
    public static final String ARG_ITEM_CONTENT = "item_problem_content";
    public static final String ARG_ITEM_DETAIL = "item_problem_detail";
    public static final String TAG="ItemProblemDetail";
    /**
     * The dummy content this fragment is presenting.
     */
    private TestContent.TestItem mItem;


    public ItemProblemDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_CONTENT)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            mItem=new TestContent.TestItem(getArguments().getString(ARG_ITEM_ID),getArguments().getString(ARG_ITEM_CONTENT),getArguments().getString(ARG_ITEM_DETAIL));
            Log.i(TAG,mItem.getContent());

        }else{
            Log.i(TAG,"null");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView =inflater.inflate(R.layout.fragment_item_problem_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.tv_problem_detail)).setText(mItem.getDetails());

            TextView textView=(TextView)rootView.findViewById(R.id.tv_problem_title);
            textView.setText(mItem.getContent());


            Button buttonViewDetail=rootView.findViewById(R.id.bt_viewDetail);
            buttonViewDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(getActivity(),VM_ProblemDetailActivity.class);
                    startActivity(intent);
                }
            });

            Button buttonMatch=rootView.findViewById(R.id.bt_match);
            buttonMatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //** 매칭완료 다이얼로그 생성
//                     Toast.makeText(getActivity(),"다이얼로그 생성 위치",Toast.LENGTH_LONG).show();

                    final VM_Dialog_match dialog= new VM_Dialog_match(rootView.getContext());

                    dialog.setDialogListener(new VM_DialogListener_matchComplete(){
                        public void onButtonYes(){
                            //네 버튼
//                            Toast.makeText(getActivity(),"네",Toast.LENGTH_LONG).show();

                            //매칭 완료 toast 뷰 형태로 띄우기
                            Toast toast = Toast.makeText(getActivity(),"",Toast.LENGTH_LONG);
                            toast.setView(getLayoutInflater().inflate(R.layout.layout_dialog_match_complete,null));
                            toast.show();
                        }
                        public void onButtonNo(){
                            //아니오 버튼
//                            Toast.makeText(getActivity(),"아니오",Toast.LENGTH_LONG).show();

                        }
                    });
                    dialog.callFunction();

                }
            });

            ImageView imageView=(ImageView)rootView.findViewById(R.id.iv_problem_image);
            String res_name="@drawable/img_math"+(Integer.parseInt(mItem.getId())%4+1);
            int resID = getResources().getIdentifier(res_name, "drawable",  getActivity().getPackageName());
            imageView.setImageResource(resID);

            Log.i(TAG,mItem.getDetails());
        }else{
            Log.i(TAG,"null");
        }

        return rootView;

    }

}
