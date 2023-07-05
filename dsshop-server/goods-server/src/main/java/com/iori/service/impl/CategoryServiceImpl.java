package com.iori.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.iori.bean.Category;
import com.iori.mapper.CategoryMapper;
import com.iori.service.CategoryService;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
}
