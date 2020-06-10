package com.example.fyp1;

public class itemdatabase {
    String itemname;
    String id;
    String userid;

    public itemdatabase() {
    }

    public itemdatabase(String itemname, String id, String userid) {
        this.itemname = itemname;
        this.id = id;
        this.userid = userid;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
