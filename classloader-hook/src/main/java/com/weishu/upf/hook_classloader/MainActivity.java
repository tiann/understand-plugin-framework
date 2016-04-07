package com.weishu.upf.hook_classloader;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.weishu.upf.hook_classloader.ams_hook.AMSHookHelper;
import com.weishu.upf.hook_classloader.classloder_hook.BaseDexClassLoaderHookHelper;
import com.weishu.upf.hook_classloader.classloder_hook.LoadedApkClassLoaderHookHelper;

/**
 * @author weishu
 * @date 16/3/28
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    // patch宿主ClassLoader的方式
    private static final int PATCH_BASE_CLASS_LOADER = 1;

    // 自定义ClassLoader的方式
    private static final int CUSTOM_CLASS_LOADER = 2;

    private static final int HOOK_METHOD = CUSTOM_CLASS_LOADER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Button t = new Button(this);
        t.setText("test button");

        setContentView(t);

        Log.d(TAG, "context classloader: " + getApplicationContext().getClassLoader());
        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //                    Intent t = new Intent();
                    //                    if (HOOK_METHOD == PATCH_BASE_CLASS_LOADER) {
                    //                        t.setComponent(new ComponentName("com.weishu.upf.dynamic_proxy_hook.app2",
                    //                                "com.weishu.upf.dynamic_proxy_hook.app2.MainActivity"));
                    //                    } else {
                    //                        t.setComponent(new ComponentName("com.weishu.upf.ams_pms_hook.app",
                    //                                "com.weishu.upf.ams_pms_hook.app.MainActivity"));
                    //                    }
                    //                    startActivity(t);

                    Uri uri = Uri.fromFile(getFileStreamPath("test.apk"));
                    boolean ret = (Boolean) invokeMethod(Class.forName("android.provider.DocumentsContract").newInstance(), "isDocumentUri", new Class[] { Context.class, Uri.class },
                            new Object[] { getApplicationContext(), uri });
                    Log.d(TAG, "ret" + ret);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static Method getDeclaredMethod(Object object, String name, Class<?>[] types) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredMethod(name, types);
            } catch (NoSuchMethodException e) {

            }
        }
        return null;
    }

    private static Object invokeMethod(Object obj, String methodName, Class<?>[] argTypes, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = getDeclaredMethod(obj, methodName, argTypes);
        if (method == null) {
            throw new IllegalAccessException("could not found method:" + methodName);
        }
        method.setAccessible(true);
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException e) {

        }
        return null;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        try {
            Utils.extractAssets(newBase, "dynamic-proxy-hook.apk");
            Utils.extractAssets(newBase, "ams-pms-hook.apk");
            Utils.extractAssets(newBase, "test.apk");

            if (HOOK_METHOD == PATCH_BASE_CLASS_LOADER) {
                File dexFile = getFileStreamPath("test.apk");
                File optDexFile = getFileStreamPath("test.dex");
                BaseDexClassLoaderHookHelper.patchClassLoader(getClassLoader(), dexFile, optDexFile);
            } else {
                LoadedApkClassLoaderHookHelper.hookLoadedApkInActivityThread(getFileStreamPath("ams-pms-hook.apk"));
            }

            AMSHookHelper.hookActivityManagerNative();
            AMSHookHelper.hookActivityThreadHandler();

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
