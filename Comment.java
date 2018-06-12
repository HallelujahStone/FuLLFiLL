package io.haruya.fullfill;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by haruya.i on 2016/11/11.
 */

public class Comment extends Activity {

    String send_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteInsert.php";
    String delete_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteDelete.php";
    String select_http_comment = "http://fullfill.sakura.ne.jp/src/commentSelect.php";
    String insert_http_comment = "http://fullfill.sakura.ne.jp/src/commentInsert.php";//userid,kotobaid,comment本文

    ArrayList<NameValuePair> nameValue_select_c = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_insert_c = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_send_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_delete_f = new ArrayList<NameValuePair>();

    boolean[] flag_array = new boolean[50];//長さをおいおい調整

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment);

        final String kotoba, whose, users_name, favorited, commented, icon_addres, kotobaid;

        final String id_of_user = "1";//useridをとりあえず定義

        //MainActivityから引数の受け取り
        Intent intent = getIntent();
        kotoba = intent.getStringExtra("cl_data1");
        whose = intent.getStringExtra("cl_data2");
        users_name = intent.getStringExtra("cl_data3");
        favorited = intent.getStringExtra("cl_data4");
        commented = intent.getStringExtra("cl_data5");
        icon_addres = intent.getStringExtra("cl_data6");
        kotobaid = intent.getStringExtra("cl_data7");

        TextView co_kotoba = (TextView) findViewById(R.id.co_kotoba);
        TextView co_whose = (TextView) findViewById(R.id.co_whose);
        TextView co_users_name = (TextView) findViewById(R.id.co_users_name);
        final TextView co_favorited = (TextView) findViewById(R.id.co_how_favorited);
        TextView co_commented = (TextView) findViewById(R.id.co_how_commented);
        ImageView co_icon = (ImageView) findViewById(R.id.co_icon);

        final ImageView bookmark_sign = (ImageView) findViewById(R.id.co_bookmark_sign);//final必要


        URL url;
        InputStream istream;
        try {
            //画像のURLを直うち
            url = new URL("http://fullfill.sakura.ne.jp" + icon_addres);
            //インプットストリームで画像を読み込む
            istream = url.openStream();
            //読み込んだファイルをビットマップに変換
            Bitmap oBmp = BitmapFactory.decodeStream(istream);
            //ビットマップをImageViewに設定
            co_icon.setImageBitmap(oBmp);
            //インプットストリームを閉じる
            istream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        co_kotoba.setText(kotoba);
        co_users_name.setText(users_name);
        co_favorited.setText(favorited);
        co_commented.setText(commented);

        if(whose.equals("koreninamaehaarimasensen") || whose.equals("")){
            co_whose.setText("");
        }else{
            co_whose.setText("(" + whose + ")");
        }


        //ブックマーククリック時の処理
        final ImageView favorite = (ImageView) findViewById(R.id.co_favorited_icon);
        favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag_array[0] == false) {//ここ問題あり、おいおい訂正

                    //sendfavorite 送信確認済み ブックマークアイコンが押されたら実行
                    try {
                        nameValue_send_f.add(new BasicNameValuePair("userid", id_of_user));//とりあえず
                        nameValue_send_f.add(new BasicNameValuePair("kotobaid", kotobaid));

                        HttpClient httpclient_send_f = new DefaultHttpClient(null);
                        HttpPost httppost_send_f = new HttpPost(send_http_favorite); //
                        httppost_send_f.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                        httppost_send_f.setEntity(new UrlEncodedFormEntity(nameValue_send_f, "UTF-8"));
                        HttpResponse response = httpclient_send_f.execute(httppost_send_f);
                        //}
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }

                    ImageView fav = (ImageView) v.findViewById(R.id.co_favorited_icon);
                    AnimationSet set = new AnimationSet(true);
                    ScaleAnimation scale = new ScaleAnimation(1, 2f, 1, 2f);
                    set.addAnimation(scale);
                    TranslateAnimation translate = new TranslateAnimation(0, -20, 0, -30);
                    set.addAnimation(translate);
                    set.setDuration(300);
                    fav.startAnimation(set);

                    bookmark_sign.setImageResource(R.drawable.ic_bookmark_white_middle);
                    TranslateAnimation mark = new TranslateAnimation(0, 0, -70, 0);
                    mark.setDuration(300);
                    bookmark_sign.startAnimation(mark);

                    Toast toast = Toast.makeText(Comment.this, "ブックマークされました", Toast.LENGTH_SHORT);
                    toast.show();

                    int how_favo_i = Integer.parseInt(favorited) + 1;
                    String how_favo_s = String.valueOf(how_favo_i);
                    co_favorited.setText(how_favo_s);

                    flag_array[0] = true;
                } else {
                    try {
                        nameValue_delete_f.add(new BasicNameValuePair("userid", id_of_user));
                        nameValue_delete_f.add(new BasicNameValuePair("kotobaid", kotobaid));

                        HttpClient httpclient_delete_f = new DefaultHttpClient(null);
                        HttpPost httppost_delete_f = new HttpPost(delete_http_favorite); //
                        httppost_delete_f.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                        httppost_delete_f.setEntity(new UrlEncodedFormEntity(nameValue_delete_f, "UTF-8"));
                        HttpResponse response = httpclient_delete_f.execute(httppost_delete_f);
                        //}
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }

                    ImageView fav = (ImageView) v.findViewById(R.id.co_favorited_icon);
                    AnimationSet set = new AnimationSet(true);
                    ScaleAnimation scale = new ScaleAnimation(1, 2f, 1, 2f);
                    set.addAnimation(scale);
                    TranslateAnimation translate = new TranslateAnimation(0, -20, 0, -30);
                    set.addAnimation(translate);
                    set.setDuration(300);
                    fav.startAnimation(set);

                    TranslateAnimation mark = new TranslateAnimation(0, 0, 0, -70);
                    mark.setDuration(300);
                    bookmark_sign.startAnimation(mark);

                    Toast toast = Toast.makeText(Comment.this, "ブックマークが解除されました", Toast.LENGTH_SHORT);
                    toast.show();

                    co_favorited.setText(favorited);

                    flag_array[0] = false;

                    //300msずらせてimageviewをnullに
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bookmark_sign.setImageDrawable(null);
                        }
                    }, 300);
                }
            }
        });


        //以下コメントの内容を取得と表示----------------------------------------------------------------
        //コメントを表示するためのViewをここで定義しとく
        final LinearLayout commentLinear = (LinearLayout) this.findViewById(R.id.comment_boss_LinearLayout);
        commentLinear.removeAllViews();

        InputStream is;
        String result_select_c = "";

        try {
            HttpClient httpclient = new DefaultHttpClient(null);
            HttpPost httppost = new HttpPost(select_http_comment);

            httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost.setEntity(new UrlEncodedFormEntity(nameValue_select_c, "UTF-8"));
            //従来のsend_to_databaseの部分
            try {
                nameValue_select_c.add(new BasicNameValuePair("kotobaid", kotobaid));

                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.setEntity(new UrlEncodedFormEntity(nameValue_select_c, "UTF-8"));
                HttpResponse response = httpclient.execute(httppost);
                //}
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }
            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result_select_c = sb.toString();

            JSONArray jArray_select_c = new JSONArray(result_select_c);

            TextView comment_title = (TextView) findViewById(R.id.comment_title);
            int howmany_comment = jArray_select_c.length() - 1;
            comment_title.setText("　コメント欄(" + howmany_comment + ")");

            //JSONObjectとしてさっきのresult_select_cを要素ごとに取得
            for (int i = 0; i < jArray_select_c.length() - 1; i++) {
                JSONObject json_data_select_c = jArray_select_c.getJSONObject(i);
                String comment = json_data_select_c.getString("comment");
                String comment_username = json_data_select_c.getString("username");
                String comment_image = json_data_select_c.getString("image");

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.comment_list, null);

                ImageView icon = (ImageView) linearLayout.findViewById(R.id.icon_in_comment);
                TextView username = (TextView) linearLayout.findViewById(R.id.username_in_comment);
                TextView comment_text = (TextView) linearLayout.findViewById(R.id.comment_text);

                if(comment_username.equals("ANNA")) {//とりあえず
                    final String[] any_comment = new String[jArray_select_c.length()-1];
                    any_comment[i] = comment;
                    final String[] any_comment_username = new String[jArray_select_c.length()-1];
                    any_comment_username[i] = comment_username;
                    final String[] any_comment_image = new String[jArray_select_c.length()-1];
                    any_comment_image[i] = comment_image;
                    ImageView edit_button = (ImageView) linearLayout.findViewById(R.id.to_edit_button_in_comment);
                    edit_button.setImageResource(R.drawable.ic_create_white_18dp);
                    edit_button.setTag(i);
                    edit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int n = (Integer) v.getTag();

                            goto_comment_edit(any_comment[n], any_comment_username[n], any_comment_image[n], kotobaid);
                        }
                    });
                    //クリックリスナーつけて編集画面へ
                }
                username.setText(comment_username);
                comment_text.setText(comment);

                URL url2;
                InputStream istream2;
                try {
                    //画像のURLを直うち
                    url2 = new URL("http://fullfill.sakura.ne.jp" + comment_image);
                    //インプットストリームで画像を読み込む
                    istream2 = url2.openStream();
                    //読み込んだファイルをビットマップに変換
                    Bitmap oBmp = BitmapFactory.decodeStream(istream2);
                    //ビットマップをiconに設定
                    icon.setImageBitmap(oBmp);
                    //インプットストリームを閉じる
                    istream2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                commentLinear.addView(linearLayout, i);//commentlinearに載っける
            }
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        //ここからコメント入力と送信時の設定
        //username変更のボタンの設定
        FloatingActionButton sending_comment = (FloatingActionButton) findViewById(R.id.sending_comment);
        sending_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText comment_editting = (EditText) findViewById(R.id.comment_editting);

                SpannableStringBuilder str1 = (SpannableStringBuilder) comment_editting.getText();
                String some_comment = str1.toString();

                try {
                    nameValue_insert_c.add(new BasicNameValuePair("userid", id_of_user));
                    nameValue_insert_c.add(new BasicNameValuePair("kotobaid", kotobaid));
                    nameValue_insert_c.add(new BasicNameValuePair("comment", some_comment));

                    HttpClient httpclient = new DefaultHttpClient(null);
                    HttpPost httppost = new HttpPost(insert_http_comment); //
                    httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValue_insert_c, "UJIS"));
                    HttpResponse response = httpclient.execute(httppost);

                    Toast toast = Toast.makeText(Comment.this, "コメントが送信されました" + some_comment, Toast.LENGTH_SHORT);
                    toast.show();
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }
                //2秒後に現在のActivityを削除
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 2000);
            }
        });
    }

    private void goto_comment_edit(String comment, String comment_username, String comment_image, String kotobaid){
        Intent intent = new Intent(this, Commentedit.class);

        intent.putExtra("ed_comment1", comment);
        intent.putExtra("ed_comment2", comment_username);
        intent.putExtra("ed_comment3", comment_image);
        intent.putExtra("ed_comment4", kotobaid);

        RelativeLayout view = (RelativeLayout) findViewById(R.id.comment_each_RelativeLayout);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, view, "hello comment").toBundle());
    }
}
