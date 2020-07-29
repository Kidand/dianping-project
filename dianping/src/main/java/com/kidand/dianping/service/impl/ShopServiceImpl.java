package com.kidand.dianping.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kidand.dianping.common.BusinessException;
import com.kidand.dianping.common.EmBusinessError;
import com.kidand.dianping.dal.ShopModelMapper;
import com.kidand.dianping.model.CategoryModel;
import com.kidand.dianping.model.SellerModel;
import com.kidand.dianping.model.ShopModel;
import com.kidand.dianping.service.CategoryService;
import com.kidand.dianping.service.SellerService;
import com.kidand.dianping.service.ShopService;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ShopServiceImpl implements ShopService {

    @Autowired
    private ShopModelMapper shopModelMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private RestHighLevelClient highLevelClient;

    @Override
    @Transactional
    public ShopModel create(ShopModel shopModel) throws BusinessException {

        shopModel.setCreatedAt(new Date());
        shopModel.setUpdatedAt(new Date());

        //校验商家是否存在正确
        SellerModel sellerModel = sellerService.get(shopModel.getSellerId());

        if (sellerModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商户不存在");
        }

        if (sellerModel.getDisabledFlag().intValue() == 1) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "商户已禁用");
        }

        //校验类目
        CategoryModel categoryModel = categoryService.get(shopModel.getCategoryId());

        if (categoryModel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "类目不存在");
        }

        shopModelMapper.insertSelective(shopModel);
        return get(shopModel.getId());
    }

    @Override
    public ShopModel get(Integer id) {
        ShopModel shopModel = shopModelMapper.selectByPrimaryKey(id);
        if (shopModel == null) {
            return null;
        }

        shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
        shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));

        return shopModel;
    }

    @Override
    public List<ShopModel> selectAll() {
        List<ShopModel> shopModelList = shopModelMapper.selectAll();
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public List<ShopModel> recommend(BigDecimal longitude, BigDecimal latitude) {
        List<ShopModel> shopModelList = shopModelMapper.recommend(longitude, latitude);
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public List<Map<String, Object>> searchGroupByTags(String keyword, Integer categoryId, String tags) {
        return shopModelMapper.searchGroupByTags(keyword, categoryId, tags);
    }

    @Override
    public Integer countAllShop() {
        return shopModelMapper.countAllShop();
    }

    @Override
    public List<ShopModel> search(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderby, Integer categoryId, String tags) {
        List<ShopModel> shopModelList = shopModelMapper.search(longitude, latitude, keyword, orderby, categoryId, tags);
        shopModelList.forEach(shopModel -> {
            shopModel.setSellerModel(sellerService.get(shopModel.getSellerId()));
            shopModel.setCategoryModel(categoryService.get(shopModel.getCategoryId()));
        });
        return shopModelList;
    }

    @Override
    public Map<String, Object> searchES(BigDecimal longitude, BigDecimal latitude, String keyword, Integer orderby, Integer categoryId, String tags) throws IOException {
        Map<String, Object> result = new HashMap<>();

//        SearchRequest searchRequest = new SearchRequest("shop");
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        //查询
//        sourceBuilder.query(QueryBuilders.matchQuery("name", keyword));
//        //等待时间
//        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
//        searchRequest.source(sourceBuilder);
//
//        //拿到hits内容
//        SearchResponse searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
//        SearchHit[] hits = searchResponse.getHits().getHits();
//
//        //用来存id
//        List<Integer> shopIdList = new ArrayList<>();
//
//        for (SearchHit hit : hits) {
//            shopIdList.add(new Integer(hit.getSourceAsMap().get("id").toString()));
//        }

        Request request = new Request("GET", "/shop/_search");

//        String reqJson = "{\n" +
//                "  \"_source\": \"*\",\n" +
//                "  \"script_fields\": {\n" +
//                "    \"distance\": {\n" +
//                "      \"script\": {\n" +
//                "        \"source\": \"haversin(lat,lon,doc['location'].lat,doc['location'].lon)\",\n" +
//                "        \"lang\": \"expression\",\n" +
//                "        \"params\": {\"lat\":" + latitude.toString() + ",\"lon\":" + longitude.toString() + "}\n" +
//                "      }\n" +
//                "    }\n" +
//                "  },\n" +
//                "  \"query\": {\n" +
//                "    \"function_score\": {\n" +
//                "      \"query\": {\n" +
//                "        \"bool\": {\n" +
//                "          \"must\": [\n" +
//                "            {\"match\": {\"name\": {\"query\": \"" + keyword + "\"}}},\n" +
//                "            {\"term\": {\"seller_disabled_flag\": 0}}\n" +
//                "          ]\n" +
//                "        }\n" +
//                "      },\n" +
//                "      \"functions\": [\n" +
//                "        {\n" +
//                "          \"gauss\": {\n" +
//                "            \"location\": {\n" +
//                "              \"origin\": \"" + latitude.toString() + "," + longitude.toString() + "\",\n" +
//                "              \"scale\": \"100km\",\n" +
//                "              \"offset\": \"0km\",\n" +
//                "              \"decay\": 0.5\n" +
//                "            }\n" +
//                "          },\n" +
//                "          \"weight\": 9\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"field_value_factor\": {\n" +
//                "            \"field\": \"remark_score\"\n" +
//                "          },\n" +
//                "          \"weight\": 0.2\n" +
//                "        },\n" +
//                "        {\n" +
//                "          \"field_value_factor\": {\n" +
//                "            \"field\": \"seller_remark_score\"\n" +
//                "          },\n" +
//                "          \"weight\": 0.1\n" +
//                "        }\n" +
//                "      ],\n" +
//                "      \"score_mode\": \"sum\",\n" +
//                "      \"boost_mode\": \"sum\"\n" +
//                "    }\n" +
//                "  },\n" +
//                "  \"sort\": [\n" +
//                "    {\n" +
//                "      \"_score\": {\n" +
//                "        \"order\": \"desc\"\n" +
//                "      }\n" +
//                "    }\n" +
//                "  ]\n" +
//                "}";

        //构建请求
        JSONObject jsonRequestObj = new JSONObject();

        //构建source部分
        jsonRequestObj.put("_source", "*");

        //构建自定义距离字段
        jsonRequestObj.put("script_fields", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").put("distance", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").put("script", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("source", "haversin(lat, lon, doc['location'].lat, doc['location'].lon)");
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("lang", "expression");
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .put("params", new JSONObject());
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lat", latitude);
        jsonRequestObj.getJSONObject("script_fields").getJSONObject("distance").getJSONObject("script")
                .getJSONObject("params").put("lon", longitude);

        //构建query
//        Map<String,Object> cixingMap = analyzeCategoryKeyword(keyword);
//        boolean isAffectFilter = false;
//        boolean isAffectOrder =  true;
        jsonRequestObj.put("query", new JSONObject());

        //构建function score
        jsonRequestObj.getJSONObject("query").put("function_score", new JSONObject());

        //构建function score的query
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("query", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").put("bool", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool").put("must", new JSONArray());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").add(new JSONObject());

        //构建match query
        int queryIndex = 0;
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).put("match", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").put("name", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("query", keyword);
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("match").getJSONObject("name").put("boost", 0.1);

        queryIndex++;
        //构建第二个query的条件
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").add(new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("seller_disabled_flag", 0);

        if(tags != null){
            queryIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("term",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("tags",tags);
        }

        if (categoryId != null) {
            queryIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).put("term", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONObject("query").getJSONObject("bool")
                    .getJSONArray("must").getJSONObject(queryIndex).getJSONObject("term").put("category_id", categoryId);
        }

        //构建functions部分
        jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("functions",new JSONArray());

        int functionIndex = 0;
        if(orderby == null) {
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("gauss", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss").put("location", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("origin", latitude.toString() + "," + longitude.toString());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("scale", "100km");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("offset", "0km");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("gauss")
                    .getJSONObject("location").put("decay", "0.5");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 9);

            functionIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field", "remark_score");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 0.2);

            functionIndex++;
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor", new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field", "seller_remark_score");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("weight", 0.1);

            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode","sum");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode","sum");
        }else{
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").add(new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).put("field_value_factor",new JSONObject());
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").getJSONArray("functions").getJSONObject(functionIndex).getJSONObject("field_value_factor")
                    .put("field","price_per_man");

            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("score_mode","sum");
            jsonRequestObj.getJSONObject("query").getJSONObject("function_score").put("boost_mode","replace");
        }

        //排序字段
        jsonRequestObj.put("sort",new JSONArray());
        jsonRequestObj.getJSONArray("sort").add(new JSONObject());
        jsonRequestObj.getJSONArray("sort").getJSONObject(0).put("_score",new JSONObject());
        if(orderby == null){
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order","desc");
        }else{
            jsonRequestObj.getJSONArray("sort").getJSONObject(0).getJSONObject("_score").put("order","asc");
        }

        //聚合字段
        jsonRequestObj.put("aggs",new JSONObject());
        jsonRequestObj.getJSONObject("aggs").put("group_by_tags",new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").put("terms",new JSONObject());
        jsonRequestObj.getJSONObject("aggs").getJSONObject("group_by_tags").getJSONObject("terms").put("field","tags");


        String reqJson = jsonRequestObj.toJSONString();

            System.out.println(reqJson);
        request.setJsonEntity(reqJson);
        Response response = highLevelClient.getLowLevelClient().performRequest(request);

        String responseStr = EntityUtils.toString(response.getEntity());
        System.out.println(responseStr);

        JSONObject jsonObject = JSONObject.parseObject(responseStr);
        JSONArray jsonArr = jsonObject.getJSONObject("hits").getJSONArray("hits");

        List<ShopModel> shopModelList = new ArrayList<>();

        for (int i = 0; i < jsonArr.size(); i++) {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            Integer id = new Integer(jsonObj.get("_id").toString());
            BigDecimal distance = new BigDecimal(jsonObj.getJSONObject("fields").getJSONArray("distance").get(0).toString());
            ShopModel shopModel = get(id);
            shopModel.setDistance(distance.multiply(new BigDecimal(1000).setScale(0, BigDecimal.ROUND_CEILING)).intValue());
            shopModelList.add(shopModel);
        }

        List<Map> tagsList = new ArrayList<>();

        JSONArray tagsJsonArray = jsonObject.getJSONObject("aggregations").getJSONObject("group_by_tags").getJSONArray("buckets");
        for(int i = 0; i < tagsJsonArray.size();i++){
            JSONObject jsonObj = tagsJsonArray.getJSONObject(i);
            Map<String,Object> tagMap = new HashMap<>();
            tagMap.put("tags",jsonObj.getString("key"));
            tagMap.put("num",jsonObj.getInteger("doc_count"));
            tagsList.add(tagMap);
        }
        result.put("tags",tagsList);

        result.put("shop", shopModelList);
        return result;
    }

}
