package com.chanyoung.market;

import static com.chanyoung.market.MainActivity.ServiceIntent;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class RestartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals("restartService")) {
            System.out.println("broad intent 받음");
            Intent i = new Intent(context, ChattingService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(ServiceIntent);
                System.out.println("broad intent 시작합나다.");
            }
            Toast.makeText(context, "restartService", Toast.LENGTH_SHORT).show();
        }
    }
}
