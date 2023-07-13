package com.iori.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iori.bean.LearningCourse;
import com.iori.bean.TaskHis;
import com.iori.config.MyConfig;
import com.iori.service.LearningCourseService;
import com.iori.service.TaskHisService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Component
public class CourseListener {


    @Autowired
    private LearningCourseService learningCourseService;
    @Autowired
    private TaskHisService taskHisService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "addCourseQueue")
    public void reviceCourse(String msg) {

        //把数据转为对象
        TaskHis taskHis = JSON.parseObject(msg, TaskHis.class);
        //调用 options()
        this.options(taskHis);

        //返回结果信息
        rabbitTemplate.convertAndSend(MyConfig.addSuccessExchange,
                MyConfig.addSuccessRouting,msg);


    }

    /**
     * 查看课程表中 有没有数据  如果有 就修改 没有就添加
     *
     * @param taskHis
     */
    @Transactional
    public void options(TaskHis taskHis) {
        //获取到 TaskHis 里面的 requestBody 转为 LearningCourse对象
        LearningCourse learningCourse = JSON.parseObject(taskHis.getRequestBody(),
                LearningCourse.class);
        //根据课程编号和用户id 去数据库查有没有数据
        QueryWrapper<LearningCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LearningCourse::getUserId, learningCourse.getUserId());
        queryWrapper.lambda().eq(LearningCourse::getCourseId, learningCourse.getCourseId());

        //查询唯一数据
        LearningCourse one = learningCourseService.getOne(queryWrapper);
        //没有就添加 有数据就修改
        if (ObjectUtils.isEmpty(one)) {
            learningCourseService.save(learningCourse);
        } else {
            //把新数据添加进去
            learningCourseService.updateById(one);
            //learningCourseService.updateById(learningCourse);
        }

        //添加历史记录
        TaskHis byId = taskHisService.getById(taskHis.getId());
        if (ObjectUtils.isEmpty(byId)) {
            taskHisService.save(taskHis);
        }


    }

}
