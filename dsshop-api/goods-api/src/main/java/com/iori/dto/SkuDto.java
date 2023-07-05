package com.iori.dto;

import lombok.Data;

import java.util.Date;

@Data
public class SkuDto {

    private String id;

    private String sn;

    private String name;

    private Integer price;

    private Integer num;

    private Integer alertNum;

    private String image;

    private String images;

    private Integer weight;

    private Date createTime;

    private Date updateTime;

    private String spuId;

    private Integer categoryId;

    private String categoryName;

    private String brandName;

    private String spec;

    private Integer saleNum;

    private Integer commentNum;

    private String status;

    private Integer version;

}
