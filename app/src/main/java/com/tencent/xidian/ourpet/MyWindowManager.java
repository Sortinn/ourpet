package com.tencent.xidian.ourpet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.tencent.xidian.ourpet.person.Chopper;
import com.tencent.xidian.ourpet.person.Law;
import com.tencent.xidian.ourpet.person.Luffy;
import com.tencent.xidian.ourpet.person.Person;
import com.tencent.xidian.ourpet.person.Zoro;

import java.lang.reflect.Method;

public class MyWindowManager {

    private static final String TAG = "MyWindowManager";
    //管理windowManager
    public WindowManager windowManager;
    private static final int  REQUEST_SYSTEM_ALERT_PERCODE = 10;

    private static WindowManager.LayoutParams layoutParams;

    private Person person ;

    private Context mContext;

    public MyWindowManager(Context context,String name){
        mContext = context;
        windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //创建人物
        this.createPerson(name);
        layoutParams = new WindowManager.LayoutParams();
        //设置参数
        // layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        layoutParams.type=WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.LEFT|Gravity.TOP;
        this.setPersonLocate();
        this.setPersonSize();
    }
    public void createPerson(String name){

        if("luffy".equals(name)) person = new Luffy(mContext);
        if("zoro".equals(name)) person = new Zoro(mContext);
        if("law".equals(name))  person = new Law(mContext);
        if("chopper".equals(name)) person = new Chopper(mContext);
        if(person == null) {
            Log.d(TAG, "MyWindowManager: person == null");
            person = new Luffy(mContext);
            Log.d(TAG, "MyWindowManager: "+person);
        }
    }

    public void setPersonLocate(){
        layoutParams.x = (int)person.getX();
        layoutParams.y = (int)person.getY();
    }
    public void setPersonSize(){
        layoutParams.width = person.getBmpW();
        layoutParams.height = person.getHeight();
    }
    public void addPersonView(){
        Log.d(TAG, "addPersonView: PermissonCheck: "+commonROMPermissonCheck(mContext));
        if(commonROMPermissonCheck(mContext)){
            Log.d(TAG, "addPersonView: windowManager");
            try{
                windowManager.addView(person,layoutParams);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            requestAlertWindowPermisson();
            if(commonROMPermissonCheck(mContext)){
                Log.d(TAG, "addPersonView: windowManager");
                try{
                    windowManager.addView(person,layoutParams);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }
    public void removePersonView(){
        try{
            windowManager.removeView(person);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updatePersonView(WindowManager.LayoutParams layoutParams){
        try{
            windowManager.updateViewLayout(person,layoutParams);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void updatePersonView(){
        windowManager.updateViewLayout(person,layoutParams);
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
    private void requestAlertWindowPermisson() {

        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
    public Person getPerson(){
        return this.person;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return layoutParams;
    }
    public void setPerson(Person person){
        this.person = person;
    }
    public void setLayoutParams(WindowManager.LayoutParams layoutParams){
        this.layoutParams = layoutParams;
    }

}
