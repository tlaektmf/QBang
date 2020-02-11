package com.example.visualmath.data;

public class VM_Data_CHAT {
    private String chatContent;//채팅내용
    private String sender;//보내는 사람 : student, teacher
    private String type;// text, video, image

    public VM_Data_CHAT(String sender, String chat, String type){
        this.sender = sender;
        this.chatContent = chat;
        this.type=type;

    }

    //>>>>여기까지



    public VM_Data_CHAT(){

    }

//    public VM_Data_CHAT(String sender, String chat){
//        this.sender = sender;
//        this.chatContent = chat;
//
//
//    }

    public String getChatContent() {
        return chatContent;
    }

    public void setChatContent(String chatContent) {
        this.chatContent = chatContent;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
