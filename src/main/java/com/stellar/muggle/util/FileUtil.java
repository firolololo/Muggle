package com.stellar.muggle.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

/**
 * @author firo
 * @version 1.0
 * @date 2021/1/4 17:32
 */
public class FileUtil {
    public static Path joinPath(String first, String... more) {
        return Paths.get(first, more);
    }

    public static byte[] readBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> readLines(Path path) {
        try {
            return Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void write(Path path, Iterable<? extends CharSequence> lines, Charset charset, OpenOption... openOptions) {
        try {
            Files.write(path, lines, charset, openOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(Path path, String content, Charset charset) {
        write(path, Collections.singleton(content), charset);
    }

    public static void write(Path path, List<String> lines, Charset charset) {
        write(path, lines, charset);
    }

    public static void append(Path path, String content, Charset charset) {
        write(path, Collections.singleton(content), charset, StandardOpenOption.APPEND);
    }

    public static void appendLines(Path path, List<String> lines, Charset charset) {
        write(path, lines, charset, StandardOpenOption.APPEND);
    }

    

}
