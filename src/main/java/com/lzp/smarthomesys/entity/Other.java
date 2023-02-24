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
 * 其他电器实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Other对象")
@AllArgsConstructor
@NoArgsConstructor
public class Other {

    @ApiModelProperty("其他电器标识")
    private String id;

    @ApiModelProperty("所属房间标识")
    private String roomId;

    @ApiModelProperty("更小位置")
    private String smallPos;

    @ApiModelProperty("0:关, 1:开")
    private Integer state;

    @ApiModelProperty("逻辑删除标志, 0表示未删除, 1表示删除")
    private Integer deleted;
}
