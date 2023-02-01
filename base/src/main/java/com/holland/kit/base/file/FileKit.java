package com.holland.kit.base.file;

import com.holland.kit.base.functional.Either;
import com.holland.kit.base.log.Log;
import com.holland.kit.base.log.LogFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileKit {
    private static final Log log = LogFactory.create(FileKit.class);

    public static Either<Throwable, File> mkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdir()) {
                mkdir(path.substring(0, path.lastIndexOf(File.separatorChar)));
                file.mkdir();
            }
            log.debug("Create directory: {}", path);
        } else {
            log.trace("Exists directory: {}", path);
        }
        return Either.success(file);
    }

    public static Either<Throwable, File> newFile(String path, String filename) {
        mkdir(path);
        String uri = path + File.separatorChar + filename;
        File file = new File(uri);
        if (file.exists()) {
            log.debug("Find file: {}", uri);
        } else {
            try {
                file.createNewFile();
                log.debug("Create file: {}", uri);
            } catch (IOException e) {
                log.error("Create file error: {}", uri, e);
                return Either.error(e);
            }
        }
        return Either.success(file);
    }

    public static Either<Throwable, File> existFile(String path, String filename) {
        mkdir(path);
        String uri = path + File.separatorChar + filename;
        File file = new File(uri);

        return file.exists()
                ? Either.success(file)
                : Either.error(new FileNotFoundException('[' + uri + ']'));
    }

    public static Either<Throwable, File> write(String path, String filename, boolean append, String content) {
        return newFile(path, filename)
                .then(file -> {
                    String uri = path + File.separatorChar + filename;
                    try (FileOutputStream os = new FileOutputStream(file, append)) {
                        String str = content + System.getProperty("line.separator");
                        os.write(str.getBytes(StandardCharsets.UTF_8));
                        log.trace("Write file: {} content: {}", uri, str);
                    } catch (IOException e) {
                        log.error("Write file error: {}", uri, e);
                        return Either.error(e);
                    }
                    return Either.success(file);
                });
    }

    public static Either<Throwable, String> read(String path, String filename) {
        return newFile(path, filename)
                .then(file -> {
                    String uri = path + File.separatorChar + filename;
                    try (FileInputStream is = new FileInputStream(file)) {
                        byte[] bytes = new byte[is.available()];
                        is.read(bytes);
                        return Either.success(new String(bytes));
                    } catch (IOException e) {
                        log.error("Write file error: {}", uri, e);
                        return Either.error(e);
                    }
                });
    }

}
