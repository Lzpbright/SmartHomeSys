package com.lzp.smarthomesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * <p>
 * 灯实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Light对象")
@AllArgsConstructor
@NoArgsConstructor
public class Light implements IDevice {

    @TableId("id")
    @ApiModelProperty("灯标识")
    private String id;

    @TableField("roomId")
    @ApiModelProperty("所属房间标识")
    private String roomId;

    @TableField("smallPos")
    @ApiModelProperty("更小位置")
    private String smallPos;

    @TableField("state")
    @ApiModelProperty("0:关, 1:开")
    private Integer state;

    @TableField("brand")
    @ApiModelProperty("品牌")
    private String brand;

    @TableField("power")
    @ApiModelProperty("额定功率(w)")
    private String power;

    @TableField("kind")
    @ApiModelProperty("灯种类")
    private String kind;

    @TableField("color")
    @ApiModelProperty("灯光颜色")
    private String color;

    @TableField("intensity")
    @ApiModelProperty("光强度，（0~100）*额定功率")
    private Integer intensity;

    @TableField("deleted")
    @ApiModelProperty("逻辑删除标志, 0表示未删除, 1表示删除")
    private Integer deleted;
}
