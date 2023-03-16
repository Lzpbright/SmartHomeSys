package com.lzp.smarthomesys.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


/**
 * use：用来生成授权码
 */
@Component
public class Token {

    public static String assembleToken(String version, String resourceName, String expirationTime,
                                       String signatureMethod, String accessKey)
            throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        StringBuilder sb = new StringBuilder();
        String res = URLEncoder.encode(resourceName, "UTF-8");
        String sig = URLEncoder.encode(generatorSignature(version, resourceName, expirationTime
                , accessKey, signatureMethod), "UTF-8");
        sb.append("version=")
                .append(version)
                .append("&res=")
                .append(res)
                .append("&et=")
                .append(expirationTime)
                .append("&method=")
                .append(signatureMethod)
                .append("&sign=")
                .append(sig);
        return sb.toString();
    }

    public static String generatorSignature(String version, String resourceName, String expirationTime,
                                            String accessKey, String signatureMethod) throws NoSuchAlgorithmException,
            InvalidKeyException {
        String encryptText = expirationTime + "\n" + signatureMethod + "\n" + resourceName + "\n" + version;
        String signature;
        byte[] bytes = HmacEncrypt(encryptText, accessKey, signatureMethod);
        signature = Base64.getEncoder().encodeToString(bytes);
        return signature;
    }

    public static byte[] HmacEncrypt(String data, String key, String signatureMethod)
            throws NoSuchAlgorithmException, InvalidKeyException {
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKeySpec signinKey = null;
        signinKey = new SecretKeySpec(Base64.getDecoder().decode(key),
                "Hmac" + signatureMethod.toUpperCase());

        //生成一个指定 Mac 算法 的 Mac 对象
        Mac mac = null;
        mac = Mac.getInstance("Hmac" + signatureMethod.toUpperCase());

        //用给定密钥初始化 Mac 对象
        mac.init(signinKey);

        //完成 Mac 操作
        return mac.doFinal(data.getBytes());
    }

    public enum SignatureMethod {
        SHA1, MD5, SHA256;
    }

    // oneNet的产品ID
    @Value("${onenet.productId}")
    private String productId;
    // 产品的access_key
    @Value("${onenet.accessKey}")
    private String accessKey;
    // 版本号，无需修改
    @Value("${onenet.version}")
    private String version;
    // 维护本类中一个静态变量
    private static Token token;

    @PostConstruct
    private void init(){
        token = this;
        token.accessKey = this.accessKey;
        token.productId = this.productId;
        token.version = this.version;
    }

    /**
     * 获取onenet平台的授权码
     * @return token
     * @throws UnsupportedEncodingException UnsupportedEncodingException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeyException InvalidKeyException
     */
    public static String getToken() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        String productId = token.productId;
        String accessKey = token.accessKey;
        String version = token.version;
        // API访问 访问资源
        String resourceName = "products/" + productId;
        // 访问过期时间
        String expirationTime = System.currentTimeMillis() / 1000 + 365 * 24 * 60 * 60 + "";    // 1年
        //签名方法，支持md5、sha1、sha256
        String signatureMethod = SignatureMethod.SHA1.name().toLowerCase();
        return assembleToken(version, resourceName, expirationTime, signatureMethod, accessKey);
    }
}
