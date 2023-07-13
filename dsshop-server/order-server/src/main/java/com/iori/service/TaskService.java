package com.iori.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.iori.bean.Task;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TaskService extends IService<Task> {

    /**
     * 查询所有id
     *
     * @return
     */
    List<Long> queryBeforeOneMinute();

    /**
     * 验证版本号
     *
     * @param taskId
     * @param version
     * @return
     */
    Integer updateLock(Long taskId, Integer version);

}
