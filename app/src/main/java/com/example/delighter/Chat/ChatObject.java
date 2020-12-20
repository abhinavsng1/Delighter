package com.example.delighter.Chat;

public class ChatObject {
    private String message;
    private boolean currentUser;
    public ChatObject(String message,Boolean currentUser){
        this.message=message;
        this.currentUser=currentUser;

    }

    public String getMessage(){ return message; }
    public Boolean getCurrentUser(){ return currentUser; }

    public void setCurrentUser(Boolean currentUser){ this.currentUser=currentUser;}
    public void setMessage(String message){ this.message=message;}
}
