package com.iori.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.util.Date;


@Data
@TableName("tb_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    private Integer totalNum;

    private Integer totalMoney;

    private Integer preMoney;

    private Integer postFee;

    private Integer payMoney;

    private String payType;

    private Date createTime;

    private Date updateTime;

    private Date payTime;

    private Date consignTime;

    private Date endTime;

    private Date closeTime;

    private String shippingName;

    private String shippingCode;

    private String username;

    private String buyerMessage;

    private String buyerRate;

    private String receiverContact;

    private String receiverMobile;

    private String receiverAddress;

    private String sourceType;

    private String transactionId;

    private String orderStatus;

    private String payStatus;

    private String consignStatus;

    private String isDelete;


}
