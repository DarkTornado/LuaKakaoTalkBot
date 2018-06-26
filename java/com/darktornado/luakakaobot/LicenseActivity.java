package com.darktornado.luakakaobot;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        if(bar!=null) bar.setTitle("라이선스 정보");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        loadLicenseInfo(layout, "Lua 카카오톡 봇", "LuaKakaoBot", "MIT License (X11)", "Dark Tornado", true);
        loadLicenseInfo(layout, "App Icon", "icon", "Apache License 2.0", "Android Studio");
        loadLicenseInfo(layout, "Lua", "Lua", "MIT License", "Lua.org & PUC-Rio");
        loadLicenseInfo(layout, "LuaJ", "LuaJ", "MIT License", "LuaJ");

        int pad = dip2px(20);
        layout.setPadding(pad, dip2px(10), pad, pad);
        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        scroll.setBackgroundColor(Color.WHITE);
        setContentView(scroll);
    }

    private void loadLicenseInfo(LinearLayout layout, String name, String fileName, String license, String dev){
        loadLicenseInfo(layout, name, fileName, license, dev, false);
    }

    private void loadLicenseInfo(LinearLayout layout, String name, String fileName, final String license, String dev, boolean tf) {
        int pad = dip2px(10);
        TextView title = new TextView(this);
        if (tf) title.setText(Html.fromHtml("<b>" + name + "<b>"));
        else title.setText(Html.fromHtml("<br><b>" + name + "<b>"));
        title.setTextSize(24);
        title.setTextColor(Color.BLACK);
        title.setPadding(pad, 0, pad, dip2px(1));
        layout.addView(title);
        TextView subtitle = new TextView(this);
        subtitle.setText("  by " + dev + ", " + license);
        subtitle.setTextSize(20);
        subtitle.setTextColor(Color.BLACK);
        subtitle.setPadding(pad, 0, pad, pad);
        layout.addView(subtitle);

        final String value = loadLicense(fileName);
        TextView txt = new TextView(this);
        txt.setText(value);
        txt.setTextSize(17);
        txt.setTextColor(Color.BLACK);
        txt.setPadding(pad, pad, pad, pad);
        txt.setBackgroundColor(Color.argb(50, 0, 0, 0));
        layout.addView(txt);
    }

    private String loadLicense(String name){
        try {
            return KakaoBot.readAsset(this.getAssets().open(name + ".txt"));
        }catch (Exception e){
            toast(e.toString());
            return "라이선스 정보 불러오기 실패";
        }
    }


    private int dip2px(int dips){
        return (int)Math.ceil(dips*this.getResources().getDisplayMetrics().density);
    }

    public void toast(final String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
