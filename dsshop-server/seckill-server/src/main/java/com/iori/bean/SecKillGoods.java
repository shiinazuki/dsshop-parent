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
@TableName("tb_seckill_goods")
public class SecKillGoods implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "spu ID")
    private Long goodsId;

    @ApiModelProperty(value = "sku ID")
    private Long itemId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "商品图片")
    private String smallPic;

    @ApiModelProperty(value = "原价格")
    private BigDecimal price;

    @ApiModelProperty(value = "秒杀价格")
    private BigDecimal costPrice;

    @ApiModelProperty(value = "商家ID")
    private String sellerId;

    @ApiModelProperty(value = "添加日期")
    private Date createTime;

    @ApiModelProperty(value = "审核日期")
    private Date checkTime;

    @ApiModelProperty(value = "审核状态，0未审核，1审核通过，2审核不通过")
    private String status;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "秒杀商品数")
    private Integer num;

    @ApiModelProperty(value = "剩余库存数")
    private Integer stockCount;

    @ApiModelProperty(value = "描述")
    private String introduction;


}
