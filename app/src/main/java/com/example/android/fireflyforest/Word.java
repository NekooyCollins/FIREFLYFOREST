package com.example.android.fireflyforest;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Lyra Malfoy on 2018/4/16.
 */

/*
* 单词类：存放单词信息
**/
public class Word {
    public String user_name = null, word = null, interpret = null, likes = null, hates = null;
    private static final int NO_IMAGE = -1;
    private int likeImageResourceID = NO_IMAGE;
    private int dislikeImageResourceID = NO_IMAGE;

    public Word(String user_name, String word, String interpret, String likes, String hates, int likeImageResourceID, int dislikeImageResourceID) {
        super();
        this.user_name = "" + user_name;
        this.word = "" + word;
        this.interpret = "" + interpret;
        this.likes = likes;
        this.hates = hates;
        this.likeImageResourceID = likeImageResourceID;
        this.dislikeImageResourceID = dislikeImageResourceID;
    }

    // 防止空指针异常
    public Word() {
        super();
        this.user_name = "";
        this.word = "";
        this.interpret = "";
        this.likes = "";
        this.hates = "";
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getInterpret() {
        return interpret;
    }

    public void setInterpret(String interpret) {
        this.interpret = interpret;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getHates() {
        return hates;
    }

    public void setHates(String hates) {
        this.hates = hates;
    }

    public int getLikeImageResourceID(){
        return likeImageResourceID;
    }

    public int getDislikeImageResourceID(){
        return dislikeImageResourceID;
    }

}
