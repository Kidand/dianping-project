package com.kidand.dianping.recommend;

import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.SparkSession;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ██╗  ██╗██╗██████╗  █████╗ ███╗   ██╗██████╗
 * ██║ ██╔╝██║██╔══██╗██╔══██╗████╗  ██║██╔══██╗
 * █████╔╝ ██║██║  ██║███████║██╔██╗ ██║██║  ██║
 * ██╔═██╗ ██║██║  ██║██╔══██║██║╚██╗██║██║  ██║
 * ██║  ██╗██║██████╔╝██║  ██║██║ ╚████║██████╔╝
 * ╚═╝  ╚═╝╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═════╝
 *
 * @description:RecommencSortService
 * @author: Kidand
 * @date: 2021/1/13 9:56 PM
 * Copyright © 2021 by Kidand
 */
@Service
public class RecommencSortService {

    private SparkSession spark;

    private LogisticRegressionModel lrModel;

    @PostConstruct
    public void init() {
        // 加载LR模型
        spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();
        lrModel = LogisticRegressionModel.load("file:///Users/Kidand/Desktop/dianping-project/data/lrmode");
    }

    public List<Integer> sort(List<Integer> shopIdLis, Integer userId) {
        // 需要根据lrModel所需要的11维的x，生成特征，然后调用其预测方法
        List<ShopSortModel> list = new ArrayList<>();

        for (Integer shopId : shopIdLis) {
            // 这里是使用假数据，可从数据库中取得真实的性别、年龄、评分、价格等做特征转化生成feture向量
            Vector vector = Vectors.dense(1, 0, 0, 0, 0, 1, 0.6, 0, 0, 1, 0);
            Vector result = lrModel.predictProbability(vector);
            double[] array = result.toArray();
            double score = array[1];

            ShopSortModel shopSortModel = new ShopSortModel();
            shopSortModel.setShopId(shopId);
            shopSortModel.setScore(score);

            list.add(shopSortModel);
        }

        //对list做排序
        list.sort(new Comparator<ShopSortModel>() {
            @Override
            public int compare(ShopSortModel o1, ShopSortModel o2) {
                if (o1.getScore() < o2.getScore()) {
                    return 1;
                } else if (o1.getScore() > o2.getScore()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        return list.stream().map(shopSortModel -> shopSortModel.getShopId()).collect(Collectors.toList());
    }
}
