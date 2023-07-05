package com.iori.controller;



import com.iori.client.UserFeignClient;
import com.iori.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pc")
public class PCController {

    @Autowired
    private UserFeignClient userFeignClient;

    /**
     * 查询全部用户
     * @return
     */
    @GetMapping("/list")
    public List<UserDTO> list() {
        return userFeignClient.list();
    }

    @GetMapping("/info")
    public UserDTO info(String id) {
        return userFeignClient.get(id);
    }

    @PostMapping("/saveOrUpdate")
    public boolean saveOrUpdate(@RequestBody UserDTO userDTO) {
       return userFeignClient.saveOrUpdate(userDTO);
    }

}
