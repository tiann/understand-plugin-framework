package com.weishu.intercept_activity.app;

import android.app.Activity;

/**
 * 原始的Activity; 这个Activity会在Manifest中注册;
 * 当启动这个Activity的时候, 我们会把它拦截;然后跳转到TargetActivity
 * <p/>
 * 用到插件里面的话,那么就是在宿主程序里面注册了一堆空的Activity
 * <p/>
 * 如果希望启动插件的Activity; 由于插件Activity没有在主程序的Manifest中注册
 * 因此直接启动肯定会问题(插件的Activity有可能在它自己的Manifest.xml 中注册
 * 但是由于插件并不是一个真正安装的程序, Android系统并不知道这件事
 * <p/>
 * 我们可以通过分析Activity的启动机制, 可以在"合适的时候" 进行偷梁换柱,
 * 虽然我们要启动TargetActivity; 但是我们在真正启动之前,暂时替换为RawActivity
 * 这样,就能绕过AMS的验证,最后真正启动的时候,我们再替换回来,保证启动的是我们自己
 * <p/>
 * 这样我们就成为了一个真正的Activity, 生命周期由系统管理!
 * Created by weishu on 16/1/7.
 */
public class StubActivity extends Activity {
    // dummy, just stub
}
