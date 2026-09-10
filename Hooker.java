package jp.yuzu;

import android.os.Process;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hooker {
    public static void init(ClassLoader classLoader) {
        XposedBridge.log("柚子Hook 加载成功！");
        XposedBridge.log("CRACK BY YUZU TEAM @Yuzu-Me");
        // n226d7500 = 写入cgroup
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "n226d7500", int.class, int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    boolean frozen = (boolean) param.args[2];
                    int uid = (int) param.args[0];
                    int pid = (int) param.args[1];
                    {
                        // 暂时没写Android 15和CGROUP V1的支持
                        String path = uid < android.os.Process.FIRST_APPLICATION_UID ? "system" : "apps";
                        try (PrintWriter writer = new PrintWriter("/sys/fs/cgroup/" + path + "/uid_" + uid + "/pid_" + pid + "/cgroup.freeze")) {
                            writer.println(frozen ? "1" : "0");
                            writer.flush();
                        }
                    }
                } catch (Throwable throwable) {
                    XposedBridge.log("[YUZU HOOK] n226d7500 HOOK:");
                    XposedBridge.log(throwable);
                }

                param.setResult(null);
            }
        });
      
        // nd09c6ff6 = 写入cgroup (不知道为什么有两个)
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "nd09c6ff6", int.class, int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    boolean frozen = (boolean) param.args[2];
                    int uid = (int) param.args[1];
                    int pid = (int) param.args[0];
                    {
                        // 暂时没写Android 15和CGROUP V1的支持
                        String path = uid < Process.FIRST_APPLICATION_UID ? "system" : "apps";
                        try (PrintWriter writer = new PrintWriter("/sys/fs/cgroup/" + path + "/uid_" + uid + "/pid_" + pid + "/cgroup.freeze")) {
                            writer.println(frozen ? "1" : "0");
                            writer.flush();
                        }
                    }
                } catch (Throwable throwable) {
                    XposedBridge.log("[YUZU HOOK] nd09c6ff6 HOOK:");
                    XposedBridge.log(throwable);
                }
                param.setResult(null);
            }
        });

        // n5bca8d9d = 不知道有啥用 先跳过 避免底层校验
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "n5bca8d9d", int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });

        // n64684989 = 激活状态
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "n64684989", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });

        // n69b3a89b = 读取进程wchan
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "n69b3a89b", int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                String content = "";
                try {
                    content = new String(Files.readAllBytes(new File("/proc/" + param.args[0] + "/wchan").toPath()), StandardCharsets.UTF_8);
                } catch (Exception e) {
                    XposedBridge.log("[YUZU HOOK] wchan HOOK:");
                    XposedBridge.log(e);
                }
                param.setResult(content);
            }
        });

        // n59f99d41 = 不知道有啥用 先跳过 避免底层校验
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "n59f99d41", "java.lang.String", "java.lang.String", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("[YUZU HOOK] UNKNOWN METHOD: " + param.args[0] + " | " + param.args[1]);
                param.setResult(true);
            }
        });

        // na217840a = 不知道有啥用 先跳过 避免底层校验
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "na217840a", "java.lang.String", boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });
    }
}
