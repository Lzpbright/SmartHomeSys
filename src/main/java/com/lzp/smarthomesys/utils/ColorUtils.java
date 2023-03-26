package com.lzp.smarthomesys.utils;

import java.awt.*;

public class ColorUtils {

    /**
     * 通过0~99的数值生成连续变化的RGB颜色
     * @param value 0~99
     * @return RGB
     */
    public static int[] gradientColor(int value) {
        if (value == 100){
            return new int[]{255, 255, 255};
        }
        float hue = value / 100.0f; // 将输入值映射到 0-1 范围内的浮点数
        Color color = Color.getHSBColor(hue, 1.0f, 1.0f); // 根据 HSB 颜色模型生成颜色
        return new int[]{color.getRed(), color.getGreen(), color.getBlue()};
    }

}
