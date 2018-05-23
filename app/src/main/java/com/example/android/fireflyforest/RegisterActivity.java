package com.example.android.fireflyforest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Lyra Malfoy on 2018/4/17.
 */

/*
* 用户注册动作
**/
public class RegisterActivity extends AppCompatActivity {

    private EditText mName;                        // 邮箱编辑
    private EditText mPwd;                          // 密码编辑
    private EditText mPwdCheck;                     // 密码编辑
    private TextView mSignUpLink;                   // 确定按钮
    private TextView mCancelLink;                   // 退出注册
    public SocketHandler conn = new SocketHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_interface);
        mName = (EditText) findViewById(R.id.new_user_name);
        mPwd = (EditText) findViewById(R.id.new_user_password);
        mPwdCheck = (EditText) findViewById(R.id.new_user_repeat_password);
        mSignUpLink = (TextView) findViewById(R.id.new_user_register);
        mCancelLink = (TextView) findViewById(R.id.new_user_cancel_register);

        // 新用户注册监听事件
        mSignUpLink.setOnClickListener(m_register_Listener);
        mCancelLink.setOnClickListener(m_register_Listener);

    }

    View.OnClickListener m_register_Listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                // 确认按钮的监听事件
                case R.id.new_user_register:
                    new Thread(new Register()).start();
                    break;
                // 取消按钮的监听事件,由注册界面返回登录界面
                case R.id.new_user_cancel_register:
                    Intent LoginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(LoginIntent);
                    finish();
                    break;
            }
        }
    };

    // 按下注册按钮后，开始验证用户输入信息是否合法
    public class Register implements Runnable {

        public void run() {
            if (isUserNameAndPwdValid()) {
                String userName = mName.getText().toString().trim();
                String userPwd = mPwd.getText().toString().trim();
                String userPwdCheck = mPwdCheck.getText().toString().trim();
                int response = 0;

                // 两次输入密码不一致，给出提示文字
                if (!userPwd.equals(userPwdCheck)) {     // 两次密码输入不一样
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this, getString(R.string.pwd_not_the_same), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                    return;
                }

                String encodePwd = encryptSHA1(userPwd);
                conn.sentUserRegisterInfo(userName + "|" + encodePwd);
                try {

                    response = conn.receiveRegisterResponse();
                    System.out.println(response);
                    // 201 表示注册成功，300 表示该用户已经被注册
                    switch (response) {
                        case 201:
                            Intent LoginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(LoginIntent);
                            finish();
                            break;
                        case 300:
                            Looper.prepare();
                            Toast.makeText(RegisterActivity.this, getString(R.string.name_already_exist), Toast.LENGTH_SHORT).show();
                            Looper.loop();
                            break;
                        default:
                            System.out.println("无效信息.");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 用户注册信息合法性验证
    public boolean isUserNameAndPwdValid() {
        if (mName.getText().toString().trim().equals("")) {
            Looper.prepare();
            Toast.makeText(this, getString(R.string.name_empty), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        } else if (mPwd.getText().toString().trim().equals("")) {
            Looper.prepare();
            Toast.makeText(this, getString(R.string.pwd_empty), Toast.LENGTH_SHORT).show();
            Looper.loop();
            return false;
        } else if (mPwd.getText().toString().trim().length() < 6) {
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
