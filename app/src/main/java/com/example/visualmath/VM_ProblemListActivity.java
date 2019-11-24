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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.visualmath.dummy.DummyContent;

import java.util.List;

public class VM_ProblemListActivity extends AppCompatActivity {
    View recyclerView;
    ViewGroup layout;

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
        recyclerView.setAdapter(new VM_ProblemListActivity.SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS));
    }


    /**
     * class SimpleItemRecyclerViewAdapter
     */
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<VM_ProblemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final VM_ProblemListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;

        /**
         * 문제 상세뷰로 이동
         */
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();

                Bundle arguments = new Bundle();
                arguments.putString(TeacherItemDetailFragment.ARG_ITEM_ID, item.id);
                ItemProblemDetailFragment fragment = new ItemProblemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_problem_detail_container, fragment)
                        .commit();
            }
        };

        SimpleItemRecyclerViewAdapter(VM_ProblemListActivity parent,
                                      List<DummyContent.DummyItem> items) {
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

            holder.mDetailView.setText(mValues.get(position).content);
            holder.mtitleView.setText(mValues.get(position).details);
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
    }

    public void showElementary(View view) {
    }

    public void showMid(View view) {
    }

    public void showHigh(View view) {
    }
}
