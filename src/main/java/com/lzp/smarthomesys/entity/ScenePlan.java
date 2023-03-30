package com.lzp.smarthomesys.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Bright J
 * @since 2023-03-26
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Sceneplan对象")
@AllArgsConstructor
@NoArgsConstructor
@TableName("sceneplan")
public class ScenePlan {

    @TableId("id")
    @ApiModelProperty("场景定时计划标识")
    private String id;

    @TableField("sceneId")
    @ApiModelProperty("场景标识")
    private String sceneId;

    @TableField("startAt")
    @ApiModelProperty("场景开始时间")
    private String startAt;

    @TableField("endAt")
    @ApiModelProperty("场景停止时间")
    private String endAt;

    @TableField("weekChoose")
    @ApiModelProperty("星期的选择1~7, 0表示单次")
    private String weekChoose;

    @TableField("state")
    @ApiModelProperty("0:关, 1:开")
    private Integer state;

    @TableField("deleted")
    @ApiModelProperty("0表示没有删除, 1表示已删除")
    private Integer deleted;
}
