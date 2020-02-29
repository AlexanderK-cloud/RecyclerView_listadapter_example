package com.example.recyclerview_listadapter_example;

public class Contact {
    private String mName;
    private boolean mOnline;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Contact(int id, String name, boolean online) {
        mName = name;
        mOnline = online;
        this.id= id;
    }

    public String getName() {
        return mName;
    }

    public boolean isOnline() {
        return mOnline;
    }

}