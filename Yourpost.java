package io.haruya.fullfill;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.transition.Fade;
import android.support.transition.TransitionSet;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by haruya.i on 2016/11/13.
 */

public class Yourpost extends Activity {

    String history_http_kotoba = "http://fullfill.sakura.ne.jp/src/kotobaHistory.php";
    String get_http_comment = "http://fullfill.sakura.ne.jp/src/commentGet.php";
    String get_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteGet.php";
    String send_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteInsert.php";
    String delete_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteDelete.php";


    ArrayList<NameValuePair> nameValue_history_k = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_get_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_get_c = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_send_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_delete_f = new ArrayList<NameValuePair>();

    TextView kotoba, whose, users_name, commented;

    boolean[] flag_array = new boolean[50];//長さをおいおい調整

    final String id_of_user = "1";//useridをとりあえず定義

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.your_post);

        tool_set();//toolbarをセット

        card_on();
    }

    private void card_on(){
        InputStream is;
        String result_history_k = "";
        String result_get_f = "";
        String result_get_c = "";

        //saveSelect開始
        try {
            HttpClient httpclient = new DefaultHttpClient(null);
            HttpPost httppost = new HttpPost(history_http_kotoba);

            httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost.setEntity(new UrlEncodedFormEntity(nameValue_history_k, "UTF-8"));
            //従来のsend_to_databaseの部分
            try {
                nameValue_history_k.add(new BasicNameValuePair("userid", id_of_user));//とりあえずの値

                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.setEntity(new UrlEncodedFormEntity(nameValue_history_k, "UTF-8"));
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

            result_history_k = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        try {
            final LinearLayout bosslayout = (LinearLayout) this.findViewById(R.id.bosslayout_yourpost);
            bosslayout.removeAllViews();

            JSONArray jArray_s = new JSONArray(result_history_k);

            HttpClient c_httpclient = new DefaultHttpClient(null);//移動してあります!!!
            HttpPost c_httppost = new HttpPost(get_http_comment);


            for (int i = 0; i < jArray_s.length() - 1; i++) {
                JSONObject json_data_get = jArray_s.getJSONObject(i);

                final String kotoba_get = json_data_get.getString("kotoba");
                String whose_get = json_data_get.getString("whose");
                final String username_get = json_data_get.getString("username");
                String image_get = json_data_get.getString("image");
                String kotobaid_get = json_data_get.getString("id");

                final String[] any_kotoba = new String[jArray_s.length() - 1];
                any_kotoba[i] = kotoba_get;
                final String[] any_whose = new String[jArray_s.length() - 1];
                any_whose[i] = whose_get;
                final String[] any_users_name = new String[jArray_s.length() - 1];
                any_users_name[i] = username_get;
                final String[] any_users_icon = new String[jArray_s.length() - 1];
                any_users_icon[i] = image_get;
                final String[] any_kotobaid = new String[jArray_s.length() - 1];
                any_kotobaid[i] = kotobaid_get;


                //追加分favoriteGet
                try {
                    HttpClient f_httpclient = new DefaultHttpClient(null);
                    HttpPost f_httppost = new HttpPost(get_http_favorite);

                    f_httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    f_httppost.setEntity(new UrlEncodedFormEntity(nameValue_get_f, "UTF-8"));
                    //従来のsend_to_databaseの部分
                    try {
                        nameValue_get_f.add(new BasicNameValuePair("kotobaid", kotobaid_get));

                        f_httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                        f_httppost.setEntity(new UrlEncodedFormEntity(nameValue_get_f, "UTF-8"));
                        HttpResponse f_response = f_httpclient.execute(f_httppost);
                        //}
                    } catch (Exception e) {
                        Log.e("log_tag", "Error converting result " + e.toString());
                    }
                    HttpResponse f_response = f_httpclient.execute(f_httppost);

                    HttpEntity f_entity = f_response.getEntity();
                    is = f_entity.getContent();

                    BufferedReader f_reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder f_sb = new StringBuilder();
                    String f_line = null;
                    while ((f_line = f_reader.readLine()) != null) {
                        f_sb.append(f_line + "\n");
                    }
                    is.close();

                    result_get_f = f_sb.toString();
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }

                JSONArray jArray_f = new JSONArray(result_get_f);
                JSONObject json_data_get_f = jArray_f.getJSONObject(0);
                String favorite_count = String.valueOf(json_data_get_f.getInt("favoritecount"));
                final String[] any_favorited = new String[jArray_s.length() - 1];
                any_favorited[i] = favorite_count;

                //追加分commentGet→省略可能確認済み
                try {
                    nameValue_get_c.add(new BasicNameValuePair("kotobaid", kotobaid_get));

                    c_httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    c_httppost.setEntity(new UrlEncodedFormEntity(nameValue_get_c, "UTF-8"));

                    HttpResponse c_response = c_httpclient.execute(c_httppost);

                    HttpEntity c_entity = c_response.getEntity();
                    is = c_entity.getContent();

                    BufferedReader c_reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder c_sb = new StringBuilder();
                    String c_line = null;
                    while ((c_line = c_reader.readLine()) != null) {
                        c_sb.append(c_line + "\n");
                    }
                    is.close();

                    result_get_c = c_sb.toString();

                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }

                JSONArray jArray_get_c = new JSONArray(result_get_c);
                JSONObject json_data_get_c = jArray_get_c.getJSONObject(0);
                String comment_count = String.valueOf(json_data_get_c.getInt("commentcount"));
                final String[] any_commented = new String[jArray_s.length() - 1];
                any_commented[i] = comment_count;

                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                LinearLayout relativeLayout = (LinearLayout) inflater.inflate(R.layout.your_post_card_list, null);
                CardView cardView = (CardView) relativeLayout.findViewById(R.id.cardView_yourpost);

                //レイアウト上にViewを設置
                kotoba = (TextView) relativeLayout.findViewById(R.id.kotoba_yourpost);
                whose = (TextView) relativeLayout.findViewById(R.id.whose_yourpost);
                users_name = (TextView) relativeLayout.findViewById(R.id.users_name_yourpost);
                final TextView favorited = (TextView) relativeLayout.findViewById(R.id.how_favorited_yourpost);
                commented = (TextView) relativeLayout.findViewById(R.id.how_commented_yourpost);
                final ImageView bookmark_sign = (ImageView) relativeLayout.findViewById(R.id.bookmark_sign_yourpost);

                //取得したアイコンの画像をimageに変換at usersicon
                ImageView users_icon = (ImageView) relativeLayout.findViewById(R.id.users_icon_yourpost);
                users_icon.setLayoutParams(new LinearLayout.LayoutParams(50, 50));
                URL url;
                InputStream istream;
                try {
                    //画像のURLを直うち
                    url = new URL("http://fullfill.sakura.ne.jp" + image_get);
                    //インプットストリームで画像を読み込む
                    istream = url.openStream();
                    //読み込んだファイルをビットマップに変換
                    Bitmap oBmp = BitmapFactory.decodeStream(istream);
                    //ビットマップをImageViewに設定
                    users_icon.setImageBitmap(oBmp);
                    //インプットストリームを閉じる
                    istream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //文字、画像をset
                kotoba.setText(kotoba_get);
                whose.setText("(" + whose_get + ")");
                users_name.setText(username_get);
                favorited.setText(favorite_count);
                commented.setText(comment_count);

                flag_array[i] = false;

                //ブックマーククリック時の処理
                final ImageView favorite = (ImageView) relativeLayout.findViewById(R.id.favorited_icon_yourpost);
                favorite.setTag(i);
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int f = (Integer) v.getTag();

                        if (flag_array[f] == false) {

                            try {
                                nameValue_send_f.add(new BasicNameValuePair("userid", id_of_user));
                                nameValue_send_f.add(new BasicNameValuePair("kotobaid", any_kotobaid[f]));

                                HttpClient httpclient_send_f = new DefaultHttpClient(null);
                                HttpPost httppost_send_f = new HttpPost(send_http_favorite); //
                                httppost_send_f.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                                httppost_send_f.setEntity(new UrlEncodedFormEntity(nameValue_send_f, "UTF-8"));
                                HttpResponse response = httpclient_send_f.execute(httppost_send_f);
                                //}
                            } catch (Exception e) {
                                Log.e("log_tag", "Error converting result " + e.toString());
                            }

                            ImageView fav = (ImageView) v.findViewById(R.id.favorited_icon_yourpost);
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

                            Toast toast = Toast.makeText(Yourpost.this, "ブックマークされました", Toast.LENGTH_SHORT);
                            toast.show();

                            int how_favo_i = Integer.parseInt(any_favorited[f]) + 1;
                            String how_favo_s = String.valueOf(how_favo_i);
                            favorited.setText(how_favo_s);

                            flag_array[f] = true;
                        } else {
                            try {
                                nameValue_delete_f.add(new BasicNameValuePair("userid", id_of_user));
                                nameValue_delete_f.add(new BasicNameValuePair("kotobaid", any_kotobaid[f]));

                                HttpClient httpclient_delete_f = new DefaultHttpClient(null);
                                HttpPost httppost_delete_f = new HttpPost(delete_http_favorite); //
                                httppost_delete_f.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                                httppost_delete_f.setEntity(new UrlEncodedFormEntity(nameValue_delete_f, "UTF-8"));
                                HttpResponse response = httpclient_delete_f.execute(httppost_delete_f);
                                //}
                            } catch (Exception e) {
                                Log.e("log_tag", "Error converting result " + e.toString());
                            }

                            TranslateAnimation mark = new TranslateAnimation(0, 0, 0, -70);
                            mark.setDuration(300);
                            bookmark_sign.startAnimation(mark);

                            Toast toast = Toast.makeText(Yourpost.this, "ブックマークが解除されました", Toast.LENGTH_SHORT);
                            toast.show();

                            int how_favo_i = Integer.parseInt(any_favorited[f]) - 1;
                            String how_favo_s = String.valueOf(how_favo_i);

                            favorited.setText(any_favorited[f]);

                            flag_array[f] = false;

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

                //コメントクリック時の動作
                ImageView comment_icon = (ImageView)relativeLayout.findViewById(R.id.comment_icon_yourpost);
                comment_icon.setTag(i);
                comment_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n = (Integer)v.getTag();

                        Intent intent = new Intent(getApplication(), Comment.class);

                        intent.putExtra("cl_data1", any_kotoba[n]);
                        intent.putExtra("cl_data2", any_whose[n]);
                        intent.putExtra("cl_data3", any_users_name[n]);
                        intent.putExtra("cl_data4", any_favorited[n]);
                        intent.putExtra("cl_data5", any_commented[n]);
                        intent.putExtra("cl_data6", any_users_icon[n]);
                        intent.putExtra("cl_data7", any_kotobaid[n]);

                        startActivity(intent);
                    }
                });

                //カードに情報を付加
                cardView.setTag(i);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n = (Integer)v.getTag();
                        goto_clicked(any_kotoba[n], any_whose[n], any_users_name[n], any_favorited[n], any_commented[n], any_users_icon[n]);
                    }
                });

                bosslayout.addView(relativeLayout, i);
            }

        }catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
    }

    private void goto_clicked(String k, String w, String us, String f, String c, String icon){
        Intent intent = new Intent(getApplication(), Clickedcard.class);

        TransitionSet ts = new TransitionSet();
        ts.addTransition(new Fade());

        //cardonでセットしたTagの番号をClickedcardに引き渡し
        intent.putExtra("data1", k);
        intent.putExtra("data2", w);
        intent.putExtra("data3", us);
        intent.putExtra("data4", f);
        intent.putExtra("data5", c);
        intent.putExtra("data7", icon);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this/*, card, getString(R.string.transition_string)*/);
            startActivity(intent, options.toBundle());
        }
        else{
            startActivity(intent);
        }
    }

    //toolbar set
    private void tool_set(){
        Toolbar toolbar = (Toolbar)findViewById(R.id.tool_bar_in_yourpost);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_18dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//backが押された際の動き
                finish();
            }
        });
    }
}
