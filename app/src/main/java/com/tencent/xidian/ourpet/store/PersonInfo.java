package com.tencent.xidian.ourpet.store;

import android.graphics.Bitmap;

public class PersonInfo {
    public final static int FLAG_NORMAL = 1;//本地标志
    public String name;//显示的人物名字
    public Bitmap image;//人物图像
    public String tag;//人物标识
    public int flag = FLAG_NORMAL;//本地标志
}
