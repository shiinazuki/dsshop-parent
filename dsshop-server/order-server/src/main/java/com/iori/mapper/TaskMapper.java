package com.iori.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.iori.bean.Task;
import io.swagger.models.auth.In;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {


    /**
     * 查询所有id
     * @return
     */
    List<Long> queryBeforeOneMinute();

    /**
     * 验证版本号
     * @param taskId
     * @param version
     * @return
     */
    Integer updateLock(@Param("taskId") Long taskId, @Param("version") Integer version);

}
