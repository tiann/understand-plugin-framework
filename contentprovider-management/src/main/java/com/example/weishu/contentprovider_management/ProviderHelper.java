package com.example.weishu.contentprovider_management;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author weishu
 * @date 16/7/8.
 */
public class ProviderHelper {

    /**
     * 解析Apk文件中的 <provider>, 并存储起来
     * 主要是调用PackageParser类的generateProviderInfo方法
     *
     * @param apkFile 插件对应的apk文件
     * @throws Exception 解析出错或者反射调用出错, 均会抛出异常
     */
    public static List<ProviderInfo> parseProviders(File apkFile) throws Exception {
        Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");
        Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);

        Object packageParser = packageParserClass.newInstance();

        // 首先调用parsePackage获取到apk对象对应的Package对象
        Object packageObj = parsePackageMethod.invoke(packageParser, apkFile, PackageManager.GET_PROVIDERS);

        // 读取Package对象里面的services字段
        // 接下来要做的就是根据这个List<Provider> 获取到Provider对应的ProviderInfo
        Field providersField = packageObj.getClass().getDeclaredField("providers");
        List providers = (List) providersField.get(packageObj);

        // 调用generateProviderInfo 方法, 把PackageParser.Provider转换成ProviderInfo
        Class<?> packageParser$ProviderClass = Class.forName("android.content.pm.PackageParser$Provider");
        Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
        Class<?> userHandler = Class.forName("android.os.UserHandle");
        Method getCallingUserIdMethod = userHandler.getDeclaredMethod("getCallingUserId");
        int userId = (Integer) getCallingUserIdMethod.invoke(null);
        Object defaultUserState = packageUserStateClass.newInstance();

        // 需要调用 android.content.pm.PackageParser#generateProviderInfo
        Method generateProviderInfo = packageParserClass.getDeclaredMethod("generateProviderInfo",
                packageParser$ProviderClass, int.class, packageUserStateClass, int.class);

        List<ProviderInfo> ret = new ArrayList<>();
        // 解析出intent对应的Provider组件
        for (Object service : providers) {
            ProviderInfo info = (ProviderInfo) generateProviderInfo.invoke(packageParser, service, 0, defaultUserState, userId);
            ret.add(info);
        }

        return ret;
    }

    /**
     * 在进程内部安装provider, 也就是调用 ActivityThread.installContentProviders方法
     *
     * @param context you know
     * @param apkFile
     * @throws Exception
     */
    public static void installProviders(Context context, File apkFile) throws Exception {
        List<ProviderInfo> providerInfos = parseProviders(apkFile);

        for (ProviderInfo providerInfo : providerInfos) {
            providerInfo.applicationInfo.packageName = context.getPackageName();
        }

        Log.d("test", providerInfos.toString());
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);
        Method installProvidersMethod = activityThreadClass.getDeclaredMethod("installContentProviders", Context.class, List.class);
        installProvidersMethod.setAccessible(true);
        installProvidersMethod.invoke(currentActivityThread, context, providerInfos);
    }
}
