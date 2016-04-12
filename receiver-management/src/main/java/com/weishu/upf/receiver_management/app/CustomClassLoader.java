package com.weishu.upf.receiver_management.app;

import java.io.File;
import java.io.IOException;

import dalvik.system.DexClassLoader;

/**
 * @author weishu
 * @date 16/4/7
 */
public class CustomClassLoader extends DexClassLoader {
    public CustomClassLoader(String dexPath, String optimizedDirectory, String libraryPath, ClassLoader parent) {
        super(dexPath, optimizedDirectory, libraryPath, parent);
    }

    /**
     * 便利方法: 获取插件的ClassLoader, 能够加载指定的插件中的类
     *
     * @param plugin
     * @param packageName
     * @return
     * @throws IOException
     */
    public static CustomClassLoader getPluginClassLoader(File plugin, String packageName) throws IOException {
        return new CustomClassLoader(plugin.getPath(),
                Utils.getPluginOptDexDir(packageName).getPath(),
                Utils.getPluginLibDir(packageName).getPath(),
                UPFApplication.getContext().getClassLoader());
    }

}
