package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.visualmath.dummy.DummyContent;

import java.util.List;

public class TeacherItemListActivity extends AppCompatActivity {
    View recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_item_list);

        init();
    }

    /**
     * 알람 종류에 따라서 각기 다른 Activity로 이동해야됨
     * @param view
     */
    public void showItemList(View view) {
        Intent intent;
        intent = new Intent(getApplicationContext(), VM_FullViewActivity.class);
        startActivity(intent);

    }

    public void init(){
        recyclerView = findViewById(R.id.teacher_item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new TeacherItemListActivity.SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS));
    }


    /**
     * class SimpleItemRecyclerViewAdapter
     */
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<TeacherItemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final TeacherItemListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();

                Bundle arguments = new Bundle();
                arguments.putString(TeacherItemDetailFragment.ARG_ITEM_ID, item.id);
                TeacherItemDetailFragment fragment = new TeacherItemDetailFragment();
                fragment.setArguments(arguments);
                mParentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.teacher_item_detail_container, fragment)
                        .commit();
            }
        };

        SimpleItemRecyclerViewAdapter(TeacherItemListActivity parent,
                                      List<DummyContent.DummyItem> items) {
            mValues = items;
            mParentActivity = parent;

        }

        @Override
        public TeacherItemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.teacher_item_list_content, parent, false);
            return new TeacherItemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final TeacherItemListActivity.SimpleItemRecyclerViewAdapter.ViewHolder holder, int position) {

            holder.mContentView.setText(mValues.get(position).content);
            holder.mIdView.setText(mValues.get(position).details);
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            final TextView mContentView;


            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.teacher_content_detail);
                mContentView = (TextView) view.findViewById(R.id.teacher_content_title);
            }
        }
    }
}
