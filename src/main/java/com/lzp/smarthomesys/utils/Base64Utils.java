package com.lzp.smarthomesys.utils;

import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * use：Base64工具类
 */
public class Base64Utils {

    private static final Base64.Encoder encoder = Base64.getEncoder();
    private static final Base64.Decoder decoder = Base64.getDecoder();

    /**
     * Base64解码
     *
     * @param bytes Base64加密的字节码
     * @return String
     * @throws IOException IOException
     */
    public static String Base64Decode(byte[] bytes)
            throws IOException {
        BASE64Decoder base64decoder = new BASE64Decoder();
        byte[] bs = base64decoder.decodeBuffer(new String(bytes));
        return new String(bs, StandardCharsets.UTF_8);
    }

    /**
     * Base64加密
     *
     * @param text 待加密内容
     * @return String
     * @throws IOException IOException
     */
    public static String encode(String text) {
        return encoder.encodeToString(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Base64解码
     *
     * @param encodedText Base64加密后内容
     * @return String
     * @throws IOException IOException
     */
    public static String decode(String encodedText) {
        return new String(decoder.decode(encodedText), StandardCharsets.UTF_8);
    }
}
