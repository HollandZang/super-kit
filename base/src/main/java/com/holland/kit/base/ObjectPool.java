package com.holland.kit.base;


import com.holland.kit.base.log.ILog;
import com.holland.kit.base.log.LogFactory;

import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public abstract class ObjectPool<T> {
    protected final ILog     log = LogFactory.create(this.getClass());
    protected final int      corePoolSize;
    protected final int      maximumPoolSize;
    protected final long     keepAliveTime;
    protected final TimeUnit unit;

    protected final ConcurrentHashMap<T, Long> locked, unlocked;

    public ObjectPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        if (corePoolSize < 0 ||
                maximumPoolSize <= 0 ||
                maximumPoolSize < corePoolSize ||
                keepAliveTime < 0)
            throw new IllegalArgumentException();

        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;

        this.locked = new ConcurrentHashMap<>(corePoolSize);
        this.unlocked = new ConcurrentHashMap<>(corePoolSize);
    }

    protected abstract T create();

    protected abstract boolean validate(T o);

    protected abstract void expire(T o);

    public synchronized T checkOut() {
        if (unlocked.size() == 0 && locked.size() == maximumPoolSize) {
            int i = 0;
            while (locked.size() == maximumPoolSize) {
                // 1秒打印一次日志
                if (i == 20) {
                    log.warn("The number of connections exceeds the maximumPoolSize, use the default waiting policy");
                    i = 0;
                } else i++;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        final long now = System.currentTimeMillis();
        T          t;
        if (unlocked.size() > 0) {
            for (Map.Entry<T, Long> entry : unlocked.entrySet()) {
                t = entry.getKey();
                final Long createTime = entry.getValue();
                if (now - createTime > unit.toMillis(keepAliveTime)) {
                    log.trace("Expire a resource: ");
                    unlocked.remove(t);
                    expire(t);
                    t = null;
                } else {
                    if (validate(t)) {
                        unlocked.remove(t);
                        locked.put(t, now);
                        return t;
                    } else {
                        unlocked.remove(t);
                        expire(t);
                        t = null;
                    }
                }
            }
        }
        t = create();
        locked.put(t, now);
        return t;
    }

    public void checkIn(T t) {
        locked.remove(t);
        unlocked.put(t, System.currentTimeMillis());
    }

    public void close() {
        closeUnlocked();

        while (locked.size() > 0) {
            log.info("waiting for running object");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Unexpected sleep interrupted", e);
            }
            closeUnlocked();
        }

        closeUnlocked();
    }

    private synchronized void closeUnlocked() {
        final Enumeration<T> keys = unlocked.keys();
        while (keys.hasMoreElements()) {
            T t = keys.nextElement();
            expire(t);
            unlocked.remove(t);
            t = null;
        }
    }
}
