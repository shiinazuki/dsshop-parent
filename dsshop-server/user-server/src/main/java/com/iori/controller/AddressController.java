package com.iori.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iori.bean.Address;
import com.iori.dto.AddressDTO;
import com.iori.mapper.AddressMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Autowired
    private AddressMapper addressMapper;

    /**
     * 查询用户地址方法 并全部返回
     * @param username
     * @return
     */
    @GetMapping("/query")
    public List<AddressDTO> query(@RequestParam("username") String username) {
        List<AddressDTO> addressDTOList = new ArrayList<>();
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username",username);
        List<Address> addressList = addressMapper.selectList(queryWrapper);
        for (Address address : addressList) {
            AddressDTO addressDTO = new AddressDTO();
            BeanUtils.copyProperties(address,addressDTO);
            addressDTOList.add(addressDTO);
        }
        return addressDTOList;
    }

}
