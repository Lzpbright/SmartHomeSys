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
 * 空调实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-22
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "Aircon对象")
@AllArgsConstructor
@NoArgsConstructor
public class Aircon implements Serializable {

    @ApiModelProperty("空调标识")
    private String id;

    @ApiModelProperty("所属房间标识")
    private String roomId;

    @ApiModelProperty("更小位置")
    private String smallPos;

    @ApiModelProperty("0:关, 1:开")
    private Integer state;

    @ApiModelProperty("空调模式")
    private String mode;

    @ApiModelProperty("温度")
    private Integer temper;

    @ApiModelProperty("风速")
    private String windSpeed;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty("额定功率(w)")
    private String power;

    @ApiModelProperty("逻辑删除辅助字段")
    private Integer deleted;
}
