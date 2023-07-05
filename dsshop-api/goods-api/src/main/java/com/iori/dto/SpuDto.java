package com.iori.dto;

import lombok.Data;

@Data
public class SpuDto {

    private String id;

    private String sn;

    private String name;

    private String caption;

    private Integer brandId;

    private Integer category1Id;

    private Integer category2Id;

    private Integer category3Id;

    private Integer templateId;

    private Integer freightId;

    private String image;

    private String images;

    private String saleService;

    private String introduction;

    private String specItems;

    private String paraItems;

    private Integer saleNum;

    private Integer commentNum;

    private String isMarketable;

    private String isEnableSpec;

    private String isDelete;

    private String status;
}
