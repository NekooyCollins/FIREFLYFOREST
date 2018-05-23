package com.example.android.fireflyforest;

import android.content.SharedPreferences;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;

/**
 * Created by Lyra Malfoy on 2018/4/25.
 */

public class SocketHandler {
    java.net.Socket socket = null;
    private static final String ADDRESS = "192.168.199.121";

    // 发送用户登录信息
    public void sentUserLoginInfo(String userLoginInfo) {
        String dataType = "0";
        try {
            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            if(socket == null) {
                socket = new java.net.Socket(serverAddr, 51701);
                // 将信息通过这个对象来发送给Server
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                        true);

                // 把用户输入的内容发送给server
                out.println(dataType + "|" + userLoginInfo);
                Log.d("TCP", "C: Sending: '" + userLoginInfo + "'");
                out.flush();
            }
            else{
                // 将信息通过这个对象来发送给Server
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                        true);

                // 把用户输入的内容发送给server
                out.println(dataType + "|" + userLoginInfo);
                Log.d("TCP", "C: Sending: '" + userLoginInfo + "'");
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 接收服务器返回的登录信息。成功返回登录用户所有信息，不成功404
    public String receiveLoginResponse() throws IOException {
        InputStream is = socket.getInputStream();

        // 步骤2：创建输入流读取器对象 并传入输入流对象
        // 该对象作用：获取服务器返回的数据
        InputStreamReader isr = new InputStreamReader(is,"UTF-8");
        BufferedReader br = new BufferedReader(isr);

        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
        String response = br.readLine();

        return response;
    }

    // 用户注册
    public void sentUserRegisterInfo(String userRegisterInfo) {
        String dataType = "1";
        try {
            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: User Register Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new java.net.Socket(serverAddr, 51701);

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                    true);

            // 把用户输入的内容发送给server
            out.println(dataType + "|" + userRegisterInfo);
            Log.d("TCP", "C: Sending: '" + userRegisterInfo + "'");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 接收用户注册时服务器的返回码，201 表示注册成功，300 表示该用户已经被注册
    public int receiveRegisterResponse() throws IOException {
        InputStream is = socket.getInputStream();

        // 步骤2：创建输入流读取器对象 并传入输入流对象
        // 该对象作用：获取服务器返回的数据
        InputStreamReader isr = new InputStreamReader(is,"UTF-8");
        BufferedReader br = new BufferedReader(isr);

        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
        String response = br.readLine();
        int res = Integer.parseInt(response);
        System.out.println(res);

        return res;
    }

    // 用户搜索单词
    public void searchWord(String wordInfo) {
        String dataType = "2";
        try {
            if(wordInfo == null || wordInfo.equals("")) {
                return;
            }

            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new java.net.Socket(serverAddr, 51701);

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                    true);

            // 把用户输入的内容发送给server
            out.println(dataType + "|" + wordInfo);
            Log.d("TCP", "C: Sending: '" + wordInfo + "'");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 服务器返回单词信息
    public String[] searchWordResponse() throws IOException {
        String[] res = new String[100];
        InputStream is = socket.getInputStream();

        // 步骤2：创建输入流读取器对象 并传入输入流对象
        // 该对象作用：获取服务器返回的数据
        InputStreamReader isr = new InputStreamReader(is,"UTF-8");
        BufferedReader br = new BufferedReader(isr);

        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
        String response = br.readLine();
        System.out.println("length:"+response.length());
        if (response.equals("null"))
            return res;
        else {
            // 将得到的结果分隔开
            res = response.split(";");
            return res;
        }
    }

    // 用户发送释义
    public void sendInterpretMsg(String wordInfo) {
        String dataType = "3";

        /* 指定Server的IP地址，此地址为局域网地址，如果是使用WIFI上网，则为PC机的WIFI IP地址
         * 在ipconfig查看到的IP地址如下：
         * Ethernet adapter 无线网络连接:
         * Connection-specific DNS Suffix  . : IP Address. . . . . . . . . . . . : 192.168.1.100
         */
        try {
            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new java.net.Socket(serverAddr, 51701);

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                    true);

            // 把用户输入的内容发送给server
            out.println(dataType + "|" + wordInfo);
            Log.d("TCP", "C: Sending: '" + wordInfo + "'");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 服务器对于用户提交释义的应答
    public int receiveInterpretResponse() throws IOException {
        InputStream is = socket.getInputStream();

        // 步骤2：创建输入流读取器对象 并传入输入流对象
        // 该对象作用：获取服务器返回的数据
        InputStreamReader isr = new InputStreamReader(is,"UTF-8");
        BufferedReader br = new BufferedReader(isr);

        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
        String response = br.readLine();
        int res = Integer.parseInt(response);

        return res;
    }

    // 用户是否已经进行过赞同操作
    public void checkLikeOperation(String checkInfo){
        String dataType = "4";

        try {
            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new java.net.Socket(serverAddr, 51701);

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                    true);

            // 把用户输入的内容发送给server
            out.println(dataType + "|" + checkInfo);
            Log.d("TCP", "C: Sending: '" + checkInfo + "'");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int receiveLikeOperation() throws IOException{
        InputStream is = socket.getInputStream();

        // 步骤2：创建输入流读取器对象 并传入输入流对象
        // 该对象作用：获取服务器返回的数据
        InputStreamReader isr = new InputStreamReader(is,"UTF-8");
        BufferedReader br = new BufferedReader(isr);

        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
        String response = br.readLine();
        int res = Integer.parseInt(response);

        return res;
    }

    // 用户是否已经进行过反对操作
    public void checkHateOperation(String checkInfo){
        String dataType = "5";

        try {
            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new java.net.Socket(serverAddr, 51701);

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                    true);

            // 把用户输入的内容发送给server
            out.println(dataType + "|" + checkInfo);
            Log.d("TCP", "C: Sending: '" + checkInfo + "'");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int receiveHateOperation() throws IOException {
        InputStream is = socket.getInputStream();

        // 步骤2：创建输入流读取器对象 并传入输入流对象
        // 该对象作用：获取服务器返回的数据
        InputStreamReader isr = new InputStreamReader(is,"UTF-8");
        BufferedReader br = new BufferedReader(isr);

        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
        String response = br.readLine();
        int res = Integer.parseInt(response);

        return res;
    }

    // 更新点赞数
    public void updateItemLikeCounter(String updateInfo) {
        String dataType = "6";

        try {
            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new java.net.Socket(serverAddr, 51701);

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                    true);

            // 把用户输入的内容发送给server
            out.println(dataType + "|" + updateInfo);
            Log.d("TCP", "C: Sending: '" + updateInfo + "'");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateItemHateCounter(String updateInfo){
        String dataType = "7";

        try {
            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new java.net.Socket(serverAddr, 51701);

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                    true);

            // 把用户输入的内容发送给server
            out.println(dataType + "|" + updateInfo);
            Log.d("TCP", "C: Sending: '" + updateInfo + "'");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 发送重置密码信息
    public void sentNewPwd(String changeInfo){
        String dataType = "8";

        try {
            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new java.net.Socket(serverAddr, 51701);

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                    true);

            // 把用户输入的内容发送给server
            out.println(dataType + "|" + changeInfo);
            Log.d("TCP", "C: Sending: '" + changeInfo + "'");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 接收重置密码返回信息
    public int receiveResetResponse() throws IOException {
        InputStream is = socket.getInputStream();

        // 步骤2：创建输入流读取器对象 并传入输入流对象
        // 该对象作用：获取服务器返回的数据
        InputStreamReader isr = new InputStreamReader(is,"UTF-8");
        BufferedReader br = new BufferedReader(isr);

        // 步骤3：通过输入流读取器对象 接收服务器发送过来的数据
        String response = br.readLine();
        int res = Integer.parseInt(response);

        return res;
    }

    public void sentUserLogoutInfo(String userInfo){
        String dataType = "9";

        try {
            InetAddress serverAddr = InetAddress.getByName(ADDRESS);// TCPServer.SERVERIP
            Log.d("TCP", "C: Connecting...");

            // 应用Server的IP和端口建立Socket对象
            socket = new java.net.Socket(serverAddr, 51701);

            // 将信息通过这个对象来发送给Server
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(),"UTF-8")),
                    true);

            // 把用户输入的内容发送给server
            out.println(dataType + "|" + userInfo);
            Log.d("TCP", "C: Sending: '" + userInfo + "'");
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
