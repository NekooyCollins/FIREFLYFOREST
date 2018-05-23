package com.example.android.fireflyforest;

import android.content.SharedPreferences;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
* 主界面：用户登录界面
* 登录成功后转至单词查询界面，如果没有账户可以在此页面转至注册界面
* 如果用户忘记了密码，可以点击最下方的" 忘记密码？" 来进入密码重置界面
**/
public class MainActivity extends AppCompatActivity {
    private EditText mNameBox;
    private EditText mPwdBox;
    private TextView mRegisterLink;
    private TextView mLoginLink;
    private TextView mForgetPwdLink;
    public SocketHandler conn = new SocketHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_interface);

        // 通过id 找到相应的控件
        mNameBox = (EditText) findViewById(R.id.user_name);
        mPwdBox = (EditText) findViewById(R.id.user_password);
        mLoginLink = (TextView) findViewById(R.id.user_login);
        mRegisterLink = (TextView) findViewById(R.id.sign_up);
        mForgetPwdLink = (TextView) findViewById(R.id.user_forget_password);

        // 采用OnClickListener方法设置点击不同控件之后的监听事件
        mRegisterLink.setOnClickListener(mListener);
        mLoginLink.setOnClickListener(mListener);
        mForgetPwdLink.setOnClickListener(mListener);

    }

    // 不同按钮按下的监听事件选择
    OnClickListener mListener = new OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.sign_up:
                    Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerIntent);
                    finish();
                    break;
                case R.id.user_login:
                    new Thread(new Login()).start();
                    break;
                case R.id.user_forget_password:
                    Intent resetPwdIntent = new Intent(MainActivity.this, ResetPwdActivity.class);
                    startActivity(resetPwdIntent);
                    finish();
                    break;
            }
        }
    };

    // 用户登录，进行邮箱和密码的验证
    public class Login implements Runnable {
        public void run() {
            if (isUserNameAndPwdValid()) {
                // 获取当前输入的用户名和密码信息
                String userName = mNameBox.getText().toString().trim();
                String userPwd = mPwdBox.getText().toString().trim();
                String response = null;
                SharedPreferences.Editor editor = getSharedPreferences("userData", MODE_PRIVATE).edit();

                String encodePwd = encryptSHA1(userPwd);
                conn.sentUserLoginInfo(userName + "|" + encodePwd);
                try {
                    response = conn.receiveLoginResponse();
                    // null 表示用户输入信息有误

                    if (response.equals("null")) {
                        Looper.prepare();
                        Toast.makeText(MainActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    } else {
                        String[] userInfo = response.split("\\|");
                        System.out.println("user information is:" + userInfo[0] + "  " + userInfo[1]);
                        editor.putString("name", userInfo[0]);
                        editor.putInt("level", Integer.parseInt(userInfo[1]));
                        editor.apply();

                        Intent SearchIntent = new Intent(MainActivity.this, DictionaryActivity.class);
                        startActivity(SearchIntent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 用户名以及密码存在性验证
    public boolean isUserNameAndPwdValid() {
        if (mNameBox.getText().toString().trim().equals("")) {
            Looper.prepare();
            Toast.makeText(this, getString(R.string.username_not_exist), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        } else if (mPwdBox.getText().toString().trim().equals("")) {
            Looper.prepare();
            Toast.makeText(this, getString(R.string.userpwd_not_exist), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        }
        else if(mPwdBox.getText().toString().trim().length() < 6){
            Looper.prepare();
            Toast.makeText(this, getString(R.string.userpwd_length), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        }
        return true;
    }

    /**
     * SHA1加密
     *
     * @param data
     * @return
     */
    public static String encryptSHA1(String data) {
        String str = null;
        try {
            // 获得SHA 摘要算法的 MessageDigest 对象
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            // 使用指定的字节更新摘要
            sha1.update(data.getBytes());
            // 获得密文
            byte[] bytes = sha1.digest();
            // 字节数组转化为16进制字符串，方法内容在最后
            str = bytesToHexString(bytes);

        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 字节数组转化为16进制字符串
     *
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int temp = bytes[i] & 0xFF;// 与运算，将byte转化为整型
            String hex = Integer.toHexString(temp);// int型转化为16进制字符串
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

}
