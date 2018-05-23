package com.example.android.fireflyforest;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Lyra Malfoy on 2018/4/21.
 */

public class DictionaryAdapter extends ArrayAdapter<Word> {
    private SocketHandler con = new SocketHandler();
    public Handler dictHandler = new Handler(Looper.getMainLooper());
    private SharedPreferences pref = null;
    private int clickLike = 0, clickHate = 0;

    public DictionaryAdapter(Context context, ArrayList<Word> androidWord) {
        super(context, 0, androidWord);
        pref = context.getSharedPreferences("userData", Context.MODE_PRIVATE);
    }

    @NonNull
    public View getView(int position, @Nullable final View convertView, @NonNull ViewGroup parent) {
        //get the position of the object in the list
        final Word currentWord = getItem(position);

        //check if existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.user_contribution_item, parent, false);
        }

        final TextView userNickName = (TextView) listItemView.findViewById(R.id.user_nick_name);
        final TextView userContributePara = (TextView) listItemView.findViewById(R.id.user_contribute_para);
        final TextView userLikesCounter = (TextView) listItemView.findViewById(R.id.user_likes_counter);
        final TextView userHatesCounter = (TextView) listItemView.findViewById(R.id.user_hates_counter);
        final ImageView imageView = (ImageView) listItemView.findViewById(R.id.image_btn_like);
        final ImageView imageView1 = (ImageView) listItemView.findViewById(R.id.image_btn_hate);

        class refreshLikeCounter implements Runnable {
            int response = 0;

            public refreshLikeCounter(int response) {
                this.response = response;
            }
            public void run() {
                System.out.println("click like is:" + clickLike);
                if ((response == 204)) {
                    System.out.println("response is 204, likes counter is:" + currentWord.getLikes());
                    userLikesCounter.setText(String.valueOf(Integer.parseInt(currentWord.getLikes()) + 1));
                } else if ((response == 304) && (clickLike == 1)) {
                    System.out.println("response is 304, mars is 1, likes counter is:" + currentWord.getLikes());
                    userLikesCounter.setText(String.valueOf(Integer.parseInt(currentWord.getLikes()) - 1));
                } else if ((response == 304) && (clickLike > 1)) {
                    System.out.println("response is 304, mars is 1, likes counter is:" + currentWord.getLikes());
                    userLikesCounter.setText(String.valueOf(Integer.parseInt(currentWord.getLikes())));
                }
            }
        }

        class refreshHateCounter implements Runnable {
            int response = 0;

            public refreshHateCounter(int response) {
                this.response = response;
            }
            public void run() {
                System.out.println("click hate is:" + clickHate);
                if (response == 205) {
                    userHatesCounter.setText(String.valueOf(Integer.parseInt(currentWord.getHates()) + 1));
                } else if ((response == 305) && (clickHate == 1)) {
                    userHatesCounter.setText(String.valueOf(Integer.parseInt(currentWord.getHates()) - 1));
                } else if ((response == 305) && (clickHate > 1)) {
                    userHatesCounter.setText(String.valueOf(Integer.parseInt(currentWord.getHates())));
                }
            }
        }

        // 发送信息线程
        class updateLikeCounter implements Runnable {
            public void run() {
                // 获取当前单词的用户名
                assert currentWord != null;
                String targetName = currentWord.getUserName();
                // 获取该单词
                String word = currentWord.getWord();
                // 获取当前单词的用户解释
                String targetTrans = currentWord.getInterpret();
                // 获取点赞操作用户的等级
                int opUserLevel = pref.getInt("level", 0);
                // 获取点赞操作用户的姓名
                String opUser = pref.getString("name", "");

                int likeRes = -1;
                clickLike ++;

                // 获得用户点赞状态
                String checkMsg = opUser + "|" + word + "|" + targetTrans;
                System.out.println("checkMsg is:" + checkMsg);
                con.checkLikeOperation(checkMsg);

                try {
                    likeRes = con.receiveLikeOperation();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("like response is:" + likeRes);
                if (likeRes == 204) {
                    String sentUpdate = targetName + "|" + word + "|" + targetTrans + "|" + String.valueOf(opUserLevel + 1);
                    con.updateItemLikeCounter(sentUpdate);
                    dictHandler.post(new refreshLikeCounter(likeRes));

                } else if (likeRes == 304) {
                    String sentUpdate = targetName + "|" + word + "|" + targetTrans + "|" + String.valueOf(-(opUserLevel + 1));
                    con.updateItemLikeCounter(sentUpdate);
                    dictHandler.post(new refreshLikeCounter(likeRes));
                }
            }
        }

        // 发送信息线程
        class updateHateCounter implements Runnable {
            public void run() {
                // 获取当前单词的用户名
                assert currentWord != null;
                String targetName = currentWord.getUserName();
                // 获取该单词
                String word = currentWord.getWord();
                // 获取当前单词的用户解释
                String targetTrans = currentWord.getInterpret();
                // 获取点赞操作用户的等级
                int opUserLevel = pref.getInt("level", 0);
                // 获取点赞操作用户的姓名
                String opUser = pref.getString("name", "");

                String checkMsg = opUser + "|" + word + "|" + targetTrans;
                con.checkHateOperation(checkMsg);

                int hateRes = -1;
                clickHate ++;

                try {
                    hateRes = con.receiveHateOperation();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("hate response is:" + hateRes);
                if (hateRes == 205) {
                    dictHandler.post(new refreshHateCounter(hateRes));
                    String sentUpdate = targetName + "|" + word + "|" + targetTrans + "|" + String.valueOf(opUserLevel + 1);
                    con.updateItemHateCounter(sentUpdate);

                } else if (hateRes == 305) {
                    dictHandler.post(new refreshHateCounter(hateRes));
                    String sentUpdate = targetName + "|" + word + "|" + targetTrans + "|" + String.valueOf(-(opUserLevel + 1));
                    con.updateItemHateCounter(sentUpdate);
                }
            }
        }

        // 不同按钮按下的监听事件选择
        View.OnClickListener mListener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.image_btn_like:
                        new Thread(new updateLikeCounter()).start();
                        break;
                    case R.id.image_btn_hate:
                        new Thread(new updateHateCounter()).start();
                        break;
                }
            }
        };

        // 每一项信息显示部分
        assert currentWord != null;
        userNickName.setText(currentWord.getUserName());

        userContributePara.setText(currentWord.getInterpret());

        userLikesCounter.setText(currentWord.getLikes());

        imageView.setImageResource(currentWord.getLikeImageResourceID());
        imageView.setVisibility(View.VISIBLE);
        imageView.setOnClickListener(mListener);

        userHatesCounter.setText(currentWord.getHates());

        imageView1.setImageResource(currentWord.getDislikeImageResourceID());
        imageView1.setVisibility(View.VISIBLE);
        imageView1.setOnClickListener(mListener);

        return listItemView;
    }
}
