package com.holland.kit.base.conf;

import com.holland.kit.base.file.FileKit;
import com.holland.kit.base.functional.Either;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class PropertiesKit implements ConfKit<Properties> {
    private static volatile PropertiesKit instance;

    private PropertiesKit() {
    }

    public static PropertiesKit getInstance() {
        if (instance == null) {
            synchronized (CachedConf.class) {
                if (instance == null) {
                    instance = new PropertiesKit();
                }
            }
        }
        return instance;
    }

    @Override
    public Either<Throwable, Properties> read(String path, String filename, boolean cached) {
        String    uri = path + File.separatorChar + filename;
        Map<?, ?> map = CachedConf.getInstance().get(uri);
        if (null != map)
            return Either.success((Properties) map);
        return FileKit.existFile(path, filename)
                .then(file -> {
                    try (FileInputStream is = new FileInputStream(file)) {
                        Properties properties = new Properties();
                        properties.load(is);
                        if (cached)
                            CachedConf.getInstance().put(uri, properties);
                        return Either.success(properties);
                    } catch (IOException e) {
                        return Either.error(e);
                    }
                });
    }

    @Override
    public void cache(String path, String filename, Properties properties) {
        String uri = path + File.separatorChar + filename;
        CachedConf.getInstance().put(uri, properties);
    }
}
