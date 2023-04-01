package com.lzp.smarthomesys;

import com.alibaba.fastjson.JSONObject;
import com.lzp.smarthomesys.controller.UserController;
import com.lzp.smarthomesys.utils.FileUtils;
import com.lzp.smarthomesys.utils.HttpUtils;
import com.lzp.smarthomesys.utils.Result;
import com.lzp.smarthomesys.utils.TokenUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//@SpringBootTest
public class GeneralTest {

//    @Value("${aliyun.accessKeyId}")
//    public String akID;
//
//    @Value("${aliyun.accessKeySecret}")
//    public String akSecret;

//    @ScheduleTask
//    public void aliyuntokentest() throws IOException {
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // ISO 8601标准
//        LocalDateTime localDateTime =LocalDateTime.now();
//        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
//        String Timestamp = DateUtil.format(date, df);
//        System.out.println(Timestamp);
//        String url = "http://nls-meta.cn-shanghai.aliyuncs.com/";
//        Map<String, String> body = new HashMap<>();
//        body.put("AccessKeyId", akID);
//        body.put("Action", "CreateToken");
//        body.put("AccessKeySecret", akSecret);
//        body.put("Version", "2019-02-28");
//        body.put("Format", "JSON");
//        body.put("RegionId", "cn-shanghai");
//        body.put("Timestamp", Timestamp);
//        body.put("SignatureMethod", "HMAC-SHA1");
//        body.put("SignatureVersion", "1.0");
//        body.put("SignatureNonce", String.valueOf(UUID.randomUUID()));
////        body.put("Signature", "");
//        Map<String, String> header = new HashMap<>();
//        header.put("Content-type", "application/x-www-form-urlencoded");
//        String res = HttpUtils.sendGet(url, header, body);
//        System.out.println("res = " + res);
//    }

//    @ScheduleTask
//    public void timeconvertest(){
//        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        LocalDateTime localDateTime =LocalDateTime.now();
//        Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
//        String format = DateUtil.format(date, df);
//
//    }

//    @SneakyThrows
//    @ScheduleTask
//    public void test(){
//        AccessToken accessToken = new AccessToken(akID, akSecret);
//        accessToken.apply();
//        String token = accessToken.getToken();
//        long expireTime = accessToken.getExpireTime();
//        System.out.println("expireTime = " + expireTime);
//        System.out.println("token = " + token);
//        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(expireTime * 1000));
//        System.out.println("time = " + time);
//    }

//    @ScheduleTask
//    public void testModifyYaml() throws IOException {
//        String src = "E:\\IDEAProject\\SmartHomeSys\\src\\main\\resources\\application.yaml";
//        Yaml yaml = new Yaml();
//        FileWriter fileWriter;
//        Map<String, Object> resultMap, aliyunMap;
//        //读取yaml文件，默认返回根目录结构
//        resultMap = yaml.load(Files.newInputStream(new File(src).toPath()));
//        //get出aliyun节点数据
//        aliyunMap = (Map<String, Object>) resultMap.get("aliyun");
//        aliyunMap.put("token", "123456");
//        //字符输出
//        fileWriter = new FileWriter(src);
//        //用yaml方法把map结构格式化为yaml文件结构
//        fileWriter.write(yaml.dumpAsMap(resultMap));
//        //刷新
//        fileWriter.flush();
//        //关闭流
//        fileWriter.close();
//    }

//    @ScheduleTask
//    public void test() throws IOException {
//        ClassPathResource classPathResource = new ClassPathResource("appFiles/aliyunToken.txt");
//        InputStream inputStream = classPathResource.getInputStream();
//        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//        String data = null;
//
//        while(bufferedReader.readLine() != null) {
//            System.out.println(data);
//        }
//        bufferedReader.close();
//        inputStreamReader.close();
//        inputStream.close();
//    }

//    @ScheduleTask
//    public void test001(){
//        FileUtils.modifyAliyunToken("niaha111");
//        System.out.println(FileUtils.readAliyunToken());
//        FileUtils.modifyAliyunToken("niahao");
//        System.out.println(FileUtils.readAliyunToken());
//    }
//
//    @ScheduleTask
//    public void test002(){
//        FileUtils.modifyAliyunToken("niahao2");
//        System.out.println(FileUtils.readAliyunToken());
//    }

//    @Resource
//    UserController userController;
//
//    @Test
//    void test0001(){
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//        String time = sdf.format(System.currentTimeMillis());
//        String date = sdf.format(new Date());
//        System.out.println("time = " + time);
//        System.out.println("date = " + date);
//        Time time1 = new Time(System.currentTimeMillis());
//        Time time2 = Time.valueOf("14:59:56");
//        System.out.println("time1 = " + time1);
//        System.out.println("time2 = " + time2);
//        System.out.println(time1.getTime() > time2.getTime());
//    }
//
//    @Test
//    void test002(){
//        Result list = userController.list();
//        System.out.println("list = " + list);
//    }

//    @Test
//    public void test001() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
//        System.out.println("token = " + TokenUtils.getOneNetToken());
//    }

//    @Test
//    void test001() {
//        String url = "https://restapi.amap.com/v3/weather/weatherInfo?key=66688effa4f403355ede760c79c29e03&city=110000";
//        String res = HttpUtils.sendGet(url, new HashMap<>(), new HashMap<>());
//        System.out.println("res = " + res);
//    }

//    @Test
//    void test001(){
//        JSONObject jsonObject = JSONObject.parseObject(new FileUtils().readFile("src/main/resources/appFiles/cityCode.json"));
//        System.out.println("jsonObject = " + jsonObject);
//    }
}
