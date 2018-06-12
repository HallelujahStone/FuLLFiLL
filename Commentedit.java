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
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by haruya.i on 2016/12/12.
 */

public class Commentedit extends Activity {

    String delete_http_comment = "http://fullfill.sakura.ne.jp/src/commentDelete.php";

    ArrayList<NameValuePair> nameValue_delete_c = new ArrayList<NameValuePair>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_edit);

        String comment, comment_username, comment_image;

        //Commentから引数の受け取り
        Intent intent = getIntent();

        comment = intent.getStringExtra("ed_comment1");
        comment_username = intent.getStringExtra("ed_comment2");
        comment_image = intent.getStringExtra("ed_comment3");
        final String kotobaid = intent.getStringExtra("ed_comment4");

        EditText ed_comment = (EditText) findViewById(R.id.comment_text_edit);
        TextView ed_comment_username = (TextView) findViewById(R.id.username_in_comment_edit);
        ImageView ed_comment_image = (ImageView)findViewById(R.id.icon_in_comment_edit);

        ed_comment.setText(comment);
        ed_comment_username.setText(comment_username);

        URL url;
        InputStream istream;
        try {
            //画像のURLを直うち
            url = new URL("http://fullfill.sakura.ne.jp" + comment_image);
            //インプットストリームで画像を読み込む
            istream = url.openStream();
            //読み込んだファイルをビットマップに変換
            Bitmap oBmp = BitmapFactory.decodeStream(istream);
            //ビットマップをImageViewに設定
            ed_comment_image.setImageBitmap(oBmp);
            //インプットストリームを閉じる
            istream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //コメント編集の更新の設定
        FloatingActionButton FA_button_edit = (FloatingActionButton) findViewById(R.id.determine_to_edit_comment);
        FA_button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText new_kotoba = (EditText) findViewById(R.id.comment_text_edit);

                SpannableStringBuilder str1 = (SpannableStringBuilder) new_kotoba.getText();
                String edited_kotoba = str1.toString();

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

        //コメント削除の設定
        FloatingActionButton FA_button_delete = (FloatingActionButton) findViewById(R.id.determine_to_delete_comment);
        FA_button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    nameValue_delete_c.add(new BasicNameValuePair("userid", "1"));//とりあえずANNAのuserid//
                    nameValue_delete_c.add(new BasicNameValuePair("kotobaid", kotobaid));// userid,kotobaid favoriteと同じ

                    HttpClient httpclient = new DefaultHttpClient(null);
                    HttpPost httppost = new HttpPost(delete_http_comment); //
                    httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValue_delete_c, "UJIS"));
                    HttpResponse response = httpclient.execute(httppost);

                    Toast toast = Toast.makeText(Commentedit.this, "このコメントは削除されました", Toast.LENGTH_SHORT);
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
