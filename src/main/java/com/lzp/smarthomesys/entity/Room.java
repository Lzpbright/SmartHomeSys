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
 * 房间实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Room对象")
@AllArgsConstructor
@NoArgsConstructor
public class Room {

    @ApiModelProperty("房间标识")
    private String id;

    @ApiModelProperty("用户标识")
    private String userId;

    @ApiModelProperty("房间位置")
    private String position;

    @ApiModelProperty("备注")
    private String remarks;

    @ApiModelProperty("逻辑删除标志, 0表示未删除, 1表示删除")
    private Integer deleted;
}
