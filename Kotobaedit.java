package io.haruya.fullfill;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by haruya.i on 2016/12/12.
 */

public class Kotobaedit extends Activity {

    String delete_http_kotoba = "http://fullfill.sakura.ne.jp/src/kotobaDelete.php";

    ArrayList<NameValuePair> nameValue_delete_k = new ArrayList<NameValuePair>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kotoba_edit);

        String kotoba, whose, users_name, favorited, commented, icon_addres;

        //MainActivityから引数の受け取り
        Intent intent = getIntent();

        kotoba = intent.getStringExtra("ed_data1");
        whose = intent.getStringExtra("ed_data2");
        users_name = intent.getStringExtra("ed_data3");
        favorited = intent.getStringExtra("ed_data4");
        commented = intent.getStringExtra("ed_data5");
        icon_addres = intent.getStringExtra("ed_data6");
        final String kotobaid = intent.getStringExtra("ed_data7");

        EditText ed_kotoba = (EditText) findViewById(R.id.kotoba_edit);
        EditText ed_whose = (EditText) findViewById(R.id.whose_edit);
        TextView ed_users_name = (TextView)findViewById(R.id.users_name_edit);
        TextView ed_favorited = (TextView)findViewById(R.id.how_favorited_edit);
        TextView ed_commented = (TextView)findViewById(R.id.how_commented_edit);
        ImageView ed_icon = (ImageView)findViewById(R.id.users_icon_edit);

        ed_kotoba.setText(kotoba);
        ed_users_name.setText(users_name);
        ed_favorited.setText(favorited);
        ed_commented.setText(commented);

        if(whose.equals("koreninamaehaarimasensen") || whose.equals("")){
            ed_whose.setText("");
        }else{
            ed_whose.setText("(" + whose + ")");
        }

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
            ed_icon.setImageBitmap(oBmp);
            //インプットストリームを閉じる
            istream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //コトバ編集の更新の設定
        FloatingActionButton FA_button_edit = (FloatingActionButton) findViewById(R.id.determine_to_edit);
        FA_button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//コトバとwhoseの二つの要素
                EditText new_kotoba = (EditText) findViewById(R.id.kotoba_edit);
                EditText new_whose = (EditText) findViewById(R.id.whose_edit);

                SpannableStringBuilder str1 = (SpannableStringBuilder) new_kotoba.getText();
                String edited_kotoba = str1.toString();
                SpannableStringBuilder str2 = (SpannableStringBuilder) new_whose.getText();
                String edited_whose = str2.toString();

                /*try {
                    nameValue_send.add(new BasicNameValuePair("userid", "1"));
                    nameValue_send.add(new BasicNameValuePair("kotoba", posted_yourwords));
                    nameValue_send.add(new BasicNameValuePair("whose", posted_whose));

                    HttpClient httpclient = new DefaultHttpClient(null);
                    HttpPost httppost = new HttpPost(send_http_kotoba); //
                    httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValue_send, "UJIS"));
                    HttpResponse response = httpclient.execute(httppost);

                    Toast toast = Toast.makeText(MainActivity.this, "投稿ボタンが押されました" + "your word is " + posted_yourwords +
                            "\nwhose is " + posted_whose, Toast.LENGTH_SHORT);
                    toast.show();
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }*/
            }
        });

        //コトバ削除の設定
        FloatingActionButton FA_button_delete = (FloatingActionButton) findViewById(R.id.determine_to_delete);
        FA_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//ここに削除の動作を
                try {
                    nameValue_delete_k.add(new BasicNameValuePair("id", kotobaid));

                    HttpClient httpclient = new DefaultHttpClient(null);
                    HttpPost httppost = new HttpPost(delete_http_kotoba); //
                    httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValue_delete_k, "UJIS"));
                    HttpResponse response = httpclient.execute(httppost);

                    Toast toast = Toast.makeText(Kotobaedit.this, "このコトバは削除されました", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }
                //1秒後に現在のActivityを削除
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        });
    }
}
