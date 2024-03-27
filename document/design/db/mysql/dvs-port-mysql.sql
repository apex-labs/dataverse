CREATE TABLE `dvs_port`
(
    `port_id`           bigint      NOT NULL AUTO_INCREMENT COMMENT '连接器ID',
    `port_code`         varchar(50) NOT NULL COMMENT '编码，每个Port唯一',
    `port_name`         varchar(50) NOT NULL COMMENT '连接器名称',
    `registry_code`     varchar(255) NULL COMMENT '向注册中心注册时的注册编码，唯一',
    `group_code`        varchar(32) NULL COMMENT '分组编码，当此编码不为空时，只有指定group_code的才可被对应code的driver链接。属于专属port',
    `group_name`        varchar(50) NULL COMMENT '分组名称【冗余】',
    `hostname`          varchar(50) NULL COMMENT '连接器所在主机名',
    `ip`                varchar(20) NULL COMMENT '连接器所在IP',
    `port`              int NULL COMMENT '连接器绑定的端口号',
    `max_driver_conns`  int         NOT NULL DEFAULT 200 COMMENT 'Driver的最大链接数',
    `connected_drivers` int NULL COMMENT '已链接的driver数量',
    `state`             varchar(20) NOT NULL COMMENT '连接器状态。BUILD, ONLINE, PAUSE, OFFLINE',
    `last_heartbeat`    datetime NULL COMMENT '最近心跳时间',
    `heartbeat_hz`      int         NOT NULL DEFAULT 5000 COMMENT '心跳频率，单位毫秒。 默认3000。',
    `description`       varchar(255) NULL COMMENT '描述',
    `is_deleted`        tinyint COMMENT '是否删除标记， 0：未删除， 1：已删除',
    `create_time`       datetime    NOT NULL default now() COMMENT '创建时间',
    `creator_id`        bigint NULL COMMENT '创建人ID',
    `creator_name`      varchar(50) NULL COMMENT '创建人姓名【冗余】',
    PRIMARY KEY (`port_id`),
    UNIQUE INDEX `idx_dvs_port_code`(`port_code`) USING BTREE,
    UNIQUE INDEX `idx_dvs_registry_key`(`registry_code`) USING BTREE,
    INDEX               `idx_dvs_port_group_code`(`group_code`) USING BTREE,
    INDEX               `idx_dvs_port_name`(`port_name`) USING BTREE
) COMMENT = 'Dataverse port';

CREATE TABLE `engine`
(
    `engine_id`        bigint      NOT NULL AUTO_INCREMENT COMMENT '数据计算引擎ID',
    `storage_id`       bigint      NOT NULL COMMENT '所属存储区ID',
    `engine_name`      varchar(50) NOT NULL COMMENT '引擎名称',
    `port_id`          bigint NULL COMMENT '启动Port的ID',
    `port_code`        varchar(20) NULL COMMENT '启动Port的编码',
    `port_name`        varchar(50) NULL COMMENT '启动Port的名称',
    `registry_code`    varchar(255) NULL COMMENT '向注册中心注册时的注册编码，唯一',
    `hostname`         varchar(50) NULL COMMENT '引擎Driver所在的机器主机名',
    `ip`               varchar(20) NOT NULL COMMENT '引擎Driver所在的机器IP',
    `port`             int         NOT NULL COMMENT '引擎Driver中server绑定的端口号',
    `engine_type`      varchar(20) NOT NULL COMMENT '引擎类型，1：SPARK，2：FLINK,',
    `engine_jar`       varchar(255) NULL COMMENT '引擎Jar包路径',
    `dependence_jars`  varchar(255) NULL COMMENT '依赖的jar包',
    `master`           varchar(100) NULL COMMENT '--master',
    `application_id`   varchar(50) NULL COMMENT '引擎启动后的AppID',
    `application_name` varchar(50) NULL COMMENT 'app name',
    `engine_state`     varchar(20) NOT NULL COMMENT '引擎状态, SUBMIT, RUNNING, FINISHED',
    `submit_jobs`      int NULL COMMENT '提交过的作业总数',
    `running_jobs`     int NULL COMMENT '运行中作业总数',
    `pending_jobs`     int NULL COMMENT '待处理作业数',
    `max_running_job`  int NULL COMMENT '最大运行作业数',
    `driver_memory`    int NULL COMMENT 'Driver内存，单位G',
    `driver_cup`       int NULL COMMENT 'Driver CPU，单位核',
    `executor_memory`  int NULL COMMENT '执行器内存，单位G',
    `executor_cup`     int NULL COMMENT '执行器CPU，单位核',
    `executors`        int NULL COMMENT '执行器数量',
    `deploy_mode`      varchar(50) NULL COMMENT '--deploy-mode',
    `queue`            varchar(50) NULL COMMENT '--queue',
    `redirect_out`     varchar(255) NULL COMMENT 'redirect out path',
    `redirect_error`   varchar(255) NULL COMMENT 'redirect error path',
    `create_time`      datetime    NOT NULL COMMENT '创建时间',
    `update_time`      datetime NULL COMMENT '更新时间',
    `description`      text NULL COMMENT '描述信息',
    PRIMARY KEY (`engine_id`),
    INDEX(`storage_id`) USING BTREE,
    INDEX(`port_id`) USING BTREE,
    INDEX(`port_code`) USING BTREE,
    UNIQUE INDEX (`registry_code`) USING BTREE
) COMMENT = '计算引擎注册表';

CREATE TABLE `jdbc_storage`
(
    `jdbc_storage_id` bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `storage_id`      bigint       NOT NULL COMMENT '数据存储区ID',
    `jdbc_url`        varchar(255) NOT NULL COMMENT 'JDBC链接URL',
    `user_name`       varchar(50)  NOT NULL COMMENT 'JDBC链接用户名',
    `password`        varchar(255) NOT NULL COMMENT 'JDBC链接密码',
    `driver_class`    varchar(50)  NOT NULL COMMENT 'JDBC驱动类，如MYSQL的com.mysql.jdbc.Driver',
    `conn_config`     text NULL COMMENT '链接配置JSON，如：{\"serverTimezone\":\"Asia/Shanghai\",\"useUnicode\":\"true\"}\n',
    `storage_type`    varchar(20)  NOT NULL COMMENT '存储区类型,1:HDFS,2:MYSQL,3:PGSQL,4:ORACLE,5:DORIS,6:STAR_ROCKS,7:CLICKHOUSE,8:ELASTICSEARCH\n',
    `description`     varchar(255) NULL COMMENT '描述',
    `create_time`     datetime     NOT NULL COMMENT '创建时间',
    `update_time`     datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`jdbc_storage_id`)
) COMMENT = 'JDBC存储区，如MYSQL, PGSQL, ORACLE, DORIS, STAR_ROCKS, CLICKHOUSE, ELASTICSEARCH';

CREATE TABLE `job_history`
(
    `job_history_id` bigint   NOT NULL AUTO_INCREMENT,
    `job_id`         char(24) NOT NULL COMMENT '作业ID。抽数作业或ETL作业的ID',
    `conn_id`        bigint   NOT NULL COMMENT 'Connection ID',
    `engine_id`      bigint NULL COMMENT 'ODPC链接的命令时为计算引擎ID',
    `engine_name`    varchar(50) NULL COMMENT 'ODPC链接的命令时为计算引擎名称',
    `command_id`     char(24) NOT NULL COMMENT '命令ID',
    `command`        text     NOT NULL COMMENT '命令消息体，Json',
    `submit_time`    datetime NOT NULL COMMENT '作业提交时间',
    `running_time`   datetime NULL COMMENT '作业开始运行时间',
    `finished_time`  datetime NULL COMMENT '作业完成时间',
    `job_state`      tinyint NULL COMMENT '作业状态运行状态',
    `message`        text NULL COMMENT '作业异常消息',
    `create_time`    datetime NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`job_history_id`),
    INDEX(`conn_id`),
    INDEX(`engine_id`),
    INDEX(`command_id`)
) COMMENT = '作业运行历史记录表';

CREATE TABLE `job_log`
(
    `job_log_id`  bigint   NOT NULL AUTO_INCREMENT COMMENT 'ID主键',
    `job_id`      char(24) NOT NULL COMMENT '作业ID',
    `log_text`    text     NOT NULL COMMENT '日志内容',
    `create_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '记录日志的时间',
    PRIMARY KEY (`job_log_id`)
) COMMENT = '作业运行日志表';

CREATE TABLE `odpc_storage`
(
    `odpc_storage_id`   bigint       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `storage_id`        bigint       NOT NULL COMMENT '存储区ID',
    `storage_type`      varchar(20)  NOT NULL COMMENT '存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle',
    `namenodes`         text NULL COMMENT 'Hadoop的NameNode节点, json array [{\"ip\":\"127.0.0.1\", \"host\":\"localhost\"}]',
    `resource_managers` text NULL COMMENT 'Hadoop的resource managers, json array [{\"ip\":\"127.0.0.1\", \"host\":\"localhost\"}]',
    `storage_path`      varchar(255) NOT NULL COMMENT '存储路径',
    `datanodes`         text NULL COMMENT 'Hadoop的datanodes, json array [{\"ip\":\"127.0.0.1\", \"host\":\"localhost\"}]',
    `description`       varchar(255) NULL COMMENT '描述',
    `create_time`       datetime     NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       datetime NULL COMMENT '更新时间',
    `version`           varchar(20) NULL COMMENT '存储区的版本号，如HDFS存储区的软件版本，如MySQL的JDBC存储区的版本',
    PRIMARY KEY (`odpc_storage_id`)
) COMMENT = '自定义ODPC存储区（用自行开发的Spark/Flink Engine)，如基于HDFS的存储区+SparkEngine';

CREATE TABLE `port_conn`
(
    `port_conn_id`  bigint NOT NULL,
    `port_id`       bigint NULL COMMENT 'Port的ID',
    `port_code`     varchar(50) NULL COMMENT 'Port的编码',
    `registry_code` varchar(255) NULL COMMENT '向注册中心注册时的注册编码，唯一',
    `ip`            varchar(20) NULL COMMENT 'driver端的IP',
    `hostname`      varchar(50) NULL COMMENT 'driver端的hostname',
    `state`         varchar(20) NULL COMMENT '链接状态',
    `conn_time`     datetime NULL COMMENT '链接时间',
    `dis_conn_time` datetime NULL COMMENT '断开链接时间',
    `description`   varchar(500) NULL COMMENT '描述',
    PRIMARY KEY (`port_conn_id`),
    UNIQUE INDEX (`registry_code`) USING BTREE,
    INDEX(`port_code`) USING BTREE,
    INDEX(`port_id`) USING BTREE
) COMMENT = '客户(Driver)端链接Port的链接记录表';

CREATE TABLE `port_event`
(
    `port_event_id` bigint       NOT NULL COMMENT 'Port的实例ID',
    `port_id`       bigint       NOT NULL COMMENT 'Port ID',
    `port_name`     varchar(50) NULL COMMENT 'Port名称',
    `hostname`      varchar(50) NULL COMMENT '连接器所在主机名',
    `ip`            varchar(20)  NOT NULL COMMENT '连接器所在IP',
    `port`          int          NOT NULL COMMENT '连接器绑定的端口号',
    `event`         varchar(255) NOT NULL COMMENT '事件，START, STOP, EXCEPTION',
    `description`   varchar(500) NULL,
    `create_time`   datetime     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`port_event_id`),
    INDEX(`port_id`) USING BTREE,
    INDEX(`port_name`) USING BTREE,
    INDEX(`ip`) USING BTREE
) COMMENT = 'Port的实例';

CREATE TABLE `port_group`
(
    `group_id`    bigint       NOT NULL COMMENT '组ID',
    `group_code`  varchar(32)  NOT NULL COMMENT '组编码，唯一',
    `group_name`  varchar(255) NOT NULL COMMENT '组名',
    `create_time` datetime     NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`group_id`),
    UNIQUE INDEX (`group_code`) USING BTREE
);

CREATE TABLE `storage`
(
    `storage_id`    bigint      NOT NULL AUTO_INCREMENT COMMENT '存储区ID',
    `storage_name`  varchar(50) NOT NULL COMMENT '存储区名称，英文名称，唯一',
    `storage_alias` varchar(50) NOT NULL COMMENT '存储区别名，显示名称',
    `storage_abbr`  varchar(20) NOT NULL COMMENT '存储区简称，英文缩写',
    `storage_type`  varchar(20) NOT NULL COMMENT '存储区类型，0：HDFS，1：MySQL，2：MariaDB，3：Doris，4：ClickHouse，5：Oracle',
    `engine_type`   varchar(20) NOT NULL COMMENT '引擎类型，1：Spark, 2: flink，3：非自研引擎（如doris, mysql)',
    `conn_type`     varchar(20) NOT NULL COMMENT '存储区链接方式，1：ODPC，2：JDBC链接',
    `description`   varchar(255) NULL COMMENT '存储区描述信息',
    `is_deleted`     tinyint COMMENT '是否删除标记， 0：未删除， 1：已删除',
    `creator_id`    bigint NULL COMMENT '创建人ID',
    `creator_name`  varchar(50) NULL COMMENT '创建人姓名【冗余】',
    `create_time`   datetime    NOT NULL COMMENT '创建时间',
    `update_time`   datetime NULL COMMENT '更新时间',
    PRIMARY KEY (`storage_id`),
    UNIQUE INDEX (`storage_name`) USING BTREE
);

CREATE TABLE `storage_conn`
(
    `storage_conn_id` bigint      NOT NULL AUTO_INCREMENT COMMENT '引擎客户端链接历史记录ID',
    `storage_id`      bigint      NOT NULL COMMENT '存储区ID',
    `engine_id`       bigint NULL COMMENT '数据计算引擎ID，当为hdfs存储区时，引擎不为空',
    `engine_name`     varchar(50) NULL COMMENT '引擎名称',
    `registry_code`   varchar(255) NULL COMMENT '向注册中心注册时的注册编码，唯一',
    `port_id`         bigint NULL COMMENT 'Port的ID',
    `port_code`       varchar(50) NULL COMMENT 'Port的编码',
    `channel_id`      varchar(50) NULL COMMENT '连接的Channel ID',
    `ip`              varchar(20) NOT NULL COMMENT '连接客户端的IP地址, port链接egine时port的ip',
    `hostname`        varchar(50) NULL COMMENT '连接客户端的host name, port链接egine时port的host',
    `conn_time`       datetime    NOT NULL COMMENT '连接时间',
    `dis_conn_time`   datetime NULL COMMENT '断开连接时间',
    `state`           varchar(20) NULL COMMENT '链接状态',
    `description`     varchar(500) NULL COMMENT '描述',
    PRIMARY KEY (`storage_conn_id`),
    INDEX(`storage_id`) USING BTREE,
    INDEX(`engine_id`) USING BTREE,
    UNIQUE INDEX (`registry_code`) USING BTREE,
    INDEX(`port_id`) USING BTREE,
    INDEX(`port_code`) USING BTREE
) COMMENT = 'Port端链接引擎记录历史';

CREATE TABLE `storage_port`
(
    `storage_port_id`  bigint      NOT NULL AUTO_INCREMENT,
    `storage_id`       bigint      NOT NULL COMMENT 'storage_id',
    `conn_type`        varchar(20) NOT NULL COMMENT '存储区链接方式，1：ODPC，2：JDBC链接',
    `engine_type`      varchar(20) NOT NULL COMMENT '引擎类型，1：Spark, 2: flink，3：非自研引擎（如doris, mysql)',
    `port_id`          bigint      NOT NULL COMMENT 'port_id',
    `port_code`        varchar(50) NOT NULL COMMENT 'port_code',
    `port_name`        varchar(50) NULL COMMENT 'port_name【冗余】',
    `min_jdbc_conns`   int NULL DEFAULT 2 COMMENT 'Port和JDBC存储区间允许创建的最小链接数。',
    `max_jdbc_conns`   int NULL DEFAULT 2 COMMENT 'Port和JDBC存储区间允许创建的最大链接数。',
    `min_odpc_engines` int NULL COMMENT 'ODPC存储区允许创建的最小引擎数',
    `max_odpc_engines` int NULL COMMENT 'ODPC存储区允许创建的最大引擎数',
    `description`      varchar(255) NULL COMMENT '描述',
    `create_time`      datetime    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`storage_port_id`)
) COMMENT = '存储区和Port映射。指定存储区映射到哪些Port，对应存储区上的命令会提交到对应的Port上进行执行。';


CREATE TABLE `storage_tenant`
(
    `auth_id`     bigint NOT NULL AUTO_INCREMENT,
    `storage_id`  bigint NOT NULL COMMENT '存储区ID',
    `tenant_id`   bigint NOT NULL COMMENT '租户ID',
    `tenant_name` varchar(50) NULL COMMENT '租户名称',
    `is_default`  tinyint(1) NULL DEFAULT 0 COMMENT '是否默认存储区，0：否，1：是',
    `create_time` datetime NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `user_id`     bigint NOT NULL COMMENT '创建人/授权人ID',
    `user_name`   varchar(50) NULL COMMENT '创建人/授权人名称【冗余】',
    PRIMARY KEY (`auth_id`),
    INDEX(`tenant_id`) USING BTREE,
    CONSTRAINT `fk_storage_auth_storage_id` FOREIGN KEY (`storage_id`) REFERENCES `storage` (`storage_id`)
) COMMENT = '授权给租户的存储区';

