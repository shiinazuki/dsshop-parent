package com.iori.listener;

import com.alibaba.fastjson.JSON;
import com.iori.bean.Order;
import com.iori.bean.Task;
import com.iori.bean.TaskHis;
import com.iori.service.OrderService;
import com.iori.service.TaskHisService;
import com.iori.service.TaskService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderListener1 {


    @Autowired
    private OrderService orderService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private TaskHisService taskHisService;


    /**
     * 监听 updateOrderQueue 拿到数据进行数据修改
     *
     * @param info
     */
    @RabbitListener(queues = "updateOrderQueue")
    public void reviceOrder(String info) {

        Map<String,String> map = JSON.parseObject(info, Map.class);

        //拿到订单编号
        String orderId = map.get("out_trade_no");
        //先查询出来
        Order order = orderService.getById(orderId);
        //修改状态
        order.setOrderStatus("1");
        order.setPayStatus("1");
        //调用修改方法
        orderService.updateById(order);
        //调用方法 封装信息 加入到task表中
        Task task = this.taskTable(order);
        taskService.saveOrUpdate(task);


    }


    /**
     * 封装task数据
     * @param order
     * @return
     */
    public Task taskTable(Order order) {


        //封装数据
        Map<String, Object> map = new HashMap<>();
        map.put("course_id", order.getId());
        map.put("user_id", order.getUsername());
        map.put("price", order.getTotalMoney());
        map.put("startTime", order.getCreateTime());
        map.put("endTime", order.getUpdateTime());
        //把map转为json
        String jsonString = JSON.toJSONString(map);
        //创建 Task对象 保存数据
        Task task = new Task();
        task.setTaskType("1");
        task.setMqExchange("addCourseExchange");
        task.setMqRoutingkey("addCourseRouting");
        task.setRequestBody(jsonString);
        task.setStatus("1");
        task.setVersion(1);

        return task;
    }


    /**
     * 监听 failOrderQueue 失败的队列 拿到数据修改状态
     *
     * @param msg
     */
    @RabbitListener(queues = "failOrderQueue")
    public void checkOrderPayStatus(String msg) {
        //根据拿到的编号 查询数据库
        Order order = orderService.getById(msg);
        //判断订单状态是否为1 如果不为则待变订单失效
        if (!"1".equals(order.getPayStatus())) {
            //将订单状态改为失效状态
            order.setOrderStatus("2");
            orderService.updateById(order);
        }
    }

    @RabbitListener(queues = "addSuccessQueue")
    public void reviceHis(String msg) {

        //把接收道德数据转为 TaskHis 对象
        TaskHis taskHis = JSON.parseObject(msg, TaskHis.class);
        //调用 option方法
        option(taskHis);

    }

    @Transactional
    public void option( TaskHis taskHis) {
        //查看数据库中有没有数据 有就删除
        Task task = taskService.getById(taskHis.getId());
        //如果有 就删除  没有就什么都不做
        if (!ObjectUtils.isEmpty(task)) {
            //删除任务数据
            taskService.removeById(task.getId());
            //查找 历史表数据
            TaskHis hisTask = taskHisService.getById(taskHis.getId());
            //判断历史表中有没有数据 如果没有就添加
            if (ObjectUtils.isEmpty(hisTask)) {
                taskHisService.save(taskHis);
            }
        }

    }


}
