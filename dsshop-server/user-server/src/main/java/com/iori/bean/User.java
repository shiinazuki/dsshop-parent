package com.iori.bean;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("tb_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "username", type = IdType.UUID)
    private String username;

    private String password;

    private String phone;

    private String email;

    @TableField(fill = FieldFill.INSERT)
    private Date created;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updated;

    private String sourceType;

    private String nickName;

    private String name;

    private String status;

    private String headPic;

    private String qq;

    private String isMobileCheck;

    private String isEmailCheck;

    private String sex;

    private Integer userLevel;

    private Integer points;

    private Integer experienceValue;

    private Date birthday;

    private Date lastLoginTime;

    @TableLogic
    private Integer deleted;

}
