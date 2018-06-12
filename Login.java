package io.haruya.fullfill;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Login extends Activity {

    /*String select_http_user = "http://fullfill.sakura.ne.jp/src/userSelect.php";

    private static String TAG = "LoginActivity";

    ArrayList<NameValuePair> nameValue_select = new ArrayList<NameValuePair>();

    EditText userID;
    EditText password;
    Button login_button;
    Button touroku_button;

    String SuserID;
    String Spassword;

    Intent intent;
    private LinearLayout inflateLayout;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            SharedPreferences saveinfo = getSharedPreferences("checkinfo", MODE_PRIVATE);
            SharedPreferences.Editor flag = saveinfo.edit();

            boolean lastcheck = saveinfo.getBoolean("checkinfo",false);

            if (lastcheck == false) {
                flag.clear().apply();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        userID = (EditText) findViewById(R.id.userID);
        password = (EditText) findViewById(R.id.password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        touroku_button = (Button) findViewById(R.id.tourokubutton);

        final CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        login_button = (Button) findViewById(R.id.login_button);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //Enter無効化
                login_button.performClick();
                return true;
            }
        });
        userID.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                //Enter無効化
                login_button.performClick();
                return true;
            }
        });

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ログイン処理
                boolean checkpoint = checkBox.isChecked();
                SharedPreferences saveinfo = getSharedPreferences("checkinfo", MODE_PRIVATE);
                SharedPreferences.Editor flag = saveinfo.edit();
                flag.putBoolean("checkinfo",checkpoint);
                flag.apply();
                login();
            }
        });

        touroku_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //新規登録処理
                touroku();
            }
        });

        checkBox.setOnClickListener(new  View.OnClickListener(){
            @Override
            public void onClick(View v){
                //チェック確認
                boolean checkpoint = checkBox.isChecked();
                if(checkpoint == true){
                    SharedPreferences saveinfo = getSharedPreferences("checkinfo", MODE_PRIVATE);
                    SharedPreferences.Editor flag = saveinfo.edit();
                    flag.putBoolean("checkinfo",true);
                    flag.apply();
                }else {
                    SharedPreferences saveinfo = getSharedPreferences("checkinfo", MODE_PRIVATE);
                    SharedPreferences.Editor flag = saveinfo.edit();
                    flag.clear().apply();

                    SharedPreferences idpass_info = getSharedPreferences("pref_data", MODE_PRIVATE);
                    SharedPreferences.Editor idpass_flag = idpass_info.edit();
                    idpass_flag.clear().apply();
                }
            }
        });

        SharedPreferences saveinfo = getSharedPreferences("checkinfo",MODE_PRIVATE);
        boolean lastcheck = saveinfo.getBoolean("checkinfo",false);

        if(lastcheck == true){
            Log.i(TAG,"認証成功");
            intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            auto_login();
        }
    }

    private void make_attention(String word){
        LinearLayout inf = (LinearLayout) findViewById(R.id.inflate_layout);
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        LinearLayout linear = (LinearLayout) inflater.inflate(R.layout.attention, null);
        TextView text = (TextView) linear.findViewById(R.id.attention);
        text.setText(word);
        inf.addView(linear);
    }

    public void login() {
        SuserID = userID.getText().toString();
        Spassword = password.getText().toString();

        InputStream is;
        String result_u = "";
        inflateLayout = (LinearLayout) findViewById(R.id.inflate_layout);
        inflateLayout.removeAllViews();

        //サーバーから情報を引き出す。
        try {
            Log.i(TAG,"サーバー接続");
            HttpClient httpclient = new DefaultHttpClient(null);
            HttpPost httppost = new HttpPost(select_http_user);
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost.setEntity(new UrlEncodedFormEntity(nameValue_select, "UTF-8"));
            HttpResponse response = httpclient.execute(httppost);

            HttpEntity entity = response.getEntity();
            is = entity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                // このif文大事。サーバーから受け取る文字列に空白があるとエラーを吐く..らしい。
                if(line.equals(" ")){
                    line = "\t";
                }
                sb.append(line + "\n");
            }
            is.close();

            result_u = sb.toString();

        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        try {
            JSONArray jArray_u = new JSONArray(result_u);

            ArrayList<String> userid = new ArrayList<String>();
            ArrayList<String> userpassword = new ArrayList<String>();//追加分
            ArrayList<String> id = new ArrayList<>();

            String finaluserid;
            String finalpassword;
            String finalid;


            //user情報を呼び出す
            for (int i = 0; i < jArray_u.length(); i++) {
                Log.i(TAG,"user情報照合");
                JSONObject json_data_user = jArray_u.getJSONObject(i);

                userid.add(json_data_user.getString("userid"));
                userpassword.add(json_data_user.getString("password"));

                id.add(json_data_user.getString("id"));

                finaluserid = id.get(i);
                finalpassword = userpassword.get(i);
                finalid = id.get(i);



                if (finaluserid.equals(SuserID) && finalpassword.equals(Spassword)) {
                    Log.i(TAG,"認証成功");

                    SharedPreferences idnumber_info = getSharedPreferences("number_data", MODE_PRIVATE);
                    SharedPreferences.Editor idnumber_data = idnumber_info.edit();
                    idnumber_data.putString("number_data",finalid);
                    idnumber_data.apply();

                    data_save();

                    intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);

                    return;
                }
            }
            make_attention("IDとPASSWORDが一致しません");
            Log.i(TAG,"認証失敗");
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }
    }

    public void data_save(){
        // ログイン成功時処理

        // ログインIDとログインパスワードのビューオブジェクトを取得
        EditText editTextLoginAccount = (EditText)findViewById(R.id.userID);
        EditText editTextLoginPass = (EditText)findViewById(R.id.password);

        // 「pref_data」という設定データファイルを読み込み
        SharedPreferences prefData = getSharedPreferences("pref_data", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefData.edit();

        // 入力されたログインIDとログインパスワード
        editor.putString("account", editTextLoginAccount.getText().toString());
        editor.putString("pass",editTextLoginPass.getText().toString());
        //editor.putString("pass", up);

        // 保存
        editor.apply();
        Log.i(TAG,"editor=="+editor);

    }


    public void auto_login(){
        // ログイン画面表示処理

        // 「pref_data」という設定データファイルを読み込み
        SharedPreferences prefData = getSharedPreferences("pref_data", MODE_PRIVATE);
        String account = prefData.getString("account", "");
        String pass = prefData.getString("pass", "");

        // 空チェック
        if (account != null && account.length() > 0) {
            // 保存済の情報をログインID欄に設定
            this.userID.setText(account);
        }
        //if (up != null && up.length() > 0)
        if(pass != null && pass.length()>0){
            // 保存済の情報をログインパスワード欄に設定
            this.password.setText(pass);
        }
    }

    public String get_id_number(){
        SharedPreferences id_number = getSharedPreferences("number_data", MODE_PRIVATE);
        String get_id_number = id_number.getString("number_data","");
        return get_id_number;
    }

    public void touroku() {
        intent = new Intent(getApplication(), Registration.class);
        startActivity(intent);
    }
*/
}




