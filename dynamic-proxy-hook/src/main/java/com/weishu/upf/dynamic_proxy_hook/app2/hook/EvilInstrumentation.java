package com.weishu.upf.dynamic_proxy_hook.app2.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author weishu
 * @date 16/1/28
 */
public class EvilInstrumentation extends Instrumentation {

    private static final String TAG = "EvilInstrumentation";

    // ActivityThread中原始的对象, 保存起来
    Instrumentation mBase;

    public EvilInstrumentation(Instrumentation base) {
        mBase = base;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {

        // Hook之前, XXX到此一游!
        Log.d(TAG, "\n执行了startActivity, 参数如下: \n" + "who = [" + who + "], " +
                "\ncontextThread = [" + contextThread + "], \ntoken = [" + token + "], " +
                "\ntarget = [" + target + "], \nintent = [" + intent +
                "], \nrequestCode = [" + requestCode + "], \noptions = [" + options + "]");

        // 开始调用原始的方法, 调不调用随你,但是不调用的话, 所有的startActivity都失效了.
        // 由于这个方法是隐藏的,因此需要使用反射调用;首先找到这个方法
        try {
            Method execStartActivity = Instrumentation.class.getDeclaredMethod(
                    "execStartActivity",
                    Context.class, IBinder.class, IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            execStartActivity.setAccessible(true);
            return (ActivityResult) execStartActivity.invoke(mBase, who,
                    contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            // 某该死的rom修改了  需要手动适配
            throw new RuntimeException("do not support!!! pls adapt it");
        }
    }
}

