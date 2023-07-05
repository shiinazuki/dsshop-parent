package com.iori.dto;

import lombok.Data;

@Data
public class CategoryDto {

    private Integer id;

    private String name;

    private Integer goodsNum;

    private String isShow;

    private String isMenu;

    private Integer seq;

    private Integer parentId;

    private Integer templateId;

}
