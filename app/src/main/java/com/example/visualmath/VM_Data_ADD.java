package com.example.visualmath;

import java.io.Serializable;

/**
 * 1. 사진 url 최대 3장
 * 2. 텍스트 1개
 */

public class VM_Data_ADD implements Serializable {

    private String detail; //질문 사항

    public VM_Data_ADD(){

    }

    public VM_Data_ADD(String _detail){
        this.detail=_detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String _detail) {
        this.detail = _detail;
    }




}
