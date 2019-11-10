package com.example.visualmath;

import android.net.Uri;

public class VM_Data_EXTRA {
    private String content;
    private Uri add_picture1;
    private Uri add_picture2;
    private Uri add_picture3;

    public VM_Data_EXTRA(VM_Data_ADD _vm_data_add){
        if(_vm_data_add!=null&&_vm_data_add.getDetail()!=null){
            content=_vm_data_add.getDetail();
        }

        if(_vm_data_add!=null){
            if(_vm_data_add.getFilePathElement(0)!=null){
                add_picture1=_vm_data_add.getFilePathElement(0);
            }else  if(_vm_data_add.getFilePathElement(1)!=null){
                add_picture2=_vm_data_add.getFilePathElement(1);
            }else  if(_vm_data_add.getFilePathElement(2)!=null){
                add_picture3=_vm_data_add.getFilePathElement(2);
            }
        }
    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Uri getAdd_picture1() {
        return add_picture1;
    }

    public void setAdd_picture1(Uri add_picture1) {
        this.add_picture1 = add_picture1;
    }

    public Uri getAdd_picture2() {
        return add_picture2;
    }

    public void setAdd_picture2(Uri add_picture2) {
        this.add_picture2 = add_picture2;
    }

    public Uri getAdd_picture3() {
        return add_picture3;
    }

    public void setAdd_picture3(Uri add_picture3) {
        this.add_picture3 = add_picture3;
    }
}
