package com.android.mylibrary;

import android.util.Log;

/**
 * Created with Android Studio.
 * User: kcj
 * Date: 2019-11-15 18:49
 * Description:
 */
public class LogUitl {
    public static final String TAG="LogUitl";
    private void  printLog(){
        Log.e(TAG,"这是来自另外一个dex中的log");
    }
}
