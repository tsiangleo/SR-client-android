package com.github.tsiangleo.sr.client.business;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.github.tsiangleo.sr.client.domain.AppInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tsiang on 2016/11/29.
 */
public class AppInfoProvider
{
    private PackageManager packageManager;

    private static Map<String,AppInfo> cache;

    public AppInfoProvider(Context context){
        packageManager = context.getPackageManager();
    }

    public List<AppInfo> getAllAppInfo()
    {
        List<AppInfo> list = new ArrayList<AppInfo>();
        AppInfo myAppInfo;
        //获取到所有安装了的应用程序的信息，包括那些卸载了的，但没有清除数据的应用程序
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for(PackageInfo info : packageInfos)
        {
            myAppInfo = new AppInfo();
            //拿到包名
            String packageName = info.packageName;
            //拿到应用程序的信息
            ApplicationInfo appInfo = info.applicationInfo;
            //拿到应用程序的图标
            Drawable icon = appInfo.loadIcon(packageManager);
            //拿到应用程序的程序名
            String appName = appInfo.loadLabel(packageManager).toString();
            myAppInfo.setAppName(appName);
            myAppInfo.setPackageName(packageName);
            myAppInfo.setIcon(icon);
            if(filterApp(appInfo))
            {
                myAppInfo.setSystemApp(false);
            }
            else
            {
                myAppInfo.setSystemApp(true);
            }
            list.add(myAppInfo);
        }
        return list;
    }

    //判断某一个应用程序是不是系统的应用程序，如果是返回true，否则返回false
    public boolean filterApp(ApplicationInfo info)
    {
        //有些系统应用是可以更新的，如果用户自己下载了一个系统的应用来更新了原来的，
        //它还是系统应用，这个就是判断这种情况的
        if((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0)
        {
            return true;
        }
        else if((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0)//判断是不是系统应用
        {
            return true;
        }
        return false;
    }


    /**
     * 根据appFullName返回对应的AppInfo对象。appFullName由AppPackageName+"#"+appName组成。
     * @param appFullName
     * @return 不存在则返回null。
     */
    public AppInfo getAppInfo(String appFullName) {

        if(cache == null){
            return loadData(appFullName);
        }

        if(cache != null && cache.containsKey(appFullName)){
            return cache.get(appFullName);
        }else{
            return  null;
        }
    }

    private AppInfo loadData(String appFullName) {
        AppInfo result = null;
        cache = new HashMap<>();
        List<AppInfo> appInfoList =  getAllAppInfo();
        if(appInfoList != null){
            for(AppInfo app:appInfoList){
                String key = app.getPackageName()+"#"+app.getAppName();
                cache.put(key,app);
                if(key.equals(appFullName)){
                    result = app;
                }
            }
        }
        return result;
    }
}
