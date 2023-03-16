package com.lzp.smarthomesys;

import com.lzp.smarthomesys.utils.DeviceUtils;
import com.lzp.smarthomesys.utils.HttpUtils;
import com.lzp.smarthomesys.utils.Token;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class HttpTest {

//    @Test
//    public void testGet() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
//        String url = "http://api.heclouds.com/cmds?device_id=" + "1037659370" + "&timeout=30";
//        Map<String, String> headers = new HashMap<>();
//        headers.put("Authorization", Token.getToken());
////        headers.put("api-key", "eaoh5qproOYKR8cmLoD0ZGLyvhY=");
//        Map<String, String> params = new HashMap<>();
//        params.put("data", "LED_LED1_OFF");
//        String res = HttpUtils.sendPostJson(url, headers, params);
//        System.out.println("res = " + res);
//    }
//
//    @Test
//    public void testDeviceUtils(){
//        System.out.println(DeviceUtils.sendCmd("1037659370", "LED_LED1_OFF", "30"));
//    }

//    @Test
//    public void testAddDevice(){
//        System.out.println(DeviceUtils.addDevice("testDevice", "firstTest"));
//    }
}
