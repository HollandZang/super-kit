package com.holland.kit.base.conf;

import com.holland.kit.base.file.PropertiesKit;
import com.holland.kit.base.log.ILog;
import com.holland.kit.base.log.LogFactory;

public class ParamDict {
    public static void main(String[] args) {
        ILog log = LogFactory.create(ParamDict.class);
        ILog log1 = LogFactory.create(PropertiesKit.class);

        log.trace("tes{}t", 1, new RuntimeException("z"));
        log.debug("tes{}t", 1, new RuntimeException("z"));
        log.info("tes{}t", 1, new RuntimeException("z"));
        log.warn("tes{}t", 1, new RuntimeException("z"));
        log.error("tes{}t", 1, new RuntimeException("z"));
        log.fatal("tes{}t", 1, new RuntimeException("z"));

        log1.trace("PropertiesKit{}t", 1, new RuntimeException("z"));
        log1.debug("PropertiesKit{}t", 1, new RuntimeException("z"));
        log1.info("PropertiesKit{}t", 1, new RuntimeException("z"));
        log1.warn("PropertiesKit{}t", 1, new RuntimeException("z"));
        log1.error("PropertiesKit{}t", 1, new RuntimeException("z"));
        log1.fatal("PropertiesKit{}t", 1, new RuntimeException("z"));
    }
}
