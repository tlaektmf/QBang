package com.example.visualmath.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualmath.fragment.ItemDetailFragment;
import com.example.visualmath.R;
import com.example.visualmath.activity.VM_FullViewActivity;
import com.example.visualmath.data.VM_Data_Default;

import java.util.ArrayList;
import java.util.List;

public class FilterAdapter extends RecyclerView.Adapter<FilterAdapter.FilterViewHolder>
    implements Filterable {

    Context context;
    List<Pair<VM_Data_Default, Pair<String,String>>> unFilteredlist;
    List<Pair<VM_Data_Default,Pair<String,String>>> FilteredList;

    public FilterAdapter(Context context, List<Pair<VM_Data_Default,Pair<String,String>>> list){
        super();
        this.context=context;
        this.unFilteredlist=list;
        this.FilteredList=list;
    }

    @NonNull
    @Override
    public FilterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_calendar,parent,false);
        return new FilterViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull FilterViewHolder holder, int position) {
        holder.mContentView.setText(FilteredList.get(position).first.getTitle());//내용(post_title)
        holder.itemView.setTag(FilteredList.get(position));

        //** Date 에서 시간만 표기 (시:분)
        String token=FilteredList.get(position).second.second.split(" ")[1];
        holder.mTimeView.setText(token);//시간
    }
    @Override
    public int getItemCount() {
        return FilteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter(){
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()){
                    FilteredList = unFilteredlist;
                }else{
                    List<Pair<VM_Data_Default,Pair<String,String>>> filteringList
                        = new ArrayList<Pair<VM_Data_Default,Pair<String,String>>>();

                    for(Pair<VM_Data_Default,Pair<String,String>> item:unFilteredlist){
                        if(item.first.getTitle().contains(charString)){
                            filteringList.add(item);
                        }
                    }
                    FilteredList=filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = FilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                FilteredList = (ArrayList<Pair<VM_Data_Default, Pair<String, String>>>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class FilterViewHolder extends RecyclerView.ViewHolder{
        final TextView mContentView;
        final TextView mTimeView;

        public FilterViewHolder(@NonNull View view) {
            super(view);

            mContentView = (TextView) view.findViewById(R.id.problem_name);
            mTimeView = (TextView) view.findViewById(R.id.endTime);


            //** 아이템 클릭 이벤트
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();

                    if (pos != RecyclerView.NO_POSITION) {
                        //** 프래그먼트의 아이템 클릭 시, FullViewActivity로 전환
                        Intent intent = new Intent(context, VM_FullViewActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, FilteredList.get(pos).second.first);
                        intent.putExtra(VM_FullViewActivity.ARG_ITEM_TITLE,FilteredList.get(pos).first.getTitle());
                        intent.putExtra(VM_FullViewActivity.ARG_ITEM_GRADE,FilteredList.get(pos).first.getGrade());
                        intent.putExtra(VM_FullViewActivity.ARG_ITEM_PROBLEM,FilteredList.get(pos).first.getProblem());
                        context.startActivity(intent);
                        Toast.makeText(v.getContext(), "확인" + pos, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }//뷰홀더 끝

}
