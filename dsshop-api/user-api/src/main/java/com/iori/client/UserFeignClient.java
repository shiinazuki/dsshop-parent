package com.iori.client;


import com.iori.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "user-server")
public interface UserFeignClient {


    /**
     * 查询全部用户
     * @return
     */
    @GetMapping("/tb-user-model/list")
    List<UserDTO> list();

    /**
     * 根据id查询用户
     * @param id
     * @return
     */
    @GetMapping(value = "/tb-user-model/queryById")
    UserDTO get(@RequestParam("id") String id);


    /**
     * 根据id查询用户权限
     * @param id
     * @return
     */
    @GetMapping(value = "/tb-user-model/getPower")
    List<String> getPower(@RequestParam("id") String id);


    /**
     * 修改或添加用户
     * @param userDTO
     * @return
     */
    @PostMapping("/tb-user-model/saveOrUpdate")
    boolean saveOrUpdate(@RequestBody UserDTO userDTO);

}
