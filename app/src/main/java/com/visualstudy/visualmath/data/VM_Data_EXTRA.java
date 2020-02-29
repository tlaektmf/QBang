package com.visualstudy.visualmath.data;

public class VM_Data_EXTRA {
    private String content;
    private String add_picture1;
    private String add_picture2;
    private String add_picture3;

    public VM_Data_EXTRA(){

    }
    public VM_Data_EXTRA(VM_Data_ADD _vm_data_add){
        if(_vm_data_add!=null&&_vm_data_add.getDetail()!=null){
            content=_vm_data_add.getDetail();
        }

    }
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAdd_picture1() {
        return add_picture1;
    }

    public void setAdd_picture1(String add_picture1) {
        this.add_picture1 = add_picture1;
    }

    public String getAdd_picture2() {
        return add_picture2;
    }

    public void setAdd_picture2(String add_picture2) {
        this.add_picture2 = add_picture2;
    }

    public String getAdd_picture3() {
        return add_picture3;
    }

    public void setAdd_picture3(String add_picture3) {
        this.add_picture3 = add_picture3;
    }
}
