package com.example.android.sunshine.app;

/**
 * Created by A.K ABHI on 09-02-2016.
 */
public class Post {
    public int userId;
    public int id;
    public String title;
    public String body;

    public Post(int userId,int id, String title,String body){
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;

    }
}
