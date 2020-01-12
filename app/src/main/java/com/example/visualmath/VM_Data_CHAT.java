package com.example.visualmath;

public class VM_Data_CHAT {
    private String chatContent;//채팅내용
    private String sender;//보내는 사람 : student, teacher

    //**여기부터지움
    private String receiver;//받는이 id
    private int who;
    private int profile;

    public VM_Data_CHAT(String sender, String chat, int who, int profile){
        this.sender = sender;
        this.chatContent = chat;
        this.who = who;
        this.profile=profile;

    }

    //>>>>여기까지



    public VM_Data_CHAT(){

    }

    public VM_Data_CHAT(String sender, String chat){
        this.sender = sender;
        this.chatContent = chat;


    }

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


}
