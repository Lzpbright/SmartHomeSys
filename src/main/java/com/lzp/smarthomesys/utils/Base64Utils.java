package com.lzp.smarthomesys.utils;

import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * use：Base64工具类
 */
public class Base64Utils {

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

}
