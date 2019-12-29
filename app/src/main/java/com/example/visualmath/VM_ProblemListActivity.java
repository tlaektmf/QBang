package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.visualmath.dummy.DummyContent;
import com.example.visualmath.dummy.TestContent;

import java.util.List;

public class VM_ProblemListActivity extends AppCompatActivity {
    View recyclerView;
    ViewGroup layout;
    public static String TAG="VM_ProblemList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__problem_list);

        init();

        //** detailView 클릭 이벤트
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), VM_ProblemDetailActivity.class);
                startActivity(intent);
//                finish();
            }
        });
    }

    public void init(){
        recyclerView = findViewById(R.id.item_problem_list);
        layout = (ViewGroup) findViewById(R.id.item_problem_detail_container);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new VM_ProblemListActivity.SimpleItemRecyclerViewAdapter(this, new TestContent().getITEMS()));
    }

    /**
     * class SimpleItemRecyclerViewAdapter
     */
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final VM_ProblemListActivity mParentActivity;
        private final List<TestContent.TestItem> mValues;

        /**
         * 문제 상세뷰로 이동
         */
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestContent.TestItem item = (TestContent.TestItem) view.getTag();

                Bundle arguments = new Bundle();
                arguments.putString(ItemProblemDetailFragment.ARG_ITEM_ID, item.getId());
                arguments.putString(ItemProblemDetailFragment.ARG_ITEM_CONTENT, item.getContent());
                arguments.putString(ItemProblemDetailFragment.ARG_ITEM_DETAIL, item.getDetails());
                ItemProblemDetailFragment fragment = new ItemProblemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_problem_detail_container, fragment)
                        .commit();
            }
        };

        SimpleItemRecyclerViewAdapter(VM_ProblemListActivity parent,
                                      List<TestContent.TestItem> items) {
            mValues = items;
            mParentActivity = parent;

        }

        @Override
        public VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_problem_list_content, parent, false);
            return new VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {

            holder.mDetailView.setText(mValues.get(position).getDetails());
            holder.mtitleView.setText(mValues.get(position).getContent());
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);

        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mtitleView;
            final TextView mDetailView;


            ViewHolder(View view) {
                super(view);
                mtitleView = (TextView) view.findViewById(R.id.problem_content_title);
                mDetailView = (TextView) view.findViewById(R.id.problem_content_detail);

            }
        }
    }

    //**
    public void activateList(View view) {
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.choose_drawer_menu);
        if(layout.getVisibility()==View.VISIBLE){
            //현재 뷰가 보이면
            layout.setVisibility(View.GONE);
        }else{
            //뷰가 보이지 않으면
            layout.setVisibility(View.VISIBLE);
        }
    }


    private void setData(@NonNull RecyclerView recyclerView,List<TestContent.TestItem> items) {
        recyclerView.setAdapter(new VM_ProblemListActivity.SimpleItemRecyclerViewAdapter(this, items));
    }

    public void showElementary(View view) {
        //** 초등
        Button btn01 = findViewById(R.id.ib_element);
        Button btn02 = findViewById(R.id.ib_mid);
        Button btn03 = findViewById(R.id.ib_high);

        btn01.setSelected(true);
        btn02.setSelected(false);
        btn03.setSelected(false);

        Log.i(TAG,"클릭");
        TestContent testContent=new TestContent();
        for(int i=0;i<3;i++){
            testContent.getITEMS().add(new TestContent.TestItem(i+"","초     등"+i,"detail"+i));
        }
        setData((RecyclerView) recyclerView,testContent.getITEMS());
    }

    public void showMid(View view) {
        Button btn01 = findViewById(R.id.ib_element);
        Button btn02 = findViewById(R.id.ib_mid);
        Button btn03 = findViewById(R.id.ib_high);

        btn01.setSelected(false);
        btn02.setSelected(true);
        btn03.setSelected(false);

        TestContent testContent=new TestContent();
        for(int i=0;i<15;i++){
            testContent.getITEMS().add(new TestContent.TestItem(i+"","중     등"+i,"detail"+i));
        }
        setData((RecyclerView) recyclerView,testContent.getITEMS());

    }

    public void showHigh(View view) {
        Button btn01 = findViewById(R.id.ib_element);
        Button btn02 = findViewById(R.id.ib_mid);
        Button btn03 = findViewById(R.id.ib_high);

        btn01.setSelected(false);
        btn02.setSelected(false);
        btn03.setSelected(true);

        TestContent testContent=new TestContent();
        for(int i=0;i<15;i++){
            testContent.getITEMS().add(new TestContent.TestItem(i+"","고     등"+i,"detail"+i));
        }
        setData((RecyclerView) recyclerView,testContent.getITEMS());
    }
}
