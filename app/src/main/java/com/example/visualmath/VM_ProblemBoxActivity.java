package com.example.visualmath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visualmath.ui.dashboard.DashboardFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class VM_ProblemBoxActivity extends AppCompatActivity {

    private Button btn_unmatched;
    private Button btn_matched;

    //문제 목록을 보여줄 리사이클러뷰
    private RecyclerView recycler_view;
    ProblemListAdapter mAdapater;
    ArrayList<problem_item> mList = new ArrayList<problem_item>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vm__problem_box);

        init();
    }
    public void init(){
        btn_unmatched = findViewById(R.id.btn_unmatched);
        btn_matched = findViewById(R.id.btn_matched);
        recycler_view = findViewById(R.id.problem_recyclerview);

//        화면 초기 상태에서는 미완료 목록이 클릭된 상태
        btn_unmatched.setSelected(true);

//        리사이클러뷰에 객체 지정
        mAdapater = new ProblemListAdapter(mList);
        recycler_view.setAdapter(mAdapater);

//        레이아웃 매니저 생성 및 지정
        recycler_view.setLayoutManager(new LinearLayoutManager(this));

//        테스트용 아이템 추가
        addItem("테스트 문제 1번","2020-01-01",true);
        addItem("테스트 문제 2번","2020-01-02",true);
        addItem("테스트 문제 3번","2020-01-03",false);
    }

    //리사이클러뷰 테스트용 아이템 추가 함수
    public void addItem(String name, String date, Boolean live){
        problem_item item = new problem_item();

        item.setProblemName(name);
        item.setProblemDate(date);
        item.setProblemLive(live);

        mList.add(item);
    }

    //매치 미완료 목록 보여주기 탭(왼쪽)
    public void show_unmatched_list(View view){
        Toast.makeText(this, "매치 미완료 목록",Toast.LENGTH_LONG).show();
        btn_unmatched.setSelected(true);
        btn_matched.setSelected(false);
    }
    //매치 완료 목록 보여주기 탭(오른쪽)
    public void show_matched_list(View view){
        Toast.makeText(this, "매치 완료 목록",Toast.LENGTH_LONG).show();
        btn_unmatched.setSelected(false);
        btn_matched.setSelected(true);
    }

    //뒤로가기 버튼
    public void cancel(View view) {
        finish();
    }

    //리사이클러뷰 부분

    //데이터 클래스
    public class problem_item {
        private String problem_name;//문제 제목
        private String problem_date;//문제 날짜
        private Boolean problem_live;//문제 라이브인지 아닌지

        public void setProblemName(String problem_name){
            this.problem_name=problem_name;
        }
        public void setProblemDate(String problem_date){
            this.problem_date=problem_date;
        }
        public void setProblemLive(Boolean problem_live){
            this.problem_live=problem_live;
        }
        public String getProblemName(){
            return this.problem_name;
        }
        public String getProblemDate(){
            return this.problem_date;
        }
        public Boolean getProblemLive(){
            return this.problem_live;
        }
    }
    //데이터 클래스 끝

    //어댑터
    public class ProblemListAdapter extends RecyclerView.Adapter<ProblemListAdapter.ViewHolder>{
        private ArrayList<problem_item> mData = null;

        ProblemListAdapter(ArrayList<problem_item> list){
            mData = list;
        }

        @NonNull
        @Override
        public ProblemListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.item_list_notime,parent,false);
            ProblemListAdapter.ViewHolder viewHolder = new ProblemListAdapter.ViewHolder(view);

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull ProblemListAdapter.ViewHolder holder, int position) {
            problem_item item = mData.get(position);

            holder.pName.setText(item.getProblemName());
            holder.pDate.setText(item.getProblemDate());

            if(item.getProblemLive()){
                //라이브인 경우 이미지 표시
                holder.pLive.setVisibility(View.VISIBLE);
            }else{
                holder.pLive.setVisibility(View.INVISIBLE);
            }

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView pName;
            TextView pDate;
            ImageView pLive;

            ViewHolder(View itemView){
                super(itemView);
                pName = itemView.findViewById(R.id.problem_name);
                pDate = itemView.findViewById(R.id.problem_date);
                pLive = itemView.findViewById(R.id.problem_live);
            }
        }
    }
}

