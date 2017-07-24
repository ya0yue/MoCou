package com.example.simplechat.mocou.data;

/**
 * Created by YaoYue on 30/3/17.
 */

public class DialogEntity {

    private String dialogId;
    private String originator;
    private String toUsername;
    private String type;
    private String content;
    private String time;
    private Boolean isMine;
    private Boolean isReceived;
    private Boolean isSent;

    public DialogEntity(String dialogId,String originator, String toUsername, String type, String content, String time, boolean isReceived, boolean isMine, boolean isSent) {
        this.originator = originator;
        this.dialogId=dialogId;
        this.toUsername = toUsername;
        this.type = type;
        this.content = content;
        this.time = time;
        this.isMine = isMine;
        this.isReceived=isReceived;
        this.isSent=isSent;
    }


    public Boolean getSent() {
        return isSent;
    }

    public void setSent(Boolean sent) {
        isSent = sent;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean mine) {
        isMine = mine;
    }

    public boolean isReceived() {
        return isReceived;
    }

    public void setReceived(boolean received) {
        isReceived = received;
    }

    public String getDialogId() {
        return dialogId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getToUsername() {
        return this.toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
