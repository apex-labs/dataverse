CREATE TABLE `label_instance_tmp_20230912`
(
    `label_instance_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'key',
    `tag_name`          varchar(50) NOT NULL COMMENT '标签名称',
    `count`             int(11) NOT NULL COMMENT '数量',
    `tag_name_enum`     text COMMENT '枚举名称',
    PRIMARY KEY (`label_instance_id`),
    KEY                 `idx_label_instance_label_id3` (`tag_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8