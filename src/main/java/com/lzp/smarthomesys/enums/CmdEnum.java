package com.lzp.smarthomesys.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum CmdEnum {

    // 空调

    // 开关
    AIR_SWITCH_ON("AIR_SWITCH_ON", "空调开"),
    AIR_SWITCH_OFF("AIR_SWITCH_OFF", "空调关"),
    // 温度设置
    AIR_SET_TEMP_("AIR_SET_TEMP_", "设置空调温度"),
    // 风速选择
    AIR_SET_FUN_("AIR_SET_FUN_", "空调设置风速"),
    // 模式选择
    AIR_MODE_AUTO("AIR_MODE_AUTO", "自动模式"),
    AIR_MODE_COOL("AIR_MODE_COOL", "制冷模式"),
    AIR_MODE_HOT("AIR_MODE_HOT", "制热模式"),
    AIR_MODE_DRY("AIR_MODE_DRY", "通风模式"),
    AIR_MODE_Econo("AIR_MODE_Econo", "节能模式"),

    // 灯泡

    // 开关
    LIGHT_SWITCH_ON("LIGHT_SWITCH_ON", "灯泡开"),
    LIGHT_SWITCH_OFF("LIGHT_SWITCH_OFF", "灯泡关"),
    // 亮度调节
    LIGHT_SET_INTENSITY_("LIGHT_SET_INTENSITY_", "设置灯泡亮度"),
    // 颜色设置
    LIGHT_SET_COLOR_("LIGHT_SET_COLOR_", "设置灯泡颜色"),

    // 其他电器

    OTHER_SWITCH_ON("OTHER_SWITCH_ON", "其他电器开"),
    OTHER_SWITCH_OFF("OTHER_SWITCH_OFF", "其他电器关"),

    // 门锁

    LOCK_SWITCH_ON("LOCK_SWITCH_ON", "门锁开"),
    LOCK_SWITCH_OFF("LOCK_SWITCH_OFF", "门锁关"),
    LOCK_PSW_RPL_("LOCK_PSW_RPL_", "设置门锁密码");


    /**
     * 构造函数
     * @param cmdValue 枚举值
     * @param cmdDesc 枚举描述
     */
    CmdEnum(String cmdValue, String cmdDesc){
        this.cmdValue = cmdValue;
        this.cmdDesc = cmdDesc;
    }
    @EnumValue
    private final String cmdValue;
    private final String cmdDesc;

    public String getCmdValue() {
        return cmdValue;
    }

    public String getCmdDesc() {
        return cmdDesc;
    }
}
