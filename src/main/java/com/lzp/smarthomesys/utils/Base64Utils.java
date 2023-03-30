package com.lzp.smarthomesys.utils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * use：Base64工具类
 */
public class Base64Utils {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    /**
     * Base64加密
     *
     * @param text 待加密内容
     * @return String
     */
    public static String encodeText(String text) {
        return encoder.encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     *
     * @param encodedText Base64加密后内容
     * @return String
     */
    public static String decode(String encodedText) {
        return new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
    }

    /**
     * 获取文件base64编码
     * @param path 文件路径
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    public static String encodeFile(String path) throws IOException {
        byte[] b = Files.readAllBytes(Paths.get(path));
        return Base64.getEncoder().encodeToString(b);
    }

    /**
     * 获取文件base64 UrlEncode编码
     * @param path 文件路径
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    static String getFileContentAsBase64Urlencoded(String path) throws IOException {
        return URLEncoder.encode(encodeFile(path), "utf-8");
    }
}
