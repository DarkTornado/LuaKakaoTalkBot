package com.darktornado.luakakaobot;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 0, 0, "알림 접근 허용");
        menu.add(0, 1, 0, "안드로이드 웨어 설치");
        menu.add(0, 2, 0, "API 목록");
        menu.add(0, 3, 0, "라이선스 정보");
        menu.add(0, 4, 0, "앱 정보 & 도움말");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case 0:
                Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intent);
                toast("알림 접근 허용 창으로 이동합니다.");
                break;
            case 1:
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.wearable.app");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
                toast("Play 스토어로 이동합니다.");
                break;
            case 2:
                showDialog("API 목록", "function response(room, msg, sender)\n\nend\n ->채팅이 수신되면 호출되는 함수. 매개변수는 각각 채팅이 수신된 방, 채팅 내용, 보낸 사람.\n\n" +
                        "print(msg);\n -> 해당 내용을 토스트 메시지로 출력.\n\n" +
                        "Bot.sendChat(room, msg);\n -> 특정 채팅방으로 채팅을 보냄. 매개변수는 채팅을 보낼 방 이름과 보낼 채팅의 내용.\n\n" +
                        "Bot.getVersion();\n -> 카카오톡 봇의 버전 반환.\n\n" +
                        "Utils.getWebText(url);\n -> 해당 url의 HTML 소스를 뜯어(?)옴.\n\n" +
                        "Utils.removeTags(str);\n -> 매개변수로 받은 문자열에서 HTML 태그들를 삭제한 문자열을 반환.\n\n" +
                        "Time.getYear();\n -> 현재 날짜(연도)을 가져옵니다.\n\n" +
                        "Time.getMonth();\n -> 현재 날짜(월)을 가져옵니다.\n\n" +
                        "Time.getDate();\n -> 현재 날짜(일)을 가져옵니다.\n\n" +
                        "Time.getDay();\n -> 현재 요일을 가져옵니다.\n\n" +
                        "Time.getHour();\n -> 현재 시간(시)을 가져옵니다.\n\n" +
                        "Time.getMinute();\n -> 현재 시간(분)을 가져옵니다.\n\n" +
                        "Time.getSecond();\n -> 현재 시간(초)을 가져옵니다.");
                break;
            case 3:
                startActivity(new Intent(this, LicenseActivity.class));
                break;
            case 4:
                showDialog("앱 정보 & 도움말", "앱 이름 : Lua 카카오톡 봇\n버전 : "+KakaoBot.VERSION+"\n제작자 : Dark Tornado\n\n 루아(Lua)라는 언어를 이용하여 카카오톡 봇을 만들 수 있는 앱입니다. 사실, 예제용(?)으로 만들어진 앱이며, MIT 라이선스(X11)가 적용되어 있습니다.");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout0 = new LinearLayout(this);
        layout0.setOrientation(1);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            toast("순순히 권한을 넘기라는 거다냥!");
            TextView txt = new TextView(this);
            txt.setText(" 권한을 순순히 념겼으면, 앱을 다시 시작하라는 거다냥!");
            txt.setTextSize(18);
            txt.setTextColor(Color.BLACK);
            layout0.addView(txt);
            int pad = dip2px(20);
            layout0.setPadding(pad, pad, pad, pad);
            ScrollView scroll = new ScrollView(this);
            scroll.addView(layout0);
            return;
        }

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(1);

        new File(sdcard + "/LuaKakaoTalkBot/").mkdirs();
        KakaoBot.initSource();

        int pad = dip2px(5);
        Switch on = new Switch(this);
        on.setText("카카오톡 봇 활성화");
        on.setTextSize(17);
        on.setTextColor(Color.BLACK);
        on.setPadding(pad, pad, pad, dip2px(15));
        on.setChecked(KakaoBot.loadSettings("botOn"));
        on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton swit, boolean onoff) {
                KakaoBot.saveSettings("botOn", onoff);
                if (onoff) toast("카카오톡 봇이 활성화되었습니다.");
                else toast("카카오톡 봇이 비활성화되었습니다.");
            }
        });
        layout0.addView(on);

        final EditText txt = new EditText(this);
        txt.setHint("소스를 입력하세요...");
        txt.setHintTextColor(Color.GRAY);
        txt.setTextColor(Color.BLACK);
        layout.addView(txt);

        LinearLayout lay2 = new LinearLayout(this);
        lay2.setOrientation(0);
        lay2.setWeightSum(2);
        Button save = new Button(this);
        save.setText("저장");
        save.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KakaoBot.saveFile(sdcard + "/LuaKakaoTalkBot/response.lua", txt.getText().toString());
                toast("저장되었습니다.");
            }
        });
        lay2.addView(save);

        Button load = new Button(this);
        load.setText("리로드");
        load.setLayoutParams(new LinearLayout.LayoutParams(-1, -2, 1));
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String src = KakaoBot.readFile(sdcard + "/LuaKakaoTalkBot/response.lua");
                if (src == null) {
                    toast("파일이 없습니다.");
                } else {
                    String result = KakaoTalkListener.loadScript(src);
                    if (result == null) toast("스크립트 리로드 완료.");
                    else toast("스크립트 리로드 실패\n" + result);
                }
            }
        });
        lay2.addView(load);

        layout0.addView(lay2);

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String value = KakaoBot.readFile(sdcard + "/LuaKakaoTalkBot/response.lua");
                if (value != null) runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txt.setText(value);
                    }
                });
            }
        }).start();

        ScrollView scroll = new ScrollView(this);
        scroll.addView(layout);
        layout0.addView(scroll);
        pad = dip2px(20);
        layout0.setPadding(pad, pad, pad, pad);
        setContentView(layout0);
    }

    public void showDialog(String title, String msg){
        try{
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(title);
            dialog.setMessage(msg);
            dialog.setNegativeButton("닫기", null);
            dialog.show();
        }catch (Exception e){
            toast(e.toString());
        }
    }

    private int dip2px(int dips) {
        return (int) Math.ceil(dips * this.getResources().getDisplayMetrics().density);
    }

    public void toast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
