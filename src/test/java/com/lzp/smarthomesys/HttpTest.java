package com.lzp.smarthomesys;

import org.json.JSONObject;
import com.lzp.smarthomesys.utils.Base64Utils;
import com.lzp.smarthomesys.utils.HttpUtils;
//import okhttp3.*;
import org.json.JSONException;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpTest {

//    @ScheduleTask
//    public void testGet() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
//        String url = "http://api.heclouds.com/cmds?device_id=" + "1037659370" + "&timeout=30";
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", TokenUtils.getOneNetToken());
////        headers.put("api-key", "eaoh5qproOYKR8cmLoD0ZGLyvhY=");
//        Map<String, String> params = new HashMap<>();
//        params.put("data", "LED_LED1_OFF");
//        String res = HttpUtils.sendPostJson(url, headers, params);
//        System.out.println("res = " + res);
//    }
//
//    @ScheduleTask
//    public void testDeviceUtils(){
//        System.out.println(DeviceUtils.sendCmd("1037659370", "LED_LED1_OFF", "30"));
//    }

//    @ScheduleTask
//    public void testAddDevice(){
//        System.out.println(DeviceUtils.addDevice("testDevice", "firstTest"));
//    }
//    static final String API_KEY = "CY2GSoeVCiQGtIs2BmONcuUL";
//    static final String SECRET_KEY = "F60D2dvxBFhMLdXnR0LUGAY9nxaA1CXI";
//
//    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();
//
//    @ScheduleTask
//    public void testYuYinShiBie() throws IOException, JSONException {
//        System.out.println("########" + getAccessToken());
//        String url = "https://vop.baidu.com/server_api";
//        Map<String, String> header = new HashMap<>();
//        header.put("Content-Type", "application/json");
//        header.put("Accept", "application/json");
//        Map<String, String> body = new HashMap<>();
//        body.put("format", "wav");
//        body.put("rate", "16000");
//        body.put("channel", "1");
//        body.put("cuid", "293dc1e89f6d4e9f8b537878d58b3246");
//        body.put("token", getAccessToken());
//        String speech = Base64Utils.encodeFile("E:\\IDEAProject\\SmartHomeSys\\src\\main\\resources\\soundFils\\16k.wav");
//        body.put("speech", speech);
//        System.out.println("speech = " + speech);
//        body.put("len", "139998");
//
//        String res = HttpUtils.sendPostJson(url, header, body);
//        System.out.println(res);
//
//    }

//    /**
//     * 从用户的AK，SK生成鉴权签名（Access TokenUtils）
//     *
//     * @return 鉴权签名（Access TokenUtils）
//     * @throws JSONException Json转换异常
//     */
//    static String getAccessToken() throws JSONException {
//        Map<String, String> header = new HashMap<>();
//        Map<String, String> bodyer = new HashMap<>();
//        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id=" + API_KEY + "&client_secret=" + SECRET_KEY;
//        header.put("Content-Type", "application/json");
//        String res = HttpUtils.sendPostJson(url, header, bodyer);
//        JSONObject resJson = JSONObject.parseObject(res);
//        return (String) resJson.get("access_token");
//    }

//    /**
//     * 从用户的AK，SK生成鉴权签名（Access TokenUtils）
//     *
//     * @return 鉴权签名（Access TokenUtils）
//     * @throws IOException IO异常
//     */
//    static String getAccessToken() throws IOException, JSONException {
//        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
//        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
//                + "&client_secret=" + SECRET_KEY);
//        Request request = new Request.Builder()
//                .url("https://aip.baidubce.com/oauth/2.0/token")
//                .method("POST", body)
//                .addHeader("Content-Type", "application/x-www-form-urlencoded")
//                .build();
//        Response response = HTTP_CLIENT.newCall(request).execute();
//        return new JSONObject(response.body().string()).getString("access_token");
//    }

//    @ScheduleTask
//    public void test001(){
//        String url = "https://nls-gateway-cn-shanghai.aliyuncs.com/stream/v1/asr";
//        File file = new File("E:\\IDEAProject\\SmartHomeSys\\src\\main\\java\\com\\lzp\\smarthomesys\\utils\\你好.wav");
//        Map<String, String> params = new HashMap<>();
//        Map<String, String> headers = new HashMap<>();
//        params.put("appkey", "SEUNvgUev8UbWM02");
//        params.put("sample_rate", "16000");
//        headers.put("X-NLS-Token", "4bd4e6dbe1b14ffa9e374152620f193f");
//        String res = HttpUtils.sendPostAFile(url, params, headers, file);
//        System.out.println("res = " + res);
//    }

//    @ScheduleTask
//    public void test0001(){
//        System.out.println(Integer.parseInt("1.2"));
//    }
//
//    @Test
//    void OneNetMesGetTest(){
//
//    }
}
