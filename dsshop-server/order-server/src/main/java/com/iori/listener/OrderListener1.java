package com.iori.listener;

import com.alibaba.fastjson.JSON;
import com.iori.bean.Order;
import com.iori.bean.Task;
import com.iori.service.OrderService;
import com.iori.service.TaskService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class OrderListener1 {


    @Autowired
    private OrderService orderService;
    @Autowired
    private TaskService taskService;


    /**
     * 监听 updateOrderQueue 拿到数据进行数据修改
     *
     * @param map
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "updateOrderQueue")
    public void reviceOrder(Map<String, String> map, Channel channel, Message message) {

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
        taskService.updateById(task);


    }


    /**
     * 封装task数据
     * @param order
     * @return
     */
    public Task taskTable(Order order) {
        //从上下文中拿到用户的信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        Map<String, Object> map = new HashMap<>();
        map.put("course_id", order.getId());
        map.put("user_id", name);
        map.put("price", order.getTotalMoney());
        map.put("startTime", order.getCreateTime());
        map.put("startTime", order.getUpdateTime());
        //把map转为json
        String jsonString = JSON.toJSONString(map);

        Task task = new Task();
        task.setTaskType("1");
        task.setMqExchange("updateExchange");
        task.setMqRoutingkey("info.order");
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


}
