package com.iori.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iori.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 获取权限
     * @param id
     * @return
     */
    List<String> getPower(@Param("id") String id);

}
