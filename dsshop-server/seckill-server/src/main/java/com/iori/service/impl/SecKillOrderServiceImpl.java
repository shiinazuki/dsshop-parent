package com.iori.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iori.bean.SecKillOrder;
import com.iori.mapper.SecKillOrderMapper;
import com.iori.service.SecKillOrderService;
import org.springframework.stereotype.Service;

@Service
public class SecKillOrderServiceImpl extends ServiceImpl<SecKillOrderMapper, SecKillOrder> implements SecKillOrderService {
}
