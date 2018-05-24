package com.zd.helloxp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.Date;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by york on 19/01/2017.
 */

public class MyHook implements IXposedHookLoadPackage {
    private static final String Tag = "xixiXp";
    private static final String myPakcageName = "cn.dong.demo";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        Log.i(Tag, "MyHook handleLoadPackage, pkg: " + loadPackageParam.packageName);
        afterHookedMethod_getDeviceId(loadPackageParam, myPakcageName);
        String packageName = "com.tencent.mm";
        afterHookedMethod_getIntent(loadPackageParam, packageName);
        beforeHookedMethod_startActivity(loadPackageParam, packageName);
        beforeHooked_startActivityForResult(loadPackageParam, packageName);
    }

    private void afterHookedMethod_getDeviceId(final XC_LoadPackage.LoadPackageParam loadPackageParam, String packageName) {
        if (!isDoHook(loadPackageParam, packageName)) {
            return;
        }
        String methodName = "getDeviceId";
        XposedHelpers.findAndHookMethod(TelephonyManager.class.getName(), loadPackageParam.classLoader, methodName, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(Tag, "MyHook afterHookedMethod " + param.method.getName());
                Object obj = param.getResult();
                Log.i(Tag, "MyHook afterHookedMethod, rst: " + obj);
                //param.setResult("hello from xposed");
            }
        });

        methodName = "formatDate";
        String className = "cn.dong.demo.util.DateUtil";

        XposedHelpers.findAndHookMethod(className, loadPackageParam.classLoader, methodName, Date.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(Tag, "MyHook beforeHookedMethod " + param.method.getName());
                if (param.args != null && param.args.length > 0) {
                    int argsSize = param.args.length;
                    for (int i = 0; i < argsSize; i++) {
                        Log.i(Tag, "MyHook beforeHookedMethod, args  " + i + " : " + param.args[i]);
                    }
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(Tag, "MyHook afterHookedMethod " + param.method.getName());
                Object obj = param.getResult();
                Log.i(Tag, "MyHook afterHookedMethod, rst: " + obj);
                //param.setResult("hello from xposed");
            }
        });
    }

    private void afterHookedMethod_getIntent(final XC_LoadPackage.LoadPackageParam loadPackageParam, String packageName) {
        if (!isDoHook(loadPackageParam, packageName)) {
            return;
        }
        String methodName = "getIntent";
        XposedHelpers.findAndHookMethod(Activity.class.getName(), loadPackageParam.classLoader, methodName, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Intent intent = (Intent) param.getResult();
                String thisObjectName = param.thisObject == null ? "" : param.thisObject.getClass().getName();
                Log.i(Tag, "MyHook afterHookedMethod caller method : " + param.method.getName() + ", thisObject: " + thisObjectName);
                walkIntent(intent);
                super.afterHookedMethod(param);
            }
        });
    }

    private void beforeHookedMethod_startActivity(final XC_LoadPackage.LoadPackageParam loadPackageParam, String packageName) {
        if (!isDoHook(loadPackageParam, packageName)) {
            return;
        }
        String methodName = "startActivity";
        XposedHelpers.findAndHookMethod(Activity.class.getName(), loadPackageParam.classLoader, methodName, Intent.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(Tag, "MyHook afterHookedMethod caller method : " + param.method.getName() + ", Intent : " + param.args[0]);
                Log.i(Tag, "MyHook afterHookedMethod caller method : " + param.method.getName() + ", Bundle : " + param.args[1]);
            }
        });
    }

    private void beforeHooked_startActivityForResult(final XC_LoadPackage.LoadPackageParam loadPackageParam, String packageName) {
        if (!isDoHook(loadPackageParam, packageName)) {
            return;
        }
        String methodName = "startActivityForResult";
        XposedHelpers.findAndHookMethod(Activity.class.getName(), loadPackageParam.classLoader, methodName, Intent.class, int.class, Bundle.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.i(Tag, "MyHook beforeHookedMethod " + param.method.getName() + " caller: " + param.thisObject);
                if (param.args[0] instanceof Intent) {
                    walkIntent((Intent) param.args[0]);
                    Log.i(Tag, "MyHook beforeHookedMethod, requestCode: " + param.args[1]);
                    Log.i(Tag, "MyHook beforeHookedMethod, options: " + param.args[2]);
                }
            }
        });
    }

    private boolean isDoHook(final XC_LoadPackage.LoadPackageParam loadPackageParam, String packageName) {
        boolean isDoHook = TextUtils.isEmpty(packageName) || loadPackageParam.packageName.equals(packageName);
        Log.i(Tag, "MyHook isDoHook : " + loadPackageParam.packageName + " ; " + packageName);
        return isDoHook;
    }

    private void walkIntent(Intent intent) {
        if (intent == null) {
            Log.i(Tag, "MyHook walkIntent, intent is null");
        } else {
            Log.i(Tag, "MyHook walkIntent, classes: " + intent.getComponent());
            Bundle extras = intent.getExtras();
            if (extras == null) {
                Log.i(Tag, "MyHook walkIntent, extras is null");
            } else {
                Log.i(Tag, "MyHook walkIntent: " + extras);
            }
        }
    }

}
