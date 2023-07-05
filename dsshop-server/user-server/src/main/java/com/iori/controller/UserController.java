package com.iori.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iori.bean.User;
import com.iori.dto.UserDTO;
import com.iori.mapper.UserMapper;
import com.iori.service.UserService;
import com.iori.util.PageUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName TbUserController
 * @Description 用户表控制器
 * @Author iori
 * @Date 2023/06/19 10:26
 **/
@RestController
@RequestMapping("/tb-user-model")
@Api(value = "TbUserController", tags = {"用户表控制器"})
public class UserController {

    @Autowired
    public UserMapper userMapper;

    @Autowired
    public UserService userService;

    @GetMapping("/list")
    public List<UserDTO> list() {
        List<User> users = userMapper.selectList(null);
        List<UserDTO> collect = users.stream().map(user -> {
            UserDTO userDTO = new UserDTO();
            BeanUtils.copyProperties(user, userDTO);
            return userDTO;
        }).collect(Collectors.toList());

        return collect;
    }

    @ApiOperation(value = "展示列表")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "页码", name = "pageNum", dataType = "Integer", required = false, example = "1", defaultValue = "1"),
            @ApiImplicitParam(value = "每页条数", name = "pageSize", dataType = "Integer", required = false, example = "10", defaultValue = "10")
    })
    @GetMapping("/page")
    public PageUtil<UserDTO> get(@RequestParam(value = "pageNum") Integer pageNum, @RequestParam(value = "pageSize") Integer pageSize) {
        PageUtil<UserDTO> pageUtil = new PageUtil<>();
        Page<User> page = userService.page(new Page<>(pageNum, pageSize));
        return null;

    }

    @GetMapping(value = "/queryById")
    @ApiOperation(value = "根据Id展示列表")
    public UserDTO get(@RequestParam("id") String id) {
        UserDTO userDTO = new UserDTO();
        User user = userService.getById(id);
        BeanUtils.copyProperties(user, userDTO);
        return userDTO;
    }

    @GetMapping(value = "/getPower")
    public List<String> getPower(@RequestParam("id") String id) {
        return userMapper.getPower(id);
    }

    @GetMapping("/remove")
    @ApiOperation(value = "移除")
    public boolean remove(@RequestParam(name = "id") String Id) {
        return userService.removeById(Id);
    }

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "保存或更新")
    public boolean saveOrUpdate(@RequestBody UserDTO userDTO) {
        User user = new User();
        BeanUtils.copyProperties(userDTO,user);
        return userService.saveOrUpdate(user);
    }
}
