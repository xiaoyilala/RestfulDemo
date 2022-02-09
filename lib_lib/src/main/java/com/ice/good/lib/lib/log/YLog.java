package com.ice.good.lib.lib.log;

import android.util.Log;

import androidx.annotation.NonNull;

import com.ice.good.lib.lib.log.base.ILogPrinter;
import com.ice.good.lib.lib.log.base.LogConfig;
import com.ice.good.lib.lib.log.base.LogType;
import com.ice.good.lib.lib.log.util.StackTraceUtil;

import java.util.Arrays;
import java.util.List;

public class YLog {
    private static final String Y_LOG_PACKAGE;

    static {
        String className = YLog.class.getName();
        Y_LOG_PACKAGE = className.substring(0, className.lastIndexOf('.') + 1);
    }

    public static void v(Object... contents) {
        log(LogType.V, contents);
    }

    public static void vt(String tag, Object... contents) {
        log(LogType.V, tag, contents);
    }

    public static void d(Object... contents) {
        log(LogType.D, contents);
    }

    public static void dt(String tag, Object... contents) {
        log(LogType.D, tag, contents);
    }

    public static void i(Object... contents) {
        log(LogType.I, contents);
    }

    public static void it(String tag, Object... contents) {
        log(LogType.I, tag, contents);
    }

    public static void w(Object... contents) {
        log(LogType.W, contents);
    }

    public static void wt(String tag, Object... contents) {
        log(LogType.W, tag, contents);
    }

    public static void e(Object... contents) {
        log(LogType.E, contents);
    }

    public static void et(String tag, Object... contents) {
        log(LogType.E, tag, contents);
    }

    public static void a(Object... contents) {
        log(LogType.A, contents);
    }

    public static void at(String tag, Object... contents) {
        log(LogType.A, tag, contents);
    }

    public static void log(@LogType.TYPE int type, Object... contents){
        log(type, YLogManager.getInstance().getLogConfig().getGlobalTag(), contents);
    }

    public static void log(@LogType.TYPE int type, @NonNull String tag, Object... contents){
        log(YLogManager.getInstance().getLogConfig(), type, tag, contents);
    }

    public static void log(@NonNull LogConfig config, @LogType.TYPE int type, @NonNull String tag, Object... contents){
        if(!config.enable()){
            return;
        }
        StringBuilder sb = new StringBuilder();
        if(config.includeThread()){
            String threadInfo = LogConfig.FORMATTER_THREAD.format(Thread.currentThread());
            sb.append(threadInfo).append("\n");
        }
        if(config.stackTraceDepth()>0){
            String stackInfo = LogConfig.FORMATTER_STACK_TRACE.format(StackTraceUtil.getCroppedRealStackTrack(new Throwable().getStackTrace(), Y_LOG_PACKAGE, config.stackTraceDepth()));
            sb.append(stackInfo).append("\n");
        }
        //使用数组接收可变参数时，即使没有传递可变参数，数组不会为null，只会是一个空数组对象
        String body = parseBody(contents, config);
        if (body != null) {//替换转义字符\
            body = body.replace("\\\"", "\"");
        }
        sb.append(body);
        List<ILogPrinter> printers = config.getLogPrinters()!=null? config.getLogPrinters() : YLogManager.getInstance().getPrinters();
        if (printers == null) {
            return;
        }
        for(ILogPrinter printer:printers){
            printer.print(config, type, tag, sb.toString());
        }
    }

    private static String parseBody(Object[] contents, LogConfig config) {
        if(config.getJsonParser()!=null){
            //只有一个数据且为String的情况下不再进行序列化
            if(contents.length == 1 && contents[0] instanceof String){
                return (String) contents[0];
            }
            return config.getJsonParser().toJson(contents);
        }
        StringBuilder sb = new StringBuilder();
        for (Object o : contents) {
            sb.append(o.toString()).append(";");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }


}
