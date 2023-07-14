package com.iori.controller;

import com.iori.bean.SecKillGoods;
import com.iori.service.SecKillGoodsService;
import com.iori.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/seck")
public class SecKillController {


    @Autowired
    private SecKillGoodsService secKillGoodsService;



    @GetMapping("/create")
    public String createOrder(@RequestParam("time") String time,
                              @RequestParam("id") String id,
                              @RequestParam("username") String username) {

        return secKillGoodsService.createOrder(time,id,username);

    }


    /**
     * 获取时间段集合
     *
     * @return
     */
    @GetMapping("/getDateMenu")
    public List<String> getDateMenu() {

        //调用getDateMenus拿到时间段集合
        List<Date> dateMenus = DateUtil.getDateMenus();
        //将 List<Date> 转为 List<String> 集合并返回
        return dateMenus.stream().map(item -> {
            try {
                String format = DateUtil.format(item, DateUtil.PATTERN_YYYYMMDDHH);
                return format;
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

    }

    /**
     * 从 redis 中取出 当前时间段的所有数据
     *
     * @param time
     * @return
     */
    @GetMapping("/list/{time}")
    public List<SecKillGoods> list(@PathVariable("time") String time) {
        return secKillGoodsService.list(time);
    }

    /**
     * 从redis 中 根据时间段 和 用户id 查询对应数据
     *
     * @param time
     * @param id
     * @return
     */
    @GetMapping("/one/{time}/{id}")
    public SecKillGoods one(@PathVariable("time") String time, @PathVariable("id") String id) {
        return secKillGoodsService.one(time, id);
    }


}
