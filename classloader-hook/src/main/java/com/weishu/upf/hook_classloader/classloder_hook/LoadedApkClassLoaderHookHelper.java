package com.weishu.upf.hook_classloader.classloder_hook;

import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.os.UserHandle;

import com.weishu.upf.hook_classloader.Utils;

/**
 * @author weishu
 * @date 16/3/29
 */
public class LoadedApkClassLoaderHookHelper {

    public static Map<String, Object> sLoadedApk = new HashMap<String, Object>();

    public static void hookLoadedApkInActivityThread(File apkFile) throws ClassNotFoundException,
            NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException, InstantiationException {

        // 先获取到当前的ActivityThread对象
        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);

        // 获取到 mPackages 这个静态成员变量, 这里缓存了dex包的信息
        Field mPackagesField = activityThreadClass.getDeclaredField("mPackages");
        mPackagesField.setAccessible(true);
        Map mPackages = (Map) mPackagesField.get(currentActivityThread);

        // android.content.res.CompatibilityInfo
        Class<?> compatibilityInfoClass = Class.forName("android.content.res.CompatibilityInfo");
        Method getPackageInfoNoCheckMethod = activityThreadClass.getDeclaredMethod("getPackageInfoNoCheck", ApplicationInfo.class, compatibilityInfoClass);

        Field defaultCompatibilityInfoField = compatibilityInfoClass.getDeclaredField("DEFAULT_COMPATIBILITY_INFO");
        defaultCompatibilityInfoField.setAccessible(true);

        Object defaultCompatibilityInfo = defaultCompatibilityInfoField.get(null);
        ApplicationInfo applicationInfo = generateApplicationInfo(apkFile, 0);

        Object loadedApk = getPackageInfoNoCheckMethod.invoke(currentActivityThread, applicationInfo, defaultCompatibilityInfo);

        String odexPath = Utils.getPluginOptDexDir(applicationInfo.packageName).getPath();
        String libDir = Utils.getPluginLibDir(applicationInfo.packageName).getPath();
        ClassLoader classLoader = new CustomClassLoader(apkFile.getPath(), odexPath, libDir, ClassLoader.getSystemClassLoader());
        Field mClassLoaderField = loadedApk.getClass().getDeclaredField("mClassLoader");
        mClassLoaderField.setAccessible(true);
        mClassLoaderField.set(loadedApk, classLoader);

        // 由于是弱引用, 因此我们必须在某个地方存一份, 不然容易被GC; 那么就前功尽弃了.
        sLoadedApk.put(applicationInfo.packageName, loadedApk);

        WeakReference weakReference = new WeakReference(loadedApk);
        mPackages.put(applicationInfo.packageName, weakReference);
    }

    /**
     * 这个方法的最终目的是调用
     * android.content.pm.PackageParser#generateActivityInfo(android.content.pm.PackageParser.Activity, int, android.content.pm.PackageUserState, int)
     */
    public static ApplicationInfo generateApplicationInfo(File apkFile, int flags)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchFieldException {

        // 首先找出需要反射的核心类: android.content.pm.PackageParser
        Class<?> packageParserClass = Class.forName("android.content.pm.PackageParser");

        // 我们的终极目标: android.content.pm.PackageParser#generateActivityInfo(android.content.pm.PackageParser.Activity, int, android.content.pm.PackageUserState, int)
        // 要调用这个方法, 需要做很多准备工作; 考验反射技术的时候到了 - -!
        // 首先, 这个里面的参数 Activity类并不是我们通常使用的 android.app.Activity, 而是PackageParser的一个内部类
        // 因此, 我们得创建出一个Activity对象出来供这个方法调用
        // 而这个需要得对象可以通过 android.content.pm.PackageParser#parsePackage 这个方法返回得 Package对象得字段获取得到
        // 下面, 我们开始这场Hack之旅吧!

        // 首先拿到我们得终极目标: generateActivityInfo方法
        //     public static final ActivityInfo generateActivityInfo(Activity a, int flags,
        // PackageUserState state, int userId)
        Class<?> packageParser$ActivityClass = Class.forName("android.content.pm.PackageParser$Activity");
        Class<?> packageUserStateClass = Class.forName("android.content.pm.PackageUserState");
        Method generateActivityInfoMethod = packageParserClass.getDeclaredMethod("generateActivityInfo",
                packageParser$ActivityClass,
                int.class,
                packageUserStateClass,
                int.class);

        // 接下来构建需要得参数

        // 第一个参数 Activity信息
        Object targetActivity;

        // 创建出一个PackageParser对象供使用
        Object packageParser = packageParserClass.newInstance();
        // 调用 PackageParser.parsePackage 解析apk的信息
        Method parsePackageMethod = packageParserClass.getDeclaredMethod("parsePackage", File.class, int.class);

        // 实际上是一个 android.content.pm.PackageParser.Package 对象
        Object packageObj = parsePackageMethod.invoke(packageParser, apkFile, 0);

        // 拿到上面那个对象的class表示, 下面反射用
        Class<?> parseParse$PackageClass = packageObj.getClass();

        // 获取它的 android.content.pm.PackageParser.Package#activities 这个字段
        Field acitvitiesField = parseParse$PackageClass.getDeclaredField("activities");

        // 实际上是一个List
        List activities = (List) acitvitiesField.get(packageObj);

        if (activities.size() <= 0) {
            throw new RuntimeException("no activities found in " + apkFile);
        }

        // 随便拿一个Activity当作参数就行, 第一个参数找出完毕
        targetActivity = activities.get(0);

        // 第二个参数flags, 由用户传递

        // 第三个参数 mDefaultPackageUserState 我们直接使用默认构造函数构造一个出来即可
        Object defaultPackageUserState = packageUserStateClass.newInstance();

        // 第四个参数 userId : 调用UserHandle.getCallingUserId
        Method getCallingUserIdMethod = UserHandle.class.getDeclaredMethod("getCallingUserId");
        Object userId = getCallingUserIdMethod.invoke(null);

        // 万事具备!!!!!!!!!!!!!! 开始获取Activity信息
        ActivityInfo activityInfo = (ActivityInfo) generateActivityInfoMethod.invoke(packageParser, targetActivity, flags, defaultPackageUserState, userId);
        ApplicationInfo applicationInfo = activityInfo.applicationInfo;
        String apkPath = apkFile.getPath();

        applicationInfo.sourceDir = apkPath;
        applicationInfo.publicSourceDir = apkPath;

        return applicationInfo;
    }
}
