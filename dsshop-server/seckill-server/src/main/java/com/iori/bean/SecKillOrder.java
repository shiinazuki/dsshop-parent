package com.iori.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("tb_seckill_order")
public class SecKillOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "id", type = IdType.UUID)
    private Long id;

    @ApiModelProperty(value = "秒杀商品ID")
    private Long seckillId;

    @ApiModelProperty(value = "支付金额")
    private BigDecimal money;

    @ApiModelProperty(value = "用户")
    private String userId;

    @ApiModelProperty(value = "商家")
    private String sellerId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "支付时间")
    private Date payTime;

    @ApiModelProperty(value = "状态，0未支付，1已支付")
    private String status;

    @ApiModelProperty(value = "收货人地址")
    private String receiverAddress;

    @ApiModelProperty(value = "收货人电话")
    private String receiverMobile;

    @ApiModelProperty(value = "收货人")
    private String receiver;

    @ApiModelProperty(value = "交易流水")
    private String transactionId;


}
