package com.lzp.smarthomesys.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import java.util.UUID;

@Component
public class UploadUtils {

    @Value("${images.winPath}")
    public String winPath;

    @Value("${images.linuxPath}")
    private String linuxPath;

    @Value("${images.accessPath}")
    private String accessPath;

    private static UploadUtils uploadUtils;

    @PostConstruct
    private void init(){
        uploadUtils = this;
        uploadUtils.winPath = this.winPath;
        uploadUtils.linuxPath = this.linuxPath;
        uploadUtils.accessPath = this.accessPath;
    }

    /**
     * 上传头像
     * @param file 头像文件
     * @return 头像存储路径
     * @throws IOException 输入输出异常
     */
    public static String uploads(MultipartFile file) throws IOException {
        String winPath = uploadUtils.winPath;
        String linuxPath = uploadUtils.linuxPath;
        String accessPath = uploadUtils.accessPath;

        String usePath;
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            usePath = winPath;
        }else {
            usePath = linuxPath;
        }
        // 获取文件后缀
        String suffix = Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf('.') + 1);

        // 使用UUID新建文件名
        String fileName = UUID.randomUUID() + "." + suffix;

        // 每天建立一个文件夹
        String time = (new SimpleDateFormat("yyyy/MM/dd")).format(System.currentTimeMillis());
        usePath += "/" + time + "/";

        // 判断是否存在本目录
        File destFile = new File(usePath + fileName);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        // 文件写入
        File descFile = new File(usePath, fileName);
        file.transferTo(descFile);

        // 返回文件url
        return accessPath + time + "/" + fileName;
    }
}
