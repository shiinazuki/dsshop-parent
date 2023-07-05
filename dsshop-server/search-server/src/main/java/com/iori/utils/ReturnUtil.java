package com.iori.utils;

import com.iori.bean.Sku;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ReturnUtil {

    private Long count;
    private List<String> brandList;
    private List<String> cateList;
    private List<Sku> skus;
    private Map<String, Set<String>> specMap;

}
