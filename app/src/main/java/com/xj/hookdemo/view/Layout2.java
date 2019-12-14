package com.xj.hookdemo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class Layout2 extends LinearLayout {

    public Layout2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("msg", "Layout2-dispatch = " + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.e("msg", "Layout2-onIntercept");
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("msg", "Layout2-onTouch = " + event.getAction());
        return false;
    }
}
