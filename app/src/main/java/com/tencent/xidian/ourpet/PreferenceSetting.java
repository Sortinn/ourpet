package com.tencent.xidian.ourpet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.CheckBox;

import com.tencent.xidian.ourpet.alarm.AlarmActivity;
import com.tencent.xidian.ourpet.store.StorePersonActivity;

public class PreferenceSetting extends PreferenceFragment  {
    private static final String TAG = "PreferenceSetting";//打印日志
    private static SharedPreferences sharedPreferences;//声明sharedPreference用于获取内存中的数据
    private Intent floatViewService;
    private static final int REQUEST_CODE = 1;//设置权限请求码
    Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        context = getActivity().getApplicationContext();
        addPreferencesFromResource(R.xml.preference_settings);//加载xml资源文件
        //sharedPreferences = this.getSharedPreferences(getString(R.string.our_pets_settings), Context.MODE_PRIVATE);
        getPreferenceManager().setSharedPreferencesName(getString(R.string.our_pets_settings));//设置文件所保存的文件名同时也是取文件名的key
        //设置数据监听器
        sharedPreferences = context.getSharedPreferences(getString(R.string.our_pets_settings),Context.MODE_PRIVATE);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                                         final Preference preference) {
        if("person_select".equals(preference.getKey())){
            //添加人物选择对话框
            select_person_dialog();
        }
        if("alarm".equals(preference.getKey())){
                openAlarmSetting();
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    /**
     * 选择人物窗口
     */
    public void select_person_dialog(){
        startActivity(new Intent(getActivity().getApplicationContext(), StorePersonActivity.class));
    }
    /**
     * 打开闹钟设置界面
     */
    public void openAlarmSetting(){
        startActivity(new Intent(getActivity(), AlarmActivity.class));
    }


}
