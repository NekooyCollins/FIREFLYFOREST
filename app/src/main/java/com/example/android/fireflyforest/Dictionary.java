package com.example.android.fireflyforest;

/**
 * Created by Lyra Malfoy on 2018/4/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.nfc.Tag;
import android.util.Log;

import org.xml.sax.InputSource;

import java.io.InputStream;
import java.io.InputStreamReader;

/*
* 一个词典类Dictionary，这个类具有和词典一样的功能
* 在词典中查找一个单词，查找一个单词的翻译，从网络上下载某个单词的信息，将某个单词添加到词典中*/
public class Dictionary {
    private static final String TAG = "Dict Table";
    public Context context = null;
    public String tableName = null;

    // 这里要用到前面的DataBaseHelper类，在Dict的构造方法中实例化该类，
    // 并且调用下面两个方法获得dbR和dbW,用于完成对数据库的增删改查操作。
    // 这里吧dbR dbW作为成员变量目的是避免反复实例化dbR  dbW造成数据库指针泄露。
    public Dictionary(Context context, String tableName) {
        this.context = context;
        this.tableName = tableName;
    }

    // 从网络查找某个单词，并且返回一个含有单词信息的Word对象
    public Word getWordFromInternet(String searchedWord) {
        System.out.println("从网络上获取单词释义");
        Word word = null;
        String tempWord = searchedWord;
        if (tempWord == null && tempWord.equals(""))
            return null;
        InputStream in = null;
        try {
            String tempUrl = ConnectNet.iCiBaURL1 + tempWord + ConnectNet.iCiBaURL2;
            in = ConnectNet.getInputStreamByUrl(tempUrl);
            if (in != null) {
                XMLParser xmlParser = new XMLParser();
                InputStreamReader reader = new InputStreamReader(in, "utf-8");
                ContentHandler contentHandler = new ContentHandler();
                xmlParser.parseXML(contentHandler, new InputSource(reader));
                word = contentHandler.getWordValue();
                word.setWord(searchedWord);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return word;
    }

}
