package jp.yuzu;

import android.os.Process;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hooker {
    private static final boolean android16;

    static {
        android16 = !Files.exists(Paths.get("/sys/fs/cgroup/uid_1000/cgroup.freeze"));
    }

    public static void init(ClassLoader classLoader) {
        XposedBridge.log("柚子Hook 加载成功！");
        XposedBridge.log("CRACK BY YUZU TEAM @Yuzu-Me");
        // n03e9b349 = 写入cgroup
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "n03e9b349", int.class, int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    boolean frozen = (boolean) param.args[2];
                    int uid = (int) param.args[0];
                    int pid = (int) param.args[1];
                    if (!android16) {
                        try (PrintWriter writer = new PrintWriter("/sys/fs/cgroup/uid_" + uid + "/pid_" + pid + "/cgroup.freeze")) {
                            writer.println(frozen ? "1" : "0");
                            writer.flush();
                        }
                    } else {
                        String path = uid < Process.FIRST_APPLICATION_UID ? "system" : "apps";
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

        // nd09c6ff6 = 调用Process.setProcessFrozen
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "nb9541ef5", int.class, int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                try {
                    boolean frozen = (boolean) param.args[2];
                    int uid = (int) param.args[1];
                    int pid = (int) param.args[0];
                    XposedHelpers.callStaticMethod(Process.class, "setProcessFrozen", pid, uid, frozen);
                } catch (Throwable throwable) {
                    XposedBridge.log("[YUZU HOOK] nd09c6ff6 HOOK:");
                    XposedBridge.log(throwable);
                }
                param.setResult(null);
            }
        });

        // n58f67e53 = 不知道有啥用 先跳过 避免底层校验
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "n58f67e53", int.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });

        // n0a228673 = 激活状态
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "n0a228673", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });

        // nf04e6f49 = 读取进程wchan
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "nf04e6f49", int.class, new XC_MethodHook() {
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

        // n2751bb6a = 不知道有啥用 先跳过 避免底层校验
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "n2751bb6a", "java.lang.String", "java.lang.String", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
            }
        });

        // nf988717d = 不知道有啥用 先跳过 避免底层校验
        XposedHelpers.findAndHookMethod("cn.myflv.noactive.jni.Core", classLoader, "nf988717d", "java.lang.String", boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);
            }
        });
    }
}
