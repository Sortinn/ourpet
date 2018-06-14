package com.tencent.xidian.ourpet;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.login.LoginException;

import butterknife.BindView;
import butterknife.OnClick;

public class PersonSettingDialog extends AlertDialog {
    private static final String TAG = "PersonSettingDialog";

    public final static String OBJECT_KEY_SP = "preference_key";
    public final static String OBJECT_KEY_ICON = "person_setting_icon";
    public final static String OBJECT_KEY_NAME = "person_setting_name";


    private Context mContext;
    private SharedPreferences sharedPreferences;//加载sharePreference保存的数据
    private int mIconId;//引入图标
    private CharSequence mTitleName;//引入名称
    private String mPersonName;//引入人物名称
    private PersonSettingAdapter personSettingAdapter;//人物列表适配器
    private final Object luffyData[][] = {
            //perference_key        //person_setting_icon     //person_setting_name
            {"luffy_action_crawl", R.raw.luffy_crawl_1, R.string.person_state_crawl}
            , {"luffy_action_eat", R.raw.luffy_eat_1, R.string.person_state_eat_meat}
            , {"luffy_action_sit", R.raw.luffy_sit_1, R.string.person_state_sit}
            , {"luffy_action_walk", R.raw.luffy_walk_1, R.string.person_state_walk}
            , {"luffy_action_stand", R.raw.luffy_stand, R.string.person_state_stand}
    };
    private final Object zoroData[][] = {
            //perference_key       //person_setting_icon   //person_setting_name
            {"zoro_action_sleep", R.raw.zoro_sleep_1, R.string.person_state_sleep}
            , {"zoro_action_eat", R.raw.zoro_eat_1, R.string.person_state_eat}
            , {"zoro_action_sit", R.raw.zoro_sit, R.string.person_state_sit}
            , {"zoro_action_walk", R.raw.zoro_walk_1, R.string.person_state_walk}
            , {"zoro_action_stand", R.raw.zoro_stand, R.string.person_state_stand}
    };
    private final Object lawData[][] = {
            //perference_key    //person_setting_icon    //person_setting_name
            {"law_action_walk", R.raw.law_walk_1, R.string.person_state_walk}
            , {"law_action_crawl", R.raw.law_crawl_1, R.string.person_state_crawl}
            , {"law_action_sit", R.raw.law_sit_1, R.string.person_state_sit}
            , {"law_action_sleepy", R.raw.law_sleepy_2, R.string.person_state_sleep}
            , {"law_action_stand", R.raw.law_stand, R.string.person_state_stand}
            , {"law_action_dance", R.raw.law_dance_1, R.string.person_state_dance}
    };
    private final Object chopperData[][] = {
            //perference_key        //person_setting_icon        //person_setting_name
            {"chopper_action_bar", R.raw.chopper_walk_0_2, R.string.person_state_ball}
            , {"chopper_action_walk", R.raw.chopper_walk_0_1, R.string.person_state_walk}
            , {"chopper_action_stand", R.raw.chopper_stand_0_1, R.string.person_state_stand}
            , {"chopper_action_sit", R.raw.chopper_sit, R.string.person_state_sit}
            , {"chopper_action_eat", R.raw.chopper_eat_1_1, R.string.person_state_eat}
            , {"chopper_action_ball", R.raw.chopper_ball, R.string.person_state_ball}
            , {"chopper_action_sleep", R.raw.chopper_sleep_1_1, R.string.person_state_sleep}
            , {"chopper_action_happy", R.raw.chopper_happy_0_1, R.string.person_state_happy}
            , {"chopper_action_shock", R.raw.chopper_shock_2_1, R.string.person_state_shock}
            , {"chopper_action_star", R.raw.chopper_star_1_1, R.string.person_state_star}
    };

    public PersonSettingDialog(Context context, String name) {
        //传入新new的上下文和所需人物的名字
        super(context);
        Log.d(TAG, "PersonSettingDialog: "+name);
        mContext = context;//获取调用者的上下文
        mPersonName = name;//获取需要的人物名字
        sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.our_pets_settings),Context.MODE_PRIVATE);//获取SharedPreferences中的数据
        if("luffy".equals(mPersonName)){
//            dialogPersonSettingIcon.setImageResource(R.raw.luffy_eat_2);//设置luff图标
//            mTitleName = mContext.getResources().getText(R.string.luffy_full_name);//设置luff图标的名字
            mIconId = R.raw.luffy_eat_2;//引入luff图标
            mTitleName = mContext.getResources().getText(R.string.luffy_full_name);//引入luff的资源名
        }
        else if("zoro".equals(mPersonName)){
            mIconId = R.raw.zoro_down_2;//引入zoro图标
            mTitleName = mContext.getResources().getText(R.string.zoro_full_name);//引入zoro的资源名
        }
        else if("law".equals(mPersonName)){
            mIconId = R.raw.law_stand;//引入law图标
            mTitleName = mContext.getResources().getText(R.string.law_full_name);//引入law的资源名
        }
        else if("chopper".equals(mPersonName)){
            mIconId = R.raw.chopper_eat_1_1;//引入chopper图标
            mTitleName = mContext.getResources().getText(R.string.chopper_full_name);//引入chopper的资源名
        }else{

        }

        //personSettingAdapter = new PersonSettingAdapter(mContext,getListData(mPersonName),0,null,null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.dialog_person_setting);//加载layout文件
        ImageView imageView = findViewById(R.id.dialog_person_setting_icons);
        TextView textView = findViewById(R.id.dialog_person_setting_title);
        imageView.setImageResource(mIconId);//将图标设置成引入的图标
        textView.setText(mTitleName);//将引入的资源名设置成图标名
        findViewById(R.id.dialog_person_setting_select).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //当用户选择之后将数据进行保存到手机内存中
                if(personSettingAdapter.isAnyCheck()){
                    //将展示的人物key"person_show_name"和value:mPersonName对应起来
                    //方便在服务中获取
                    sharedPreferences
                            .edit()
                            .putString("person_show_name",mPersonName)
                            .apply();
                    PersonSettingDialog.this.cancel();
                }else{
                    Toast.makeText(mContext,R.string.choice_at_least_one,Toast.LENGTH_SHORT).show();
                }
            }
        });
        findViewById(R.id.dialog_person_setting_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonSettingDialog.this.cancel();
            }
        });

        personSettingAdapter = new PersonSettingAdapter(mContext,getListData(mPersonName),0,null,null);//引入适配器
        ListView listView = (ListView) findViewById(R.id.dialog_person_setting_listView);
        listView.setAdapter(personSettingAdapter);//将列表适配器加入进去并生成View
        Log.d(TAG, "onCreate: setAdapter(personSettingAdapter)");
    }

    private List<HashMap<String,Object>> getListData(String name){
        List<HashMap<String,Object>> listData = new ArrayList<>();

        Object dataArray[][] = null;
        if("luffy".equals(name)){
            dataArray = luffyData;
        }else if("zoro".equals(name)){
            dataArray = zoroData;
        }else if("law".equals(name)){
            dataArray = lawData;
        }else if("chopper".equals(name)){
            dataArray = chopperData;
        }
        //for 循环遍历设置 key--value
        for(Object data[]:dataArray){
            //为每一项设置key key通过key将数据取出
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(OBJECT_KEY_SP, 	 data[0]);//将OBJECT_KEY_SP和人物动作进行匹配起来。
            map.put(OBJECT_KEY_ICON, BitmapFactory.decodeResource(mContext.getResources(), (Integer) data[1]));//将OBJECT_KEY_ICON和人物图片资源匹配起来
            map.put(OBJECT_KEY_NAME, mContext.getText((Integer) data[2]));//将OBJECT_KEY_NAME和资源文件中的资源匹配起来。
            listData.add(map);//
        }

        return listData;//表示取出一个人物的所有数据
    }


}
