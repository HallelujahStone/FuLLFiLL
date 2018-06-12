package io.haruya.fullfill;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.transition.Fade;
import android.support.transition.TransitionSet;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
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

/**
 * Created by haruya.i on 2016/11/10.
 */

public class Clickedcard extends Activity {

    String send_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteInsert.php";
    String delete_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteDelete.php";

    ArrayList<NameValuePair> nameValue_send_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_delete_f = new ArrayList<NameValuePair>();

    final String id_of_user = "1";//useridをとりあえず定義

    boolean[] flag_array = new boolean[50];//長さをおいおい調整


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clicked_cardlist);

        final String kotoba, whose, users_name, favorited, commented, icon_addres;

        boolean favorite_or_not;

        //MainActivityから引数の受け取り
        Intent intent = getIntent();
        kotoba = intent.getStringExtra("data1");
        whose = intent.getStringExtra("data2");
        users_name = intent.getStringExtra("data3");
        favorited = intent.getStringExtra("data4");
        commented = intent.getStringExtra("data5");
        icon_addres = intent.getStringExtra("data7");
        final String kotobaid = intent.getStringExtra("data8");


        TextView cl_kotoba = (TextView)findViewById(R.id.cl_kotoba);
        TextView cl_whose = (TextView)findViewById(R.id.cl_whose);
        TextView cl_users_name = (TextView)findViewById(R.id.cl_users_name);
        final TextView cl_favorited = (TextView)findViewById(R.id.cl_how_favorited);
        final TextView cl_commented = (TextView)findViewById(R.id.cl_how_commented);
        ImageView cl_icon = (ImageView)findViewById(R.id.cl_icon);

        final ImageView bookmark_sign = (ImageView) findViewById(R.id.cl_bookmark_sign);//final必要

        cl_kotoba.setText(kotoba);
        cl_users_name.setText(users_name);
        cl_favorited.setText(favorited);
        cl_commented.setText(commented);

        if(whose.equals("koreninamaehaarimasensen") || whose.equals("")){
            cl_whose.setText("");
        }else{
            cl_whose.setText("(" + whose + ")");
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
            cl_icon.setImageBitmap(oBmp);
            //インプットストリームを閉じる
            istream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        //ブックマーククリック時の処理
        final ImageView favorite = (ImageView) findViewById(R.id.cl_favorited_icon);
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

                    ImageView fav = (ImageView) v.findViewById(R.id.cl_favorited_icon);
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

                    Toast toast = Toast.makeText(Clickedcard.this, "ブックマークされました", Toast.LENGTH_SHORT);
                    toast.show();

                    int how_favo_i = Integer.parseInt(favorited) + 1;
                    String how_favo_s = String.valueOf(how_favo_i);
                    cl_favorited.setText(how_favo_s);

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

                    ImageView fav = (ImageView) v.findViewById(R.id.cl_favorited_icon);
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

                    Toast toast = Toast.makeText(Clickedcard.this, "ブックマークが解除されました", Toast.LENGTH_SHORT);
                    toast.show();

                    cl_favorited.setText(favorited);

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

        comment(kotoba, whose, users_name, favorited, commented, icon_addres, kotobaid);
    }

    private void comment(final String k, final String w, final String us, final String f, final String c, final String icon, final String kotobaid) {
        ImageView tocomment = (ImageView) this.findViewById(R.id.cl_comment_icon);

        tocomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Comment.class);

                //Commentへ引き渡し
                intent.putExtra("cl_data1", k);
                intent.putExtra("cl_data2", w);
                intent.putExtra("cl_data3", us);
                intent.putExtra("cl_data4", f);
                intent.putExtra("cl_data5", c);
                intent.putExtra("cl_data6", icon);
                intent.putExtra("cl_data7", kotobaid);

                startActivity(intent);
            }
        });
    }

    /*private void goto_comment(String k, String w, String us, String f, String c, int i) {
        Intent intent = new Intent(getApplication(), Comment.class);

        TransitionSet ts = new TransitionSet();
        ts.addTransition(new Fade());

        //Commentへ引き渡し
        intent.putExtra("cl_data1", k);
        intent.putExtra("cl_data2", w);
        intent.putExtra("cl_data3", us);
        intent.putExtra("cl_data4", f);
        intent.putExtra("cl_data5", c);
        intent.putExtra("cl_data6", i);

        CardView card = (CardView) findViewById(R.id.cardView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, card, getString(R.string.transition_string));
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }*/
}
