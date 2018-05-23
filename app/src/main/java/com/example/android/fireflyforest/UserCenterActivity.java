package com.example.android.fireflyforest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Lyra Malfoy on 2018/5/6.
 */

public class UserCenterActivity extends AppCompatActivity {
    public TextView userName = null;
    public TextView userLevel = null;
    public ImageView goBack = null;
    public Button logout = null;
    private SharedPreferences pref = null;
    private String name = null;
    private int level = 0;

    public SocketHandler conn = new SocketHandler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info_interface);

        userName = (TextView) findViewById(R.id.name);
        userLevel = (TextView) findViewById(R.id.level);
        goBack = (ImageView) findViewById(R.id.go_back);
        logout = (Button) findViewById(R.id.logout);

        pref = getSharedPreferences("userData", MODE_PRIVATE);
        name = pref.getString("name", "");
        level = pref.getInt("level", 0);

        userName.setText(name);
        userLevel.setText(String.valueOf(level));
        goBack.setOnClickListener(mListener);
        logout.setOnClickListener(mListener);
    }

    View.OnClickListener mListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    Intent DictIntent = new Intent(UserCenterActivity.this, DictionaryActivity.class);
                    startActivity(DictIntent);
                    break;

                case R.id.logout:
                    new Thread(new UserLogout()).start();
                    break;
            }
        }
    };

    class UserLogout implements Runnable {
        public void run() {
            SharedPreferences pref = getSharedPreferences("userData", MODE_PRIVATE);

            // 获取用户名，然后发送给服务器端
            String user = pref.getString("name", "");
            conn.sentUserLogoutInfo(user);

            // 清空SharedPreferences
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.apply();

            // 页面跳转
            Intent LoginIntent = new Intent(UserCenterActivity.this, MainActivity.class);
            startActivity(LoginIntent);
        }
    }
}
