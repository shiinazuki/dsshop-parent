package com.iori.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iori.bean.Brand;
import com.iori.mapper.BrandMapper;
import com.iori.service.BrandService;
import org.springframework.stereotype.Service;

@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {
}
