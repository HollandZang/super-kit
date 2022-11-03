package com.holland.kit.base.log;

import com.holland.kit.base.functional.NonFunc;

public enum Level {
    ALL((byte) 64),
    TRACE((byte) 32),
    DEBUG((byte) 16),
    INFO((byte) 8),
    WARN((byte) 4),
    ERROR((byte) 2),
    FATAL((byte) 1),
    OFF((byte) 0);
    public final byte b;

    Level(byte b) {
        this.b = b;
    }

    public boolean notNeedWrite() {
        switch (this) {
            case TRACE:
                return this.b >> 4 == 0;
            case DEBUG:
                return this.b >> 3 == 0;
            case INFO:
                return this.b >> 2 == 0;
            case ERROR:
                return this.b >> 1 == 0;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    public void ifWrite(NonFunc f) {
        if (!notNeedWrite()) f.execute();
    }
}
