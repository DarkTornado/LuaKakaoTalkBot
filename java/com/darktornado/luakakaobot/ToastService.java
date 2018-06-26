package com.darktornado.luakakaobot;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class ToastService extends Service {


    public int onStartCommand(Intent intent, int flags, int startId) {
        String msg = intent.getStringExtra("toast");
        if(msg!=null) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
