package com.iori.job;

import com.alibaba.fastjson.JSON;
import com.iori.bean.Task;
import com.iori.config.MQConfig;
import com.iori.service.TaskService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class ChooseCourseTask {


    @Autowired
    private TaskService taskService;
    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 定时任务代码 每10秒执行一次 可自行设置
     */
    @Scheduled(cron = "0/30 * * * * *") //每隔3秒执行一次
    public void testTask() {

        System.out.println("执行定时任务");
        //先查数据库中一分钟以前的数据id
        List<Long> tasks = taskService.queryBeforeOneMinute();
        //遍历
        for (Long id : tasks) {
            System.out.println(id);
            //获取最新数据 防止高并发 数据不对
            Task byId = taskService.getById(id);

            //加锁 基于数据库的行锁实现
            if (taskService.updateLock(byId.getId(),byId.getVersion()) > 0) {
                //修改时间
                byId.setUpdateTime(new Date());
                //修改version版本
                byId.setVersion(2);
                taskService.updateById(byId);
                //发送到MQ
                rabbitTemplate.convertAndSend(MQConfig.addCourseExchange,
                        MQConfig.addCourseRouting, JSON.toJSONString(byId));
            }
        }
    }


}
