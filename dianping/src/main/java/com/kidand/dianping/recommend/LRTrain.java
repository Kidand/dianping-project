package com.kidand.dianping.recommend;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.classification.LogisticRegression;
import org.apache.spark.ml.classification.LogisticRegressionModel;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.ml.linalg.VectorUDT;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.*;

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
 * @description:LRTrain LR训练
 * @author: Kidand
 * @date: 2021/1/13 8:36 PM
 * Copyright © 2021 by Kidand
 */
public class LRTrain implements Serializable {
    public static void main(String[] args) throws IOException {
        // 初始化spark运行环境
        SparkSession spark = SparkSession.builder().master("local").appName("DianpingApp").getOrCreate();

        // 加载特征及label训练文件
        JavaRDD<String> csvFile = spark.read().textFile("file:///Users/Kidand/Desktop/dianping-project/data/feature.csv").toJavaRDD();
        // 转化
        JavaRDD<Row> rowJavaRDD = csvFile.map(new Function<String, Row>() {
            @Override
            public Row call(String s) throws Exception {
                s = s.replace("\"", "");
                String[] strArr = s.split(",");
                return RowFactory.create(new Double(strArr[11]), Vectors.dense(Double.valueOf(strArr[0])), Vectors.dense(Double.valueOf(strArr[1])), Vectors.dense(Double.valueOf(strArr[2])),
                        Vectors.dense(Double.valueOf(strArr[3])), Vectors.dense(Double.valueOf(strArr[4])), Vectors.dense(Double.valueOf(strArr[5])), Vectors.dense(Double.valueOf(strArr[6])),
                        Vectors.dense(Double.valueOf(strArr[7])), Vectors.dense(Double.valueOf(strArr[8])), Vectors.dense(Double.valueOf(strArr[9])), Vectors.dense(Double.valueOf(strArr[10])));
            }
        });
        StructType schema = new StructType(
                new StructField[]{
                        new StructField("label", DataTypes.DoubleType, false, Metadata.empty()),
                        new StructField("features", new VectorUDT(), false, Metadata.empty())
                }
        );
        Dataset<Row> data = spark.createDataFrame(rowJavaRDD, schema);

        //分开测试集和训练集
        Dataset<Row>[] dataArr = data.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainData = dataArr[0];
        Dataset<Row> testData = dataArr[1];

        LogisticRegression lr = new LogisticRegression().setMaxIter(10).setRegParam(0.3).setElasticNetParam(0.8).setFamily("multinomial");

        // 模型训练
        LogisticRegressionModel lrModel = lr.fit(trainData);

        lrModel.save("file:///Users/Kidand/Desktop/dianping-project/data/lrmode");

        // 测试评估
        Dataset<Row> predictions = lrModel.transform(testData);

        // 评价指标
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator();
        double accuracy = evaluator.setMetricName("accuracy").evaluate(predictions);

        System.out.println("auc = " + accuracy);
    }
}
