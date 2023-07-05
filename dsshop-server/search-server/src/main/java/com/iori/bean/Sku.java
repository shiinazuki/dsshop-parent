package com.iori.bean;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Sku {


    private String id;
    private String sn;
    private String name;
    private Double price;
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
    private Character status;
    private Integer version;
    private Map<String,Object> specMap;


}
