package com.ice.good.lib.lib.log.formatter;

import com.ice.good.lib.lib.log.base.ILogFormatter;

public class ThreadFormatter implements ILogFormatter<Thread> {
    @Override
    public String format(Thread data) {
        return "Thread: "+data.getName();
    }
}
