package com.iori.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iori.bean.User;
import com.iori.mapper.UserMapper;
import com.iori.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @ClassName TbUserServiceImpl
 * @Description 用户表服务接口实现
 * @Author iori
 * @Date 2023/06/19 10:26
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
