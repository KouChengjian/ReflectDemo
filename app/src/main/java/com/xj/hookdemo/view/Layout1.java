package com.xj.hookdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class Layout1 extends LinearLayout {


    public Layout1(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("msg", "Layout1-dispatch = " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("msg","Layout1-onIntercept");
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("msg","Layout1-onTouch = " + event.getAction());
        return false;
    }
}
