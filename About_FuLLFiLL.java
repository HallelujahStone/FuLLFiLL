package io.haruya.fullfill;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by haruya.i on 2016/12/15.
 */

public class About_FuLLFiLL extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_fullfill);

        TextView aboutFullfill = (TextView) findViewById(R.id.about_fullfill);

        aboutFullfill.setText("アプリ名:FuLLFiLL-無料名言共有アプリ\n" +
                "\n" +
                "名言共有、あなたのコトバを持ち歩く。\n" +
                "\n" +
                "詳細：\"FuLLFiLL\"は新しい形の名言アプリです。\n" +
                "あなたの気に入ったコトバを持ち歩き、いつでも、どこでも、コトバを見ることができます。\n" +
                "貴方自身もコトバの発信者になることが可能です。\n" +
                "美しい言葉で日常を豊かに彩りましょう。\n" +
                "\n" +
                "〈主な機能〉\n" +
                "・名言保存機能\n" +
                "\n" +
                "・名言発信機能\n" +
                "\n" +
                "・ランキング\n" +
                "\n" +
                "etc..\n" +
                "\n" +
                "\n" +
                "〈その他〉");
    }
}
