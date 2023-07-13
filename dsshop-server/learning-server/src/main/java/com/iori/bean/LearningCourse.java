package com.iori.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName LearningCourse
 * @Description 模型对象
 * @Author iori
 * @Date 2023/07/13 15:15
 **/
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("learning_course")
@ApiModel(value = "LearningCourse", description = "")
public class LearningCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "课程id")
    private String courseId;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "收费规则")
    private String charge;

    @ApiModelProperty(value = "课程价格")
    private BigDecimal price;

    @ApiModelProperty(value = "有效性")
    private String valid;

    private Date startTime;

    private Date endTime;

    @ApiModelProperty(value = "选课状态")
    private String status;


}
