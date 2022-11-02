package com.holland.kit.base.conf;

import com.holland.kit.base.log.ILog;
import com.holland.kit.base.log.LogFactory;

public class ParamDict {
    public static void main(String[] args) {
        ILog log = LogFactory.create(ParamDict.class);
        ILog log1 = LogFactory.create(ParamDict.class);

        log.info("test");
        log.info("test");
        log1.info("test");
        System.out.println();
    }
}
