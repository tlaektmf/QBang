package com.example.visualmath.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualmath.R;
import com.example.visualmath.VM_ENUM;
import com.example.visualmath.data.AlarmItem;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.data.VM_Data_CHAT;
import com.example.visualmath.fragment.ItemDetailFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference reference;
    public static String TAG = VM_ENUM.TAG;
    public View recyclerView;

    private ValueEventListener valueEventListener;

    //lhj_0
    private ProgressBar list_loading_bar;
    //lhj_0

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        recyclerView = findViewById(R.id.item_list);
        list_loading_bar = findViewById(R.id.list_loading_bar);

        // ActionBar 숨기기
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }


        Log.d(VM_ENUM.TAG,"[ItemListActivity] onCreate 호출");
        initData();


    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView,   List<AlarmItem> alarms) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, alarms, mTwoPane));
    }

    /**
     * itemListView 활성화 비활성화
     *
     * @param view
     */
    public void showItemList(View view) {
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.drawer_menu);
        View clickview = findViewById(R.id.student_clickview);
        if (layout.getVisibility() == View.VISIBLE) {
            //현재 뷰가 보이면
            layout.setVisibility(View.GONE);
            clickview.setVisibility(View.INVISIBLE);
        } else {
            //뷰가 보이지 않으면
            layout.setVisibility(View.VISIBLE);
            clickview.setVisibility(View.VISIBLE);
            clickview.setClickable(true);
        }
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<AlarmItem> mAlarms; //알람뿌려줄 데이터 리스트
        private final boolean mTwoPane;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlarmItem item = (AlarmItem) view.getTag();

                if (mTwoPane) {//태블릿 모드 -> 태블릿에서 작동함
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getId()); //** DetailFragment로 post_id 전달
                    arguments.putString(VM_ENUM.IT_ALARM_MESSAGE, item.getDetails());
                    //** DetailFragment 프래그먼트 생성
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments); //** 프래그먼트 초기 세팅
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else { //안드로이드 폰 모드

                    //ds.shim >start<
                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.getId());
                    arguments.putString(VM_ENUM.IT_ALARM_MESSAGE, item.getDetails());
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                    //ds.shim >end<
                }
            }
        };

        SimpleItemRecyclerViewAdapter(ItemListActivity parent,
                                      List<AlarmItem> items,
                                      boolean twoPane) {
            mAlarms = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
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
            if(mAlarms==null){
                return 0;
            }
            return mAlarms.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mDetailView;
            final TextView mTitleView;

            ViewHolder(View view) {
                super(view);
                mDetailView = (TextView) view.findViewById(R.id.detail);
                mTitleView = (TextView) view.findViewById(R.id.title);
            }
        }
    }

    /****
     * 데이터베이스 트랜젝션
     * write
     */
    public void initData() {

        //alarms = new ArrayList<AlarmItem>();
        firebaseDatabase = FirebaseDatabase.getInstance();


        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        String user = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가
        Log.d(VM_ENUM.TAG, "[학생 알람] " + user + " 의 데이터 접근");
        reference = firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
                .child(user)
                .child(VM_ENUM.DB_ALARMS);

        valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<AlarmItem>> t = new GenericTypeIndicator<List<AlarmItem>>() {};
                List<AlarmItem> alarms= new ArrayList<AlarmItem>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {//hash key를 돌면서 시작
                    alarms.add(snapshot.getValue(AlarmItem.class));
                    Log.d(TAG, "[학생 알람] ValueEventListener : " +snapshot.getKey() );
                }

                setupRecyclerView((RecyclerView) recyclerView,alarms);

                //lhj_3
                list_loading_bar.setVisibility(View.INVISIBLE);
                //lhj_3
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                ///Toast.makeText(getBaseContext(), "데이터베이스 오류", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "Failed to read value", databaseError.toException());
            }
        };




    }


    @Override
    protected void onStop() {
        //리스너 해제
        Log.d(TAG, "[ItemListActivity] onStop addValueEventListener  리스너 삭제");
        reference.removeEventListener(valueEventListener);

        super.onStop();

    }

    @Override
    protected void onDestroy() {//리스너 해제
        Log.d(TAG, "[ItemListActivity] onDestroy addValueEventListener  리스너 삭제");
        reference.removeEventListener(valueEventListener);

        super.onDestroy();

    }

    @Override
    protected void onStart() {
        Log.d(TAG, "[ItemListActivity] addValueEventListener 호출");
        reference.addValueEventListener(valueEventListener);

        super.onStart();
    }
}
