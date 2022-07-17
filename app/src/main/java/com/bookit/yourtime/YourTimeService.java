/*
 * Created by 8bookit8 on 22. 7. 17. 오후 11:21
 * Copyright (c) 2022. All rights reserved.
 * Last modified 22. 7. 17. 오후 11:18
 */

package com.bookit.yourtime;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class YourTimeService extends Service {

    private BroadcastReceiver receiver;

    private YourTimeService yourTimeService;
    public static boolean isRunning = false;

    public static boolean isScreenOn = false;
    private static int time = 0;
    private static int hour = 1;
    private static int min = 5;

    public static boolean isOnTimeWarning;
    public static boolean isOnEyeAlram;

    private static Handler handler = new Handler();
    private static Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            String channelId = "channel_1";
            NotificationChannel channel = new NotificationChannel(channelId, "YourTime", NotificationManager.IMPORTANCE_LOW);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, channelId)
                                                                .setContentTitle("YourTime 앱이 백그라운드에서 작동 중입니다.")
                                                                .setSmallIcon(R.drawable.app_icon)
                                                                .build();
            startForeground(1, notification);
        }

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        receiver = new YourTimeBroadcastReceiver();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        yourTimeService = this;
        isRunning = true;
        runnable = new Runnable() {
            @Override
            public void run() {
                if (isScreenOn) {
                    time++;

                    if (time / 3600 == hour) {
                        if (isOnTimeWarning) {
                            notify("과도한 휴대전화 사용은 일생생활에 지장을 줄 수 있습니다.", "휴대전화 사용시간이 " + hour +"시간을 경과했습니다.");
                        }
                        hour++;
                    }

                    if ((time - (hour - 1) * 3600) / 60 == min) {
                        if (isOnEyeAlram && !(min == 0 && isOnTimeWarning)) {
                            notify("눈 깜빡임 알람", "휴대전화 사용 중 눈을 자주 깜빡여주세요.");
                        }
                        min = min >= 55 ? 0 : min + 5;
                    }
                    handler.postDelayed(this, 1000);
                }
            }

            private void notify(String title, String text) {
                Intent fullScreenIntent = new Intent(yourTimeService, MainActivity.class);
                PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(yourTimeService, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

                String channelId = "channel_2";
                NotificationManager notificationManager = ((NotificationManager) getSystemService(NOTIFICATION_SERVICE));
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(channelId, "YourTime", NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }

                NotificationCompat.Builder builder = new NotificationCompat.Builder(yourTimeService, channelId)
                        .setSmallIcon(R.drawable.app_icon)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setCategory(Notification.CATEGORY_ALARM)
                        .setContentIntent(fullScreenPendingIntent)
                        .setAutoCancel(true);

                notificationManager.notify(2, builder.build());
            }
        };
        handler.postDelayed(runnable, 1000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        isRunning = false;
        super.onDestroy();
    }

    public static void screenOn() {
        isScreenOn = true;
        handler.postDelayed(runnable, 1000);
    }

    public static void screenOff() {
        isScreenOn = false;
        time = 0;
        hour = 1;
    }

    public static int getTime() { return time; }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
