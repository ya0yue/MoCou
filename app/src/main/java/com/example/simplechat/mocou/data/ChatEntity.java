package com.example.simplechat.mocou.data;

/**
 * Created by YaoYue on 30/3/17.
 */

public class ChatEntity {
    private String originator;
    private String chatToName;
    private String chatContent;
    private String type;
    private String time;

    public ChatEntity(String originator, String chatToName,String type, String chatContent, String time) {
        this.originator = originator;
        this.chatToName = chatToName;
        this.type = type;
        this.chatContent = chatContent;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOriginator() {
        return originator;
    }

    public void setOriginator(String originator) {
        this.originator = originator;
    }

    public String getChatToName() {
        return this.chatToName;
    }

    public void setChatToName(String chatToName) {
        this.chatToName = chatToName;
    }

    public String getchatContent() {
        return this.chatContent;
    }

    public void setContent(String content) {
        this.chatContent = content;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
