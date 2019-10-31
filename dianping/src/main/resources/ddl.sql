CREATE TABLE `dianpingdb`.`user`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `created_at` datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00',
  `updated_at` datetime(0) NOT NULL DEFAULT '0000-00-00 00:00:00',
  `telphone` varchar(40) NOT NULL DEFAULT '',
  `password` varchar(200) NOT NULL DEFAULT '',
  `nick_name` varchar(40) NOT NULL DEFAULT '',
  `gender` int(0) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `telphone_unique_index`(`telphone`) USING BTREE
);

CREATE TABLE `dianpingdb`.`seller`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL DEFAULT '',
  `created_at` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL ON UPDATE CURRENT_TIMESTAMP(0),
  `remark_score` decimal(2, 1) NOT NULL DEFAULT 0,
  `disabled_flag` int(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`)
);