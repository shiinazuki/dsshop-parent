package com.iori.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.Date;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("tb_task_his")
@ApiModel(value = "TbTaskHis", description = "")
public class TaskHis implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Date createTime;

    private Date updateTime;

    private Date deleteTime;

    @ApiModelProperty(value = "任务类型")
    private String taskType;

    @ApiModelProperty(value = "交换机名称")
    private String mqExchange;

    @ApiModelProperty(value = "routingkey")
    private String mqRoutingkey;

    @ApiModelProperty(value = "任务请求的内容")
    private String requestBody;

    @ApiModelProperty(value = "任务状态")
    private String status;

    private String errormsg;


}
