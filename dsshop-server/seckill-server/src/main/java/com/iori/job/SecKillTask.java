package com.iori.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.iori.bean.SecKillGoods;
import com.iori.service.SecKillGoodsService;
import com.iori.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SecKillTask {

    @Autowired
    private SecKillGoodsService secKillGoodsService;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 定时任务
     */
    @Scheduled(cron = "0/10 * * * * *")
    public void run() {

        //获取当前时间段
        List<Date> dateList = DateUtil.getDateMenus();
        for (Date date : dateList) {
            System.out.println(date);
            //设置redis的key
            String extTime = DateUtil.data2str(date, DateUtil.PATTERN_YYYYMMDDHH);

            //在查询mysql数据库之前先查询 redis 里有没有数据
            Set keys = redisTemplate.boundHashOps("seckill:" + extTime).keys();

            //创建 LambdaQueryWrapper 设置条件
            LambdaQueryWrapper<SecKillGoods> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.ge(SecKillGoods::getStartTime, date);
            queryWrapper.le(SecKillGoods::getEndTime, DateUtil.addDateHour(date, 2));
            queryWrapper.gt(SecKillGoods::getStockCount, 0);
            //防止数据库从新覆盖redis的记录
            if (keys != null && keys.size() > 0) {
                queryWrapper.notIn(SecKillGoods::getId, keys);
            }
            //开始查询 返回集合
            List<SecKillGoods> list = secKillGoodsService.list(queryWrapper);
            for (SecKillGoods secKillGoods : list) {
                System.out.println(secKillGoods);
                //将数据加入到redis中
                redisTemplate.boundHashOps("seckill:" + extTime).put(secKillGoods.getId().toString(), secKillGoods);
            }


        }

    }

}
