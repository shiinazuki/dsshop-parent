package com.iori.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo {

    private String contact;
    private String mobile;
    private String address;
    private String[] ids;
    private String payType;
    private String buyerMessage;


}
