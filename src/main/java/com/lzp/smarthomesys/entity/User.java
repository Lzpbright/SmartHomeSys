package com.lzp.smarthomesys.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

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
@AllArgsConstructor
@NoArgsConstructor
@TableName("`user`")
public class User {

    @TableId("id")
    @ApiModelProperty("用户标识")
    private String id;

    @TableField("email")
    @ApiModelProperty("用户邮箱")
    private String email;

    @TableField("password")
    @ApiModelProperty("账号密码")
    private String password;

    @TableField("teleNumber")
    @ApiModelProperty("手机号码")
    private String teleNumber;

    @TableField("nickname")
    @ApiModelProperty("昵称")
    private String nickname;

    @TableField("sex")
    @ApiModelProperty("性别")
    private String sex;

    @TableField("userIcon")
    @ApiModelProperty("用户头像")
    private String userIcon;

    @TableField("location")
    @ApiModelProperty("用户位置")
    private String location;

    @TableField("deleted")
    @ApiModelProperty("逻辑删除标志, 0表示未删除, 1表示删除")
    private Integer deleted;
}
