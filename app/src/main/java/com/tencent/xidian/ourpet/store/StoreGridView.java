package com.tencent.xidian.ourpet.store;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;

public class StoreGridView extends GridView {

    public StoreGridView(Context context) {
        super(context);
    }

    public StoreGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StoreGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }
}

