package com.iori.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;


@Data
@TableName("tb_order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.UUID)
    private String id;

    private Integer categoryId1;

    private Integer categoryId2;

    private Integer categoryId3;

    private String spuId;

    private String skuId;

    private String orderId;

    private String name;

    private Integer price;

    private Integer num;

    private Integer money;

    private Integer payMoney;

    private String image;

    private Integer weight;

    private Integer postFee;

    private String isReturn;


}
