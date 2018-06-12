package io.haruya.fullfill;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Fade;
import android.support.transition.TransitionSet;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Slide;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by haruya.i on 2016/11/14.
 */

public class Searching extends AppCompatActivity {

    String search_http_kotoba = "http://fullfill.sakura.ne.jp/src/kotobaSearch.php";
    String select_http_genre = "http://fullfill.sakura.ne.jp/src/genreSelect.php";
    //追加分
    String select_http_user = "http://fullfill.sakura.ne.jp/src/userSelect.php";
    String select_http_company = "http://fullfill.sakura.ne.jp/src/companySelect.php";
    String get_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteGet.php";
    String delete_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteDelete.php";
    String get_http_comment = "http://fullfill.sakura.ne.jp/src/commentGet.php";
    String send_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteInsert.php";
    String select_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteSelect.php";
    String count_http_favorite = "http://fullfill.sakura.ne.jp//src/favoritecount.php";

    ArrayList<NameValuePair> nameValue_search_k = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_select_g = new ArrayList<NameValuePair>();
    //追加分
    ArrayList<NameValuePair> nameValue_send_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_get_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_get_c = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_select_u = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_select_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_delete_f = new ArrayList<NameValuePair>();


    InputStream is;
    String result = "";
    String result_u = "";
    String result_f = "";
    String result_get_f = "";
    String result_get_c = "";
    String result_com = "";
    String result_search_k = "";
    String result_select_g = "";

    String kotoba_search;
    HttpClient httpclient = new DefaultHttpClient(null);
    HttpResponse response;
    HttpEntity entity;
    BufferedReader reader;
    HttpPost httppost_kotoba_search = new HttpPost(search_http_kotoba);
    StringBuilder sb_kotoba_search = new StringBuilder();
    HttpPost httppost_genre_select = new HttpPost(select_http_genre);
    StringBuilder sb_genre_select = new StringBuilder();

    //userSelectの値の初期化
    HttpPost httppost_user_select = new HttpPost(select_http_user);
    StringBuilder sb_user_select = new StringBuilder();
    //companySelectの値の初期化
    HttpPost httppost_company_select = new HttpPost(select_http_company);
    StringBuilder sb_company_select = new StringBuilder();
    //favoriteSelectの値の初期化
    HttpPost httppost_favorite_select = new HttpPost(select_http_favorite);
    StringBuilder sb_favorite_select = new StringBuilder();
    //favoriteGetの値の初期化
    HttpPost httppost_favorite_get = new HttpPost(get_http_favorite);
    //commentGetの値の初期化
    HttpPost httppost_comment_get = new HttpPost(get_http_comment);

    int i, j, l, m, n; //for文用→処理速度の向上に
    int x=0;

    TextView kotoba, whose, users_name,  commented;
    ImageView company, users_icon, comment_icon, favorite;

    final String id_of_user = "1";//使用者のuseridをとりあえず定義
    final String name_of_user = "ANNA";//使用者のusernameをとりあえず定義

    String genre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searching);

        toolbar_on();
    }

    protected void toolbar_on() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_in_searching);
        setSupportActionBar(toolbar);
    }

    //radioButtonの設定
    protected void onRadioButtonClicked(View view) {
        RadioButton radioButton = (RadioButton) view;
        genre = radioButton.getText().toString();// ラジオボタンのテキストを取得
    }

    //searchViewに関する設定
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final LinearLayout searchboss = (LinearLayout) this.findViewById(R.id.search_bosslayout);
        getMenuInflater().inflate(R.menu.active_searchview, menu);
        //final LinearLayout scrollboss = (LinearLayout)findViewById(R.id.scrollboss);
        final ScrollView linearboss = (ScrollView)findViewById(R.id.linear_boss);

        MenuItem searchMenuItem = menu.findItem(R.id.activity_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setIconified(false);//アイコンの表示か非表示か
        searchView.setQueryHint("検索");

        //検索候補の文字列をここで初期化
        String[] candidates = getResources().getStringArray(R.array.search_posting_name);
        //whoseの情報も必要
        final ListView lv = (ListView)findViewById(R.id.searcing_list);

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1, candidates){
            @Override
            public View getView(int position, View convertView, ViewGroup parent){
                TextView view = (TextView)super.getView(position, convertView, parent);
                view.setTextSize(14);//リストの文字の大きさを設定
                return view;
            }
        };//文字列(candidates)を格納

        lv.setTextFilterEnabled(true);//フィルターを設置

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override//検索ボタンが押下されると呼び出されるメソッド
            public boolean onQueryTextSubmit(String query) {
                searchboss.removeAllViews();

                //kotobaSearch
                try {
                    if(genre.equals("")){
                        genre = "nakamuraanna";
                    }
                    if(query.equals("")){
                        query = "nakamuraanna";
                    }
                    nameValue_search_k.add(new BasicNameValuePair("offset", "0"));
                    nameValue_search_k.add(new BasicNameValuePair("genre", genre));
                    nameValue_search_k.add(new BasicNameValuePair("word", query));

                    httppost_kotoba_search.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    httppost_kotoba_search.setEntity(new UrlEncodedFormEntity(nameValue_search_k, "UJIS"));

                    response = httpclient.execute(httppost_kotoba_search);

                    entity= response.getEntity();
                    is = entity.getContent();

                    reader = new BufferedReader(new InputStreamReader(is), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();

                    result_search_k = sb.toString();

                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }

                searched_card_on(result_search_k);

                return false;
            }

            @Override//テキスト内容が変更されるたびに呼び出されるメソッド
            public boolean onQueryTextChange(String newText) {
                LinearLayout radio = (LinearLayout)findViewById(R.id.radioboss);
                radio.removeAllViews();

                searchboss.removeAllViews();
                linearboss.removeAllViews();
                linearboss.addView(searchboss);
                searchboss.addView(lv);

                //searchboss.removeAllViews();
                //searchboss.addView(lv);
                //alv.setTextFilterEnabled(true);//フィルターを設置

                lv.setAdapter(adapter);

                if (TextUtils.isEmpty(newText)) {
                    lv.clearTextFilter();
                } else {
                    lv.setFilterText(newText.toString());
                }

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ListView list = (ListView)parent;
                        String msg = (String)list.getItemAtPosition(position);

                        searchboss.removeAllViews();

                        //kotobaSearch
                        try {
                            if(genre.equals("")){
                                genre = "nakamuraanna";
                            }
                            if(msg.equals("")){
                                msg = "nakamuraanna";
                            }

                            nameValue_search_k.add(new BasicNameValuePair("offset", "0"));
                            nameValue_search_k.add(new BasicNameValuePair("genre", genre));
                            nameValue_search_k.add(new BasicNameValuePair("word", msg));

                            httppost_kotoba_search.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                            httppost_kotoba_search.setEntity(new UrlEncodedFormEntity(nameValue_search_k, "UJIS"));

                            response = httpclient.execute(httppost_kotoba_search);

                            entity= response.getEntity();
                            is = entity.getContent();

                            reader = new BufferedReader(new InputStreamReader(is), 8);
                            StringBuilder sb = new StringBuilder();
                            String line = null;
                            while ((line = reader.readLine()) != null) {
                                sb.append(line + "\n");
                            }
                            is.close();

                            result_search_k = sb.toString();

                        } catch (Exception e) {
                            Log.e("log_tag", "Error converting result " + e.toString());
                        }

                        searched_card_on(result_search_k);

                    }
                });

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    //MainActivityのcard_onとほぼ同じ
    private void searched_make_card(String result, int x){

        try {//追加分userSelect
            httppost_user_select.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost_user_select.setEntity(new UrlEncodedFormEntity(nameValue_select_u, "UTF-8"));
            response = httpclient.execute(httppost_user_select);

            entity = response.getEntity();
            is = entity.getContent();

            reader = new BufferedReader(new InputStreamReader(is), 8);
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb_user_select.append(line + "\n");
            }
            is.close();

            result_u = sb_user_select.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        //companySelect
        /*try {
            httppost_company_select.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost_company_select.setEntity(new UrlEncodedFormEntity(nameValue_select_com, "UTF-8"));
            response = httpclient.execute(httppost_company_select);

            entity = response.getEntity();
            is = entity.getContent();

            reader = new BufferedReader(new InputStreamReader(is), 8);//変更中
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb_company_select.append(line + "\n");
            }
            is.close();

            result_com = sb_company_select.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }*/

        //favoriteSelect
        try {
            nameValue_select_f.add(new BasicNameValuePair("userid", id_of_user));

            httppost_favorite_select.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost_favorite_select.setEntity(new UrlEncodedFormEntity(nameValue_select_f, "UTF-8"));

            response = httpclient.execute(httppost_favorite_select);

            entity= response.getEntity();
            is = entity.getContent();

            reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();

            result_f = sb.toString();

        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }


        try {
            final LinearLayout searchboss = (LinearLayout) this.findViewById(R.id.search_bosslayout);

            JSONArray jArray = new JSONArray(result);
            JSONArray jArray_u = new JSONArray(result_u);
            //JSONArray jArray_com = new JSONArray(result_com);
            JSONArray jArray_f_select = new JSONArray(result_f);

            final ArrayList<String> kotobaid = new ArrayList<>();
            ArrayList<String> kotoba_userid = new ArrayList<>();
            ArrayList<String> userid = new ArrayList<>();
            ArrayList<String> username = new ArrayList<>();
            ArrayList<String> usericon = new ArrayList<>();
            /*ArrayList<String> ccop_id = new ArrayList<>();
            ArrayList<String> catchcopy = new ArrayList<>();
            ArrayList<String> ccop_name = new ArrayList<>();
            ArrayList<String> ccop_image = new ArrayList<>();*/

            //追加
            ArrayList<String> user_id = new ArrayList<>();
            ArrayList<String> userpass = new ArrayList<>();

            String u_id, k_id, k_u_id, c_id;
            String favorite_count, comment_count;

            int jau = jArray.length()-1;
            int jaul = jArray_u.length()-1;
            //int jac = jArray_com.length()-1;
            int jaf = jArray_f_select.length()-1;

            //save_kotoba_end = save_kotoba_start + save_kotoba_interval;    //お試し

            URL url;
            InputStream istream;

            //kotobaに関するデータを取得
            for(m = 0; m<jau; m++) {
                JSONObject json_data = jArray.getJSONObject(m);

                k_id = String.valueOf(json_data.getInt("id"));
                kotobaid.add(k_id);
                k_u_id = String.valueOf(json_data.getInt("userid"));
                kotoba_userid.add(k_u_id);
            }
            //usernameに関するデータを取得
            for (i = 0; i<jaul; i++) {
                JSONObject json_data_user = jArray_u.getJSONObject(i);

                u_id = String.valueOf(json_data_user.getInt("id"));
                username.add(json_data_user.getString("username"));
                userid.add(u_id);
                usericon.add(json_data_user.getString("image"));
                user_id.add(json_data_user.getString("userid")); //中村くんの確認用で実行した
                userpass.add(json_data_user.getString("password"));
            }
            //companyに関するデータを取得
            /*for (l = 0; l<jac; l++) {
                JSONObject json_data_ccop = jArray_com.getJSONObject(l);

                c_id = String.valueOf(json_data_ccop.getInt("id"));
                ccop_id.add(c_id);
                catchcopy.add(json_data_ccop.getString("catchcopy"));
                ccop_name.add(json_data_ccop.getString("name"));
                ccop_image.add(json_data_ccop.getString("image"));
            }*/

            final String[] any_kotoba = new String[jau];
            final String[] any_whose = new String[jau];
            final String[] any_users_name = new String[jau];
            final String[] any_users_icon = new String[jau];
            final String[] any_favorited = new String[jau];
            final String[] any_commented = new String[jau];

            final boolean[] favorite_or_not = new boolean[jau];//長さをおいおい調整

            //レイアウトの上に載っける部分
            for (j = 0; j<jau; j++) {
                //for(j=save_kotoba_start; j<save_kotoba_end; j++){   //お試し

                //レイアウト、カードビューの定義
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.cardlist, null);
                final CardView cardView = (CardView) linearLayout.findViewById(R.id.cardView);

                //kotobaに関するデータを取得
                JSONObject json_data = jArray.getJSONObject(j);

                k_id = String.valueOf(json_data.getInt("id"));
                kotobaid.add(k_id);
                k_u_id = String.valueOf(json_data.getInt("userid"));
                kotoba_userid.add(k_u_id);

                any_kotoba[j] = json_data.getString("kotoba");
                any_users_name[j] = username.get(userid.indexOf(kotoba_userid.get(j)));
                any_users_icon[j] = usericon.get(userid.indexOf(kotoba_userid.get(j)));

                if (json_data.getString("whose") != "null") {
                    any_whose[j] = json_data.getString("whose");
                }

                //追加分favoriteGet→省略化確認済み
                try {
                    nameValue_get_f.add(new BasicNameValuePair("kotobaid", kotobaid.get(j)));

                    httppost_favorite_get.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    httppost_favorite_get.setEntity(new UrlEncodedFormEntity(nameValue_get_f, "UTF-8"));

                    response = httpclient.execute(httppost_favorite_get);

                    entity= response.getEntity();
                    is = entity.getContent();

                    reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();

                    result_get_f = sb.toString();
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }

                JSONArray jArray_f = new JSONArray(result_get_f);
                JSONObject json_data_get_f = jArray_f.getJSONObject(0);
                favorite_count = String.valueOf(json_data_get_f.getInt("favoritecount"));
                any_favorited[j] = favorite_count;


                //追加分commentGet→それぞれのコトバに対するコメントの数を取得→省略化確認済み
                try {
                    nameValue_get_c.add(new BasicNameValuePair("kotobaid", kotobaid.get(j)));

                    httppost_comment_get.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    httppost_comment_get.setEntity(new UrlEncodedFormEntity(nameValue_get_c, "UTF-8"));

                    response = httpclient.execute(httppost_comment_get);

                    entity= response.getEntity();
                    is = entity.getContent();

                    reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();

                    result_get_c = sb.toString();
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }

                JSONArray jArray_get_c = new JSONArray(result_get_c);
                JSONObject json_data_get_c = jArray_get_c.getJSONObject(0);
                comment_count = String.valueOf(json_data_get_c.getInt("commentcount"));
                any_commented[j] = comment_count;


                //レイアウト上にViewを設置
                kotoba = (TextView) linearLayout.findViewById(R.id.kotoba);
                whose = (TextView) linearLayout.findViewById(R.id.whose);
                users_name = (TextView) linearLayout.findViewById(R.id.users_name);
                final TextView favorited = (TextView) linearLayout.findViewById(R.id.how_favorited);//final
                commented = (TextView) linearLayout.findViewById(R.id.how_commented);
                final ImageView bookmark_sign = (ImageView) linearLayout.findViewById(R.id.bookmark_sign);//final必要

                company = (ImageView) linearLayout.findViewById(R.id.company_sign);

                //取得したアイコンの画像をimageに変換at usersicon
                users_icon = (ImageView) linearLayout.findViewById(R.id.users_icon);
                users_icon.setLayoutParams(new LinearLayout.LayoutParams(90, 90));

                try {
                    //画像のURLを直うち
                    url = new URL("http://fullfill.sakura.ne.jp" + any_users_icon[j]);
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

                //カードのeditの設定
                if(any_users_name[j].equals(name_of_user)) {//とりあえずANNA
                    ImageView edit_button = (ImageView) linearLayout.findViewById(R.id.to_edit_button);
                    edit_button.setImageResource(R.drawable.ic_create_white_18dp);
                    edit_button.setTag(j);
                    edit_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int n = (Integer) v.getTag();

                            goto_kotoba_edit(any_kotoba[n], any_whose[n], any_users_name[n], any_favorited[n], any_commented[n], any_users_icon[n], kotobaid.get(n));
                        }
                    });
                }

                //文字をset
                kotoba.setText(any_kotoba[j]);
                whose.setText("(" + any_whose[j] + ")");
                users_name.setText(any_users_name[j]);
                favorited.setText(any_favorited[j]);
                commented.setText(any_commented[j]);

                if(any_whose[j].equals("koreninamaehaarimasensen") || any_whose[j].equals("")){
                    whose.setText("");
                }else{
                    whose.setText("(" + any_whose[j] + ")");
                }

                //企業のマークをセット
                /*if (j==2) {
                    company.setImageResource(R.drawable.ic_verified_user_white_18dp);
                }*/

                //favoriteされているコトバにあらかじめブックマークをつける
                for(n=0; n<jaf; n++){
                    JSONObject json_data_fav_sel = jArray_f_select.getJSONObject(n);

                    if(kotobaid.get(j).equals(json_data_fav_sel.getString("kotobaid"))) {
                        bookmark_sign.setImageResource(R.drawable.ic_bookmark_white_middle);
                        favorite_or_not[j] = true;
                    }
                }


                //ブックマーククリック時の処理
                favorite = (ImageView) linearLayout.findViewById(R.id.favorited_icon);
                favorite.setTag(j);
                favorite.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int f = (Integer) v.getTag();

                        if (favorite_or_not[f] == false) {
                            //sendfavorite 送信確認済み ブックマークアイコンが押されたら実行
                            try {
                                nameValue_send_f.add(new BasicNameValuePair("userid", id_of_user));
                                nameValue_send_f.add(new BasicNameValuePair("kotobaid", kotobaid.get(f)));

                                HttpClient httpclient_send_f = new DefaultHttpClient(null);
                                HttpPost httppost_send_f = new HttpPost(send_http_favorite); //
                                httppost_send_f.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                                httppost_send_f.setEntity(new UrlEncodedFormEntity(nameValue_send_f, "UTF-8"));
                                HttpResponse response = httpclient_send_f.execute(httppost_send_f);
                            } catch (Exception e) {
                                Log.e("log_tag", "Error converting result " + e.toString());
                            }

                            bookmark_sign.setImageResource(R.drawable.ic_bookmark_white_middle);
                            ImageView fav = (ImageView) v.findViewById(R.id.favorited_icon);
                            AnimationSet set = new AnimationSet(true);
                            ScaleAnimation scale = new ScaleAnimation(1, 2f, 1, 2f);
                            set.addAnimation(scale);
                            TranslateAnimation translate = new TranslateAnimation(0, -20, 0, -30);
                            set.addAnimation(translate);
                            set.setDuration(300);
                            fav.startAnimation(set);

                            TranslateAnimation mark = new TranslateAnimation(0, 0, -70, 0);
                            mark.setDuration(300);
                            bookmark_sign.startAnimation(mark);

                            Toast toast = Toast.makeText(Searching.this, "ブックマークされました", Toast.LENGTH_SHORT);
                            toast.show();

                            int int_add_f = Integer.parseInt(any_favorited[f]) + 1;
                            any_favorited[f] = String.valueOf(int_add_f);
                            favorited.setText(any_favorited[f]);

                            favorite_or_not[f] = true;
                        } else {
                            try {
                                nameValue_delete_f.add(new BasicNameValuePair("userid", id_of_user));
                                nameValue_delete_f.add(new BasicNameValuePair("kotobaid", kotobaid.get(f)));

                                HttpClient httpclient_delete_f = new DefaultHttpClient(null);
                                HttpPost httppost_delete_f = new HttpPost(delete_http_favorite); //
                                httppost_delete_f.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                                httppost_delete_f.setEntity(new UrlEncodedFormEntity(nameValue_delete_f, "UTF-8"));
                                HttpResponse response = httpclient_delete_f.execute(httppost_delete_f);
                                //}
                            } catch (Exception e) {
                                Log.e("log_tag", "Error converting result " + e.toString());
                            }

                            ImageView fav = (ImageView) v.findViewById(R.id.favorited_icon);
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

                            Toast toast = Toast.makeText(Searching.this, "ブックマークが解除されました", Toast.LENGTH_SHORT);
                            toast.show();

                            int int_remove_f = Integer.parseInt(any_favorited[f]) - 1;
                            any_favorited[f] = String.valueOf(int_remove_f);
                            favorited.setText(any_favorited[f]);

                            favorite_or_not[f] = false;

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
                //カードに情報を付加
                cardView.setTag(j);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n = (Integer) v.getTag();
                        goto_clicked(any_kotoba[n], any_whose[n], any_users_name[n], any_favorited[n], any_commented[n], any_users_icon[n], kotobaid.get(n), favorite_or_not[n]);
                    }
                });

                //コメントクリック時の動作
                comment_icon = (ImageView) linearLayout.findViewById(R.id.comment_icon);
                comment_icon.setTag(j);
                comment_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n = (Integer) v.getTag();

                        Intent intent = new Intent(getApplication(), Comment.class);

                        intent.putExtra("cl_data1", any_kotoba[n]);
                        intent.putExtra("cl_data2", any_whose[n]);
                        intent.putExtra("cl_data3", any_users_name[n]);
                        intent.putExtra("cl_data4", any_favorited[n]);
                        intent.putExtra("cl_data5", any_commented[n]);
                        intent.putExtra("cl_data6", any_users_icon[n]);
                        intent.putExtra("cl_data7", kotobaid.get(n));
                        intent.putExtra("cl_data8", favorite_or_not[n]);


                        startActivity(intent);
                    }
                });

                searchboss.addView(linearLayout);

            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }

    private void searched_card_on(final String result) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout linearButton = (LinearLayout) inflater.inflate(R.layout.addbutton, null);
        final LinearLayout searchboss = (LinearLayout) this.findViewById(R.id.search_bosslayout);
        x = 0;
        searched_make_card(result, x);

        searchboss.addView(linearButton);

        FloatingActionButton button1 = (FloatingActionButton) findViewById(R.id.addbutton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchboss.removeView(linearButton);
                x++;
                searched_make_card(result, x);
                searchboss.addView(linearButton);
            }
        });
    }

    private void goto_clicked(String k, String w, String us, String f, String c, String icon, String kotobaid, Boolean favorite_or_not) {
        Intent intent = new Intent(getApplication(), Clickedcard.class);

        int requestCode = 1001;//遷移先から戻って来た際の識別コードの設定

        TransitionSet ts = new TransitionSet();
        ts.addTransition(new Fade());

        //cardonでセットしたTagの番号をClickedcardに引き渡し
        intent.putExtra("data1", k);
        intent.putExtra("data2", w);
        intent.putExtra("data3", us);
        intent.putExtra("data4", f);
        intent.putExtra("data5", c);
        intent.putExtra("data7", icon);
        intent.putExtra("data8", kotobaid);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            startActivityForResult(intent, requestCode, options.toBundle());
        }
    }

    private void goto_kotoba_edit(String k, String w, String us, String f, String c, String icon, String kotobaid){
        Intent intent = new Intent(this, Kotobaedit.class);

        intent.putExtra("ed_data1", k);
        intent.putExtra("ed_data2", w);
        intent.putExtra("ed_data3", us);
        intent.putExtra("ed_data4", f);
        intent.putExtra("ed_data5", c);
        intent.putExtra("ed_data6", icon);
        intent.putExtra("ed_data7", kotobaid);

        CardView cardView = (CardView)findViewById(R.id.cardView);
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, cardView, "hello awesome").toBundle());
    }
}
