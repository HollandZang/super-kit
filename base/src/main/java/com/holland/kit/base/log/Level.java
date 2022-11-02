package com.holland.kit.base.log;

import com.holland.kit.base.functional.NonFunc;

public enum Level {
    TRACE(16), DEBUG(8), INFO(4), ERROR(2);
    public final int i;

    Level(int i) {
        this.i = i;
    }

    public boolean notNeedWrite() {
        switch (this) {
            case TRACE:
                return this.i >> 4 == 0;
            case DEBUG:
                return this.i >> 3 == 0;
            case INFO:
                return this.i >> 2 == 0;
            case ERROR:
                return this.i >> 1 == 0;
            default:
                throw new IllegalStateException("Unexpected value: " + this);
        }
    }

    public void ifWrite(NonFunc f) {
        if (!notNeedWrite()) f.execute();
    }
}
