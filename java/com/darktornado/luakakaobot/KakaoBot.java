package com.darktornado.luakakaobot;

import android.app.Application;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class KakaoBot extends Application {

    private static String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
    private static Context ctx;
    public static final String VERSION = "1.0";

    @Override
    public void onCreate() {
        super.onCreate();
        ctx = getApplicationContext();
    }

    public static Context getAppContext() {
        return ctx;
    }

    public static String readAsset(InputStream is) {
        try {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String str = br.readLine();
            String line = "";
            while ((line = br.readLine()) != null) {
                str += "\n" + line;
            }
            isr.close();
            br.close();
            return str;
        } catch (Exception e) {
            //toast(e.toString());
        }
        return null;
    }

    public static String readFile(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) return null;
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String str = br.readLine();
            String line = "";
            while ((line = br.readLine()) != null) {
                str += "\n" + line;
            }
            fis.close();
            isr.close();
            br.close();
            return str;
        } catch (Exception e) {
            //toast(e.toString());
        }
        return null;
    }

    public static void saveFile(String path, String value) {
        try {
            File file = new File(path);
            FileOutputStream fos = new java.io.FileOutputStream(file);
            fos.write(value.getBytes());
            fos.close();
        } catch (Exception e) {
            //toast(e.toString());
        }
    }

    public static String readData(String name) {
        return readFile(sdcard + "/LuaKakaoTalkBot/" + name + ".txt");
    }

    public static void saveData(String name, String value) {
        saveFile(sdcard + "/LuaKakaoTalkBot/" + name + ".txt", value);
    }

    public static boolean loadSettings(String name) {
        String cache = readData(name);
        if (cache == null) return false;
        return cache.equals("true");
    }

    public static void saveSettings(String name, boolean settings) {
        saveData(name, String.valueOf(settings));
    }

    public static void initSource() {
        File file = new File(sdcard + "/LuaKakaoTalkBot/response.lua");
        if(!file.exists()) saveFile(sdcard + "/LuaKakaoTalkBot/response.lua", "function response(room, msg, sender)\n\nend");
    }

    public static void toast(String msg) {
        Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
    }

    public static String getDataFromServer(String link){
        try{
            URL url = new URL(link);
            URLConnection con = url.openConnection();
            if(con!=null) {
                con.setConnectTimeout(5000);
                con.setUseCaches(false);
                InputStreamReader isr = new InputStreamReader(con.getInputStream());
                BufferedReader br = new BufferedReader(isr);
                String str = br.readLine();
                String line = "";
                while((line = br.readLine()) != null){
                    str += "\n" + line;
                }
                br.close();
                isr.close();
                return str;
            }
        }
        catch(Exception e) {
            //toast(e.toString());
        }
        return null;
    }

}
