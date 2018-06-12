package io.haruya.fullfill;

import android.accounts.Account;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by haruya.i on 2016/12/08.
 */

public class Accountsettings extends Activity {

    String select_http_user = "http://fullfill.sakura.ne.jp/src/mySelect.php";
    String update_htttp_name = "http://fullfill.sakura.ne.jp/src/mynameUpdate.php";
    String update_htttp_image = "http://fullfill.sakura.ne.jp/src/myimageUpdate.php";
    String update_htttp_pw = "http://fullfill.sakura.ne.jp/src/mypasswordUpdate.php";
    String update_htttp_kotoba = "http://fullfill.sakura.ne.jp/src/myselfkotobaUpdate.php";

    InputStream is;
    boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_settings);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        final ArrayList<NameValuePair> nameValue_select_u = new ArrayList<>();
        final ArrayList<NameValuePair> nameValue_update_n = new ArrayList<NameValuePair>();
        final ArrayList<NameValuePair> nameValue_update_i = new ArrayList<NameValuePair>();
        final ArrayList<NameValuePair> nameValue_update_p = new ArrayList<NameValuePair>();
        final ArrayList<NameValuePair> nameValue_update_k = new ArrayList<NameValuePair>();

        // myIDはIntentクラスのputExtra()メソッドなどで他のActivityから引き継ぐ
        int ID = 5;
        final String myID = String.valueOf(ID);

        String account_name = "";
        String account_image = "";
        String account_kotoba = "";
        TextView now_name, now_kotoba;
        ImageView now_image;

        URL url;
        InputStream istream;


        //現在のアカウント情報の取得
        try {
            String result_get_p = "";

            try {
                nameValue_select_u.add(new BasicNameValuePair("id", myID));

                HttpClient httpclient = new DefaultHttpClient(null);
                HttpPost i_httppost = new HttpPost(select_http_user);
                i_httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                i_httppost.setEntity(new UrlEncodedFormEntity(nameValue_select_u, "UJIS"));
                HttpResponse response = httpclient.execute(i_httppost);

                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                result_get_p = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }

            JSONArray jArray_p = new JSONArray(result_get_p);

            JSONObject json_data_get_p = jArray_p.getJSONObject(0);
            account_name = json_data_get_p.getString("username");
            account_image = json_data_get_p.getString("image");
            account_kotoba = json_data_get_p.getString("selfkotoba");

        }  catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        now_name = (TextView) findViewById(R.id.usersname_in_accountsettings);
        now_name.setText(account_name);


        // サーバに画像を送れるようになったら実装
        now_image = (ImageView) findViewById(R.id.icon_in_accountsettings);
        // now_image.setLayoutParams(new LinearLayout.LayoutParams(90, 90));

        try {
            //画像のURLを直うち
            url = new URL("http://fullfill.sakura.ne.jp" + account_image);
            //インプットストリームで画像を読み込む
            istream = url.openStream();
            //読み込んだファイルをビットマップに変換
            Bitmap oBmp = BitmapFactory.decodeStream(istream);
            //ビットマップをImageViewに設定
            now_image.setImageBitmap(oBmp);
            //インプットストリームを閉じる
            istream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        now_kotoba = (TextView) findViewById(R.id.yourwords_in_accountsettings);
        now_kotoba.setText(account_kotoba);

        //toolbarを設置
        toolbar_on();

        //username変更のボタンの設定
        FloatingActionButton name_Button = (FloatingActionButton)findViewById(R.id.changename_button);
        name_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText)findViewById(R.id.change_users_name);
                SpannableStringBuilder str1 = (SpannableStringBuilder)username.getText();
                String new_name = str1.toString();

                flag = true;

                if (new_name.length() >20 || new_name.length() == 0){
                    Toast toast = Toast.makeText(Accountsettings.this, "ユーザーネームの文字数が不正です。20字以内で入力をおこなってください。", Toast.LENGTH_SHORT);
                    toast.show();
                    flag = false;
                }

                // 登録内容をpostする
                if (flag == true) {
                    try {
                        nameValue_update_n.add(new BasicNameValuePair("username", new_name));
                        nameValue_update_n.add(new BasicNameValuePair("id", myID));

                        HttpClient httpclient = new DefaultHttpClient(null);
                        HttpPost httppost = new HttpPost(update_htttp_name);
                        httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                        httppost.setEntity(new UrlEncodedFormEntity(nameValue_update_n, "UJIS"));
                        HttpResponse response = httpclient.execute(httppost);

                        Toast toast = Toast.makeText(Accountsettings.this, "new username is  " + new_name , Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }
                    onCreate(Bundle.EMPTY);
                }
            }
        });



        //あなたの好きなコトバ変更のボタンの設定
        FloatingActionButton word_Button = (FloatingActionButton)findViewById(R.id.changeword_button);
        word_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send_to_database(list, send_http_kotoba);//, "userid", "2", "kotoba", "名言なんです","whose", "サンプル");

                EditText yourword = (EditText)findViewById(R.id.change_yourwords);

                SpannableStringBuilder str2 = (SpannableStringBuilder)yourword.getText();
                String new_word = str2.toString();

                flag = true;

                if (new_word.length() >140 || new_word.length() == 0){
                    Toast toast = Toast.makeText(Accountsettings.this, "好きなコトバの文字数が不正です。140字以内で入力をおこなってください。", Toast.LENGTH_SHORT);
                    toast.show();
                    flag = false;
                }


                // 登録内容をpostする
                if (flag == true) {
                    try {
                        nameValue_update_k.add(new BasicNameValuePair("selfkotoba", new_word));
                        nameValue_update_k.add(new BasicNameValuePair("id", myID));

                        HttpClient httpclient = new DefaultHttpClient(null);
                        HttpPost httppost = new HttpPost(update_htttp_kotoba);
                        httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                        httppost.setEntity(new UrlEncodedFormEntity(nameValue_update_k, "UJIS"));
                        HttpResponse response = httpclient.execute(httppost);

                        Toast toast = Toast.makeText(Accountsettings.this, "new yourword is " + new_word, Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }
                    onCreate(Bundle.EMPTY);
                }
            }
        });



        //パスワード変更ボタンの設定
        FloatingActionButton pw_Button = (FloatingActionButton)findViewById(R.id.changepw_button);
        pw_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send_to_database(list, send_http_kotoba);//, "userid", "2", "kotoba", "名言なんです","whose", "サンプル");


                EditText text_pw = (EditText)findViewById(R.id.change_yourpw);
                EditText text_repw = (EditText)findViewById(R.id.change_re_yourpw);

                SpannableStringBuilder str3 = (SpannableStringBuilder)text_pw.getText();
                String new_pw = str3.toString();
                SpannableStringBuilder str4 = (SpannableStringBuilder)text_repw.getText();
                String new_repw = str4.toString();

                flag = true;

                if (new_pw.length() == 0 || new_repw.length() == 0) {
                    Toast toast = Toast.makeText(Accountsettings.this, "変更するパスワードが未入力です。", Toast.LENGTH_SHORT);
                    toast.show();
                    flag = false;
                } else if (!(new_pw.equals(new_repw))) {
                    Toast toast = Toast.makeText(Accountsettings.this, "パスワードが一致しません。もう一度入力してください。", Toast.LENGTH_SHORT);
                    toast.show();
                    flag = false;
                } else if(new_pw.length() < 8 || new_pw.length() > 20) {
                    Toast toast = Toast.makeText(Accountsettings.this, "パスワードの文字数が不正です。8-20字にしてください。", Toast.LENGTH_SHORT);
                    toast.show();
                    flag = false;
                }


                // 登録内容をpostする
                if (flag == true) {
                    try {
                        nameValue_update_p.add(new BasicNameValuePair("password", new_pw));
                        nameValue_update_p.add(new BasicNameValuePair("id", myID));

                        HttpClient httpclient = new DefaultHttpClient(null);
                        HttpPost httppost = new HttpPost(update_htttp_pw);
                        httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                        httppost.setEntity(new UrlEncodedFormEntity(nameValue_update_p, "UJIS"));
                        HttpResponse response = httpclient.execute(httppost);


                        Toast toast = Toast.makeText(Accountsettings.this, "パスワードの変更を受け付けました。", Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }
                    onCreate(Bundle.EMPTY);
                }
            }
        });

    }

    protected void toolbar_on() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar_in_accountsettings);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
