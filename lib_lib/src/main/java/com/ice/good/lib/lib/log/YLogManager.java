package com.ice.good.lib.lib.log;

import android.util.LogPrinter;

import androidx.annotation.NonNull;

import com.ice.good.lib.lib.log.base.ILogPrinter;
import com.ice.good.lib.lib.log.base.LogConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.LogManager;

public class YLogManager {
    private LogConfig logConfig;
    private List<ILogPrinter> printers = new ArrayList<>();

    private static YLogManager instance;
    public static YLogManager getInstance() {
        return instance;
    }
    private YLogManager(LogConfig config, ILogPrinter[] printers){
        this.logConfig = config;
        this.printers.addAll(Arrays.asList(printers));
    }

    public static void init(@NonNull LogConfig config, ILogPrinter... printers) {
        instance = new YLogManager(config, printers);
    }

    public LogConfig getLogConfig() {
        return logConfig;
    }

    public List<ILogPrinter> getPrinters() {
        return printers;
    }

    public void addPrinter(ILogPrinter printer){
        printers.add(printer);
    }

    public void removePrinter(ILogPrinter printer){
        if(printer!=null){
            printers.remove(printer);
        }
    }
}
