package com.ice.good.lib.lib.log.base;

public interface ILogFormatter<T> {
    String format(T data);
}
