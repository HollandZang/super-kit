package com.holland.kit.base.conf;

import com.holland.kit.base.functional.Either;

import java.util.Map;

public interface ConfKit<T extends Map<?, ?>> {

    Either<Throwable, T> read(String path, String filename, boolean cached);

    void cache(String path, String filename, T config);

}
