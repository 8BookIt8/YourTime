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
            Toast.makeText(context, "SCREEN ON!", Toast.LENGTH_SHORT).show();
        } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            YourTimeService.screenOff();
            Toast.makeText(context, "SCREEN OFF!", Toast.LENGTH_SHORT).show();
        }
    }
}
