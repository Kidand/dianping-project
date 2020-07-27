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

        String reqJson = "{\n" +
                "  \"_source\": \"*\",\n" +
                "  \"script_fields\": {\n" +
                "    \"distance\": {\n" +
                "      \"script\": {\n" +
                "        \"source\": \"haversin(lat,lon,doc['location'].lat,doc['location'].lon)\",\n" +
                "        \"lang\": \"expression\",\n" +
                "        \"params\": {\"lat\":" + latitude.toString() + ",\"lon\":" + longitude.toString() + "}\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"query\": {\n" +
                "    \"function_score\": {\n" +
                "      \"query\": {\n" +
                "        \"bool\": {\n" +
                "          \"must\": [\n" +
                "            {\"match\": {\"name\": {\"query\": \"" + keyword + "\"}}},\n" +
                "            {\"term\": {\"seller_disabled_flag\": 0}}\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"functions\": [\n" +
                "        {\n" +
                "          \"gauss\": {\n" +
                "            \"location\": {\n" +
                "              \"origin\": \"" + latitude.toString() + "," + longitude.toString() + "\",\n" +
                "              \"scale\": \"100km\",\n" +
                "              \"offset\": \"0km\",\n" +
                "              \"decay\": 0.5\n" +
                "            }\n" +
                "          },\n" +
                "          \"weight\": 9\n" +
                "        },\n" +
                "        {\n" +
                "          \"field_value_factor\": {\n" +
                "            \"field\": \"remark_score\"\n" +
                "          },\n" +
                "          \"weight\": 0.2\n" +
                "        },\n" +
                "        {\n" +
                "          \"field_value_factor\": {\n" +
                "            \"field\": \"seller_remark_score\"\n" +
                "          },\n" +
                "          \"weight\": 0.1\n" +
                "        }\n" +
                "      ],\n" +
                "      \"score_mode\": \"sum\",\n" +
                "      \"boost_mode\": \"sum\"\n" +
                "    }\n" +
                "  },\n" +
                "  \"sort\": [\n" +
                "    {\n" +
                "      \"_score\": {\n" +
                "        \"order\": \"desc\"\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}";

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
            shopModel.setDistance(distance.multiply(new BigDecimal(1000).setScale(0,BigDecimal.ROUND_CEILING)).intValue());
            shopModelList.add(shopModel);
        }

        result.put("shop", shopModelList);
        return result;
    }

}
