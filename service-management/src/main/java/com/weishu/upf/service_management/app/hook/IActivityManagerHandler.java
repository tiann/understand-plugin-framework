package com.weishu.upf.service_management.app.hook;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import android.content.ComponentName;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.weishu.upf.service_management.app.ProxyService;
import com.weishu.upf.service_management.app.ServiceManager;
import com.weishu.upf.service_management.app.UPFApplication;

/**
 * @author weishu
 * @dete 16/1/7.
 */
/* package */ class IActivityManagerHandler implements InvocationHandler {

    private static final String TAG = "IActivityManagerHandler";

    Object mBase;

    public IActivityManagerHandler(Object base) {
        mBase = base;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if ("startService".equals(method.getName())) {
            // 只拦截这个方法
            // API 23:
            // public ComponentName startService(IApplicationThread caller, Intent service,
            //        String resolvedType, int userId) throws RemoteException

            // 找到参数里面的第一个Intent 对象
            Pair<Integer, Intent> integerIntentPair = foundFirstIntentOfArgs(args);
            Intent newIntent = new Intent();

            // 代理Service的包名, 也就是我们自己的包名
            String stubPackage = UPFApplication.getContext().getPackageName();

            // 这里我们把启动的Service替换为ProxyService, 让ProxyService接收生命周期回调
            ComponentName componentName = new ComponentName(stubPackage, ProxyService.class.getName());
            newIntent.setComponent(componentName);

            // 把我们原始要启动的TargetService先存起来
            newIntent.putExtra(AMSHookHelper.EXTRA_TARGET_INTENT, integerIntentPair.second);

            // 替换掉Intent, 达到欺骗AMS的目的
            args[integerIntentPair.first] = newIntent;

            Log.v(TAG, "hook method startService success");
            return method.invoke(mBase, args);

        }

        //     public int stopService(IApplicationThread caller, Intent service,
        // String resolvedType, int userId) throws RemoteException
        if ("stopService".equals(method.getName())) {
            Intent raw = foundFirstIntentOfArgs(args).second;
            if (!TextUtils.equals(UPFApplication.getContext().getPackageName(), raw.getComponent().getPackageName())) {
                // 插件的intent才做hook
                Log.v(TAG, "hook method stopService success");
                return ServiceManager.getInstance().stopService(raw);
            }
        }

        return method.invoke(mBase, args);
    }

    private Pair<Integer, Intent> foundFirstIntentOfArgs(Object... args) {
        int index = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof Intent) {
                index = i;
                break;
            }
        }
        return Pair.create(index, (Intent) args[index]);
    }
}
