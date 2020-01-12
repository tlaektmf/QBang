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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.dummy.AlarmItem;
import com.example.visualmath.dummy.DummyContent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherItemListActivity extends AppCompatActivity {
    private View recyclerView;
    private ViewGroup layout;
    public List<AlarmItem> alarms; //알람뿌려줄 데이터 리스트
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    private boolean mTwoPane;
    public static String TAG="TeacherItemList";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_item_list);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        initData();

        init();



//
//        //** detailView 클릭 이벤트
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Intent intent = new Intent(getApplicationContext(), VM_FullViewActivity.class);
//                startActivity(intent);
////                finish();
//            }
//        });

    }


    public void activateList(View view) {

        // 메뉴 리스트 활성 비활성
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.drawer_menu);
        View clickview = findViewById(R.id.clickview);

        if(layout.getVisibility()==View.VISIBLE){
            //현재 뷰가 보이면
            layout.setVisibility(View.GONE);
            clickview.setVisibility(View.INVISIBLE);
        }else{
            //뷰가 보이지 않으면
            layout.setVisibility(View.VISIBLE);
            clickview.setVisibility(View.VISIBLE);
            clickview.setClickable(true);
        }
    }

    public void init(){
        recyclerView = findViewById(R.id.teacher_item_list);
        layout = (ViewGroup) findViewById(R.id.teacher_item_detail_container);

        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, alarms,mTwoPane));
    }



    /**
     * class SimpleItemRecyclerViewAdapter
     */
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final TeacherItemListActivity mParentActivity;
        private final List<AlarmItem> mAlarms; //알람뿌려줄 데이터 리스트
        private final boolean mTwoPane;

        /**
         * 알람 종류에 따라서 각기 다른 Activity로 이동해야됨
         */
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"클릭함");
                AlarmItem item = (AlarmItem)view.getTag();

                if(mTwoPane){//태블릿 모드 -> 태블릿에서 작동함
                    Bundle arguments = new Bundle();
                    arguments.putString(TeacherItemDetailFragment.ARG_ITEM_ID, item.getId()); //** TeacherItemDetailFragment로 post_id 전달

                    //** TeacherItemDetailFragment 프래그먼트 생성
                  TeacherItemDetailFragment fragment = new TeacherItemDetailFragment();
                    fragment.setArguments(arguments);//** 프래그먼트 초기 세팅
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.teacher_item_detail_container, fragment)
                            .commit();
                }else{
                    //안드로이드 폰 모드
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getId());
                    TeacherItemDetailFragment fragment = new TeacherItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.teacher_item_detail_container, fragment)
                            .commit();
                }

            }
        };

        SimpleItemRecyclerViewAdapter(TeacherItemListActivity parent,
                                      List<AlarmItem> items,boolean twoPane) {
            mAlarms=items;
            mParentActivity = parent;
            mTwoPane = twoPane;

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.teacher_item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            holder.mDetailView.setText(mAlarms.get(position).getDetails());
            holder.mTitleView.setText(mAlarms.get(position).getTitle());

            holder.itemView.setTag(mAlarms.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);

        }

        @Override
        public int getItemCount() {
            return mAlarms.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mDetailView;
            final TextView mTitleView;


            ViewHolder(View view) {
                super(view);
                mDetailView = (TextView) view.findViewById(R.id.teacher_content_detail);
                mTitleView = (TextView) view.findViewById(R.id.teacher_content_title);
            }
        }
    }
    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void initData(){
        alarms=new ArrayList<AlarmItem>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        reference=firebaseDatabase.getReference("STUDENTS");
        reference=reference.child("user_name")
                .child("posts").child("unsolved");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String post_id=snapshot.getKey();
                    String post_title=snapshot.child("title").getValue().toString();
                    alarms.add(new AlarmItem(post_id,post_title,VM_ENUM.SOLVED));

                    ///Log.d(TAG, "ValueEventListener : " +post_id );

                }
                setupRecyclerView((RecyclerView) recyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getBaseContext(),"데이터베이스 오류",Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        });
    }

}
