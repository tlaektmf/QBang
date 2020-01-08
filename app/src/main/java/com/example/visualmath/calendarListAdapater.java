package com.example.visualmath;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualmath.ui.dashboard.DashboardListFragment;

import java.util.Calendar;
import java.util.List;

public class calendarListAdapater extends RecyclerView.Adapter<calendarListAdapater.ViewHolder>{

//    private Activity activity;
    private Fragment fragment;
    private List<date_data> date_content;
//    액티비티 대신 프래그먼트에서 사용
//    private MainActivity ac;
    private DashboardListFragment ac;

    public calendarListAdapater(Fragment fragment, List<date_data> date_content){
        this.fragment=fragment;
        this.date_content=date_content;
    }

    @Override
    public int getItemCount() {
        return date_content.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CalendarView cal;

        public ViewHolder(View itemView) {
            super(itemView);
            cal = (CalendarView) itemView.findViewById(R.id.calendar_listitem);

//            캘린더 목록 클릭은 필요없을 듯
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(activity, "click ", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cal_content, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // 재활용 되는 View가 호출, Adapter가 해당 position에 해당하는 데이터를 결합
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        date_data one_data = date_content.get(position);

        // 데이터 결합
        Calendar calendar_milli = Calendar.getInstance();
        calendar_milli.set(Calendar.YEAR,one_data.year);
        calendar_milli.set(Calendar.MONTH,one_data.month);
        calendar_milli.set(Calendar.DAY_OF_MONTH,one_data.day);
        long milliTime = calendar_milli.getTimeInMillis();

        holder.cal.setDate(milliTime);
    }
}
