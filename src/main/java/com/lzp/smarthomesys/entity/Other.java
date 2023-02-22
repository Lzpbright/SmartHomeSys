package com.lzp.generatortest.entity;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 其他电器实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Other对象")
public class Other implements Serializable {

    @ApiModelProperty("其他电器标识")
    private String id;

    @ApiModelProperty("所属房间标识")
    private String roomId;

    @ApiModelProperty("更小位置")
    private String smallPos;

    @ApiModelProperty("0:关, 1:开")
    private Integer state;

    @ApiModelProperty("逻辑删除辅助字段")
    private Integer deleted;
}
