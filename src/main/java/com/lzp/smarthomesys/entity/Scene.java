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
 * 场景实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Scene对象")
@AllArgsConstructor
@NoArgsConstructor
public class Scene {

    @ApiModelProperty("场景标识")
    private String id;

    @ApiModelProperty("所属房间标识")
    private String roomId;

    @ApiModelProperty("场景介绍")
    private String intro;

    @ApiModelProperty("相关电器标识")
    private String appliance;

    @ApiModelProperty("0表示未选用, 1表示选用")
    private Integer state;

    @ApiModelProperty("逻辑删除标志, 0表示未删除, 1表示删除")
    private Integer deleted;
}
