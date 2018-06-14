package com.tencent.xidian.ourpet.alarm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by maokelong on 2016/4/29.
 *
 *接受来自"AlarmAlertBroadcastReciever"转发的广播并开启"AlarmService"服务
 *
 */
public class AlarmServiceBroadcastReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmServiceBroadcastReciever", "onReceive()");
        Intent serviceIntent = new Intent(context, AlarmService.class);
        context.startService(serviceIntent);
        //测试延迟
    }

}
