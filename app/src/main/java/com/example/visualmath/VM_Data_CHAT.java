package com.example.visualmath;

public class VM_Data_CHAT {
    private String chatContent;
    private String sender;//보내는이 id
    private String receiver;//받는이 id
    private int who;
    private int profile;

    public VM_Data_CHAT(String sender, String chat, int who, int profile){
        this.sender = sender;
        this.chatContent = chat;
        this.who = who;
        this.profile=profile;

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

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public int getWho() {
        return who;
    }

    public void setWho(int who) {
        this.who = who;
    }
}
