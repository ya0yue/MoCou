package com.example.simplechat.mocou.data;

/**
 * Created by Larry on 4/7/17.
 */

public class ContactEntity {

    public ContactEntity(String originator, String username) {
        this.originator = originator;
        this.username = username;
    }

    public String originator;
    public String username;
}
