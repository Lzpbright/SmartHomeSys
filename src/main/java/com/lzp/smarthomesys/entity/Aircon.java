package com.lzp.smarthomesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 空调实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Aircon对象")
@AllArgsConstructor
@NoArgsConstructor
@TableName("air_con")
public class Aircon implements IDevice {

    @TableId("id")
    @ApiModelProperty("空调标识")
    private String id;

    @TableField("room_id")
    @ApiModelProperty("所属房间标识")
    private String roomId;

    @TableField("small_pos")
    @ApiModelProperty("更小位置")
    private String smallPos;

    @TableField("state")
    @ApiModelProperty("0:关, 1:开")
    private Integer state;

    @TableField("mode")
    @ApiModelProperty("空调模式")
    private String mode;

    @TableField("temper")
    @ApiModelProperty("温度")
    private Integer temper;

    @TableField("wind_speed")
    @ApiModelProperty("风速")
    private String windSpeed;

    @TableField("brand")
    @ApiModelProperty("品牌")
    private String brand;

    @TableField("power")
    @ApiModelProperty("额定功率(w)")
    private String power;

    @TableField("deleted")
    @ApiModelProperty("逻辑删除标志, 0表示未删除, 1表示删除")
    private Integer deleted;
}
