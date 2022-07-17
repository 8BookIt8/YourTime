/*
 * Created by 8bookit8 on 22. 7. 17. 오후 11:21
 * Copyright (c) 2022. All rights reserved.
 * Last modified 22. 7. 17. 오후 11:18
 */

package com.bookit.yourtime;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private boolean isOnTimeWarning;
    private boolean isOnEyeAlram;

    private TextView tv_timer_hour;
    private TextView tv_timer_min;

    private ImageView ivMenuBackground;
    private TextView tvTimeWarning;
    private TextView tvEyeAlarm;

    private FloatingActionButton fbtnMenu;

    private boolean isOpeningMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        isOnTimeWarning = sharedPreferences.getBoolean("TimeWarning", true);
        isOnEyeAlram = sharedPreferences.getBoolean("EyeAlarm", true);
        YourTimeService.isOnTimeWarning = isOnTimeWarning;
        YourTimeService.isOnEyeAlram = isOnEyeAlram;

        tvTimeWarning = findViewById(R.id.tv_time_warning);
        tvEyeAlarm = findViewById(R.id.tv_eye_alarm);

        tvTimeWarning.setTextColor(Color.parseColor(isOnTimeWarning ? "#0276f9" : "#b4b4b4"));
        tvEyeAlarm.setTextColor(Color.parseColor(isOnEyeAlram ? "#0276f9" : "#b4b4b4"));

        tv_timer_hour = findViewById(R.id.tv_timer_hour);
        tv_timer_min = findViewById(R.id.tv_timer_min);

        Thread timerUpdateThread = new Thread(new TimerUpdateThread());
        timerUpdateThread.start();

        if (!YourTimeService.isRunning) {
            Intent intent = new Intent(getApplicationContext(), YourTimeService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }

        YourTimeService.isScreenOn = true;

        Animation animOpenMenu = AnimationUtils.loadAnimation(this, R.anim.anim_open_menu);
        ivMenuBackground = findViewById(R.id.iv_menu_background);

        fbtnMenu = findViewById(R.id.fbtn_menu);
        fbtnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOpeningMenu) {
                    isOpeningMenu = true;

                    fbtnMenu.setVisibility(View.GONE);

                    ivMenuBackground.startAnimation(animOpenMenu);

                    tvTimeWarning.setVisibility(View.VISIBLE);
                    tvEyeAlarm.setVisibility(View.VISIBLE);
                }
            }
        });

        tvTimeWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpeningMenu) {
                    isOnTimeWarning = !isOnTimeWarning;
                    YourTimeService.isOnTimeWarning = isOnTimeWarning;
                    tvTimeWarning.setTextColor(Color.parseColor(isOnTimeWarning ? "#0276f9" : "#b4b4b4"));
                }
            }
        });

        tvEyeAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOpeningMenu) {
                    isOnEyeAlram = !isOnEyeAlram;
                    YourTimeService.isOnEyeAlram = isOnEyeAlram;
                    tvEyeAlarm.setTextColor(Color.parseColor(isOnEyeAlram ? "#0276f9" : "#b4b4b4"));
                }
            }
        });

        ImageView ivInformation = findViewById(R.id.iv_information);
        ivInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setIcon(R.drawable.app_icon);
                dialog.setTitle("Application Inforrmation");
                dialog.setMessage("사용된 글꼴 : \n- AppleSDGothicNeoH00\n- Helvetica Condensed Black\n\nOpenSource : github.com/8bookit8\nDesigned by LeeVibeZ");
                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isOpeningMenu) {
            isOpeningMenu = false;

            Animation animCloseMenu = AnimationUtils.loadAnimation(this, R.anim.anim_close_menu);
            ivMenuBackground.startAnimation(animCloseMenu);

            tvTimeWarning.setVisibility(View.GONE);
            tvEyeAlarm.setVisibility(View.GONE);
            fbtnMenu.setVisibility(View.VISIBLE);
            return ;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("TimeWarning", isOnTimeWarning);
        editor.putBoolean("EyeAlarm", isOnEyeAlram);
        editor.commit();
        super.onDestroy();
    }

    class TimerUpdateThread implements Runnable {

        @Override
        public void run() {
            while(true) {
                int time = YourTimeService.getTime();
                int hour = time / 3600;
                int min = (time - hour * 3600) / 60;

                tv_timer_hour.setText(String.format("%02d", hour));
                tv_timer_min.setText(String.format("%02d", min));
            }
        }
    }
}

