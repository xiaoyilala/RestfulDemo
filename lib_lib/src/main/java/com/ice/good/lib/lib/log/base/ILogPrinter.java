package com.ice.good.lib.lib.log.base;

import androidx.annotation.NonNull;

public interface ILogPrinter {
    void print(@NonNull LogConfig config, int level, String tag, @NonNull String printString);
}
