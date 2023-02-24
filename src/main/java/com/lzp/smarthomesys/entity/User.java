package com.lzp.smarthomesys.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户实体类
 * </p>
 *
 * @author Bright J
 * @since 2023-02-24
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "User对象")
public class User {

    @ApiModelProperty("用户标识")
    private String id;

    @ApiModelProperty("用户邮箱")
    private String email;

    @ApiModelProperty("账号密码")
    private String password;

    @ApiModelProperty("手机号码")
    private String teleNumber;

    @ApiModelProperty("昵称")
    private String nickname;

    @ApiModelProperty("昵称")
    private String sex;

    @ApiModelProperty("用户头像")
    private String userIcon;

    @ApiModelProperty("用户位置")
    private String location;

    @ApiModelProperty("逻辑删除标志, 0表示未删除, 1表示删除")
    private Integer deleted;
}
