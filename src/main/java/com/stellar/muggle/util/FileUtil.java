package com.stellar.muggle.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
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

    /**
     * 从文件读取
     * @param path
     * @return
     */
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

    /**
     * 写入文件
     * @param path
     * @param lines
     * @param charset
     * @param openOptions
     */
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

    /**
     * 拷贝文件
     * @param fromPath
     * @param toPath
     */
    public static void copyIfAbsent(Path fromPath, Path toPath) {
        try {
            Files.copy(fromPath, toPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void copy(Path fromPath, Path toPath) {
        try {
            Files.copy(fromPath, toPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
