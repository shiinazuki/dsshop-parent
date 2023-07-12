package com.iori.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iori.bean.Task;
import com.iori.mapper.TaskMapper;
import com.iori.service.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {
}
