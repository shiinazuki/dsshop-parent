package com.iori.repository;

import com.alibaba.fastjson.JSON;
import com.iori.bean.Sku;
import com.iori.utils.ReturnUtil;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class SkuRepository {

    @Autowired
    private RestHighLevelClient client;

    private List<Sku> skuList;
    private Sku sku;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ReturnUtil search(Map<String, String> param) throws IOException {

        ReturnUtil result = new ReturnUtil();
        //获取关键词
        String keyword = param.get("keyword");
        //获取当前页码数
        Integer index = Integer.parseInt(param.get("index"));
        //获取每页显示条数
        Integer size = Integer.parseInt(param.get("size"));

        //判断关键词为不为 null  如果为 null 就给一个默认值
        if (StringUtils.isEmpty(keyword)) {
            keyword = "小米";
        }


        //先创建查询表对象 并指定表 searchRequest
        SearchRequest searchRequest = new SearchRequest("tb_sku");
        //创建查询方式对象 searchSourceBuilder
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //设置分页
        searchSourceBuilder.from((index - 1) * size);
        searchSourceBuilder.size(size);


        //创建组合查询对象 BoolQueryBuilder对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //分词查询 第一个变量是 关键词 第二个是要查询哪些列
        MultiMatchQueryBuilder multiMatchQueryBuilder =
                QueryBuilders.multiMatchQuery(keyword, "name");

        //添加条件
        boolQueryBuilder.must(multiMatchQueryBuilder);
        //判断brand这个条件为不为null 不为null则设置条件
        if (!StringUtils.isEmpty(param.get("brand"))) {
            String[] brands = param.get("brand").split(",");
            TermsQueryBuilder brandTerms = QueryBuilders.termsQuery("brand_name", brands);
            boolQueryBuilder.filter(brandTerms);
        }
        //判断cate条件为不为bull 不为null则设置条件
        if (!StringUtils.isEmpty(param.get("cate"))) {
            String[] brands = param.get("cate").split(",");
            TermsQueryBuilder cateTerms = QueryBuilders.termsQuery("category_name", brands);
            boolQueryBuilder.filter(cateTerms);
        }

        //遍历 param集合
        for (String key : param.keySet()) {
            //判断 如果 key是以 spec_ 开头的 就设置条件
            if (key.startsWith("spec_")) {
                TermsQueryBuilder termsQueryBuilder =
                        QueryBuilders.termsQuery("specMap." +
                                key.substring(5) + ".keyword", param.get(key));
                boolQueryBuilder.filter(termsQueryBuilder);
            }
        }

        //排序判断
        String sortField = param.get("sortField");
        String sortRule = param.get("sortRule");
        //如果这两条件都不为空
        if (!StringUtils.isEmpty(sortField) && !StringUtils.isEmpty(sortRule)) {
            //就设置排序条件
            searchSourceBuilder.sort(new FieldSortBuilder(sortField)
                    .order("ASC".equals(sortRule) ? SortOrder.ASC : SortOrder.DESC));
        }

        //判断price条件为不为bull 不为null则设置条件
        if (!StringUtils.isEmpty(param.get("price"))) {
            //按 - 进行分割
            String[] prices = param.get("price").split("-");
            RangeQueryBuilder rangeQueryBuilder = null;
            //如果范围是 xxx - * 则设置大于 xxx 即可 否则设置在什么区间
            if ("*".equalsIgnoreCase(prices[1])) {
                rangeQueryBuilder = QueryBuilders.rangeQuery("price").gte(prices[0]);
            } else {
                rangeQueryBuilder = QueryBuilders.rangeQuery("price").from(prices[0]).to(prices[1]);
            }
            boolQueryBuilder.filter(rangeQueryBuilder);
        }


        //在分词列表中查询brand的分组数据
        searchSourceBuilder.aggregation(AggregationBuilders.terms("brandGroup").field("brand_name")).size(50);
        searchSourceBuilder.aggregation(AggregationBuilders.terms("cateGroup").field("category_name")).size(50);
        searchSourceBuilder.aggregation(AggregationBuilders.terms("specGroup").field("spec")).size(50);


        //执行查询 把 boolQueryBuilder 设置给 searchSourceBuilder
        searchSourceBuilder.query(boolQueryBuilder);

        //查询对象添加 构建查询对象 把 searchSourceBuilder 设置给 searchRequest
        searchRequest.source(searchSourceBuilder);

        //高亮设置
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置前缀标签
        highlightBuilder.preTags("<div style='color:red'>");
        //设置后缀标签
        highlightBuilder.postTags("<div>");
        //设置要高亮显示的字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        //把 highlightBuilder 对象放入到 searchSourceBuilder 对象中
        searchSourceBuilder.highlighter(highlightBuilder);

        //发起请求 获取响应对象
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //获取分组数据
        Terms brandTerms = searchResponse.getAggregations().get("brandGroup");
        Terms cateTerms = searchResponse.getAggregations().get("cateGroup");
        Terms specTerms = searchResponse.getAggregations().get("specGroup");
        //调用 termsAsList() 拿到封装的数据
        List<String> brandList = this.termsAsList(brandTerms);
        List<String> cateList = this.termsAsList(cateTerms);
        Map<String, Set<String>> specMap = this.termAsMap(specTerms);

        //打印一下看看数据
/*        for (String s : brandList) {
            System.out.println(s);
        }
        System.out.println("=======================>");
        for (String s : cateList) {
            System.out.println(s);
        }*/

        //获取分词数据
        SearchHits searchHits = searchResponse.getHits();
        SearchHit[] hits = searchHits.getHits();
        //取出总数据量
        long count = searchHits.getTotalHits();

        //调用方法 将数据封装为Sku对象
        List<Sku> skus = null;
        try {
            skus = skus(hits);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        //把拿到的数据放到返回对象中
        result.setCount(count);
        result.setSkus(skus);
        result.setBrandList(brandList);
        result.setCateList(cateList);
        result.setSpecMap(specMap);

        return result;
    }

    /**
     * 将specGroup 转成 Map<String,Set<String>> 结构
     *
     * @param terms
     * @return
     */
    public Map<String, Set<String>> termAsMap(Terms terms) {
        Map<String, Set<String>> result = new HashMap<>();
        //先转为List<String>集合
        List<String> specList = this.termsAsList(terms);
        //将全部数据转为map
        for (String one : specList) {
            Map<String, String> map = JSON.parseObject(one, Map.class);
            //遍历拿到的数据
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                //根据key尝试获取set集合
                Set<String> valSet = result.get(key);
                //判断set集合为不为 null 如果没有就创建set对象并把值放入
                if (ObjectUtils.isEmpty(valSet)) {
                    valSet = new HashSet<>();
                }
                valSet.add(value);
                //将数据添加到返回的map对象上
                result.put(key, valSet);
            }
        }

        return result;
    }

    /**
     * 将map转为List<Sku> 集合返回
     *
     * @param hits
     * @return
     * @throws ParseException
     */
    private List<Sku> skus(SearchHit[] hits) throws ParseException {
        skuList = new ArrayList<>();
        for (SearchHit hit : hits) {
            String id = hit.getId();
            //System.out.println(id);
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            //封装数据
            sku = new Sku();
            sku.setId(id);
            sku.setSn((String) sourceAsMap.get("sn"));
            sku.setName((String) sourceAsMap.get("name"));
            sku.setPrice((Double) sourceAsMap.get("price"));
            sku.setNum((Integer) sourceAsMap.get("num"));
            sku.setAlertNum((Integer) sourceAsMap.get("alert_num"));
            sku.setImage((String) sourceAsMap.get("image"));
            sku.setImages((String) sourceAsMap.get("images"));
            sku.setWeight((Integer) sourceAsMap.get("weight"));
            Date createTime = simpleDateFormat.parse((String) sourceAsMap.get("create_time"));
            sku.setCreateTime(createTime);
            Date updateTime = simpleDateFormat.parse((String) sourceAsMap.get("update_time"));
            sku.setUpdateTime(updateTime);
            sku.setSpuId((String) sourceAsMap.get("spu_id"));
            sku.setCategoryId((Integer) sourceAsMap.get("category_id"));
            sku.setCategoryName((String) sourceAsMap.get("category_name"));
            sku.setBrandName((String) sourceAsMap.get("brand_name"));
            sku.setSpec((String) sourceAsMap.get("spec"));
            sku.setSaleNum((Integer) sourceAsMap.get("sale_num"));
            sku.setCommentNum((Integer) sourceAsMap.get("comment_num"));
            sku.setStatus(((String) sourceAsMap.get("status")).toCharArray()[0]);
            sku.setVersion((Integer) sourceAsMap.get("version"));
            sku.setSpecMap((Map<String, Object>) sourceAsMap.get("specMap"));

            //获取高亮
            Map<String, HighlightField> namehl = hit.getHighlightFields();
            //获取高亮字段
            HighlightField nameField = namehl.get("name");
            //判断 nameField 对象为不为null
            if (!ObjectUtils.isEmpty(nameField)) {
                StringBuilder nameAppend = new StringBuilder();
                Text[] texts = nameField.getFragments();
                for (Text text : texts) {
                    nameAppend.append(text.toString());
                }
                sku.setName(nameAppend.toString());
            }


            skuList.add(sku);
            //看看数据
/*            Set<String> strings = sourceAsMap.keySet();
            for (String string : strings) {
                Object value = sourceAsMap.get(string);
                System.out.println(string);
                System.out.println(value);
            }
            System.out.println("-------------------------");*/

        }
        return skuList;
    }


    /**
     * 将传来的数据 封装为List并返回
     *
     * @param terms
     * @return
     */
    private List<String> termsAsList(Terms terms) {
        List<String> list = new ArrayList<>();
        //循环封装数据
        for (Terms.Bucket bucket : terms.getBuckets()) {
            String data = bucket.getKey().toString();
            list.add(data);
        }
        return list;
    }


    public void importData() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.01:3306/shop_goods?useUnicode=true&characterEncoding=utf8&useSSL=true&serverTimezone=Asia/Shanghai", "root", "1234");
        String sql = "select * from tb_sku";
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet rs = pstm.executeQuery();
        while (rs.next()) {
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("sn", rs.getString("sn"));
            jsonMap.put("name", rs.getString("name"));
            jsonMap.put("price", rs.getFloat("price"));
            jsonMap.put("num", rs.getInt("num"));
            jsonMap.put("alert_num", rs.getInt("alert_num"));
            jsonMap.put("image", rs.getString("image"));
            jsonMap.put("images", rs.getString("images"));
            jsonMap.put("weight", rs.getInt("weight"));
            jsonMap.put("spu_id", rs.getString("spu_id"));
            jsonMap.put("category_id", rs.getInt("category_id"));
            jsonMap.put("category_name", rs.getString("category_name"));
            jsonMap.put("brand_name", rs.getString("brand_name"));
            jsonMap.put("create_time", rs.getDate("create_time").toString());
            jsonMap.put("update_time", rs.getDate("update_time").toString());
            jsonMap.put("spec", rs.getString("spec"));
            jsonMap.put("sale_num", rs.getInt("sale_num"));
            jsonMap.put("comment_num", rs.getInt("comment_num"));
            jsonMap.put("status", rs.getString("status"));
            jsonMap.put("version", rs.getInt("version"));
            Map specMap = JSON.parseObject(rs.getString("spec"), Map.class);
            jsonMap.put("specMap", specMap);
            IndexRequest request = new IndexRequest("tb_sku", "doc", rs.getString("id"));
            request.source(jsonMap);
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        }
        System.out.println("数据添加完成");
    }

    public void create() throws IOException {
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("tb_sku");
        createIndexRequest.settings(Settings.builder()
                .put("number_of_shards", "1")
                .put("number_of_replicas", "0"));

        createIndexRequest.mapping("doc", "{\n" +
                "\t\"properties\": {\n" +
                "\t\t\"sn\": {\n" +
                "\t\t\t\"type\": \"text\"\n" +
                "\t\t},\n" +
                "\t\t\"name\": {\n" +
                "\t\t\t\"type\": \"text\",\n" +
                "\t\t\t\"analyzer\": \"ik_max_word\",\n" +
                "\t\t\t\"search_analyzer\": \"ik_smart\"\n" +
                "\t\t},\n" +
                "\t\t\"price\": {\n" +
                "\t\t\t\"type\": \"float\"\n" +
                "\t\t},\n" +
                "\t\t\"num\": {\n" +
                "\t\t\t\"type\": \"integer\"\n" +
                "\t\t},\n" +
                "\t\t\"alert_num\": {\n" +
                "\t\t\t\"type\": \"integer\"\n" +
                "\t\t},\n" +
                "\t\t\"image\": {\n" +
                "\t\t\t\"type\": \"text\"\n" +
                "\t\t},\n" +
                "\t\t\"images\": {\n" +
                "\t\t\t\"type\": \"text\"\n" +
                "\t\t},\n" +
                "\t\t\"weight\": {\n" +
                "\t\t\t\"type\": \"integer\"\n" +
                "\t\t},\n" +
                "\t\t\"create_time\": {\n" +
                "\t\t\t\"type\": \"date\",\n" +
                "\t\t\t\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd\"\n" +
                "\t\t},\n" +
                "\t\t\"update_time\": {\n" +
                "\t\t\t\"type\": \"date\",\n" +
                "\t\t\t\"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd\"\n" +
                "\t\t},\n" +
                "\t\t\"spu_id\": {\n" +
                "\t\t\t\"type\": \"text\"\n" +
                "\t\t},\n" +
                "\t\t\"category_id\": {\n" +
                "\t\t\t\"type\": \"integer\"\n" +
                "\t\t},\n" +
                "\t\t\"category_name\": {\n" +
                "\t\t\t\"type\": \"keyword\"\n" +
                "\t\t},\n" +
                "\t\t\"brand_name\": {\n" +
                "\t\t\t\"type\": \"keyword\"\n" +
                "\t\t},\n" +
                "\t\t\"spec\": {\n" +
                "\t\t\t\"type\": \"keyword\"\n" +
                "\t\t},\n" +
                "\t\t\"sale_num\": {\n" +
                "\t\t\t\"type\": \"integer\"\n" +
                "\t\t},\n" +
                "\t\t\"comment_num\": {\n" +
                "\t\t\t\"type\": \"integer\"\n" +
                "\t\t},\n" +
                "\t\t\"status\": {\n" +
                "\t\t\t\"type\": \"keyword\"\n" +
                "\t\t},\n" +
                "\t\t\"version\": {\n" +
                "\t\t\t\"type\": \"integer\"\n" +
                "\t\t},\n" +
                "\t\t\"specMap\":{\n" +
                "               \"properties\":{\n" +
                "                   \"test\":{\n" +
                "                       \"type\": \"keyword\"\n" +
                "}\n" +
                "}\n" +
                "\t\t}\n" +
                "\t}\n" +
                "}", XContentType.JSON);

        CreateIndexResponse response = client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
        System.out.println(response.index());
    }

}
