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
 * 操作日志实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Log对象")
@AllArgsConstructor
@NoArgsConstructor
@TableName("`log`")
public class Log {

    @TableId("id")
    @ApiModelProperty("日志标识")
    private String id;

    @TableField("time")
    @ApiModelProperty("记录时间")
    private String time;

    @TableField("user_id")
    @ApiModelProperty("用户标识")
    private String userId;

    @TableField("target")
    @ApiModelProperty("房间+具体位置+电器+电器id")
    private String target;

    @TableField("action")
    @ApiModelProperty("电器动作")
    private String action;

    @TableField("deleted")
    @ApiModelProperty("逻辑删除标志, 0表示未删除, 1表示删除")
    private Integer deleted;
}
