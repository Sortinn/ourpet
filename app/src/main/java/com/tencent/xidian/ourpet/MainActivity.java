package com.tencent.xidian.ourpet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;

import com.tencent.xidian.ourpet.store.StorePersonActivity;

import java.lang.reflect.Method;

public class MainActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "MainActivity";
    SharedPreferences sharedPreferences;
    private Intent floatViewService;
    private static final int REQUEST_SYSTEM_ALERT_CODE = 1024;//设置请求权限码
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: addFragment");
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content,new PreferenceSetting())
                .commit();

        //数据监听器

        sharedPreferences = getSharedPreferences(getString(R.string.our_pets_settings),MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        Log.d(TAG, "onCreate: registerOnSharedPreferenceChangeListener");

        floatViewService = new Intent(this,PersonFloatService.class);
        Log.d(TAG, "onCreate: "+ floatViewService);
       // Log.d(TAG, "onCreate: floatViewService");
        if(sharedPreferences.getBoolean("person_visible",false)){
           if(!commonROMPermissonCheck(this)){
               requestAlertWindowPermisson();
             //  Log.d(TAG, "onCreate: start floatViewService");
               startService(floatViewService);
           }else{
               startService(floatViewService);
           }
        }
    }
    //判断权限
    private boolean commonROMPermissonCheck(Context context){
        Boolean result = true;
        if(Build.VERSION.SDK_INT >=23){
            try{
                Class classzz = Settings.class;
                Method canDrawOverLays = classzz.getDeclaredMethod("canDrawOverlays", Context.class);
                result = (Boolean) canDrawOverLays.invoke(null,context);
            }catch (Exception e){
                Log.e(TAG,Log.getStackTraceString(e) );
            }
        }
        return result;
    }
    //动态申请悬浮窗权限
    private void requestAlertWindowPermisson(){
        Intent intent =  new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:"+getPackageName()));
        Log.d(TAG, "requestAlertWindowPermisson: add startActivityForResult");
        startActivityForResult(intent,REQUEST_SYSTEM_ALERT_CODE);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_SYSTEM_ALERT_CODE){
            if(Settings.canDrawOverlays(this)){
                Log.d(TAG, "onActivityResult: granted");
            }
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG, "onSharedPreferenceChanged: onSharedPreferenceChanged");

        if("person_visible".equals(key)){
            if(sharedPreferences.getBoolean(key,false)){
                //Log.d(TAG, "onSharedPreferenceChanged: startService");
                startService(floatViewService);
            }else{
                //停止该悬浮窗服务
               //Log.d(TAG, "onSharedPreferenceChanged: "+key);
                stopService(floatViewService);
            }
        }
    }
}
