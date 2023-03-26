package com.lzp.smarthomesys.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;

import static java.nio.charset.StandardCharsets.*;

@Component
public class FileUtils {

    @Value("${aliyun.tokenPath}")
    private String tokenPath;


    // 不是所有的工具类就必须使用静态方法, 当不需要使用非静态变量时候才如此
    /**
     * 读取阿里云文件的token
     * @return String
     */
    public String readAliyunToken() {
        String token = null;
        try {
            // 判断文件是否存在
            File file=new File(tokenPath + "aliyunToken.txt");
            // 判断文件父目录是否存在, 不存在则创建
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            // 判断文件是否存在, 不存在就创建
            if(!file.exists())
                file.createNewFile();

            FileInputStream fileInputStream = new FileInputStream(tokenPath + "aliyunToken.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader br = new BufferedReader(inputStreamReader);
            token = br.readLine(); // 只有一行
            br.close();
            inputStreamReader.close();
            fileInputStream.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        return token;
    }

    public void modifyAliyunToken(String token) throws IOException {
        try {
            // 判断文件是否存在
            File file = new File(tokenPath + "aliyunToken.txt");
            if (!file.getParentFile().exists())
                file.getParentFile().mkdirs();
            if(!file.exists())
                file.createNewFile();

            FileOutputStream fos = new FileOutputStream(tokenPath + "aliyunToken.txt",false);
            byte[] bytes = token.getBytes(UTF_8);  // 将字符串按指定编码集编码--》将信息转成二进制数
            fos.write(bytes);  // 这样写入的数据，会将文件中的原数据覆盖
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
