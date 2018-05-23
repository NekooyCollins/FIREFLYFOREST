package com.example.android.fireflyforest;

/**
 * Created by Lyra Malfoy on 2018/4/18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
* 用户忘记登录密码，进入密码重置界面
**/
public class ResetPwdActivity extends AppCompatActivity {
    private EditText mName;                        // 邮箱编辑
    private EditText mPwdNew;                       // 密码编辑
    private EditText mPwdCheck;                     // 密码编辑
    private Button mSureButton;                     // 确定按钮
    private Button mCancelButton;                   // 取消按钮
    private SocketHandler conn = new SocketHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_pwd_interface);
        mName = (EditText) findViewById(R.id.resetpwd_edit_name);
        mPwdNew = (EditText) findViewById(R.id.resetpwd_edit_password);
        mPwdCheck = (EditText) findViewById(R.id.resetpwd_edit_password_check);

        mSureButton = (Button) findViewById(R.id.resetpwd_btn_confirm);
        mCancelButton = (Button) findViewById(R.id.resetpwd_btn_cancel);

        // 注册界面两个按钮的监听事件
        mSureButton.setOnClickListener(m_resetpwd_Listener);
        mCancelButton.setOnClickListener(m_resetpwd_Listener);
    }

    // 不同按钮按下的监听事件选择
    View.OnClickListener m_resetpwd_Listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                // 确认按钮的监听事件
                case R.id.resetpwd_btn_confirm:
                    new Thread(new resetPwdCheck()).start();
                    break;
                // 取消按钮的监听事件,由注册界面返回登录界面
                case R.id.resetpwd_btn_cancel:
                    Intent loginIntent = new Intent(ResetPwdActivity.this, MainActivity.class);    //切换Resetpwd Activity至Login Activity
                    startActivity(loginIntent);
                    finish();
                    break;
            }
        }
    };

    // 按下确认按钮的事件处理
    public class resetPwdCheck implements Runnable {
        public void run() {
            if (isUserNameAndPwdValid()) {
                String userName = mName.getText().toString().trim();
                String userPwdNew = mPwdNew.getText().toString().trim();
                String userPwdCheck = mPwdCheck.getText().toString().trim();

                // 两次密码输入不一样
                if (!userPwdNew.equals(userPwdCheck)) {
                    Toast.makeText(ResetPwdActivity.this, getString(R.string.pwd_not_the_same), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    String encodePwd = encryptSHA1(userPwdNew);
                    conn.sentNewPwd(userName + "|" + encodePwd);
                    try {
                        int flag = conn.receiveResetResponse();
                        if (flag == 404) {
                            Looper.prepare();
                            Toast.makeText(ResetPwdActivity.this, getString(R.string.resetpwd_fail), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        } else if (flag == 204) {
                            Looper.prepare();
                            Toast.makeText(ResetPwdActivity.this, getString(R.string.resetpwd_success), Toast.LENGTH_SHORT).show();
                            Looper.loop();

                            Intent loginIntent = new Intent(ResetPwdActivity.this, MainActivity.class);
                            startActivity(loginIntent);
                            finish();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // 密码合法性验证
    // 用户注册信息合法性验证
    public boolean isUserNameAndPwdValid() {
        if (mName.getText().toString().trim().equals("")) {
            Looper.prepare();
            Toast.makeText(this, getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        } else if (mPwdNew.getText().toString().trim().equals("")) {
            Looper.prepare();
            Toast.makeText(this, getString(R.string.pwd_empty), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        } else if (mPwdNew.getText().toString().trim().length() < 6) {
            Looper.prepare();
            Toast.makeText(this, getString(R.string.userpwd_length), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        } else if (mPwdCheck.getText().toString().trim().equals("")) {
            Looper.prepare();
            Toast.makeText(this, getString(R.string.pwd_check_empty), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        }
        return true;
    }

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

