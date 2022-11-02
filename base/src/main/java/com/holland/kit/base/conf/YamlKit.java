package com.holland.kit.base.conf;

import com.holland.kit.base.functional.Either;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class YamlKit implements ConfKit<Map<String, Object>> {
    private static volatile YamlKit instance;

    private YamlKit() {
    }

    public static YamlKit getInstance() {
        if (instance == null) {
            synchronized (CachedConf.class) {
                if (instance == null) {
                    instance = new YamlKit();
                }
            }
        }
        return instance;
    }

    @Override
    public Either<Throwable, Map<String, Object>> read(String path, String filename, boolean cached) {
        String    uri = path + File.separatorChar + filename;
        Map<?, ?> map = CachedConf.getInstance().get(uri);
        if (null != map)
            //noinspection unchecked
            return Either.success((Map<String, Object>) map);

        ClassLoader classLoader = this.getClass().getClassLoader();
        URL         resource    = classLoader.getResource(uri);
        if (null == resource)
            return Either.error(new FileNotFoundException(uri));

        Yaml yaml = new Yaml();
        try (InputStream inputStream = resource.openStream()) {
            Map<String, Object> obj  = yaml.load(inputStream);
            if (cached)
                CachedConf.getInstance().put(uri, obj);
            return Either.success(obj);
        } catch (IOException e) {
            return Either.error(e);
        }
    }

    @Override
    public void cache(String path, String filename, Map<String, Object> properties) {
        String uri = path + File.separatorChar + filename;
        CachedConf.getInstance().put(uri, properties);
    }
}
