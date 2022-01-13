package com.kidand.dianping.recommend;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.io.IOException;
import java.io.Serializable;

/**
 * ██╗  ██╗██╗██████╗  █████╗ ███╗   ██╗██████╗
 * ██║ ██╔╝██║██╔══██╗██╔══██╗████╗  ██║██╔══██╗
 * █████╔╝ ██║██║  ██║███████║██╔██╗ ██║██║  ██║
 * ██╔═██╗ ██║██║  ██║██╔══██║██║╚██╗██║██║  ██║
 * ██║  ██╗██║██████╔╝██║  ██║██║ ╚████║██████╔╝
 * ╚═╝  ╚═╝╚═╝╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═══╝╚═════╝
 *
 * @description: AlsRecall Asl召回算法训练
 * @author: Kidand
 * @date: 2021/1/13 15:15
 * Copyright © 2021 by Kidand
 */
public class AlsRecallTrain implements Serializable {

    public static void main(String[] args) throws IOException {
        // System.setProperty("hadoop.home.dir", "/usr/local/Cellar/hadoop/3.3.1");
        // 初始化spark运行环境
        SparkSession spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();
        JavaRDD<String> csvFile = spark.read().textFile("file:///Users/Kidand/Desktop/dianping-project/data/behavior.csv").toJavaRDD();

        JavaRDD<Rating> ratingJavaRDD = csvFile.map(new Function<String, Rating>() {
            @Override
            public Rating call(String s) throws Exception {
                return Rating.parseRating(s);
            }
        });

        Dataset<Row> rating = spark.createDataFrame(ratingJavaRDD, Rating.class);

        // 将所有的rating数据80%训练20%验证
        Dataset<Row>[] splits = rating.randomSplit(new double[]{0.8, 0.2});

        Dataset<Row> trainingData = splits[0];
        Dataset<Row> testingData = splits[1];

        // 注意过拟合，增加数据规模，减少RANK，增大正则化系数
        ALS als = new ALS().setMaxIter(10).setRank(5).setRegParam(0.01)
                .setUserCol("userId").setItemCol("shopId").setRatingCol("rating");

        // 模型训练
        ALSModel alsModel = als.fit(trainingData);

        // 模型评测
        Dataset<Row> predictions = alsModel.transform(testingData);

        // rmse 均方根误差
        RegressionEvaluator evaluator = new RegressionEvaluator().setMetricName("rmse").setLabelCol("rating").setPredictionCol("predictiom");

        double rmse = evaluator.evaluate(predictions);

        System.out.println("rmse = " + rmse);

        alsModel.save("file:///Users/Kidand/Desktop/dianping-project/data/alsmodel");
    }

    public static class Rating implements Serializable {
        private int userId;
        private int shopId;
        private int rating;

        public static Rating parseRating(String str) {
            str = str.replace("\"", "");
            String[] strArr = str.split(",");
            int userId = Integer.parseInt(strArr[0]);
            int shopId = Integer.parseInt(strArr[1]);
            int rating = Integer.parseInt(strArr[2]);

            return new Rating(userId, shopId, rating);
        }

        public Rating(int userId, int shopId, int rating) {
            this.userId = userId;
            this.shopId = shopId;
            this.rating = rating;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getShopId() {
            return shopId;
        }
    }
}
