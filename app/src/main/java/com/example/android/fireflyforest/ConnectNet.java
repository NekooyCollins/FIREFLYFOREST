package com.example.android.fireflyforest;

/**
 * Created by Lyra Malfoy on 2018/4/16.
 */

import android.util.Log;

import java.io.InputStream;
import java.net.URL;
import java.net.HttpURLConnection;

/* 连接网络类
* 这个类的功能就是根据给出的URL，从网络获得输入流，
* iCiBaURL1 和iCiBaURL2是用于构成查单词的URL的。
* iCiBaURL1+ 要查的单词+iCiBaURL2  就构成了金山查单词的URL
**/
public class ConnectNet {
    public final static String iCiBaURL1 = "http://dict-co.iciba.com/api/dictionary.php?w=";
    public final static String iCiBaURL2 = "&key=504E11AE1E4F568447C899039A25F685";
    private static final String LOG_TAG = ConnectNet.class.getSimpleName();

    public static InputStream getInputStreamByUrl(String urlStr) {
        InputStream tempInput = null;
        URL url = null;
        HttpURLConnection connection = null;

        try {
            url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(10000);

            // 如果连接网络成功（返回码为200 ），则开始接收数据
            if (connection.getResponseCode() == 200) {
                tempInput = connection.getInputStream();   //receive the response
            } else {
                Log.e(LOG_TAG, "Problems with network:" + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempInput;
    }
}
