package com.threefourfive.meet;

/**
 * Created by joel on 6/1/16.
 */
public class Scoped_Profile {

    String app_scoped_id;
    String photoURL;
    int mutual_likes;
    int mutual_friends;

    public Scoped_Profile(String app_scoped_id, String photoURL, int mutual_likes, int mutual_friends) {
        this.app_scoped_id = app_scoped_id;
        this.photoURL = photoURL;
        this.mutual_likes = mutual_likes;
        this.mutual_friends = mutual_friends;
    }

    public String getApp_scoped_id() {
        return app_scoped_id;
    }

    public void setApp_scoped_id(String app_scoped_id) {
        this.app_scoped_id = app_scoped_id;
    }

    public int getMutual_likes() {
        return mutual_likes;
    }

    public void setMutual_likes(int mutual_likes) {
        this.mutual_likes = mutual_likes;
    }

    public int getMutual_friends() {
        return mutual_friends;
    }

    public void setMutual_friends(int mutual_friends) {
        this.mutual_friends = mutual_friends;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

}