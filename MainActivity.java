package io.haruya.fullfill;

import android.accounts.Account;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.transition.Fade;
import android.support.transition.TransitionSet;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActionBarDrawerToggle vDrawerToggle;

    String send_http_kotoba = "http://fullfill.sakura.ne.jp/src/kotobaInsert.php";
    String select_http_kotoba = "http://fullfill.sakura.ne.jp/src/kotobaSelect.php";
    String select_http_user = "http://fullfill.sakura.ne.jp/src/userSelect.php";
    String select_http_company = "http://fullfill.sakura.ne.jp/src/companySelect.php";
    String get_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteGet.php";
    String delete_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteDelete.php";
    String get_http_comment = "http://fullfill.sakura.ne.jp/src/commentGet.php";
    String get_http_firstrank = "http://fullfill.sakura.ne.jp/src/firstRank.php";
    String get_http_secondrank = "http://fullfill.sakura.ne.jp/src/secondRank.php";
    String get_http_thirdrank = "http://fullfill.sakura.ne.jp/src/thirdRank.php";
    String send_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteInsert.php";
    String select_http_favorite = "http://fullfill.sakura.ne.jp/src/favoriteSelect.php";
    String select_http_my = "http://fullfill.sakura.ne.jp/src/mySelect.php";
    String count_http_favorite = "http://fullfill.sakura.ne.jp//src/favoritecount.php";

    String[] get_rank = {get_http_firstrank, get_http_secondrank, get_http_thirdrank};

    ArrayList<NameValuePair> nameValue_send = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_send_k = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_send_u = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_send_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_get_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_get_c = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_rankget_c = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_rankget_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_rankselect_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_select = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_select_u = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_select_s = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_select_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_delete_f = new ArrayList<NameValuePair>();
    ArrayList<NameValuePair> nameValue_select_com = new ArrayList<NameValuePair>();//もともとonCreateの中にあったから不具合があるかもしれん
    ArrayList<NameValuePair> nameValue_rank = new ArrayList<NameValuePair>();


    HttpClient httpclient = new DefaultHttpClient(null);
    HttpResponse response;
    HttpEntity entity;
    BufferedReader reader;

    int i, j, l, m, n, x; //for文用→処理速度の向上に

    TextView kotoba, whose, users_name,  commented;
    ImageView company, users_icon, comment_icon, favorite;

    //Login login = new Login();

    final String id_of_user = "1";//login.get_id_number();//使用者のuseridをとりあえず定義

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //swipe to refreshによる更新動作
        final SwipeRefreshLayout mSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefresh.setRefreshing(false);
                final LinearLayout cardLinear = (LinearLayout) findViewById(R.id.cardLinear);
                cardLinear.removeAllViews();
                card_on();//あくまで並列の処理は出来ていない、非同期処理
                Toast toast = Toast.makeText(MainActivity.this, "更新されました", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        toolbar_on();//toolbar（上のActionbarみたいなやつ ）を設置
        initTabs();//Tabを設定

        card_on();

        posting();//投稿画面の設定


        ranking_on();
        your_book();//あなたのコトバへ
        your_post();//投稿履歴画面へ
        account_settings();//アカウント設定画面へ

        //fullfillをクリックしたらクルクルさせるちょっとした遊び心
        final ImageView fullfill = (ImageView)findViewById(R.id.toolbar_icon);
        fullfill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(fullfill, "rotation", 0f, 360f);
                objectAnimator.setDuration(800);
                objectAnimator.start();
            }
        });

    }


    //toolbarを設置
    protected void toolbar_on() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        toolbar.setNavigationIcon(R.drawable.ic_view_headline_white_18dp);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        vDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.empty, R.string.empty);
        vDrawerToggle.setDrawerIndicatorEnabled(true);
        drawer.setDrawerListener(vDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        NavigationView navigationView = (NavigationView) findViewById(R.id.main_navigation_view2);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.activity_main);
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    /*case R.id.navigation_login:
                        Intent intent_log = new Intent(getApplication(), Login.class);
                        startActivity(intent_log);

                        Toast toast = Toast.makeText(MainActivity.this, "ログアウトしました", Toast.LENGTH_SHORT);
                        toast.show();
                        return true;

                    case R.id.navigation_account_setting:
                        Intent intent_ac = new Intent(getApplication(), Accountsettings.class);
                        startActivity(intent_ac);
                        return true;

                    case R.id.navigation_yourbook:
                        Intent intent_yb = new Intent(getApplication(), Yourbook.class);
                        startActivity(intent_yb);
                        return true;

                    case R.id.navigation_registration:
                        Intent intent_reg = new Intent(getApplication(), Registration.class);
                        startActivity(intent_reg);
                        return true;*/


                    case R.id.navigation_terms_of_service:
                        Intent intent_t_o_s = new Intent(getApplication(), Terms_of_service.class);
                        startActivity(intent_t_o_s);
                        return true;

                    case R.id.navigation_privacy_policy:
                        Intent intent_p_p = new Intent(getApplication(), Privacy_policy.class);
                        startActivity(intent_p_p);
                        return true;

                    case R.id.navigation_about_fullfill:
                        Intent intent_a_fullfill = new Intent(getApplication(), About_FuLLFiLL.class);
                        startActivity(intent_a_fullfill);
                        return true;

                    default:
                        return true;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.non_active_searchview, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//OptionItem(検索アイコン)がクリックされた時の動作

        //TransitionのアニメーションをFadeとして定義
        TransitionSet ts = new TransitionSet();
        ts.addTransition(new Fade());

        switch (item.getItemId()) {
            case R.id.non_activity_search:
                Intent intent = new Intent(MainActivity.this, Searching.class);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                return true;
            default:
                break;
        }
        return vDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        vDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        vDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected void initTabs() {
        try {
            TabHost tabhost = (TabHost) findViewById(android.R.id.tabhost);
            tabhost.setup();

            TabHost.TabSpec tab1 = tabhost.newTabSpec("tab1");
            tab1.setIndicator("", getResources().getDrawable(R.drawable.tab1_image));
            tab1.setContent(R.id.tab1);
            tabhost.addTab(tab1);

            TabHost.TabSpec tab2 = tabhost.newTabSpec("tab2");
            tab2.setIndicator("", getResources().getDrawable(R.drawable.tab2_image));
            tab2.setContent(R.id.tab2);
            tabhost.addTab(tab2);

            TabHost.TabSpec tab3 = tabhost.newTabSpec("tab3");
            tab3.setIndicator("", getResources().getDrawable(R.drawable.tab3_image));
            tab3.setContent(R.id.tab3);
            tabhost.addTab(tab3);

            TabHost.TabSpec tab4 = tabhost.newTabSpec("tab4");
            tab4.setIndicator("", getResources().getDrawable(R.drawable.tab4_image));
            tab4.setContent(R.id.tab4);
            tabhost.addTab(tab4);

            tabhost.setCurrentTab(0);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    private void make_card(int x) {
        InputStream is;
        String result = "";
        String result_u = "";
        String result_f = "";
        String result_get_f = "";
        String result_get_c = "";
        String result_com = "";

        //kotobaSelectの値の初期化
        HttpPost httppost_kotoba_select = new HttpPost(select_http_kotoba);
        StringBuilder sb_kotoba_select = new StringBuilder();
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

        String offset_x = String.valueOf(x);

        //kotobaSelect
        try {
            nameValue_send.add(new BasicNameValuePair("offset", offset_x));

            httppost_kotoba_select.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost_kotoba_select.setEntity(new UrlEncodedFormEntity(nameValue_send, "UTF-8"));
            response = httpclient.execute(httppost_kotoba_select);

            entity = response.getEntity();
            is = entity.getContent();

            reader = new BufferedReader(new InputStreamReader(is), 8);//変更中
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb_kotoba_select.append(line + "\n");
            }
            is.close();

            result = sb_kotoba_select.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        try {//追加分userSelect
            httppost_user_select.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost_user_select.setEntity(new UrlEncodedFormEntity(nameValue_select_u, "UTF-8"));
            response = httpclient.execute(httppost_user_select);

            entity = response.getEntity();
            is = entity.getContent();

            reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
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
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb_favorite_select.append(line + "\n");
            }
            is.close();

            result_f = sb_favorite_select.toString();

        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }


        try {
            final LinearLayout cardLinear = (LinearLayout) this.findViewById(R.id.cardLinear);

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
                userid.add(u_id);
                username.add(json_data_user.getString("username"));
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
            final String[] any_users_id = new String[jau];
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
                any_users_id[j] = userid.get(userid.indexOf(kotoba_userid.get(j)));
                any_whose[j] = json_data.getString("whose");

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
                if(any_users_id[j].equals(id_of_user)) {//とりあえずANNA
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

                            Toast toast = Toast.makeText(MainActivity.this, "ブックマークされました", Toast.LENGTH_SHORT);
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

                            Toast toast = Toast.makeText(MainActivity.this, "ブックマークが解除されました", Toast.LENGTH_SHORT);
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

                cardLinear.addView(linearLayout);
            }

        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
    }


    private void card_on() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout linearButton = (LinearLayout) inflater.inflate(R.layout.addbutton, null);
        final LinearLayout cardLinear = (LinearLayout) this.findViewById(R.id.cardLinear);
        x = 0;
        make_card(x);

        cardLinear.addView(linearButton);

        FloatingActionButton button1 = (FloatingActionButton) findViewById(R.id.addbutton);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardLinear.removeView(linearButton);
                x++;
                make_card(x);
                cardLinear.addView(linearButton);
            }
        });
    }



    private void ranking_on() {
        int x=-1;
        int genre = 1;
        int ranking = 3;
        for(int i=0; i < genre; i++) {
            x++;
            make_ranking_title(i, x);
            for(int j=0; j < ranking; j++){
                x++;
                make_ranking_card(j, x);
            }
        }

    }

    private void make_ranking_title(int i,int x){
        final LinearLayout rankingLinear = (LinearLayout) this.findViewById(R.id.rankingLinear);

        LayoutInflater inflater_title = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout linearLayout_title = (LinearLayout) inflater_title.inflate(R.layout.ranking_title, null);

        TextView textView = (TextView) linearLayout_title.findViewById((R.id.ranking_title));
        String si = String.valueOf("総合");
        textView.setText(si);

        rankingLinear.addView(linearLayout_title, x);
    }

    private void make_ranking_card(final int j,int x){
        String result_rank_f = "";

        InputStream is;

        URL url;
        InputStream istream;
        final LinearLayout rankingLinear = (LinearLayout) this.findViewById(R.id.rankingLinear);
        LayoutInflater inflater_card = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        final LinearLayout linearLayout_card = (LinearLayout) inflater_card.inflate(R.layout.ranking_cardlist, null);
        final CardView cardView = (CardView) linearLayout_card.findViewById(R.id.card_in_ranking);


        try {
            String result_rank = "";
            String result_count = "";

            // コトバ情報の取得
            try{
                HttpClient httpclient = new DefaultHttpClient(null);
                HttpPost g_httppost = new HttpPost(get_rank[j]);
                g_httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                g_httppost.setEntity(new UrlEncodedFormEntity(nameValue_rank, "UJIS"));
                HttpResponse response = httpclient.execute(g_httppost);

                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                result_rank = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }
            JSONArray jArray_rank = new JSONArray(result_rank);
            JSONObject json_rank = jArray_rank.getJSONObject(0);

            final String rank_users_name = json_rank.getString("username");
            final String rank_id = json_rank.getString("id");
            final String rank_whose = json_rank.getString("whose");
            final String rank_kotoba = json_rank.getString("kotoba");
            final String rank_icon = json_rank.getString("image");
            String s = String.valueOf(j);

            final boolean[] favorite_or_not = new boolean[j+1];//長さをおいおい調整
            final String kotobaid = rank_id;

            // favoritecountの取得
            try {
                nameValue_rankget_f.add(new BasicNameValuePair("kotobaid", rank_id));

                HttpClient httpclient = new DefaultHttpClient(null);
                HttpPost f_httppost = new HttpPost(get_http_favorite);
                f_httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                f_httppost.setEntity(new UrlEncodedFormEntity(nameValue_rankget_f, "UJIS"));
                HttpResponse response = httpclient.execute(f_httppost);

                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                result_count = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }

            JSONArray jArray_f_count = new JSONArray(result_count);
            JSONObject json_f_count = jArray_f_count.getJSONObject(0);

            final String rank_favorited = json_f_count.getString("favoritecount");



            // commentcountの取得
            try {
                nameValue_rankget_c.add(new BasicNameValuePair("kotobaid", rank_id));

                HttpClient httpclient = new DefaultHttpClient(null);
                HttpPost c_httppost = new HttpPost(get_http_comment);
                c_httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                c_httppost.setEntity(new UrlEncodedFormEntity(nameValue_rankget_c, "UJIS"));
                HttpResponse response = httpclient.execute(c_httppost);

                HttpEntity entity = response.getEntity();
                is = entity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                result_count = sb.toString();
            } catch (Exception e) {
                Log.e("log_tag", "Error converting result " + e.toString());
            }

            JSONArray jArray_c_count = new JSONArray(result_count);
            JSONObject json_c_count = jArray_c_count.getJSONObject(0);

            final String rank_commented = json_c_count.getString("commentcount");


            // inflateレイアウト用のText, Imageの定義
            final TextView kotoba = (TextView) linearLayout_card.findViewById(R.id.kotoba_r);
            TextView whose = (TextView) linearLayout_card.findViewById(R.id.whose_r);
            TextView users_name = (TextView) linearLayout_card.findViewById(R.id.users_name_r);
            final TextView favorited = (TextView) linearLayout_card.findViewById(R.id.how_favorited_r);
            TextView commented = (TextView) linearLayout_card.findViewById(R.id.how_commented_r);
            final ImageView bookmark_sign = (ImageView) linearLayout_card.findViewById(R.id.bookmark_sign_r);
            ImageView icon = (ImageView)linearLayout_card.findViewById(R.id.users_icon_r);
            company = (ImageView) linearLayout_card.findViewById(R.id.company_sign_r);




            //取得したアイコンの画像をimageに変換at usersicon
            icon.setLayoutParams(new LinearLayout.LayoutParams(90, 90));

            try {
                //画像のURLを直うち
                url = new URL("http://fullfill.sakura.ne.jp" + rank_icon);
                //インプットストリームで画像を読み込む
                istream = url.openStream();
                //読み込んだファイルをビットマップに変換
                Bitmap oBmp = BitmapFactory.decodeStream(istream);
                //ビットマップをImageViewに設定
                icon.setImageBitmap(oBmp);
                //インプットストリームを閉じる
                istream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*//カードのeditの設定
            if(rank_whose.equals("ANNA")) {//とりあえずANNA
                ImageView edit_button = (ImageView) linearLayout_card.findViewById(R.id.to_edit_button_r);
                edit_button.setImageResource(R.drawable.ic_create_white_18dp);
                edit_button.setTag(i);
                edit_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int n = (Integer) v.getTag();

                        goto_kotoba_edit(rank_kotoba, rank_whose, rank_users_name, rank_favorited, rank_commented, rank_icon, kotobaid);
                    }
                });
            }*/

            //文字をset
            kotoba.setText(rank_kotoba);
            users_name.setText(rank_users_name);
            favorited.setText(rank_favorited);
            commented.setText(rank_commented);

            if(rank_whose.equals("koreninamaehaarimasensen") || rank_whose.equals("")){
                whose.setText("");
            }else{
                whose.setText("(" + rank_whose + ")");
            }


            /*//企業のマークをセット
            if (j==2) {
                company.setImageResource(R.drawable.ic_verified_user_white_18dp);
            }*/

           favorite_or_not[j]=false;

            //ブックマーククリック時の処理
            favorite = (ImageView) linearLayout_card.findViewById(R.id.favorited_icon_r);
            favorite.setTag(j);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int f = (Integer) v.getTag();

                    if (favorite_or_not[j] == false) {

                        //sendfavorite 送信確認済み ブックマークアイコンが押されたら実行
                        try {
                            nameValue_send_f.add(new BasicNameValuePair("userid", id_of_user));
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

                        bookmark_sign.setImageResource(R.drawable.ic_bookmark_white_middle);
                        ImageView fav = (ImageView) v.findViewById(R.id.favorited_icon_r);
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

                        Toast toast = Toast.makeText(MainActivity.this, "ブックマークされました", Toast.LENGTH_SHORT);
                        toast.show();

                        int how_favo_i = Integer.parseInt(rank_favorited) + 1;
                        String how_favo_s = String.valueOf(how_favo_i);
                        favorited.setText(how_favo_s);

                        favorite_or_not[j] = true;
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

                        ImageView fav = (ImageView) v.findViewById(R.id.favorited_icon_r);
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

                        Toast toast = Toast.makeText(MainActivity.this, "ブックマークが解除されました", Toast.LENGTH_SHORT);
                        toast.show();

                        favorited.setText(rank_favorited);

                        favorite_or_not[j] = false;

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
                    goto_clicked(rank_kotoba, rank_whose, rank_users_name, rank_favorited, rank_commented, rank_icon , kotobaid, favorite_or_not[j]);
                }
            });

            //コメントクリック時の動作
            comment_icon = (ImageView) linearLayout_card.findViewById(R.id.comment_icon_r);
            comment_icon.setTag(j);
            comment_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int n = (Integer) v.getTag();

                    Intent intent = new Intent(getApplication(), Comment.class);

                    intent.putExtra("cl_data1", rank_kotoba);
                    intent.putExtra("cl_data2", rank_whose);
                    intent.putExtra("cl_data3", rank_users_name);
                    intent.putExtra("cl_data4", rank_favorited);
                    intent.putExtra("cl_data5", rank_commented);
                    intent.putExtra("cl_data6", rank_icon);
                    intent.putExtra("cl_data7", kotobaid);

                    startActivity(intent);
                }
            });

            ImageView crown = (ImageView) linearLayout_card.findViewById(R.id.crown);

            if(j==0){
                crown.setImageDrawable(getResources().getDrawable(R.drawable.gold));
            } else if(j==1) {
                crown.setImageDrawable(getResources().getDrawable(R.drawable.silver));
            } else if(j==2) {
                crown.setImageDrawable(getResources().getDrawable(R.drawable.bronze));
            }

        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
        rankingLinear.addView(linearLayout_card, x);
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

    protected void your_book() {
        LinearLayout linear = (LinearLayout) this.findViewById(R.id.to_your_book);
        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Yourbook.class);
                startActivity(intent);
            }
        });
    }

    protected void your_post() {
        LinearLayout linear = (LinearLayout) this.findViewById(R.id.to_your_post);
        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), Yourpost.class);
                startActivity(intent);
            }
        });
    }

    protected void account_settings() {
        LinearLayout linear = (LinearLayout) this.findViewById(R.id.to_account_settings);
        linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getApplication(), Accountsettings.class);
                //startActivity(intent);//現段階ではアカウント設定に飛べないように

                Toast toast = Toast.makeText(MainActivity.this, "現在工事中です", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    private void posting() {
        //投稿画面のスピナーの呼び込み
        ArrayAdapter adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = (Spinner)findViewById(R.id.spinner_posting);
        spinner.setAdapter(adapter);

        String genre = (String)spinner.getSelectedItem();

        FloatingActionButton FA_button = (FloatingActionButton) findViewById(R.id.post_button);
        FA_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText yourwords = (EditText) findViewById(R.id.post_yourwords);
                EditText whose = (EditText) findViewById(R.id.post_whose);

                SpannableStringBuilder str1 = (SpannableStringBuilder) yourwords.getText();
                String posted_yourwords = str1.toString();
                SpannableStringBuilder str2 = (SpannableStringBuilder) whose.getText();
                String posted_whose = str2.toString();

                TextView danger = (TextView)findViewById(R.id.danger_text);

                //switch(posted_yourwords){
                // 入力内容の正否をチェック。アカウント設定の使い回し
                if (posted_yourwords.equals("")){
                    danger.setText("コトバの欄が未記入です。");
                    return;
                }

                if(posted_yourwords.length() > 140) {
                    danger.setText("コトバは140字以内でお願いします。");
                    return;
                }

                if(posted_whose.length() > 100) {
                    danger.setText("誰が言ったかは100字以内でお願いします。");
                    return;
                }
                else if(posted_yourwords.equals("\'") || posted_yourwords.equals("\"")) {
                    danger.setText("\'" + "や" + "\"" + "は使用できません。");
                    return;
                }

                if(posted_whose.equals("")){
                    posted_whose = "koreninamaehaarimasensen";
                }

                String genre = (String)spinner.getSelectedItem();

                try {
                    nameValue_send.add(new BasicNameValuePair("userid", id_of_user));
                    nameValue_send.add(new BasicNameValuePair("kotoba", posted_yourwords));
                    nameValue_send.add(new BasicNameValuePair("whose", posted_whose));
                    nameValue_send.add(new BasicNameValuePair("genre", genre));

                    HttpClient httpclient = new DefaultHttpClient(null);
                    HttpPost httppost = new HttpPost(send_http_kotoba); //
                    httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValue_send, "UJIS"));
                    HttpResponse response = httpclient.execute(httppost);

                    Toast toast = Toast.makeText(MainActivity.this, "投稿が完了しました", Toast.LENGTH_SHORT);
                    toast.show();
                } catch (Exception e) {
                    Log.e("log_tag", "Error converting result " + e.toString());
                }

                /*final LinearLayout cardLinear = (LinearLayout) findViewById(R.id.cardLinear);
                cardLinear.removeAllViews();
                card_on();*/
            }
        });
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

    /*public String getid(){
        SharedPreferences saveid = getSharedPreferences("number_data", MODE_PRIVATE);
        String getid = saveid.getString("number_data", "");

        return getid;
    }

    public String get_id_number(){
        SharedPreferences id_number = getSharedPreferences("number_data", MODE_PRIVATE);
        String get_id_number = id_number.getString("number_data","");
        return get_id_number;
    }*/

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        }
        else{
                /*
                Toast toast = Toast.makeText(MainActivity.this, "ログイン画面に戻れなくなりました", Toast.LENGTH_SHORT);
                toast.show();
                return false;
                */
            // 確認ダイアログの生成
            LayoutInflater inflater = (LayoutInflater)this.getSystemService(
                    LAYOUT_INFLATER_SERVICE);
            AlertDialog.Builder alertDlg = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            alertDlg.setTitle("確認");
            alertDlg.setMessage("アプリを終了してもよろしいですか？");
            alertDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    MainActivity.this.finish();
                    moveTaskToBack(true);
                }
            });
            alertDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Toast toast = Toast.makeText(MainActivity.this, "了解", Toast.LENGTH_SHORT);
                    //toast.show();
                }
            });

            // 表示
            alertDlg.create().show();
        }
        return false;
    }
}