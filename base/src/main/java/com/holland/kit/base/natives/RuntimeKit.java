package com.holland.kit.base.natives;

import com.holland.kit.base.functional.Either;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RuntimeKit {
    public static void main(String[] args) {
        execSyncWrapper("whereis sqlite3")
                .end(e -> {
                    throw new RuntimeException(e);
                }, s -> {
                    System.out.println(s);
                });
    }

    public static String execSync(String command) {
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (InputStream is = p.getInputStream();
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader reader = new BufferedReader(isr)
        ) {

            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            p.waitFor();
            p.destroy();

            return builder.toString();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Either<Throwable, String> execSyncWrapper(String command) {
        try {
            return Either.success(execSync(command));
        } catch (Exception e) {
            return Either.error(e);
        }
    }
}
