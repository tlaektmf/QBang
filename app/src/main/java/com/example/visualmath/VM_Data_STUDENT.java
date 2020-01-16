package com.example.visualmath;

public class VM_Data_STUDENT {
    private String s_id; //학생 id
    private String date; //가입 날짜 : SimpleDateFormat("yyyy-MM-dd");//사용할 포맷
    private int solve_problem;// 여태 푼 문제 수
   // private VM_Data_POST vm_data_post;

    public VM_Data_STUDENT(){

    }

    public VM_Data_STUDENT(String _s_id,String _date){
        this.s_id=_s_id;
        this.date=_date;
        this.solve_problem=0;
    }
    public String getS_id() {
        return s_id;
    }

    public void setS_id(String s_id) {
        this.s_id = s_id;
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
