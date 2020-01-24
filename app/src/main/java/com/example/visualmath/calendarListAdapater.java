package com.example.visualmath;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.visualmath.ui.dashboard.DashboardListFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class calendarListAdapater extends RecyclerView.Adapter<calendarListAdapater.ViewHolder>{

//    private Activity activity;
    private Fragment fragment;
//    private List<date_data> date_content;
    private List<date_data> date_content;
//    액티비티 대신 프래그먼트에서 사용
//    private MainActivity ac;
    private DashboardListFragment ac;
    private ArrayList<CalendarDay> dotted_date_list;

    public calendarListAdapater(Fragment fragment, List<date_data> date_content,ArrayList<CalendarDay> dotted_date_list){
        this.fragment=fragment;
        this.date_content=date_content;
        this.dotted_date_list=dotted_date_list;
    }

    @Override
    public int getItemCount() {
        return date_content.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        MaterialCalendarView cal;

        public ViewHolder(View itemView) {
            super(itemView);
            cal = itemView.findViewById(R.id.calendar_listitem);

//            캘린더 목록 클릭은 필요없을 듯
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Toast.makeText(view.getContext(), "달력 클릭 확인", Toast.LENGTH_SHORT).show();
//
//                }
//            });
            cal.setOnDateChangedListener(new OnDateSelectedListener() {
                @Override
                public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
//                    Toast.makeText(this,Integer.toString(date.getDay())+" 배열에 추가",Toast.LENGTH_LONG).show();
                    dotted_date_list.add(date);
                    cal.addDecorator(new EventDecorator(Color.RED, dotted_date_list));
                }
            });
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
        calendar_milli.set(Calendar.YEAR,one_data.getYear());
        calendar_milli.set(Calendar.MONTH,one_data.getMonth());
        calendar_milli.set(Calendar.DAY_OF_MONTH,one_data.getDay());
        long milliTime = calendar_milli.getTimeInMillis();

        Date date = new Date(milliTime);
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM",Locale.KOREA);
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd",Locale.KOREA);

        Integer year = Integer.parseInt(yearFormat.format(date));
        Integer month = Integer.parseInt(monthFormat.format(date));
        Integer day = Integer.parseInt(dayFormat.format(date));

//        holder.cal.setCurrentDate(one_data);
        CalendarDay calday = CalendarDay.from(year,month,day);
        holder.cal.setCurrentDate(calday);
    }
}
