package com.tencent.xidian.ourpet;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.tencent.xidian.ourpet.person.Chopper;
import com.tencent.xidian.ourpet.person.Law;
import com.tencent.xidian.ourpet.person.Luffy;
import com.tencent.xidian.ourpet.person.Person;
import com.tencent.xidian.ourpet.person.Zoro;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class PersonFloatService extends Service implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "PersonFloatService";
    private MyWindowManager myWindowManager;
    /**悬浮窗界面的显示*/
    /**属于桌面的应用包名称*/
    private WindowManager.LayoutParams layoutParams;
    private Person person;
    private List<String> homePackageNames;
    /**线程的使用*/
    private Handler handler = new Handler();
    private DrawRunnable drawRunnable = new DrawRunnable();
    private HomeRunnable homeRunnable = new HomeRunnable();
    private ChangRunnable changRunnable = new ChangRunnable();
    /**存储数据*/
    private SharedPreferences sharedPreferences;
    /**是否在桌面的标识*/
    private boolean isHome = true;
    /**每帧运行时间,默认150*/
    private int frameTime;
    /**随机动画改变时间,默认5秒*/
    private int randomTime;
    /**注册监听器，监听屏幕的Off和on状态*/
    private Receivers receivers = new Receivers();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: PersonFloatService Start");
        super.onCreate();
        init();
        registerReceivers();
    }
    public void init(){
        //对sharedPreference进行监听
        sharedPreferences = getSharedPreferences(getString(R.string.our_pets_settings),MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        String personName = sharedPreferences.getString("person_show_name","luffy");
        Log.d(TAG, "init: personName "+personName);
        //初始化人物和设置好layoutParams
        myWindowManager = new MyWindowManager(this,personName);
        person = myWindowManager.getPerson();
        layoutParams = myWindowManager.getLayoutParams();
        Log.d(TAG, "init: addPersonView");
        if(sharedPreferences.getBoolean("person_visible",false)){
            //将人物进行添加到桌面
            myWindowManager.addPersonView();
            //将人物画进桌面
            handler.post(drawRunnable);
            handler.post(changRunnable);
            //若用户没有开启打开状态则将其关闭
        }
//        //将人物进行添加到桌面
//        myWindowManager.addPersonView();
//        //将人物画进桌面
//        handler.post(drawRunnable);
//        handler.post(changRunnable);
//        //若用户没有开启打开状态则将其关闭
        if(!sharedPreferences.getBoolean("display_all_the_time",false)){
            Log.d(TAG, "init: key");
            Log.d(TAG, "init://若用户没有开启打开状态则将其关闭 ");
            handler.post(homeRunnable);
        }
        //获取所有的本APP中的Activity
        homePackageNames = getHomes();
        frameTime = Integer.parseInt(sharedPreferences.getString("frame_time","150"));
        randomTime = Integer.parseInt(sharedPreferences.getString("random_time","5000"));
        //屏幕为on时
        sharedPreferences.edit().putBoolean("screen_on",true).apply();
    }
    private List<String> getHomes(){
        List<String> names = new ArrayList<String>();
        // 获取应用程序包管理类packageManager一个对象
        PackageManager packageManager = this.getPackageManager();
        //获取属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        //intent 查询条件，Activity所配置的action和category flags:MATCH_DEFAULT_ONLY:
        //Category必须带有CATEGORY_DEFAULTD的Activity才匹配
        //返回Activity列表
        List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo ri:resolveInfos){
            //取出每个Activity的名字
            names.add(ri.activityInfo.name);
        }
        names.add("com.tencent.xidian.ourpet.MainActivity");
        names.add("com.tencent.xidian.ourpet.store.StorePersonActivity");
        for(String str:names){
            Log.d(TAG, "getHomes: Activity:"+ str);
        }
        return names;
    }

    private void showErrorAndClose(Exception e){
        Toast.makeText(PersonFloatService.this,
                sharedPreferences.getString("person_show_name","")+"something error please restall",Toast.LENGTH_SHORT).show();
        stopSelf();
    }
    /**
     * 绘制人物图像，悬浮窗位置变化
     */
    class DrawRunnable implements Runnable{
        @Override
        public void run() {
           // Log.d(TAG, "run: DrawRunable running");
            //获取绘制开始系统时间
            long start = System.currentTimeMillis();
            try{
                //如果人物是可见的则禁止 整个view//需要补充说明
//                person.invalidate();
//                layoutParams.x = (int)person.getX();
//                layoutParams.y = (int)person.getY();
//                //myWindowManager.setPersonLayoutParam(layoutParams);
//                myWindowManager.windowManager.updateViewLayout(person,layoutParams);
                person.invalidate();
                myWindowManager.setPersonLocate();
                myWindowManager.updatePersonView();
                //长按启动 peron.getOnPerson()是获取长按状态 给用户其他操作
                if(person.getOnPerson()==1&&System.currentTimeMillis()-person.getTouchDownTime()>1000){

                    ActivityManager mActivityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                    //需要进行替换
                    List<ActivityManager.RunningTaskInfo> runningTaskInfoList = mActivityManager.getRunningTasks(1);
                    if(!runningTaskInfoList.get(0).topActivity.getPackageName().equals("com.tencent.xidian.ourpet")){
                        Intent intent = new Intent(PersonFloatService.this,MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                    person.setOnPerson(0);
                }
                long end = System.currentTimeMillis();
                if(end - start <frameTime){
                    handler.postDelayed(this,frameTime-(end-start));
                }else{
                    handler.post(this);
                }
            }catch (Exception e){
                e.printStackTrace();
                showErrorAndClose(e);
            }
        }
    }
    /**
     * 动画之间变换
     */
    class ChangRunnable implements Runnable{
        @Override
        public void run() {
           // Log.d(TAG, "run: ChangeRunable");
            try{
                person.randomChange();
                handler.postDelayed(this,randomTime);
            }catch (Exception e){
                e.printStackTrace();
                showErrorAndClose(e);
            }
        }
    }
private boolean isHome() {
    ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
    //该修改的地方
    List<ActivityManager.RunningTaskInfo> runningTaskInfoList = mActivityManager.getRunningTasks(1);
    if (runningTaskInfoList.size() == 0) {
        return false;
    }
    return homePackageNames.contains(runningTaskInfoList.get(0).topActivity.getClassName());
}

    /**
     *检测人物是否在桌面上，每秒钟检测一次
     */
    class HomeRunnable implements Runnable{
        @Override
        public void run() {
//            Log.d(TAG, "run: HomeRunnable");
//            Log.d(TAG, "run: HomewRunnable isHome is: "+isHome);
//            Log.d(TAG, "run: HomewRunnable isHome() is: "+isHome());
            if(isHome()&&!isHome){
                isHome = true;

                myWindowManager.addPersonView();

                handler.post(drawRunnable);
//                handler.removeCallbacks(drawRunnable);
                handler.postDelayed(changRunnable,randomTime);
//                handler.removeCallbacks(changRunnable);
            }
            else if(!isHome()&&isHome){
                isHome = false;
                handler.removeCallbacks(drawRunnable);
                handler.removeCallbacks(changRunnable);
                myWindowManager.removePersonView();
            }
            handler.postDelayed(this,1000);
        }
    }

    /**
     * 监听当前屏幕是否亮着
     */
    public static class Receivers extends BroadcastReceiver{
        public Receivers(){}//提供创建对象构造函数
        @Override
        public void onReceive(final Context context, Intent intent) {
            SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.our_pets_settings),MODE_PRIVATE);
            if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
                Toast.makeText(context,"随开机自启",Toast.LENGTH_SHORT).show();
                if(sharedPreferences.getBoolean("start_during_boot",false)&&sharedPreferences.getBoolean("person_visible",false)){
                   Handler hr = new Handler();
                   hr.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           context.startService(new Intent(context,PersonFloatService.class));
                       }
                   },2*60*1000);
                }
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){
                sharedPreferences.edit().putBoolean("screen_on",true).apply();
            }
            else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){
                sharedPreferences.edit().putBoolean("screen_on",false).apply();
            }
        }
    }
    /**
    * 广播注册监听器
    */
    private void registerReceivers(){
    IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_SCREEN_ON);
    filter.addAction(Intent.ACTION_SCREEN_OFF);
    registerReceiver(receivers,filter);
}


    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(drawRunnable);
        handler.removeCallbacks(changRunnable);
        handler.removeCallbacks(homeRunnable);
        myWindowManager.removePersonView();
        unregisterReceiver(receivers);
    }

    /**
     * 屏幕旋转
     * @param newConfig
     */

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        person.measureScreen();
    }

    /**
     * 设置发生变化时
     * @param sharedPreferences
     * @param key
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(person != null)
            person.onSharedPreferenceChanged(sharedPreferences,key);
        if("display_all_the_time".equals(key)) {
          //  Log.d(TAG, "onSharedPreferenceChanged: "+key);
            if (!sharedPreferences.getBoolean("display_all_the_time", false))
                handler.post(homeRunnable);
            else
                handler.removeCallbacks(homeRunnable);
        }else if("person_show_name".equals(key)){
            Log.d(TAG, "onSharedPreferenceChanged: 人物选择发生改变");
            myWindowManager.removePersonView();
            SharedPreferences sharedPreferences1 = getSharedPreferences(getString(R.string.our_pets_settings),Context.MODE_PRIVATE);
            String name = sharedPreferences1.getString("person_show_name","luffy");
            myWindowManager = new MyWindowManager(this,name);
            myWindowManager.addPersonView();
        }else if ("frame_time".equals(key)){
            Log.d(TAG, "onSharedPreferenceChanged: 时间间隔发生改变");
            frameTime = Integer.parseInt(sharedPreferences.getString("frame_time","150"));
        }else if("random_time".equals(key)){
            randomTime = Integer.parseInt(sharedPreferences.getString("random_time","5000"));
        }else if("person_size".equals(key)){
            Log.d(TAG, "onSharedPreferenceChanged: 人物大小发生改变");
            try{
                layoutParams.width = person.getBmpW();
                layoutParams.height =person.getBmpH();
            }catch(Exception e){
                e.printStackTrace();
            }
        }else if("person_visible".equals(key)){
            if(sharedPreferences.getBoolean("person_visible",false)){
                stopSelf();
            }else {
                myWindowManager.setPersonSize();
                layoutParams.width = person.getBmpW();
                layoutParams.height =person.getBmpH();
                myWindowManager.addPersonView();
            }
        }else if("screen_on".equals(key)){
            Log.d(TAG, "onSharedPreferenceChanged: 屏幕休息");
            if(!sharedPreferences.getBoolean(key,false)){
                //screen_on
                if(!sharedPreferences.getBoolean("display_all_the_time",false)){
                    isHome  = false;
                    handler.post(homeRunnable);
                }else if(sharedPreferences.getBoolean("person_visible",false)){
                    myWindowManager.addPersonView();
                    handler.post(drawRunnable);
                    handler.post(changRunnable);
                }
            }else {
                //screen_off
                handler.removeCallbacks(drawRunnable);
                handler.removeCallbacks(changRunnable);
                handler.removeCallbacks(homeRunnable);
                myWindowManager.removePersonView();
            }
        }
    }
    
}

