package com.iori.dto;


import lombok.Data;

import java.io.Serializable;


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
