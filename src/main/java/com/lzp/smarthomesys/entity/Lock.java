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

import java.io.Serializable;

/**
 * <p>
 * 锁实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Lock对象")
@AllArgsConstructor
@NoArgsConstructor
@TableName("`lock`")
public class Lock {

    @TableId("id")
    @ApiModelProperty("门锁标识")
    private String id;

    @TableField("roomId")
    @ApiModelProperty("所属房间标识")
    private String roomId;

    @TableField("smallpos")
    @ApiModelProperty("更小位置")
    private String smallPos;

    @TableField("state")
    @ApiModelProperty("0:未锁, 1:已锁, 2:反锁")
    private Integer state;

    @TableField("brand")
    @ApiModelProperty("品牌")
    private String brand;

    @TableField("password")
    @ApiModelProperty("密码，用于解锁")
    private String password;

    @TableField("temPassword")
    @ApiModelProperty("临时密码, 用于临时解锁")
    private String temPassword;

    @TableField("remains")
    @ApiModelProperty("临时密码剩余使用次数")
    private Integer remains;

    @TableField("deleted")
    @ApiModelProperty("逻辑删除标志, 0表示未删除, 1表示删除")
    private Integer deleted;
}
