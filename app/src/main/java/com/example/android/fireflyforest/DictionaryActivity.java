package com.example.android.fireflyforest;

/**
 * Created by Lyra Malfoy on 2018/4/17.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

/*
* 把所有的findViewById 全部放在了initial()方法中，方便调用
* 这个Activity 类的主要目的就是给各个按钮设置监听器，开启查询单词的线程
**/
public class DictionaryActivity extends Activity {

    public ImageButton imageBtnDictSerach = null;
    public ImageButton imageBtnDictDelteEditText = null;
    public ImageButton imageBtnUserCenter = null;
    public Button submitButton = null;
    public SwipeRefreshLayout swipeRefresh;

    public EditText editTextDictSearch = null;
    public EditText editTextToSubmit = null;

    public Dictionary dict = null;

    public Word w = new Word();
    ArrayList<Word> words = new ArrayList<Word>();

    public static String searchedWord = null;

    public Handler dictHandler = null;
    public SocketHandler conn = new SocketHandler();

    private SharedPreferences pref = null;
    private String opUser = null;

    private int answer1 = 0, answer2 = 0;

    // 初始化函数，将界面上所有的模块与对应对象相连接
    public void initial() {
        imageBtnDictSerach = (ImageButton) findViewById(R.id.image_btn_magnifier);
        imageBtnDictDelteEditText = (ImageButton) findViewById(R.id.image_btn_delete);
        imageBtnUserCenter = (ImageButton) findViewById(R.id.user_center);
        submitButton = (Button) findViewById(R.id.submit_button);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        editTextDictSearch = (EditText) findViewById(R.id.edit_text_dictionary);
        editTextDictSearch.setOnEditorActionListener(new EditTextDictEditActionIs());

        editTextToSubmit = (EditText) findViewById(R.id.edit_text_to_submit);
        editTextToSubmit.setOnEditorActionListener(new EditTextUserParaActionIs());

        dict = new Dictionary(DictionaryActivity.this, "dict");
        dictHandler = new Handler(Looper.getMainLooper());

        pref = getSharedPreferences("userData", Context.MODE_PRIVATE);
        opUser = pref.getString("name", "");

        // 对searchedWord 进行初始化
        Intent intent = this.getIntent();
        searchedWord = intent.getStringExtra("word");
        if (searchedWord == null)
            searchedWord = "";

    }

    // 点击监听
    public void setOnClickLis() {
        imageBtnDictDelteEditText.setOnClickListener(new IBDictDeleteEditTextClickIs());
        imageBtnDictSerach.setOnClickListener(new IBDictSearchClickIs());
        imageBtnUserCenter.setOnClickListener(new IBUserCenterClickIs());
        submitButton.setOnClickListener(new submitButtonClickis());
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItemInterface();
            }
        });
    }

    // 主函数，包括初始化函数的调用、监听函数调用和线程的创建
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_interface);

        initial();
        setOnClickLis();

    }

    // 连接服务器或者金山词霸API 来获取单词释义，并对原始数据进行处理
    public class DictSearchWord implements Runnable {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            String[] box = new String[5];
            Word[] wordArr = new Word[100];
            String currentUserName = null, currentWord = null, currentUserInterpret = null, currentUserLikesCounter = null, currentUserHateCounter = null;

            // 连接服务器并查询
            conn.searchWord(searchedWord);
            try {
                // 获取服务器的数据
                String[] temp = (String[]) conn.searchWordResponse().clone();

                // 服务器的数据库内没有相应的解释，从网络上获取解释
                if (temp[0] == null) {
                    System.out.println(searchedWord);
                    if ((w = dict.getWordFromInternet(searchedWord)) == null || w.getWord().equals("")) {
                        return;
                    } else {
                        words.clear();

                        // 从网络获取的词典信息
                        String allInterpret = w.getInterpret();
                        String[] singerInterpret = allInterpret.split("；|，");
                        String sentMsg = "";

                        for (int i = 0; i < singerInterpret.length; i++) {
                            w.setUserName("kingsoft");
                            w.setWord(searchedWord);
                            w.setInterpret(singerInterpret[i]);
                            w.setLikes("0");
                            w.setHates("0");
                            wordArr[i] = w;

                            // 将单词信息进行存放，便于后续发给服务器
                            sentMsg = "kingsoft" + "|" + searchedWord + "|" + singerInterpret[i] + "|" + "0" + "|" + "0" + ";" + sentMsg;
                            w = new Word();
                        }
                        dictHandler.post(new RunnableDictSetTextInterface(wordArr));

                        // 发送给服务器
                        conn.sendInterpretMsg(sentMsg);
                    }
                }
                // 服务器上有解释，获取服务器上的数据并展示
                else {
                    System.out.println("数据来自服务器");
                    words.clear();

                    for (int i = 0; i < temp.length; i++) {
                        box = temp[i].split("\\|");
                        currentUserName = box[0];
                        currentWord = box[1];
                        currentUserInterpret = box[2];
                        currentUserLikesCounter = box[3];
                        currentUserHateCounter = box[4];

                        w.setUserName(currentUserName);
                        w.setWord(currentWord);
                        w.setInterpret(currentUserInterpret);
                        w.setLikes(currentUserLikesCounter);
                        w.setHates(currentUserHateCounter);

                        wordArr[i] = w;
                        w = new Word();
                    }
                    dictHandler.post(new RunnableDictSetTextInterface(wordArr));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 将处理过的单词释义显示在界面上
    public class RunnableDictSetTextInterface implements Runnable {
        ArrayList<Word> words = new ArrayList<Word>();
        Word[] wArray = null;

        public RunnableDictSetTextInterface(Word[] wordArray) {
            super();
            wArray = wordArray.clone();
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (wArray == null || wArray.length == 0)
                return;
            else {
                for (int i = 0; wArray[i] != null; i++) {
                    String currentUserName = wArray[i].getUserName();
                    String currentUserInterpret = wArray[i].getInterpret();
                    String currentWord = wArray[i].getWord();
                    String currentUserLikesCounter = wArray[i].getLikes();
                    String currentUserHateCounter = wArray[i].getHates();

                    words.add(new Word(currentUserName, currentWord, currentUserInterpret,
                            currentUserLikesCounter, currentUserHateCounter, R.drawable.like, R.drawable.dislike));
                }
                DictionaryAdapter adapter = new DictionaryAdapter(DictionaryActivity.this, words);
                ListView listView = (ListView) findViewById(R.id.list);
                listView.setAdapter(adapter);
            }
        }
    }

    // 用户进行下拉刷新
    public void refreshItemInterface() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 睡眠2s，为了显示出效果
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }//end for catch

                // 运行该线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new RefreshShow()).start();

                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }//end for run
        }).start();//end for thread
    }

    class RefreshShow implements Runnable{
        String[] box = new String[5];
        Word[] wordArr = new Word[100];
        String currentUserName = null, currentWord = null, currentUserInterpret = null, currentUserLikesCounter = null, currentUserHateCounter = null;

        public void run(){
            conn.searchWord(searchedWord);
            try {
                String[] temp = conn.searchWordResponse().clone();
                words.clear();
                for (int i = 0; i < temp.length; i++) {
                    box = temp[i].split("\\|");
                    currentUserName = box[0];
                    currentWord = box[1];
                    currentUserInterpret = box[2];
                    currentUserLikesCounter = box[3];
                    currentUserHateCounter = box[4];

                    w.setUserName(currentUserName);
                    w.setWord(currentWord);
                    w.setInterpret(currentUserInterpret);
                    w.setLikes(currentUserLikesCounter);
                    w.setHates(currentUserHateCounter);

                    wordArr[i] = w;
                    w = new Word();
                }
                dictHandler.post(new RunnableDictSetTextInterface(wordArr));
                System.out.println("word size:" + words.size());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
    // 监听单词输入栏
    class EditTextDictEditActionIs implements OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
            // TODO Auto-generated method stub
            if (arg1 == EditorInfo.IME_ACTION_SEARCH || arg2 != null && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                startSearch();
                return true;
            }
            return false;
        }
    }

    // 监听用户释义输入栏
    class EditTextUserParaActionIs implements OnEditorActionListener {

        @Override
        public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
            // TODO Auto-generated method stub
            if (arg1 == EditorInfo.IME_ACTION_DONE || arg2 != null && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                startSearch();
                return true;
            }
            return false;
        }
    }

    // 监听搜索图标按钮
    class IBDictSearchClickIs implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            startSearch();
        }
    }

    // 监听删除输入内容图标按钮
    class IBDictDeleteEditTextClickIs implements OnClickListener {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            editTextDictSearch.setText("");
        }
    }

    // 监听进入用户中心图标按钮
    class IBUserCenterClickIs implements OnClickListener {
        public void onClick(View arg0) {
            Intent UserCenterIntent = new Intent(DictionaryActivity.this, UserCenterActivity.class);
            startActivity(UserCenterIntent);
        }
    }

    // 监听用户编写释义的提交按钮
    class submitButtonClickis implements OnClickListener {
        public void onClick(View arg0) {
            new Thread(new submitUserParaphrase()).start();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(editTextToSubmit.getWindowToken(), 0);
            }
        }
    }

    // 开始在数据库和网络上搜索顶部输入框中输入的单词
    public void startSearch() {
        // 提取输入栏中输入的内容
        String str = editTextDictSearch.getText().toString();
        if (str == null || str.equals(""))
            return;
        searchedWord = str;

        new Thread(new DictSearchWord()).start();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(editTextDictSearch.getWindowToken(), 0);
        }
    }

    // 用户提交释义处理线程
    public class submitUserParaphrase implements Runnable {
        public void run() {
            // 提取输入栏中输入的内容
            String userParaphrase = editTextToSubmit.getText().toString();

            if (userParaphrase == null || userParaphrase.equals("")) {
                Looper.prepare();
                Toast.makeText(DictionaryActivity.this, getString(R.string.empty_submit), Toast.LENGTH_SHORT).show();
                Looper.loop();
            } else {
                // 获取当前登录用户的用户名
                SharedPreferences pref = getSharedPreferences("userData", MODE_PRIVATE);
                String submitUser = pref.getString("name", "");
                String sentInfo = submitUser + "|" + searchedWord + "|" + userParaphrase + "|" + "0" + "|" + "0";

                conn.sendInterpretMsg(sentInfo);
                try {
                    int response = conn.receiveInterpretResponse();
                    // 服务器返回303，用户提交的释义已经存在，提交失败；
                    // 服务返回202，释义提交成功
                    if (response == 301) {
                        Looper.prepare();
                        Toast.makeText(DictionaryActivity.this, getString(R.string.trans_already_exist), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else if (response == 202) {
                        Looper.prepare();
                        Toast.makeText(DictionaryActivity.this, getString(R.string.submit_success), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
