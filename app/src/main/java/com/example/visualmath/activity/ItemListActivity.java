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
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.data.VM_Data_CHAT;
import com.example.visualmath.fragment.ItemDetailFragment;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

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
    ///private ValueEventListener singleEventListener;
    private  String user;
    private String path;

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

    private void setupRecyclerView(@NonNull RecyclerView recyclerView,   List<Pair<String, AlarmItem>> alarms) {

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

    public  class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final ItemListActivity mParentActivity;
        private final List<Pair<String,AlarmItem>> mAlarms; //알람뿌려줄 데이터 리스트
        private final boolean mTwoPane;

        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Pair<String,AlarmItem> item = (Pair<String, AlarmItem>) view.getTag();

                if (mTwoPane) {//안드로이드 폰 모드
                    Log.d(VM_ENUM.TAG,"[ItemListActivity] 아이템 선택");
                    deleteAlarmData(item.first,item.second.getId());

                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.second.getId()); //** DetailFragment로 post_id 전달
                    arguments.putString(VM_ENUM.IT_ALARM_MESSAGE, item.second.getDetails());
                    //** DetailFragment 프래그먼트 생성
                    ItemDetailFragment fragment = new ItemDetailFragment();
                    fragment.setArguments(arguments); //** 프래그먼트 초기 세팅
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                } else {//태블릿 모드 -> 태블릿에서 작동함

                    //ds.shim >start<
                    //** Alarm 에서 삭제
                    //** 1. UNMATCHED 에서 삭제
                    Log.d(VM_ENUM.TAG,"[ItemListActivity] 아이템 선택");
                    deleteAlarmData(item.first,item.second.getId());

                    Bundle arguments = new Bundle();
                    arguments.putString(ItemDetailFragment.ARG_ITEM_ID, item.second.getId());
                    arguments.putString(VM_ENUM.IT_ALARM_MESSAGE, item.second.getDetails());
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
                                      List<Pair<String,AlarmItem>> items,
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
            holder.mDetailView.setText(mAlarms.get(position).second.getDetails());
            holder.mTitleView.setText(mAlarms.get(position).second.getTitle());

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

        firebaseDatabase = FirebaseDatabase.getInstance();


        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String mailDomain = currentUserEmail.split("@")[1].split("\\.")[0];
        user = currentUserEmail.split("@")[0] + "_" + mailDomain;//이메일 형식은 파이어베이스 정책상 불가
        Log.d(VM_ENUM.TAG, "[학생 알람] " + user + " 의 데이터 접근");
        reference = firebaseDatabase.getReference(VM_ENUM.DB_STUDENTS)
                .child(user)
                .child(VM_ENUM.DB_ALARMS);


        valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ///GenericTypeIndicator<List<AlarmItem>> t = new GenericTypeIndicator<List<AlarmItem>>() {};
                List<Pair<String,AlarmItem>> alarms= new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {//hash key를 돌면서 시작
                    alarms.add(Pair.create(snapshot.getKey(),snapshot.getValue(AlarmItem.class)));
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


        //** 알람 데이터 삭제 전, 데이터가 유효한지 검사 가능
//        singleEventListener=new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                Log.d(TAG, "[Alarm 에서 삭제완료]"+dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        };
//>> 알람 데이터 삭제 시, 데이터가 있는지를 확인 할 수 있음
    }

    public void deleteAlarmData(String key,String post_id){
        //>>알람 데이터 삭제전, 데이터가 유효한 지 확인하려면 코드 오픈
        ///reference.orderByKey().equalTo(key).addListenerForSingleValueEvent(singleEventListener);
        //>>>>
        reference.child(key).removeValue();
        Log.d(TAG, "[ItemListActivity]deleteAlarmData 데이터 삭제-> "+ key);

        //** 바로 위의 key 값을 가져옴
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        path=VM_ENUM.DB_STUDENTS+"/"+user+"/"+post_id;

        // 하위 데이터만 삭제 -> collection을 제거
        Log.d(TAG, "[ItemListActivity]collection을  삭제-> "+ path);
        db.collection(path).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                Log.d(TAG, "[firestore 데이터 삭제 성공]"+document.getId());
                                db.collection(path).document(document.getId()).delete();
                            }
                        } else {
                            Log.d(TAG, "Error => ", task.getException());
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "[firestore 데이터 삭제 실패]");
            }
        });




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
