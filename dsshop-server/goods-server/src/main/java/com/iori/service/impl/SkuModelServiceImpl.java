package com.iori.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iori.bean.SkuModel;
import com.iori.mapper.SkuModelMapper;
import com.iori.service.SkuModelService;
import org.springframework.stereotype.Service;

@Service
public class SkuModelServiceImpl extends ServiceImpl<SkuModelMapper, SkuModel> implements SkuModelService {
}
