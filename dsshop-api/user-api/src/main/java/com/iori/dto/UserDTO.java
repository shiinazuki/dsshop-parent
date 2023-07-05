package com.iori.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName TbUserModel
 * @Description 用户表模型对象
 * @Author zj
 * @Date 2023/06/19 10:26
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "UserDTO", description = "用户表")
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码，加密存储")
    private String password;

    @ApiModelProperty(value = "注册手机号")
    private String phone;

    @ApiModelProperty(value = "注册邮箱")
    private String email;

    @ApiModelProperty(value = "创建时间")
    private Date created;

    @ApiModelProperty(value = "修改时间")
    private Date updated;

    @ApiModelProperty(value = "会员来源：1:PC，2：H5，3：Android，4：IOS")
    private String sourceType;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "真实姓名")
    private String name;

    @ApiModelProperty(value = "使用状态（1正常 0非正常）")
    private String status;

    @ApiModelProperty(value = "头像地址")
    private String headPic;

    @ApiModelProperty(value = "QQ号码")
    private String qq;

    @ApiModelProperty(value = "手机是否验证 （0否  1是）")
    private String isMobileCheck;

    @ApiModelProperty(value = "邮箱是否检测（0否  1是）")
    private String isEmailCheck;

    @ApiModelProperty(value = "性别，1男，0女")
    private String sex;

    @ApiModelProperty(value = "会员等级")
    private Integer userLevel;

    @ApiModelProperty(value = "积分")
    private Integer points;

    @ApiModelProperty(value = "经验值")
    private Integer experienceValue;

    @ApiModelProperty(value = "出生年月日")
    private Date birthday;

    @ApiModelProperty(value = "最后登录时间")
    private Date lastLoginTime;


}
