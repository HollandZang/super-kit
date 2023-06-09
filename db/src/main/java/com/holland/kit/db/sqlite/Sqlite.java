package com.holland.kit.db.sqlite;

import com.holland.kit.base.functional.Either;

public interface Sqlite {
    void checkEnv() throws Exception;

    boolean existTable();

    Either<RuntimeException, Object> createTableWrapper();

    String find(String command);

}
