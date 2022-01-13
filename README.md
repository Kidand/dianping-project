# dianping-project
## 项目简介

+ 项目亮点：本项目类似大众点评，主要侧重点是基于大数据的千人千面的搜索与推荐

+ 主要技术栈：SpringBoot, MySQL, ElasticSearch, Spark

+ 项目分为三个分支：v1.0, v2.0, v3.0 三个分支对应三个不同的阶段

## 项目版本

### v1.0

+ 基础项目搭建
+ 系统分为了用户服务，运营管理服务，商户服务
+ 通过SQL实现简单的推荐与搜索

### v2.0

+ 集成了ElasticSearch搜索，需要与ElasticSearch中间件配合
+ 基于logstash和canal实现MySQL与ElasticSearch的数据同步
+ 基于ElasticSeatch提供的分词、搜索、自定义排序模型建立系统的搜索模型
+ 基于扩展词库，同义词，词性识别完成相关性搜索的高阶搜索

### v3.0

+ 改造系统的推荐部分，引入Spark
+ 实现ALS推荐召回算法
+ 实现LR推荐排序算法
+ 结合万条数据完成个性化千人千面推荐功能

