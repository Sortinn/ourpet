package com.tencent.xidian.ourpet.store;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.tencent.xidian.ourpet.PersonSettingDialog;
import com.tencent.xidian.ourpet.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class StorePersonActivity extends Activity {
    private static final String TAG = "StorePersonActivity";

    @BindView(R.id.store_person_grid_view)
    StoreGridView storePersonGridView;//网格型状布局
    private SharedPreferences sharedPreferences;//存取内存中SharePreference数据
    /**
     * the grid view of person
     */
    private Context mContext;//上下文
    private int mType = StorePersonAdapter.TYPE_LOCAL;//存储对象的类型
    private List<PersonInfo> personData;//存储人物列表
    private StorePersonAdapter storePersonAdapter;//存储人物适配器

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.store_person_layout);
        ButterKnife.bind(this);
        mContext = this;
        sharedPreferences = getSharedPreferences(getString(R.string.our_pets_settings), MODE_PRIVATE);//获取内存中的配置
        storePersonGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final PersonInfo personInfo = (PersonInfo) view.getTag();//从view中取出personInfo
                if (mType == StorePersonAdapter.TYPE_LOCAL) {
                   try{
                       PersonSettingDialog dialog = new PersonSettingDialog(mContext, personInfo.tag);
//                    Log.d(TAG, "onItemClick: personInfo.tag:"+personInfo.tag);
                       dialog.show();
//                    Log.d(TAG, "onItemClick: dialog.show()");
                   }catch (Exception e){
                       e.printStackTrace();
                       sharedPreferences.edit().putBoolean("person_visible",false).apply();
                   }

                }
            }
        });
        storePersonAdapter = new StorePersonAdapter(this, mType);
        storePersonGridView.setAdapter(storePersonAdapter);

    }

}