package com.iori.controller;

import com.iori.bean.Brand;
import com.iori.bean.Category;
import com.iori.bean.SkuModel;
import com.iori.bean.Spu;
import com.iori.dto.BrandDto;
import com.iori.dto.CategoryDto;
import com.iori.dto.SkuDto;
import com.iori.dto.SpuDto;
import com.iori.service.BrandService;
import com.iori.service.CategoryService;
import com.iori.service.SkuModelService;
import com.iori.service.SpuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/goods-sku")
public class SkuController {

    @Autowired
    private SkuModelService skuModelService;
    @Autowired
    private BrandService brandService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SpuService spuService;


    @GetMapping("/info/{skuId}")
    public Map<String, Object> info(@PathVariable("skuId") String skuId) {
        Map<String, Object> result = new HashMap<>();
        SkuDto skuDto = new SkuDto();
        SpuDto spuDto = new SpuDto();
        CategoryDto c1Dto = new CategoryDto();
        CategoryDto c2Dto = new CategoryDto();
        CategoryDto c3Dto = new CategoryDto();
        BrandDto brandDto = new BrandDto();


        SkuModel sku = skuModelService.getById(skuId);

        Spu spu = spuService.getById(sku.getSpuId());

        Category c1 = categoryService.getById(spu.getCategory1Id());
        Category c2 = categoryService.getById(spu.getCategory2Id());
        Category c3 = categoryService.getById(spu.getCategory3Id());

        Brand brand = brandService.getById(spu.getBrandId());

        BeanUtils.copyProperties(sku,skuDto);
        BeanUtils.copyProperties(spu,spuDto);
        BeanUtils.copyProperties(c1,c1Dto);
        BeanUtils.copyProperties(c2,c2Dto);
        BeanUtils.copyProperties(c3,c3Dto);
        BeanUtils.copyProperties(brand,brandDto);

        result.put("sku",skuDto);
        result.put("spu",spuDto);
        result.put("c1",c1Dto);
        result.put("c2",c2Dto);
        result.put("c3",c3Dto);
        result.put("brand",brandDto);

        return result;

    }


}
