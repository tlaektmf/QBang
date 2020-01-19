package com.example.visualmath;

public class VM_Data_TEACHER {
    private String t_id; //선생님 고유 id
    private String date; //가입 날짜 : SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);//사용할 포맷
    private int solve_problem;// 여태 푼 문제 수

    public VM_Data_TEACHER(){

    }
    public VM_Data_TEACHER(String _t_id,String _date){
        this.t_id=_t_id;
        this.date=_date;
        this.solve_problem=0;
    }
    public String getT_id() {
        return t_id;
    }

    public void setT_id(String t_id) {
        this.t_id = t_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getSolve_problem() {
        return solve_problem;
    }

    public void setSolve_problem(int solve_problem) {
        this.solve_problem = solve_problem;
    }
}
