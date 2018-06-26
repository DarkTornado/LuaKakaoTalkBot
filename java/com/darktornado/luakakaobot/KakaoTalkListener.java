package com.darktornado.luakakaobot;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.widget.Toast;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;
import org.luaj.vm2.lib.jse.CoerceJavaToLua;
import org.luaj.vm2.lib.jse.JsePlatform;

import java.util.HashMap;

import static org.luaj.vm2.LuaValue.NIL;

public class KakaoTalkListener extends NotificationListenerService {

    private static String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static Handler handler = new Handler();
    public static Context ctx;
    private static Globals globals;
    public static HashMap<String, Session> sessions = new HashMap<>();

    @Override
    public void onCreate(){
        super.onCreate();
        ctx = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);
        if (!KakaoBot.loadSettings("botOn")) return;
        if (sbn.getPackageName().equals("com.kakao.talk")) {
            try {
                Notification.WearableExtender wExt = new Notification.WearableExtender(sbn.getNotification());
                for (Notification.Action act : wExt.getActions()) {
                    if (act.getRemoteInputs() != null && act.getRemoteInputs().length > 0) {
                        if (act.title.toString().toLowerCase().contains("reply") ||
                                act.title.toString().toLowerCase().contains("답장")) {
                            Bundle data = sbn.getNotification().extras;
                            String room, sender, msg;
                            boolean isGroupChat = data.get("android.text") instanceof SpannableString;
                            if (Build.VERSION.SDK_INT > 23) {
                                room = data.getString("android.summaryText");
                                if (room == null) isGroupChat = false;
                                else isGroupChat = true;
                                sender = data.get("android.title").toString();
                                msg = data.get("android.text").toString();
                            } else {
                                room = data.getString("android.title");
                                if (isGroupChat) {
                                    String html = Html.toHtml((Spanned) data.get("android.text"));
                                    sender = Html.fromHtml(html.split("<b>")[1].split("</b>")[0]).toString();
                                    msg = Html.fromHtml(html.split("</b>")[1].split("</p>")[0].substring(1)).toString();
                                } else {
                                    sender = room;
                                    room = null;
                                    msg = data.get("android.text").toString();
                                }
                            }
                            chatHook(sender, msg.trim(), room, isGroupChat, act);
                        }
                    }
                }
            } catch (Exception e) {
                KakaoBot.toast(e.toString() + "\nAt:" + e.getStackTrace()[0].getLineNumber());
            }
        }
    }

    public void chatHook(String sender, String msg, String room, boolean isGroupChat, Notification.Action act) {
        try {
            //toast("sender: " + sender + "\nmsg: " + msg + "\nroom: " + room + "\nisGroupChat: " + isGroupChat);
            if (room == null) sessions.put(sender, new Session(sender, new Replier(act)));
            else sessions.put(room, new Session(room, new Replier(act)));
            callResponseMethod(room, msg, sender);
        } catch (Exception e) {
            toast(e.toString());
        }
    }

    public static String loadScript(String src) {
        try {
            globals = JsePlatform.standardGlobals();
            globals.set("print", CoerceJavaToLua.coerce(new Print()));

            LuaTable[] table = new LuaTable[3];

            LuaValue bot = CoerceJavaToLua.coerce(new Bot());
            globals.set("Bot", bot);
            table[0] = new LuaTable();
             table[0].set("sendChat", new Bot.SendChat());
            table[0].set("getVersion", new Bot.GetVersion());
            table[0].set("__index", table[0]);
            bot.setmetatable(table[0]);

            LuaValue utils = CoerceJavaToLua.coerce(new LuaApi.Utils());
            globals.set("Utils", utils);
            table[1] = new LuaTable();
            table[1].set("getWebText", new LuaApi.Utils.GetWebText());
            table[1].set("removeTags", new LuaApi.Utils.RemoveTags());
            table[1].set("__index", table[1]);
            utils.setmetatable(table[1]);


            LuaValue time = CoerceJavaToLua.coerce(new LuaApi.Time());
            globals.set("Time", time);
            table[2] = new LuaTable();
            table[2].set("getYear", new LuaApi.Time.GetYear());
            table[2].set("getMonth", new LuaApi.Time.GetMonth());
            table[2].set("getDate", new LuaApi.Time.GetDate());
            table[2].set("getDay", new LuaApi.Time.GetDay());
            table[2].set("getHour", new LuaApi.Time.GetHour());
            table[2].set("getMinute", new LuaApi.Time.GetMinute());
            table[2].set("getSecond", new LuaApi.Time.GetSecond());
            table[2].set("__index", table[2]);
            time.setmetatable(table[2]);

            LuaValue chunk = globals.load(src);
            chunk.call();

            return null;
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static void callResponseMethod(String room, String msg, String sender) {
        try {
            LuaValue func = globals.get("response");
            if(room==null) func.call(NIL, LuaValue.valueOf(msg), LuaValue.valueOf(sender));
            else func.call(LuaValue.valueOf(room), LuaValue.valueOf(msg), LuaValue.valueOf(sender));
        } catch (Exception e) {
            //String ee[] = e.toString().split(":");
            //String error = ee[0] + " " + ee[ee.length - 1];
            toast(ctx, "이벤트 리스너(response) 호출 실패\n" + e);
        }
    }

    public void toast(final String msg){
         Intent intent = new Intent(this, ToastService.class);
        intent.putExtra("toast", msg);
        startService(intent);
    }

    private static void toast(Context ctx, String msg) {
        Intent intent = new Intent(ctx, ToastService.class);
        intent.putExtra("toast", msg);
        ctx.startService(intent);
    }

    public static void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }

    private static class Print extends OneArgFunction {

        @Override
        public LuaValue call(final LuaValue msg) {
            KakaoTalkListener.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Toast.makeText(ctx, msg.tojstring(), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        KakaoBot.saveData("Fuck", e.toString());
                    }
                }
            });
            return msg;
        }
    }

    private static class Bot{

        static class SendChat extends TwoArgFunction {
            @Override
            public LuaValue call(LuaValue room, LuaValue _msg) {
                KakaoTalkListener.Session session = sessions.get(room.tojstring());
                if (session == null) return LuaValue.valueOf(false);
                session.replier.reply(_msg.tojstring());
                return _msg;
            }
        }

        static class GetVersion extends ZeroArgFunction {
            @Override
            public LuaValue call() {
                return LuaValue.valueOf(KakaoBot.VERSION);
            }
        }

    }


    private static class Session {
        public String name;
        public Replier replier;

        public Session(String name, Replier replier) {
            this.name = name;
            this.replier = replier;
        }

    }

    public static class Replier {

        private Notification.Action session = null;

        public Replier(Notification.Action act) {
            super();
            session = act;
        }

        public void reply(String value) {
            Intent sendIntent = new Intent();
            Bundle msg = new Bundle();
            for (RemoteInput inputable : session.getRemoteInputs()) {
                msg.putCharSequence(inputable.getResultKey(), value);
            }
            RemoteInput.addResultsToIntent(session.getRemoteInputs(), sendIntent, msg);

            try {
                session.actionIntent.send(ctx, 0, sendIntent);
            } catch (PendingIntent.CanceledException e) {

            }
        }

    }

}
