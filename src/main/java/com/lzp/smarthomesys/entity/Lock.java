package com.lzp.smarthomesys.entity;

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
public class Lock implements Serializable {

    @ApiModelProperty("门锁标识")
    private String id;

    @ApiModelProperty("所属房间标识")
    private String roomId;

    @ApiModelProperty("更小位置")
    private String smallPos;

    @ApiModelProperty("0:未锁, 1:已锁, 2:反锁")
    private Integer state;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("密码，用于解锁")
    private String password;

    @ApiModelProperty("临时密码, 用于临时解锁")
    private String temPassword;

    @ApiModelProperty("临时密码剩余使用次数")
    private Integer remains;

    @ApiModelProperty("逻辑删除辅助字段")
    private Integer deleted;
}
