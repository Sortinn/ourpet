package com.tencent.xidian.ourpet;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tencent.xidian.ourpet.PersonSettingDialog.OBJECT_KEY_ICON;
import static com.tencent.xidian.ourpet.PersonSettingDialog.OBJECT_KEY_NAME;
import static com.tencent.xidian.ourpet.PersonSettingDialog.OBJECT_KEY_SP;

public class PersonSettingAdapter extends SimpleAdapter {
    private static final String TAG = "PersonSettingAdapter";
    private Context mContext;//获取调用对象的上下文
    private List<HashMap<String, Object>> mData;//获取调用对象的数据
    SharedPreferences sharedPreferences;//获取物理内存中的数据

    public PersonSettingAdapter(Context context, List<HashMap<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        Log.d(TAG, "PersonSettingAdapter: ");
        mContext = context;//得到调用对象的上下文
        mData = data;//得到调用对象的数据
        sharedPreferences = mContext.getSharedPreferences(mContext.getString(R.string.our_pets_settings), Context.MODE_PRIVATE);//得到物理内存中的数据
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//在android进行绘制每个子view的时候调用
            ViewHolder viewHolder;
            Log.d(TAG, "getView:drawable_item......");
            convertView = LayoutInflater.from(mContext).inflate(R.layout.person_list_setting, null);//初始化子项拿到一个view
            viewHolder = new ViewHolder(convertView);//实例化viewHolder
            viewHolder.personSettingIcon.setImageBitmap((Bitmap) mData.get(position).get(OBJECT_KEY_ICON));//拿到调用对象的图形数据并设置为子项的图标
            viewHolder.personSettingName.setText((String) mData.get(position).get(OBJECT_KEY_NAME));//拿到调用对象的字符串资源数据并设置为子项的图标说明文字

            viewHolder.personSettingSelected.setId(position);//找到子项的单选框

            viewHolder.personSettingSelected.setChecked(sharedPreferences.
                    getBoolean((String) mData.get(position).get(OBJECT_KEY_SP), false));//设置所获取的子项中的所对应的key的Value值为false;
            viewHolder.personSettingSelected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                //设置单选框状态改变监听事件
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //如果选中的该子项的话。将选中的状态保留到内存中进行永久性存储
                        sharedPreferences
                                .edit()
                                .putBoolean((String) mData.get(buttonView.getId()).get(OBJECT_KEY_SP), true)
                                .apply();
                    } else {
                        //如果未选中该子项的话，将所有子项至少一个设置为选中状态
                        boolean isAnyChecked = false;
                        for (HashMap<String, Object> map : mData) {
                            //如果用户选择的一项和唯一的子项相同时跳过对该次的设置。
                            if (mData.indexOf(map) == buttonView.getId())
                                continue;
                            //add chopper bar遇到了乔巴的棍子的时候该子项不能单独存在，直接跳过
                            if (((String) map.get(OBJECT_KEY_SP)).equals("chopper_action_bar")) {
                                continue;
                            }
                            //检测是否存在一个子项处于被选中状态如果选中则将isAnyChecked设置成true并跳出本次循环
                            if (sharedPreferences.getBoolean((String) map.get(OBJECT_KEY_SP), false)) {
                                isAnyChecked = true;
                                break;
                            }
                        }
                        //如果所有项都为false的话就讲该点中的子项设置为true并提示用户要至少选择一项
                        //否则的话这个子项就可以重新设置为false
                        if (!isAnyChecked) {
                            buttonView.setChecked(true);
                            Toast.makeText(mContext, R.string.choice_at_least_one, Toast.LENGTH_SHORT).show();
                        } else {
                            sharedPreferences.edit()
                                    .putBoolean((String) mData.get(buttonView.getId()).get(OBJECT_KEY_SP), false)
                                    .apply();
                        }
                    }
                }
            });
        return convertView;
    }

    //检测所有人物列表子项中是否满足至少选择一个的条件
    public boolean isAnyCheck() {
        boolean checked = false;
        for (HashMap<String, Object> map : mData) {
            //乔巴的棍不能单独存在，因此在检测的过程中将该子项进行跳过处理
            if (((String) map.get(OBJECT_KEY_SP)).equals("chopper_action_bar")) {
                continue;
            }
            //如果所有子项中存在至少一个为被选中的状态下则break掉for循环 并修改返回结果状态
            if (sharedPreferences.getBoolean((String) map.get(OBJECT_KEY_SP), false)) {
                checked = true;
                break;
            }
        }
        return checked;
    }
    static class ViewHolder {
        @BindView(R.id.person_setting_icon)
        ImageView personSettingIcon;
        @BindView(R.id.person_setting_name)
        TextView personSettingName;
        @BindView(R.id.person_setting_selected)
        CheckBox personSettingSelected;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
