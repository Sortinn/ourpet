package com.tencent.xidian.ourpet.store;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.tencent.xidian.ourpet.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */

public class StorePersonAdapter extends SimpleAdapter {
    public final static int TYPE_LOCAL = 0;//本地人物
    private Context mContext;

    private List<PersonInfo> personData;//保存人物数据

    public StorePersonAdapter(Context context, int type) {
        super(context, null, 0, null, null);
        mContext = context;
        personData = getDataByType(type);
    }
    public  void setAdapterData(List<PersonInfo> data){
        personData = data;
    }
    private List<PersonInfo> getDataByType(int type) {
        if (type == TYPE_LOCAL)
            return initLocal(mContext);
        return null;//否则初始化失败
    }

    private List<PersonInfo> initLocal(Context context) {
        List<PersonInfo> personData = new ArrayList<>();
        String name[] = {"luffy", "chopper", "zoro", "law","piggy","pikachu"};
        int iconId[] = {R.raw.luffy_eat_2, R.raw.chopper_eat_1_1, R.raw.zoro_down_2, R.raw.law_stand, R.raw.piggy_drink_0, R.raw.stand_1};
        int nameId[] = {R.string.luffy, R.string.chopper, R.string.zoro, R.string.law,R.string.piggy_full_name,R.string.pikachu_full_name};

        for (int i = 0; i < nameId.length; i++) {
            try {
                PersonInfo personInfo = new PersonInfo();
                personInfo.tag = name[i];
                personInfo.image = BitmapFactory.decodeResource(context.getResources(), iconId[i]);
                personInfo.name = context.getResources().getText(nameId[i]).toString();
                personData.add(personInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return personData;
    }

    @Override
    public int getCount() {
        return personData.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder = null;//自定义一个类用来缓存 convertview

            convertView = LayoutInflater.from(mContext).inflate(R.layout.person_item_layout, null);
            viewHolder = new ViewHolder(convertView);
//            viewHolder.personItemImage = convertView.findViewById(R.id.person_item_image);//加载人物图片
//            viewHolder.personItemText = (TextView) convertView.findViewById(R.id.person_item_text);//加载人物文字说明
            convertView.setTag(personData.get(position));
        try {
            viewHolder.personItemImage.setImageBitmap((Bitmap) personData.get(position).image);//将图片进行Bitmp编码
            viewHolder.personItemText.setText(personData.get(position).name);//显示设置人物名字
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.person_item_image)
        ImageView personItemImage;
        @BindView(R.id.person_item_text)
        TextView personItemText;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
