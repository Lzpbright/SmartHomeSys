package com.lzp.generatortest.entity;

import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 操作日志实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Getter
@Setter
@Accessors(chain = true)
@ApiModel(value = "Log对象")
public class Log implements Serializable {

    @ApiModelProperty("日志标识")
    private String id;

    @ApiModelProperty("记录时间")
    private String time;

    @ApiModelProperty("房间+具体位置+电器+电器id")
    private String target;

    @ApiModelProperty("电器动作")
    private String action;

    @ApiModelProperty("逻辑删除辅助字段")
    private Integer deleted;
}
