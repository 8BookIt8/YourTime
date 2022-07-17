/*
 * Created by 8bookit8 on 22. 7. 17. 오후 11:21
 * Copyright (c) 2022. All rights reserved.
 * Last modified 22. 7. 17. 오후 11:18
 */

package com.bookit.yourtime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class YourTimeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(Intent.ACTION_SCREEN_ON)) {
            YourTimeService.screenOn();
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            YourTimeService.screenOff();
        }
    }
}
