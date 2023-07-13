package com.iori.bean;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@TableName("tb_task")
public class Task implements Serializable {

    private Long id;
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    @TableField(fill = FieldFill.DEFAULT)
    private Date deleteTime;
    private String taskType;
    private String mqExchange;
    private String mqRoutingkey;
    private String requestBody;
    private String status;
    private String errormsg;
    private Integer version;


}
