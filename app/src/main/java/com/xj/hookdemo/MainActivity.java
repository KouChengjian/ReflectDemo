package com.xj.hookdemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.mylibrary.LogUitl;
import com.xj.hookdemo.activityhook.TestClipboardActivity;
import com.xj.hookdemo.activityhook.TestHookStartActivity;
import com.xj.hookdemo.hook.notification.NotificationHookHelper;
import com.xj.hookdemo.utils.HookViewClickUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MainActivity extends AppCompatActivity {

    ImageView iv_loader_skin;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_loader_skin = findViewById(R.id.iv_loader_skin);

        iv_loader_skin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("MainActivity","iv_loader_skin 被点击了");
            }
        });

        HookViewClickUtil.hookView(iv_loader_skin);

    }

    public void onButtonClick(View view) {
        switch (view.getId()) {
            case R.id.btn_1:
                jump(this, TestOnClickActivity.class);
                break;
            case R.id.btn_2:
                jump(this, TestHookStartActivity.class);

                break;
            case R.id.btn_3:
                try {
                    NotificationHookHelper.hookNotificationManager(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                testNotification();
                break;
            case R.id.btn_4:
                jump(this, TestClipboardActivity.class);
                break;
            case R.id.btn_5:
                loaderJar();
                break;
            case R.id.btn_6:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    loaderApkToSkin();
                }
                break;
            case R.id.btn_7:

                break;
        }
    }

    private boolean mChange = false;

    /**
     * 获取未安装apk的信息
     * @param context
     * @param pApkFilePath apk文件的path
     * @return
     */
    private String getUninstallApkPkgName(Context context, String pApkFilePath) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(pApkFilePath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo != null) {
            ApplicationInfo appInfo = pkgInfo.applicationInfo;
            return appInfo.packageName;
        }
        return "";
    }

    /**
     * 获取AssetManager   用来加载插件资源
     * @param pFilePath  插件的路径
     * @return
     */
    private AssetManager createAssetManager(String pFilePath) {
        try {
            final AssetManager assetManager = AssetManager.class.newInstance();
            final Class<?> assetManagerClazz = Class.forName("android.content.res.AssetManager");
            final Method addAssetPathMethod = assetManagerClazz.getDeclaredMethod("addAssetPath", String.class);
            addAssetPathMethod.setAccessible(true);
            addAssetPathMethod.invoke(assetManager, pFilePath);
            return assetManager;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Resources  createResources(String pFilePath){
        final AssetManager assetManager = createAssetManager(pFilePath);
        Resources superRes = this.getResources();
        return new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private  void dynamicLoadApk(String pApkFilePath, String pApkPacketName){
        File file=getDir("dex", Context.MODE_PRIVATE);
        //第一个参数：是dex压缩文件的路径
        //第二个参数：是dex解压缩后存放的目录
        //第三个参数：是C/C++依赖的本地库文件目录,可以为null
        //第四个参数：是上一级的类加载器
        DexClassLoader  classLoader=new DexClassLoader(pApkFilePath,file.getAbsolutePath(),null,getClassLoader());
        try {
            final Class<?> loadClazz = classLoader.loadClass(pApkPacketName + ".R$mipmap");
            //插件中皮肤的名称是skin_one
            final Field skinOneField = loadClazz.getDeclaredField("bg_1");
            skinOneField.setAccessible(true);
            //反射获取skin_one的resousreId
            final int resousreId = (int) skinOneField.get(R.id.class);
            Log.e("resousreId",resousreId+"========");
            //可以加载插件资源的Resources
            final Resources resources = createResources(pApkFilePath);
            if (resources != null) {
                final Drawable drawable = resources.getDrawable(resousreId);
                iv_loader_skin.setBackground(drawable);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("=====",e.toString()+"========");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void loaderApkToSkin() {
        String skinType = "";
        if (!mChange) {
            skinType = "moudle_skin_1.apk";
            mChange = true;
        } else {
            skinType = "moudle_skin_2.apk";
            mChange = false;
        }
        final String path = Environment.getExternalStorageDirectory() + File.separator + "dex" + File.separator + skinType;
        final String pkgName = getUninstallApkPkgName(this, path);
        dynamicLoadApk(path,pkgName);
    }

    @SuppressLint("LongLogTag")
    private void loaderJar() {
        //dex解压释放后的目录
        final File dexOutPutDir = getDir("dex", 0);
        //dex所在目录
//        Log.e("Environment.getDataDirectory()",  Environment.getDataDirectory() + "  ==========");
//        Log.e("Environment.getDownloadCacheDirectory()",  Environment.getDownloadCacheDirectory() + "  ==========");
//        Log.e("Environment.getExternalStorageDirectory()",  Environment.getExternalStorageDirectory() + "  ==========");
//        Log.e("Environment.getRootDirectory()",  Environment.getRootDirectory() + "  ==========");
//
//
//        Log.e("getFilesDir()",  getFilesDir() + "  ==========");
//        Log.e("getCacheDir()",  getCacheDir() + "  ==========");
//        Log.e("getExternalFilesDir()",  getExternalFilesDir("xxxx") + "  ==========");
//        Log.e("getExternalCacheDir()",  getExternalCacheDir() + "  ==========");

        final String dexPath = Environment.getExternalStorageDirectory().toString() + File.separator + "dex/jar/log.jar";
        Log.e("dexPath", dexPath + "  ==========");
        Log.e("dexOutPutDir.getAbsolutePath()", dexOutPutDir.getAbsolutePath() + "  ==========");
        //第一个参数：是dex压缩文件的路径
        //第二个参数：是dex解压缩后存放的目录
        //第三个参数：是C/C++依赖的本地库文件目录,可以为null
        //第四个参数：是上一级的类加载器
        DexClassLoader classLoader = new DexClassLoader(dexPath, dexOutPutDir.getAbsolutePath(), null, getClassLoader());

        try {
            final Class<?> loadClazz = classLoader.loadClass("com.android.mylibrary.LogUitl");
            final Object o = loadClazz.newInstance();
            final Method printLogMethod = loadClazz.getDeclaredMethod("printLog");
            printLogMethod.setAccessible(true);
            printLogMethod.invoke(o);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("dexPath", e.toString() + " ");
        }
    }

    private void testNotification() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable
                .ic_launcher_background);
        Intent intent = new Intent(MainActivity.this, TestNotificationActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, 0, intent,
                FLAG_UPDATE_CURRENT);
        NotificationHelper.notification(MainActivity.this, bitmap, R.mipmap.ic_launcher, "title",
                "content", "subText", 1, pendingIntent);

    }

    public static <T extends Activity> void jump(Context context, Class<T> clz) {
        Intent intent = new Intent(context, clz);
        if (false == (context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);

    }
}
