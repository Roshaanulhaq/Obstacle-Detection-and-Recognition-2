package com.example.fyp1;

public class picturedatabase {
    String imageid;
    String itemid;
    String userid;
    String imageURI;
    String itemname;

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public picturedatabase() {
    }

    public picturedatabase(String imageid, String itemid, String userid) {
        this.imageid = imageid;
        this.itemid = itemid;
        this.userid = userid;
    }

    public String getImageid() {

        return imageid;
    }

    public void setImageid(String imageid)
    {
        this.imageid = imageid;
    }

    public String getItemid()
    {
        return itemid;
    }

    public void setItemid(String itemid) {

        this.itemid = itemid;
    }

    public String getUserid() {

        return userid;
    }

    public void setUserid(String userid)
    {
        this.userid = userid;
    }
}
