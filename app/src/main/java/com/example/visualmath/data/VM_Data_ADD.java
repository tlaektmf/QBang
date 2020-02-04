

package com.example.visualmath.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 1. 사진 url 최대 3장 -> content provider 사용해야됨 (미정)
 * 2. 텍스트 1개
 * 3. 사진 url 최대 3장
 */

public class VM_Data_ADD  {

    private String detail; // 추가 질문 사항
    private Uri[] filePathList;



    public VM_Data_ADD(){
        filePathList=new Uri[3];
        for(int i=0;i<3;i++){
            filePathList[i]=null;
        }
        detail=null;
    }

    public Uri[] getFilePathList() {
        return filePathList;
    }

    public void setFilePathList(Uri[] filePathList) {
        this.filePathList = filePathList;
    }

    public void setFilePathElement(Uri _filePathElement,int _index){
        this.filePathList[_index]=_filePathElement;
    }
    public Uri getFilePathElement(int _index){
        return this.filePathList[_index];
    }



    public String getDetail() {
        return detail;
    }

    public void setDetail(String _detail) {
        this.detail = _detail;
    }

}








//package com.example.visualmath.data;
//
//import android.net.Uri;
//import android.os.Parcel;
//import android.os.Parcelable;
//
///**
// * 1. 사진 url 최대 3장 -> content provider 사용해야됨 (미정)
// * 2. 텍스트 1개
// * 3. 사진 url 최대 3장
// */
//
//public class VM_Data_ADD implements Parcelable {
//
//    private String detail; // 추가 질문 사항
//    private Uri[] filePathList;
//
//
//    protected VM_Data_ADD(Parcel in) {// writeToParcel()에 기록된 순서와 동일하게 복원
//        detail = in.readString();
//
//        Uri.Builder builder = new Uri.Builder();
//        int lenght =3;
//        filePathList=new Uri[lenght];
//
//        for(int i=0;i<lenght;i++){
//            filePathList[i]=(Uri)in.readParcelable(null);//Parcelable로 읽어서 Uri로 캐스트하여 데이터를 복원함
//        }
//
//    }
//
//    public static final Creator<VM_Data_ADD> CREATOR = new Creator<VM_Data_ADD>() {
//        @Override
//        public VM_Data_ADD createFromParcel(Parcel in) {
//
//            return new VM_Data_ADD(in);
//        }
//
//        @Override
//        public VM_Data_ADD[] newArray(int size) {
//            return new VM_Data_ADD[size];
//        }
//    };
//
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(detail);
//
//        // Uri는 Parcelable을 이용해서 기록
//        for(int i=0; i<3; i++){
//
//            //** 사진을 0번째부터 정렬하고 싶으면 이 코드 open
////            if(filePathList[i]!=null){
////                dest.writeParcelable(filePathList[i],flags);
////            }
//
//            dest.writeParcelable(filePathList[i],flags);
//
//        }
//
//    }
//
//
//    public VM_Data_ADD(){
//        filePathList=new Uri[3];
//        for(int i=0;i<3;i++){
//            filePathList[i]=null;
//        }
//        detail=null;
//    }
//
//    public Uri[] getFilePathList() {
//        return filePathList;
//    }
//
//    public void setFilePathList(Uri[] filePathList) {
//        this.filePathList = filePathList;
//    }
//
//    public void setFilePathElement(Uri _filePathElement,int _index){
//        this.filePathList[_index]=_filePathElement;
//    }
//    public Uri getFilePathElement(int _index){
//        return this.filePathList[_index];
//    }
//
//
//
//    public String getDetail() {
//        return detail;
//    }
//
//    public void setDetail(String _detail) {
//        this.detail = _detail;
//    }
//
//}
//
//
//
//
