package com.iori.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName TbAddressModel
 * @Description 模型对象
 * @Author zj
 * @Date 2023/07/05 16:33
 **/
@Data
public class AddressDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String username;

    private String provinceid;

    private String cityid;

    private String areaid;

    private String phone;

    private String address;

    private String contact;

    private String isDefault;

    private String alias;


}
