package com.ice.good.lib.lib.log.base;

import com.ice.good.lib.lib.log.formatter.StackTraceFormatter;
import com.ice.good.lib.lib.log.formatter.ThreadFormatter;

import java.util.List;

public class LogConfig {
    public static int MAX_LEN = 512;
    public static ThreadFormatter FORMATTER_THREAD = new ThreadFormatter();
    public static StackTraceFormatter FORMATTER_STACK_TRACE = new StackTraceFormatter();
    private JsonParser jsonParser;
    private List<ILogPrinter> logPrinters;

    public JsonParser injectJsonParser() {
        return null;
    }

    public JsonParser getJsonParser() {
        if(jsonParser!=null){
            return jsonParser;
        }
        jsonParser = injectJsonParser();
        return jsonParser;
    }

    public String getGlobalTag(){
        return "YLog";
    }

    public boolean enable(){
        return true;
    }

    public boolean includeThread(){
        return true;
    }

    public int stackTraceDepth() {
        return 5;
    }

    public List<ILogPrinter> getLogPrinters() {
        return logPrinters;
    }

    public void setLogPrinters(List<ILogPrinter> logPrinters) {
        this.logPrinters = logPrinters;
    }

    public interface JsonParser{
        String toJson(Object src);
    }
}
