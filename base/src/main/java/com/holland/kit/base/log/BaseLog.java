package com.holland.kit.base.log;

public abstract class BaseLog implements ILog {
    protected Meta meta;

    public BaseLog(Meta meta) {
        this.meta = meta;
    }
}
